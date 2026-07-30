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

    public BossBarChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        handle(snap, format, true);
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean requireDisplayEnabled) {
        final PluginConfig cfg = plugin.config();
        if (requireDisplayEnabled && !cfg.bossbar()) {
            return;
        }
        final Player viewer = snap.viewer();
        if (viewer == null || !ViewAccess.canSee(plugin, viewer)) {
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

        TrackedBar tracked = this.bars.get(playerId);
        if (tracked == null || !tracked.entityId.equals(entityId)) {
            if (tracked != null) {
                viewer.hideBossBar(tracked.bar());
            }
            final BossBar bar = BossBar.bossBar(title, progress, color, cfg.bossbarOverlay());
            viewer.showBossBar(bar);
            tracked = new TrackedBar(bar, entityId, new AtomicInteger());
            this.bars.put(playerId, tracked);
        } else {
            tracked.bar().name(title);
            tracked.bar().progress(progress);
            tracked.bar().color(color);
            tracked.bar().overlay(cfg.bossbarOverlay());
        }

        final int gen = tracked.generation.incrementAndGet();
        final TrackedBar ref = tracked;
        Schedulers.globalDelayed(plugin, cfg.bossbarHideTicks(), () -> {
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

        private TrackedBar(final BossBar bar, final UUID entityId, final AtomicInteger generation) {
            this.bar = bar;
            this.entityId = entityId;
            this.generation = generation;
        }

        private BossBar bar() {
            return bar;
        }
    }
}
