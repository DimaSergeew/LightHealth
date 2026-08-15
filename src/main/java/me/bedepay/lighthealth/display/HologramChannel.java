package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.DisplayViewers;
import me.bedepay.lighthealth.util.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class HologramChannel {

    private final LightHealth plugin;
    private final Map<UUID, ActiveHolo> active = new ConcurrentHashMap<>();
    private final AtomicInteger lifetime = new AtomicInteger();

    public HologramChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean fromDamage) {
        final PluginConfig cfg = plugin.config();
        if (fromDamage && !cfg.hologram()) {
            return;
        }
        final LivingEntity entity = snap.entity();
        if (!entity.isValid()) {
            return;
        }

        final int gen = this.lifetime.get();
        final Component text = format.hologram(entity, snap.health(), snap.maxHealth(), snap.damageAmount());
        final Player viewer = snap.viewer();
        Schedulers.entity(plugin, entity, () -> {
            if (this.lifetime.get() != gen) {
                return;
            }
            upsert(entity, text, cfg, fromDamage, viewer);
        });
    }

    private void upsert(
            final LivingEntity entity,
            final Component text,
            final PluginConfig cfg,
            final boolean fromDamage,
            final @Nullable Player viewer
    ) {
        if (!entity.isValid()) {
            return;
        }
        if (fromDamage
                && viewer == null
                && entity.getWorld().getNearbyPlayers(entity.getLocation(), cfg.hologramViewDistance()).isEmpty()) {
            return;
        }

        final UUID id = entity.getUniqueId();
        final ActiveHolo existing = this.active.get(id);
        if (existing != null && existing.display().isValid()) {
            existing.display().text(text);
            existing.refreshOffset(entity, cfg);
            existing.share(entity, cfg, fromDamage, viewer);
            return;
        }

        remove(id);

        final boolean mount = shouldMount(entity);
        final float attachY = mount ? (float) cfg.hologramYOffset() : 0f;
        final Location base = mount
                ? entity.getLocation()
                : worldLocation(entity, cfg);

        final TextDisplay display = entity.getWorld().spawn(base, TextDisplay.class, d -> {
            d.text(text);
            d.setBillboard(Display.Billboard.CENTER);
            DisplayViewers.prepare(d);
            d.setShadowed(true);
            d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            d.setDefaultBackground(false);
            d.setPersistent(false);
            d.setViewRange((float) (cfg.hologramViewDistance() / 64.0));
            d.setTransformation(new Transformation(
                    new Vector3f(0f, attachY, 0f),
                    new AxisAngle4f(0f, 0f, 0f, 1f),
                    new Vector3f(1f, 1f, 1f),
                    new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
            d.setTeleportDuration(mount ? 0 : 1);
        });

        boolean mounted = false;
        if (mount && entity.addPassenger(display)) {
            mounted = true;
        }

        final AtomicInteger generation = new AtomicInteger();
        final ActiveHolo holo = new ActiveHolo(display, generation, mounted, !fromDamage);
        this.active.put(id, holo);
        if (!mounted) {
            if (mount) {
                display.setTransformation(new Transformation(
                        new Vector3f(0f, 0f, 0f),
                        new AxisAngle4f(0f, 0f, 0f, 1f),
                        new Vector3f(1f, 1f, 1f),
                        new AxisAngle4f(0f, 0f, 0f, 1f)
                ));
                display.setTeleportDuration(1);
            }
            display.teleportAsync(worldLocation(entity, cfg));
            holo.startFollow(plugin, entity, id);
        }
        holo.share(entity, cfg, fromDamage, viewer);
    }

    /**
     * Riding a TextDisplay blocks horses / pigs / striders and stacks on jockeys.
     * Those stay world-space and follow with a short teleport timer.
     */
    private static boolean shouldMount(final LivingEntity entity) {
        if (entity.isDead() || entity instanceof Player || entity instanceof Vehicle) {
            return false;
        }
        return entity.getPassengers().isEmpty();
    }

    private static Location worldLocation(final LivingEntity entity, final PluginConfig cfg) {
        return entity.getLocation().add(0.0, entity.getHeight() + cfg.hologramYOffset(), 0.0);
    }

    public void concealPlayer(final UUID playerId) {
        for (final UUID entityId : this.active.keySet().toArray(UUID[]::new)) {
            final ActiveHolo holo = this.active.get(entityId);
            if (holo == null) {
                continue;
            }
            holo.dropLookAt(playerId);
            DisplayViewers.hide(plugin, holo.display(), playerId);
            if (holo.lookAtOnly() && !holo.hasLookAtViewers()) {
                remove(entityId);
            }
        }
    }

    public void hideIfLookAt(final UUID playerId, final UUID entityId) {
        final ActiveHolo holo = this.active.get(entityId);
        if (holo == null) {
            return;
        }
        holo.dropLookAt(playerId);
        if (holo.lookAtOnly() && !holo.hasLookAtViewers()) {
            remove(entityId);
        }
    }

    public void remove(final UUID entityId) {
        final ActiveHolo holo = this.active.remove(entityId);
        if (holo != null) {
            holo.destroy();
        }
    }

    public void shutdown() {
        this.lifetime.incrementAndGet();
        for (final UUID id : this.active.keySet().toArray(UUID[]::new)) {
            remove(id);
        }
    }

    private final class ActiveHolo {
        private final TextDisplay display;
        private final AtomicInteger generation;
        private final boolean mounted;
        private final Set<UUID> lookAtViewers = ConcurrentHashMap.newKeySet();
        private volatile boolean lookAtOnly;
        private volatile Object followTask;

        private ActiveHolo(
                final TextDisplay display,
                final AtomicInteger generation,
                final boolean mounted,
                final boolean lookAtOnly
        ) {
            this.display = display;
            this.generation = generation;
            this.mounted = mounted;
            this.lookAtOnly = lookAtOnly;
        }

        private TextDisplay display() {
            return display;
        }

        private boolean lookAtOnly() {
            return lookAtOnly;
        }

        private boolean hasLookAtViewers() {
            return !this.lookAtViewers.isEmpty();
        }

        private void share(
                final LivingEntity entity,
                final PluginConfig cfg,
                final boolean fromDamage,
                final @Nullable Player viewer
        ) {
            if (fromDamage) {
                this.lookAtOnly = false;
                DisplayViewers.show(plugin, display, viewer);
                DisplayViewers.showNearby(plugin, display, entity.getLocation(), cfg.hologramViewDistance());
                scheduleHide(plugin, entity, entity.getUniqueId(), cfg.hologramHideTicks());
                return;
            }
            if (viewer != null) {
                this.lookAtViewers.add(viewer.getUniqueId());
                DisplayViewers.show(plugin, display, viewer);
            }
        }

        private void dropLookAt(final UUID playerId) {
            if (!this.lookAtViewers.remove(playerId)) {
                return;
            }
            if (this.lookAtOnly) {
                DisplayViewers.hide(plugin, display, playerId);
            }
        }

        private void startFollow(final LightHealth plugin, final LivingEntity entity, final UUID entityId) {
            final Object[] holder = new Object[1];
            holder[0] = Schedulers.entityTimer(plugin, entity, 1L, 1L, () -> {
                if (!entity.isValid() || !display.isValid()) {
                    Schedulers.cancel(holder[0]);
                    remove(entityId);
                    return;
                }
                display.teleportAsync(worldLocation(entity, plugin.config()));
            });
            this.followTask = holder[0];
        }

        private void refreshOffset(final LivingEntity entity, final PluginConfig cfg) {
            if (!display.isValid()) {
                return;
            }
            if (mounted) {
                final Transformation cur = display.getTransformation();
                display.setTransformation(new Transformation(
                        new Vector3f(0f, (float) cfg.hologramYOffset(), 0f),
                        cur.getLeftRotation(),
                        cur.getScale(),
                        cur.getRightRotation()
                ));
                return;
            }
            display.teleportAsync(worldLocation(entity, cfg));
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
                if (current != this) {
                    return;
                }
                if (!this.lookAtViewers.isEmpty()) {
                    revertToLookAtOnly();
                    return;
                }
                remove(entityId);
            });
        }

        private void revertToLookAtOnly() {
            this.lookAtOnly = true;
            if (!display.isValid()) {
                return;
            }
            final Location loc = display.getLocation();
            if (loc.getWorld() == null) {
                return;
            }
            final double range = plugin.config().hologramViewDistance();
            for (final Player player : loc.getWorld().getNearbyPlayers(loc, range)) {
                if (!this.lookAtViewers.contains(player.getUniqueId())) {
                    DisplayViewers.hide(plugin, display, player.getUniqueId());
                }
            }
        }

        private void destroy() {
            this.generation.incrementAndGet();
            Schedulers.cancel(this.followTask);
            this.followTask = null;
            this.lookAtViewers.clear();
            Schedulers.removeEntity(plugin, this.display);
        }
    }
}
