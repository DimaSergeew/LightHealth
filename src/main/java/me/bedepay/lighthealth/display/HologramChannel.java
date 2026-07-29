package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Schedulers;
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
            existing.scheduleHide(plugin, entity, id, cfg.hologramHideTicks());
            return;
        }

        remove(id);

        final Location base = entity.getLocation().add(0.0, entity.getHeight() + cfg.hologramYOffset(), 0.0);
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
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(0f, 0f, 0f, 1f),
                    new Vector3f(1f, 1f, 1f),
                    new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
            d.setTeleportDuration(1);
        });

        final AtomicInteger generation = new AtomicInteger();
        final Object followTask = Schedulers.entityTimer(plugin, entity, 1L, 1L, () -> {
            if (!entity.isValid() || entity.isDead() || !display.isValid()) {
                remove(id);
                return;
            }
            final Location at = entity.getLocation().add(0.0, entity.getHeight() + cfg.hologramYOffset(), 0.0);
            display.teleportAsync(at);
        });

        final ActiveHolo holo = new ActiveHolo(display, followTask, generation);
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
        private final Object followTask;
        private final AtomicInteger generation;

        private ActiveHolo(final TextDisplay display, final Object followTask, final AtomicInteger generation) {
            this.display = display;
            this.followTask = followTask;
            this.generation = generation;
        }

        private TextDisplay display() {
            return display;
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
            Schedulers.cancel(this.followTask);
            if (this.display.isValid()) {
                this.display.remove();
            }
        }
    }
}
