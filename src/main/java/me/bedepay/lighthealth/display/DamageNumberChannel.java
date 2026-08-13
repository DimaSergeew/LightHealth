package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.DisplayViewers;
import me.bedepay.lighthealth.util.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class DamageNumberChannel {

    private final LightHealth plugin;
    /** Victim entity id → active floating numbers (for death/remove cleanup). */
    private final Map<UUID, Set<ActiveNumber>> byVictim = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastEnvTick = new ConcurrentHashMap<>();
    private final AtomicInteger lifetime = new AtomicInteger();

    public DamageNumberChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        final PluginConfig cfg = plugin.config();
        if (!cfg.damageNumbers() || snap.damageAmount() <= 0.0) {
            return;
        }
        final LivingEntity entity = snap.entity();
        if (!entity.isValid()) {
            return;
        }
        final UUID victimId = entity.getUniqueId();
        if (snap.viewer() == null) {
            final int interval = cfg.damageEnvIntervalTicks();
            if (interval > 0) {
                final long now = plugin.getServer().getCurrentTick();
                final Long prev = this.lastEnvTick.get(victimId);
                if (prev != null && now - prev < interval) {
                    return;
                }
                this.lastEnvTick.put(victimId, now);
            }
        }

        final boolean crit = snap.critical();
        final Component text = format.damage(
                entity, snap.health(), snap.maxHealth(), snap.damageAmount(), crit);
        final float scale = cfg.damageScale(crit);
        final double rise = crit ? cfg.damageRisePerTick() * 1.25 : cfg.damageRisePerTick();
        final int duration = crit
                ? Math.min(cfg.damageDurationTicks() + 6, cfg.damageDurationTicks() * 2)
                : cfg.damageDurationTicks();

        final int gen = this.lifetime.get();
        Schedulers.entity(plugin, entity, () -> {
            if (this.lifetime.get() != gen) {
                return;
            }
            spawn(entity, victimId, text, cfg, scale, rise, duration, snap.viewer());
        });
    }

    private void spawn(
            final LivingEntity entity,
            final UUID victimId,
            final Component text,
            final PluginConfig cfg,
            final float scale,
            final double rise,
            final int duration,
            final @Nullable Player viewer
    ) {
        if (!entity.isValid()) {
            return;
        }
        if (viewer == null
                && entity.getWorld().getNearbyPlayers(entity.getLocation(), cfg.damageViewDistance()).isEmpty()) {
            return;
        }
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        final double ox = (rng.nextDouble() - 0.5) * 0.7;
        final double oz = (rng.nextDouble() - 0.5) * 0.7;
        final Location start = entity.getLocation().add(ox, entity.getHeight() * 0.75, oz);

        final TextDisplay display = entity.getWorld().spawn(start, TextDisplay.class, d -> {
            d.text(text);
            d.setBillboard(Display.Billboard.CENTER);
            DisplayViewers.prepare(d);
            d.setShadowed(true);
            d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            d.setDefaultBackground(false);
            d.setPersistent(false);
            d.setViewRange((float) (cfg.damageViewDistance() / 64.0));
            d.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(0f, 0f, 0f, 1f),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
            d.setTeleportDuration(1);
        });

        final AtomicInteger tick = new AtomicInteger();
        final Object[] taskHolder = new Object[1];
        // Bind the animation to the *display* entity so it survives victim death on Folia
        // and can still clean itself up. Victim death also triggers removeVictim().
        taskHolder[0] = Schedulers.entityTimer(plugin, display, 1L, 1L, () -> {
            final int t = tick.incrementAndGet();
            if (!display.isValid() || t >= duration) {
                Schedulers.cancel(taskHolder[0]);
                destroyDisplay(victimId, display, taskHolder[0]);
                return;
            }
            display.teleportAsync(display.getLocation().add(0.0, rise, 0.0));
        });

        final ActiveNumber active = new ActiveNumber(display, taskHolder);
        this.byVictim
                .computeIfAbsent(victimId, id -> ConcurrentHashMap.newKeySet())
                .add(active);
        DisplayViewers.show(plugin, display, viewer);
        DisplayViewers.showNearby(plugin, display, start, cfg.damageViewDistance());
    }

    public void concealPlayer(final UUID playerId) {
        for (final Set<ActiveNumber> set : this.byVictim.values()) {
            for (final ActiveNumber n : set) {
                DisplayViewers.hide(plugin, n.display, playerId);
            }
        }
    }

    public void removeVictim(final UUID victimId) {
        this.lastEnvTick.remove(victimId);
        final Set<ActiveNumber> set = this.byVictim.remove(victimId);
        if (set == null) {
            return;
        }
        for (final ActiveNumber n : set) {
            n.destroy();
        }
        set.clear();
    }

    private void destroyDisplay(final UUID victimId, final TextDisplay display, final Object task) {
        Schedulers.cancel(task);
        Schedulers.removeEntity(plugin, display);
        final Set<ActiveNumber> set = this.byVictim.get(victimId);
        if (set == null) {
            return;
        }
        set.removeIf(n -> n.display.equals(display) || !n.display.isValid());
        if (set.isEmpty()) {
            this.byVictim.remove(victimId, set);
        }
    }

    public void shutdown() {
        this.lifetime.incrementAndGet();
        for (final UUID id : this.byVictim.keySet().toArray(UUID[]::new)) {
            removeVictim(id);
        }
        this.byVictim.clear();
        this.lastEnvTick.clear();
    }

    private final class ActiveNumber {
        private final TextDisplay display;
        private final Object[] taskHolder;

        private ActiveNumber(final TextDisplay display, final Object[] taskHolder) {
            this.display = display;
            this.taskHolder = taskHolder;
        }

        private void destroy() {
            Schedulers.cancel(taskHolder[0]);
            Schedulers.removeEntity(plugin, display);
        }
    }
}
