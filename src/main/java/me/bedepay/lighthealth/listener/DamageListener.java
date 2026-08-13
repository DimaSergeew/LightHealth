package me.bedepay.lighthealth.listener;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.display.FormatService;
import me.bedepay.lighthealth.util.Crits;
import me.bedepay.lighthealth.util.DamageViewers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

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
        final Player viewer = DamageViewers.of(event);

        plugin.displays().onDamage(living, healthAfter, max, damage, critical, viewer);
    }

    @EventHandler
    public void onRemove(final EntityRemoveEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        plugin.displays().onEntityRemove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final UUID id = event.getPlayer().getUniqueId();
        plugin.displays().onPlayerQuit(id);
        plugin.lookAt().clearPlayer(id);
    }
}
