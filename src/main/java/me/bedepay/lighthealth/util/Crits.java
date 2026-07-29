package me.bedepay.lighthealth.util;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public final class Crits {

    private Crits() {
    }

    public static boolean isCritical(final EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent by)) {
            return false;
        }
        return by.isCritical();
    }
}
