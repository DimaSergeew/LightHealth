package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.LookAtSettings;
import me.bedepay.lighthealth.config.PluginConfig;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Level;

public final class DisplayService {

    private final LightHealth plugin;
    private final HologramChannel hologram;
    private final DamageNumberChannel damageNumbers;
    private final ActionBarChannel actionbar;
    private final BossBarChannel bossbar;
    private volatile FormatService format;

    public DisplayService(final LightHealth plugin) {
        this.plugin = plugin;
        this.hologram = new HologramChannel(plugin);
        this.damageNumbers = new DamageNumberChannel(plugin);
        this.actionbar = new ActionBarChannel(plugin);
        this.bossbar = new BossBarChannel(plugin);
        this.format = new FormatService(plugin.config());
    }

    public void reloadFormat() {
        this.format = new FormatService(plugin.config());
        this.hologram.shutdown();
        this.damageNumbers.shutdown();
        this.bossbar.shutdown();
        this.actionbar.shutdown();
    }

    public void onDamage(
            final LivingEntity entity,
            final double healthAfter,
            final double maxHealth,
            final double damageAmount,
            final boolean critical,
            final @Nullable Player viewer
    ) {
        final PluginConfig cfg = plugin.config();
        if (!cfg.anyChannelEnabled()) {
            return;
        }
        if (!isAllowed(entity, cfg)) {
            return;
        }

        final HealthSnapshot snap = new HealthSnapshot(
                entity, healthAfter, maxHealth, damageAmount, critical, viewer);

        // only-when-damaged applies to the hologram channel alone
        final boolean showHologram = cfg.hologram()
                && (damageAmount > 0.0 || !cfg.hologramOnlyWhenDamaged());

        if (showHologram) {
            safe("hologram", () -> this.hologram.handle(snap, this.format, true));
        }
        if (cfg.damageNumbers()) {
            safe("numbers", () -> this.damageNumbers.handle(snap, this.format));
        }
        if (cfg.actionbar()) {
            safe("actionbar", () -> this.actionbar.handle(snap, this.format, true));
        }
        if (cfg.bossbar()) {
            safe("bossbar", () -> this.bossbar.handle(snap, this.format, true));
        }
    }

    public void onLookAt(final LivingEntity entity, final Player viewer, final LookAtSettings lookAt) {
        final PluginConfig cfg = plugin.config();
        if (!isAllowed(entity, cfg)) {
            return;
        }

        final double max = FormatService.maxHealth(entity);
        final double health = Math.max(0.0, Math.min(max, entity.getHealth()));
        final HealthSnapshot snap = new HealthSnapshot(entity, health, max, 0.0, false, viewer);

        if (lookAt.hologram()) {
            safe("look-hologram", () -> this.hologram.handle(snap, this.format, false));
        }
        if (lookAt.actionbar()) {
            safe("look-actionbar", () -> this.actionbar.handle(snap, this.format, false));
        }
        if (lookAt.bossbar()) {
            safe("look-bossbar", () -> this.bossbar.handle(snap, this.format, false));
        }
    }

    public void hideLookAt(final UUID playerId, final UUID entityId) {
        this.hologram.hideIfLookAt(playerId, entityId);
        this.actionbar.hideIfLookAt(playerId);
        this.bossbar.hideIfLookAt(playerId);
    }

    private void safe(final String channel, final Runnable action) {
        try {
            action.run();
        } catch (final RuntimeException e) {
            plugin.getLogger().log(Level.WARNING, "LightHealth " + channel + " failed", e);
        }
    }

    private static boolean isAllowed(final LivingEntity entity, final PluginConfig cfg) {
        if (!cfg.isEntityAllowed(entity.getType())) {
            return false;
        }
        if (!cfg.isWorldAllowed(entity.getWorld().getName())) {
            return false;
        }
        return !(entity instanceof Player) || cfg.showPlayers();
    }

    public void onEntityRemove(final UUID entityId) {
        this.hologram.remove(entityId);
        this.damageNumbers.removeVictim(entityId);
    }

    public void onPlayerQuit(final UUID playerId) {
        this.bossbar.removePlayer(playerId);
        this.actionbar.removePlayer(playerId);
        // EntityRemoveEvent is never fired for players, so their victim state is dropped here.
        this.hologram.remove(playerId);
        this.damageNumbers.removeVictim(playerId);
    }

    public void clearPersonal(final UUID playerId) {
        this.bossbar.removePlayer(playerId);
        this.actionbar.removePlayer(playerId);
        this.hologram.concealPlayer(playerId);
        this.damageNumbers.concealPlayer(playerId);
    }

    public void shutdown() {
        this.hologram.shutdown();
        this.damageNumbers.shutdown();
        this.bossbar.shutdown();
        this.actionbar.shutdown();
    }
}
