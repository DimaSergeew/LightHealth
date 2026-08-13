package me.bedepay.lighthealth.listener;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.LookAtSettings;
import me.bedepay.lighthealth.util.Schedulers;
import me.bedepay.lighthealth.util.ViewAccess;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LookAtService {

    private final LightHealth plugin;
    private final Map<UUID, UUID> lastTarget = new ConcurrentHashMap<>();
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
        for (final UUID playerId : this.lastTarget.keySet().toArray(UUID[]::new)) {
            clearPlayer(playerId);
        }
    }

    public void clearPlayer(final UUID playerId) {
        final UUID previous = this.lastTarget.remove(playerId);
        if (previous != null) {
            plugin.displays().hideLookAt(playerId, previous);
        }
    }

    public boolean hasTarget(final UUID playerId) {
        return this.lastTarget.containsKey(playerId);
    }

    public boolean isLookingAt(final UUID playerId, final UUID entityId) {
        return entityId.equals(this.lastTarget.get(playerId));
    }

    private void tick() {
        final LookAtSettings settings = plugin.config().lookAt();
        if (!settings.enabled() || !settings.anyChannel()) {
            return;
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            Schedulers.entity(plugin, player, () -> checkPlayer(player, settings));
        }
    }

    private void checkPlayer(final Player player, final LookAtSettings settings) {
        if (!player.isOnline()
                || player.getGameMode() == GameMode.SPECTATOR
                || !ViewAccess.canSee(plugin, player)) {
            clearPlayer(player.getUniqueId());
            return;
        }
        final LivingEntity target = findTarget(player, settings.range());
        final UUID playerId = player.getUniqueId();
        final UUID previous = this.lastTarget.get(playerId);
        if (target == null) {
            if (previous != null) {
                this.lastTarget.remove(playerId, previous);
                plugin.displays().hideLookAt(playerId, previous);
            }
            return;
        }
        final UUID targetId = target.getUniqueId();
        if (previous != null && !previous.equals(targetId)) {
            plugin.displays().hideLookAt(playerId, previous);
        }
        this.lastTarget.put(playerId, targetId);
        Schedulers.entity(plugin, target, () -> {
            if (!target.isValid()) {
                return;
            }
            plugin.displays().onLookAt(target, player, settings);
        });
    }

    private static LivingEntity findTarget(final Player player, final int range) {
        final RayTraceResult hit = player.getWorld().rayTrace(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                range,
                FluidCollisionMode.NEVER,
                true,
                0.4,
                entity -> entity instanceof LivingEntity living
                        && living.isValid()
                        && !living.isDead()
                        && !entity.equals(player)
        );
        if (hit == null || !(hit.getHitEntity() instanceof LivingEntity living)) {
            return null;
        }
        return living;
    }
}
