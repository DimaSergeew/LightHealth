package me.bedepay.lighthealth.listener;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.display.FormatService;
import me.bedepay.lighthealth.util.Crits;
import me.bedepay.lighthealth.util.DamageViewers;
import me.bedepay.lighthealth.util.Schedulers;
import me.bedepay.lighthealth.util.ViewAccess;
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

        final double damage = event.getFinalDamage();
        final Player viewer = DamageViewers.of(event);
        showOnboarding(viewer, damage);

        if (!plugin.config().anyChannelEnabled()) {
            return;
        }

        final double max = FormatService.maxHealth(living);
        final double healthAfter = Math.max(0.0, living.getHealth() - damage);
        final boolean critical = Crits.isCritical(event);

        plugin.displays().onDamage(living, healthAfter, max, damage, critical, viewer);
    }

    private void showOnboarding(final Player viewer, final double damage) {
        if (viewer == null
                || damage <= 0.0
                || !plugin.config().onboarding().enabled()
                || !plugin.config().anyFeedbackEnabled()
                || !ViewAccess.canSee(plugin, viewer)
                || !plugin.prefs().markOnboardingShown(viewer.getUniqueId())) {
            return;
        }
        Schedulers.entity(plugin, viewer, () ->
                plugin.messages().send(viewer, "onboarding-first-hit"));
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
