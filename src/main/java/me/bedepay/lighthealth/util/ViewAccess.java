package me.bedepay.lighthealth.util;

import me.bedepay.lighthealth.LightHealth;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Shared gate for personal HP feedback (prefs + permission).
 * World-visible TextDisplays (hologram / damage numbers) also consult this
 * when a player viewer is known, so {@code /lh toggle} is consistent.
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
