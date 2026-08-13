package me.bedepay.lighthealth.util;

import me.bedepay.lighthealth.LightHealth;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class DisplayViewers {

    private DisplayViewers() {
    }

    public static void prepare(final TextDisplay display) {
        display.setVisibleByDefault(false);
        display.setSeeThrough(false);
    }

    public static void show(final LightHealth plugin, final Entity display, final @Nullable Player player) {
        if (player == null) {
            return;
        }
        Schedulers.entity(plugin, player, () -> {
            if (!player.isOnline() || !display.isValid() || !ViewAccess.canSee(plugin, player)) {
                return;
            }
            player.showEntity(plugin, display);
        });
    }

    public static void showNearby(
            final LightHealth plugin,
            final Entity display,
            final Location location,
            final double range
    ) {
        if (location.getWorld() == null) {
            return;
        }
        for (final Player player : location.getWorld().getNearbyPlayers(location, range)) {
            show(plugin, display, player);
        }
    }

    public static void hide(final LightHealth plugin, final Entity display, final UUID playerId) {
        final Player player = plugin.getServer().getPlayer(playerId);
        if (player == null) {
            return;
        }
        Schedulers.entity(plugin, player, () -> {
            if (player.isOnline() && display.isValid()) {
                player.hideEntity(plugin, display);
            }
        });
    }
}
