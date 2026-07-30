package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.LookAtSettings;
import me.bedepay.lighthealth.config.PluginConfig;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class DisplayService {

    private final LightHealth plugin;
    private final HologramChannel hologram;
    private final DamageNumberChannel damageNumbers;
    private final ActionBarChannel actionbar;
    private final BossBarChannel bossbar;
    private FormatService format;

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
            this.hologram.handle(snap, this.format, true);
        }
        if (cfg.damageNumbers()) {
            this.damageNumbers.handle(snap, this.format);
        }
        if (cfg.actionbar()) {
            this.actionbar.handle(snap, this.format, true);
        }
        if (cfg.bossbar()) {
            this.bossbar.handle(snap, this.format, true);
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
            this.hologram.handle(snap, this.format, false);
        }
        if (lookAt.actionbar()) {
            this.actionbar.handle(snap, this.format, false);
        }
        if (lookAt.bossbar()) {
            this.bossbar.handle(snap, this.format, false);
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

    public void onEntityRemove(final java.util.UUID entityId) {
        this.hologram.remove(entityId);
        this.damageNumbers.removeVictim(entityId);
    }

    public void onPlayerQuit(final java.util.UUID playerId) {
        this.bossbar.removePlayer(playerId);
        this.actionbar.removePlayer(playerId);
        this.plugin.prefs().clearPlayer(playerId);
    }

    public void clearPersonal(final java.util.UUID playerId) {
        this.bossbar.removePlayer(playerId);
        this.actionbar.removePlayer(playerId);
    }

    public void shutdown() {
        this.hologram.shutdown();
        this.damageNumbers.shutdown();
        this.bossbar.shutdown();
        this.actionbar.shutdown();
    }
}
