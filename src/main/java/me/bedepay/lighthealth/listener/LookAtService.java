package me.bedepay.lighthealth.listener;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.LookAtSettings;
import me.bedepay.lighthealth.util.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public final class LookAtService {

    private final LightHealth plugin;
    private Object task;

    public LookAtService(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        final LookAtSettings settings = plugin.config().lookAt();
        if (!settings.enabled() || !settings.anyChannel()) {
            return;
        }
        final long interval = settings.intervalTicks();
        this.task = Schedulers.globalTimer(plugin, interval, interval, this::tick);
    }

    public void stop() {
        Schedulers.cancel(this.task);
        this.task = null;
    }

    private void tick() {
        final LookAtSettings settings = plugin.config().lookAt();
        if (!settings.enabled() || !settings.anyChannel()) {
            return;
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            if (!plugin.prefs().isEnabled(player.getUniqueId())) {
                continue;
            }
            if (!player.hasPermission("lighthealth.see")) {
                continue;
            }
            Schedulers.entity(plugin, player, () -> checkPlayer(player, settings));
        }
    }

    private void checkPlayer(final Player player, final LookAtSettings settings) {
        if (!player.isOnline()) {
            return;
        }
        final LivingEntity target = findTarget(player, settings.range());
        if (target == null) {
            return;
        }
        plugin.displays().onLookAt(target, player, settings);
    }

    private static LivingEntity findTarget(final Player player, final int range) {
        final Entity direct = player.getTargetEntity(range, false);
        if (direct instanceof LivingEntity living && living.isValid() && !living.isDead()) {
            return living;
        }

        final RayTraceResult hit = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                range,
                0.4,
                entity -> entity instanceof LivingEntity
                        && entity.isValid()
                        && !entity.equals(player)
        );
        if (hit == null) {
            return null;
        }
        if (hit.getHitEntity() instanceof LivingEntity living && !living.isDead()) {
            return living;
        }
        return null;
    }
}
