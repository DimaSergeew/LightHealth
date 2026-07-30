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

    public ActionBarChannel(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public void handle(final HealthSnapshot snap, final FormatService format) {
        handle(snap, format, true);
    }

    public void handle(final HealthSnapshot snap, final FormatService format, final boolean requireDisplayEnabled) {
        final PluginConfig cfg = plugin.config();
        if (requireDisplayEnabled && !cfg.actionbar()) {
            return;
        }
        final Player viewer = snap.viewer();
        if (viewer == null || !ViewAccess.canSee(plugin, viewer)) {
            return;
        }

        final Component text = format.actionbar(
                snap.entity(),
                snap.health(),
                snap.maxHealth(),
                snap.damageAmount(),
                snap.critical()
        );
        viewer.sendActionBar(text);

        final UUID id = viewer.getUniqueId();
        final AtomicInteger gen = this.generations.computeIfAbsent(id, u -> new AtomicInteger());
        final int token = gen.incrementAndGet();
        Schedulers.globalDelayed(plugin, cfg.actionbarDurationTicks(), () -> {
            if (gen.get() != token) {
                return;
            }
            final Player online = plugin.getServer().getPlayer(id);
            if (online != null && online.isOnline()) {
                online.sendActionBar(Component.empty());
            }
            this.generations.remove(id, gen);
        });
    }

    public void removePlayer(final UUID playerId) {
        final AtomicInteger gen = this.generations.remove(playerId);
        if (gen != null) {
            gen.incrementAndGet();
        }
        final Player online = plugin.getServer().getPlayer(playerId);
        if (online != null) {
            online.sendActionBar(Component.empty());
        }
    }

    public void shutdown() {
        for (final UUID id : this.generations.keySet().toArray(UUID[]::new)) {
            removePlayer(id);
        }
        this.generations.clear();
    }
}
