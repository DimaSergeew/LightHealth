package me.bedepay.lighthealth.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class Schedulers {

    private Schedulers() {
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static void entity(final Plugin plugin, final Entity entity, final Runnable task) {
        if (isFolia()) {
            entity.getScheduler().run(plugin, scheduled -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void entityDelayed(
            final Plugin plugin,
            final Entity entity,
            final long delayTicks,
            final Runnable task
    ) {
        if (isFolia()) {
            entity.getScheduler().runDelayed(plugin, scheduled -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static Object entityTimer(
            final Plugin plugin,
            final Entity entity,
            final long delayTicks,
            final long periodTicks,
            final Runnable task
    ) {
        if (isFolia()) {
            return entity.getScheduler().runAtFixedRate(
                    plugin, scheduled -> task.run(), null, Math.max(1L, delayTicks), periodTicks);
        }
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }

    public static void cancel(final Object handle) {
        if (handle == null) {
            return;
        }
        if (handle instanceof BukkitTask bukkitTask) {
            bukkitTask.cancel();
            return;
        }
        try {
            handle.getClass().getMethod("cancel").invoke(handle);
        } catch (final ReflectiveOperationException ignored) {
        }
    }

    public static void global(final Plugin plugin, final Runnable task) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduled -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void globalDelayed(final Plugin plugin, final long delayTicks, final Runnable task) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduled -> task.run(), Math.max(1L, delayTicks));
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static Object globalTimer(
            final Plugin plugin,
            final long delayTicks,
            final long periodTicks,
            final Runnable task
    ) {
        final long delay = Math.max(1L, delayTicks);
        final long period = Math.max(1L, periodTicks);
        if (isFolia()) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduled -> task.run(), delay, period);
        }
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }
}

