package me.bedepay.lighthealth.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class Schedulers {

    private static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (final ClassNotFoundException ignored) {
        }
        FOLIA = folia;
    }

    private Schedulers() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static void entity(final Plugin plugin, final Entity entity, final Runnable task) {
        if (FOLIA) {
            try {
                if (Bukkit.isOwnedByCurrentRegion(entity)) {
                    task.run();
                    return;
                }
            } catch (final RuntimeException ignored) {
            }
            entity.getScheduler().run(plugin, scheduled -> task.run(), null);
            return;
        }
        if (Bukkit.isPrimaryThread()) {
            task.run();
            return;
        }
        Bukkit.getScheduler().runTask(plugin, task);
    }

    public static void entityDelayed(
            final Plugin plugin,
            final Entity entity,
            final long delayTicks,
            final Runnable task
    ) {
        if (FOLIA) {
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
        if (FOLIA) {
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
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduled -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void globalDelayed(final Plugin plugin, final long delayTicks, final Runnable task) {
        if (FOLIA) {
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
        if (FOLIA) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduled -> task.run(), delay, period);
        }
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }
}
