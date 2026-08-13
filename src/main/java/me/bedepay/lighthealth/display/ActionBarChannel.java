package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Schedulers;
import me.bedepay.lighthealth.util.ViewAccess;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ActionBarChannel {

    private final LightHealth plugin;
    private final Map<UUID, AtomicInteger> generations = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> lastFromDamage = new ConcurrentHashMap<>();
    private final AtomicInteger lifetime = new AtomicInteger();

    public ActionBarChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        handle(snap, format, true);
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean fromDamage) {
        final PluginConfig cfg = plugin.config();
        if (fromDamage && !cfg.actionbar()) {
            return;
        }
        final Player viewer = snap.viewer();
        if (viewer == null) {
            return;
        }

        final Component text = format.actionbar(
                snap.entity(),
                snap.health(),
                snap.maxHealth(),
                snap.damageAmount(),
                snap.critical()
        );
        final int duration = cfg.actionbarDurationTicks();
        final int gen = this.lifetime.get();
        Schedulers.entity(plugin, viewer, () -> {
            if (this.lifetime.get() != gen) {
                return;
            }
            show(viewer, text, fromDamage, duration);
        });
    }

    private void show(
            final Player viewer,
            final Component text,
            final boolean fromDamage,
            final int duration
    ) {
        if (!ViewAccess.canSee(plugin, viewer)) {
            return;
        }
        final UUID id = viewer.getUniqueId();
        if (!fromDamage && Boolean.TRUE.equals(this.lastFromDamage.get(id))) {
            return;
        }

        viewer.sendActionBar(text);
        this.lastFromDamage.put(id, fromDamage);
        if (!fromDamage) {
            return;
        }

        final AtomicInteger gen = this.generations.computeIfAbsent(id, u -> new AtomicInteger());
        final int token = gen.incrementAndGet();
        Schedulers.entityDelayed(plugin, viewer, duration, () -> {
            if (gen.get() != token) {
                return;
            }
            if (viewer.isOnline()) {
                viewer.sendActionBar(Component.empty());
            }
            this.generations.remove(id, gen);
            this.lastFromDamage.remove(id, Boolean.TRUE);
        });
    }

    public void hideIfLookAt(final UUID playerId) {
        if (!Boolean.FALSE.equals(this.lastFromDamage.get(playerId))) {
            return;
        }
        removePlayer(playerId);
    }

    public void removePlayer(final UUID playerId) {
        this.lastFromDamage.remove(playerId);
        final AtomicInteger gen = this.generations.remove(playerId);
        if (gen != null) {
            gen.incrementAndGet();
        }
        final Player online = plugin.getServer().getPlayer(playerId);
        if (online != null) {
            Schedulers.entity(plugin, online, () -> {
                if (online.isOnline()) {
                    online.sendActionBar(Component.empty());
                }
            });
        }
    }

    public void shutdown() {
        this.lifetime.incrementAndGet();
        for (final UUID id : this.lastFromDamage.keySet().toArray(UUID[]::new)) {
            removePlayer(id);
        }
        this.generations.clear();
        this.lastFromDamage.clear();
    }
}
