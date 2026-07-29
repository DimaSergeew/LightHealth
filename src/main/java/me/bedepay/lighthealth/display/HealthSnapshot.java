package me.bedepay.lighthealth.display;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public record HealthSnapshot(
        LivingEntity entity,
        double health,
        double maxHealth,
        double damageAmount,
        boolean critical,
        @Nullable Player viewer
) {
}
