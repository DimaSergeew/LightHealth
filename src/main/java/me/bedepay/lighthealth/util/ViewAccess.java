package me.bedepay.lighthealth.util;

import me.bedepay.lighthealth.LightHealth;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Shared gate for personal HP feedback (prefs + permission).
 * TextDisplays are hidden by default and only shown to players who pass this check.
 */
public final class ViewAccess {

    private ViewAccess() {
    }

    public static boolean canSee(final LightHealth plugin, final @Nullable Player player) {
        if (player == null || !player.isOnline()) {
            return false;
        }
        if (!plugin.prefs().isEnabled(player.getUniqueId())) {
            return false;
        }
        return player.hasPermission("lighthealth.see");
    }
}
