package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Schedulers;
import me.bedepay.lighthealth.util.ViewAccess;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class BossBarChannel {

    private final LightHealth plugin;
    private final Map<UUID, TrackedBar> bars = new ConcurrentHashMap<>();
    private final AtomicInteger lifetime = new AtomicInteger();

    public BossBarChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        handle(snap, format, true);
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean fromDamage) {
        final PluginConfig cfg = plugin.config();
        if (fromDamage && !cfg.bossbar()) {
            return;
        }
        final Player viewer = snap.viewer();
        if (viewer == null) {
            return;
        }
        if (cfg.bossbarMinMaxHealth() > 0.0 && snap.maxHealth() < cfg.bossbarMinMaxHealth()) {
            return;
        }

        final Component title = format.bossbar(
                snap.entity(),
                snap.health(),
                snap.maxHealth(),
                snap.damageAmount(),
                snap.critical()
        );
        final float progress = (float) Math.max(0.0, Math.min(1.0,
                snap.health() / Math.max(0.1, snap.maxHealth())));
        final BossBar.Color color = format.bossBarColor(snap.health(), snap.maxHealth());
        final UUID playerId = viewer.getUniqueId();
        final UUID entityId = snap.entity().getUniqueId();
        final int hideTicks = cfg.bossbarHideTicks();
        final BossBar.Overlay overlay = cfg.bossbarOverlay();
        final int gen = this.lifetime.get();

        Schedulers.entity(plugin, viewer, () -> {
            if (this.lifetime.get() != gen) {
                return;
            }
            show(viewer, playerId, entityId, title, progress, color, overlay, hideTicks, fromDamage);
        });
    }

    private void show(
            final Player viewer,
            final UUID playerId,
            final UUID entityId,
            final Component title,
            final float progress,
            final BossBar.Color color,
            final BossBar.Overlay overlay,
            final int hideTicks,
            final boolean fromDamage
    ) {
        if (!ViewAccess.canSee(plugin, viewer)) {
            return;
        }

        TrackedBar tracked = this.bars.get(playerId);
        if (tracked == null || !tracked.entityId.equals(entityId)) {
            if (tracked != null) {
                viewer.hideBossBar(tracked.bar());
            }
            final BossBar bar = BossBar.bossBar(title, progress, color, overlay);
            viewer.showBossBar(bar);
            tracked = new TrackedBar(bar, entityId, new AtomicInteger(), fromDamage);
            this.bars.put(playerId, tracked);
        } else {
            tracked.bar().name(title);
            tracked.bar().progress(progress);
            tracked.bar().color(color);
            tracked.bar().overlay(overlay);
            if (fromDamage) {
                tracked.fromDamage = true;
            }
        }

        final int gen = tracked.generation.incrementAndGet();
        final TrackedBar ref = tracked;
        Schedulers.globalDelayed(plugin, hideTicks, () -> {
            if (ref.generation.get() != gen) {
                return;
            }
            final TrackedBar current = this.bars.get(playerId);
            if (current != ref) {
                return;
            }
            final Player online = plugin.getServer().getPlayer(playerId);
            if (online != null) {
                online.hideBossBar(ref.bar());
            }
            this.bars.remove(playerId, ref);
        });
    }

    public void hideIfLookAt(final UUID playerId) {
        final TrackedBar tracked = this.bars.get(playerId);
        if (tracked == null || tracked.fromDamage) {
            return;
        }
        removePlayer(playerId);
    }

    public void removePlayer(final UUID playerId) {
        final TrackedBar tracked = this.bars.remove(playerId);
        if (tracked == null) {
            return;
        }
        tracked.generation.incrementAndGet();
        final Player online = plugin.getServer().getPlayer(playerId);
        if (online != null) {
            online.hideBossBar(tracked.bar());
        }
    }

    public void shutdown() {
        this.lifetime.incrementAndGet();
        for (final Map.Entry<UUID, TrackedBar> e : this.bars.entrySet()) {
            e.getValue().generation.incrementAndGet();
            final Player online = plugin.getServer().getPlayer(e.getKey());
            if (online != null) {
                online.hideBossBar(e.getValue().bar());
            }
        }
        this.bars.clear();
    }

    private static final class TrackedBar {
        private final BossBar bar;
        private final UUID entityId;
        private final AtomicInteger generation;
        private volatile boolean fromDamage;

        private TrackedBar(
                final BossBar bar,
                final UUID entityId,
                final AtomicInteger generation,
                final boolean fromDamage
        ) {
            this.bar = bar;
            this.entityId = entityId;
            this.generation = generation;
            this.fromDamage = fromDamage;
        }

        private BossBar bar() {
            return bar;
        }
    }
}
