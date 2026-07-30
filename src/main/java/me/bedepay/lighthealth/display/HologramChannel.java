package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Schedulers;
import me.bedepay.lighthealth.util.ViewAccess;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class HologramChannel {

    private final LightHealth plugin;
    private final Map<UUID, ActiveHolo> active = new ConcurrentHashMap<>();

    public HologramChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        handle(snap, format, true);
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean requireDisplayEnabled) {
        final PluginConfig cfg = plugin.config();
        if (requireDisplayEnabled && !cfg.hologram()) {
            return;
        }
        // Known player viewer must be allowed (toggle / permission).
        // No viewer (e.g. fire damage) still spawns a world-visible hologram.
        if (snap.viewer() != null && !ViewAccess.canSee(plugin, snap.viewer())) {
            return;
        }
        final LivingEntity entity = snap.entity();
        if (!entity.isValid() || entity.isDead()) {
            return;
        }

        final Component text = format.hologram(entity, snap.health(), snap.maxHealth(), snap.damageAmount());
        Schedulers.entity(plugin, entity, () -> upsert(entity, text, cfg));
    }

    private void upsert(final LivingEntity entity, final Component text, final PluginConfig cfg) {
        if (!entity.isValid() || entity.isDead()) {
            return;
        }
        final UUID id = entity.getUniqueId();
        final ActiveHolo existing = this.active.get(id);
        if (existing != null && existing.display().isValid()) {
            existing.display().text(text);
            existing.refreshOffset(entity, cfg);
            existing.scheduleHide(plugin, entity, id, cfg.hologramHideTicks());
            return;
        }

        remove(id);

        final float y = (float) (entity.getHeight() + cfg.hologramYOffset());
        final Location base = entity.getLocation();
        final TextDisplay display = entity.getWorld().spawn(base, TextDisplay.class, d -> {
            d.text(text);
            d.setBillboard(Display.Billboard.CENTER);
            d.setSeeThrough(true);
            d.setShadowed(true);
            d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            d.setDefaultBackground(false);
            d.setPersistent(false);
            d.setViewRange((float) (cfg.hologramViewDistance() / 64.0));
            d.setTransformation(new Transformation(
                    new Vector3f(0f, y, 0f),
                    new AxisAngle4f(0f, 0f, 0f, 1f),
                    new Vector3f(1f, 1f, 1f),
                    new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
            d.setTeleportDuration(0);
        });

        // Ride the entity so we do not need a per-tick teleport follow task.
        entity.addPassenger(display);

        final AtomicInteger generation = new AtomicInteger();
        final ActiveHolo holo = new ActiveHolo(display, generation);
        this.active.put(id, holo);
        holo.scheduleHide(plugin, entity, id, cfg.hologramHideTicks());
    }

    public void remove(final UUID entityId) {
        final ActiveHolo holo = this.active.remove(entityId);
        if (holo != null) {
            holo.destroy();
        }
    }

    public void shutdown() {
        for (final UUID id : this.active.keySet().toArray(UUID[]::new)) {
            remove(id);
        }
    }

    private final class ActiveHolo {
        private final TextDisplay display;
        private final AtomicInteger generation;

        private ActiveHolo(final TextDisplay display, final AtomicInteger generation) {
            this.display = display;
            this.generation = generation;
        }

        private TextDisplay display() {
            return display;
        }

        private void refreshOffset(final LivingEntity entity, final PluginConfig cfg) {
            if (!display.isValid()) {
                return;
            }
            final float y = (float) (entity.getHeight() + cfg.hologramYOffset());
            final Transformation cur = display.getTransformation();
            display.setTransformation(new Transformation(
                    new Vector3f(0f, y, 0f),
                    cur.getLeftRotation(),
                    cur.getScale(),
                    cur.getRightRotation()
            ));
        }

        private void scheduleHide(
                final LightHealth plugin,
                final Entity entity,
                final UUID entityId,
                final int hideTicks
        ) {
            final int gen = this.generation.incrementAndGet();
            Schedulers.entityDelayed(plugin, entity, hideTicks, () -> {
                if (this.generation.get() != gen) {
                    return;
                }
                final ActiveHolo current = HologramChannel.this.active.get(entityId);
                if (current == this) {
                    remove(entityId);
                }
            });
        }

        private void destroy() {
            this.generation.incrementAndGet();
            if (this.display.isValid()) {
                this.display.remove();
            }
        }
    }
}
