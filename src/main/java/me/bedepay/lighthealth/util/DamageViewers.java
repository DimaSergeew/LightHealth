package me.bedepay.lighthealth.util;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jspecify.annotations.Nullable;

public final class DamageViewers {

    private static final int MAX_DEPTH = 6;

    private DamageViewers() {
    }

    public static @Nullable Player of(final EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent by)) {
            return null;
        }
        return fromEntity(by.getDamager(), 0);
    }

    private static @Nullable Player fromEntity(final @Nullable Entity entity, final int depth) {
        if (entity == null || depth > MAX_DEPTH) {
            return null;
        }
        if (entity instanceof Player player) {
            return player;
        }
        if (entity instanceof Projectile projectile) {
            return fromSource(projectile.getShooter(), depth);
        }
        if (entity instanceof TNTPrimed tnt) {
            return fromEntity(tnt.getSource(), depth + 1);
        }
        if (entity instanceof AreaEffectCloud cloud) {
            return fromSource(cloud.getSource(), depth);
        }
        if (entity instanceof Tameable tameable) {
            return tameable.getOwner() instanceof Player player ? player : null;
        }
        if (entity instanceof EvokerFangs fangs) {
            return fromEntity(fangs.getOwner(), depth + 1);
        }
        if (entity instanceof LightningStrike lightning) {
            final Player causingPlayer = lightning.getCausingPlayer();
            if (causingPlayer != null) {
                return causingPlayer;
            }
            return fromEntity(lightning.getCausingEntity(), depth + 1);
        }
        return null;
    }

    private static @Nullable Player fromSource(final @Nullable ProjectileSource source, final int depth) {
        if (source instanceof Player player) {
            return player;
        }
        if (source instanceof Entity entity) {
            return fromEntity(entity, depth + 1);
        }
        return null;
    }
}
