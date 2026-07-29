package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class DamageNumberChannel {

    private final LightHealth plugin;

    public DamageNumberChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        final PluginConfig cfg = plugin.config();
        if (!cfg.damageNumbers() || snap.damageAmount() <= 0.0) {
            return;
        }
        final LivingEntity entity = snap.entity();
        if (!entity.isValid()) {
            return;
        }

        final boolean crit = snap.critical();
        final Component text = format.damage(
                entity, snap.health(), snap.maxHealth(), snap.damageAmount(), crit);
        final float scale = cfg.damageScale(crit);
        final double rise = crit ? cfg.damageRisePerTick() * 1.25 : cfg.damageRisePerTick();
        final int duration = crit
                ? Math.min(cfg.damageDurationTicks() + 6, cfg.damageDurationTicks() * 2)
                : cfg.damageDurationTicks();

        Schedulers.entity(plugin, entity, () -> spawn(entity, text, cfg, scale, rise, duration));
    }

    private void spawn(
            final LivingEntity entity,
            final Component text,
            final PluginConfig cfg,
            final float scale,
            final double rise,
            final int duration
    ) {
        if (!entity.isValid()) {
            return;
        }
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        final double ox = (rng.nextDouble() - 0.5) * 0.7;
        final double oz = (rng.nextDouble() - 0.5) * 0.7;
        final Location start = entity.getLocation().add(ox, entity.getHeight() * 0.75, oz);

        final TextDisplay display = entity.getWorld().spawn(start, TextDisplay.class, d -> {
            d.text(text);
            d.setBillboard(Display.Billboard.CENTER);
            d.setSeeThrough(true);
            d.setShadowed(true);
            d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            d.setDefaultBackground(false);
            d.setPersistent(false);
            d.setViewRange((float) (cfg.damageViewDistance() / 64.0));
            d.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(0f, 0f, 0f, 1f),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f(0f, 0f, 0f, 1f)
            ));
            d.setTeleportDuration(1);
        });

        final AtomicInteger tick = new AtomicInteger();
        final Object[] taskHolder = new Object[1];
        taskHolder[0] = Schedulers.entityTimer(plugin, entity, 1L, 1L, () -> {
            final int t = tick.incrementAndGet();
            if (!display.isValid() || t >= duration) {
                Schedulers.cancel(taskHolder[0]);
                if (display.isValid()) {
                    display.remove();
                }
                return;
            }
            display.teleportAsync(display.getLocation().add(0.0, rise, 0.0));
        });
    }
}
