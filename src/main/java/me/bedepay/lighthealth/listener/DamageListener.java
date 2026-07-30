package me.bedepay.lighthealth.listener;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.display.FormatService;
import me.bedepay.lighthealth.util.Crits;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jspecify.annotations.Nullable;

public final class DamageListener implements Listener {

    private final LightHealth plugin;

    public DamageListener(final LightHealth plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }
        if (!plugin.config().anyChannelEnabled()) {
            return;
        }

        final double damage = event.getFinalDamage();
        final double max = FormatService.maxHealth(living);
        final double healthAfter = Math.max(0.0, living.getHealth() - damage);
        final boolean critical = Crits.isCritical(event);
        final Player viewer = resolveViewer(event);

        plugin.displays().onDamage(living, healthAfter, max, damage, critical, viewer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final EntityDeathEvent event) {
        plugin.displays().onEntityRemove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onRemove(final EntityRemoveEvent event) {
        plugin.displays().onEntityRemove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        plugin.displays().onPlayerQuit(event.getPlayer().getUniqueId());
    }

    private static @Nullable Player resolveViewer(final EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent by)) {
            return null;
        }
        final Entity damager = by.getDamager();
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof Projectile projectile) {
            final ProjectileSource src = projectile.getShooter();
            if (src instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}
