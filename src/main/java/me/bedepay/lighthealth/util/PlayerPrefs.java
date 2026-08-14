package me.bedepay.lighthealth.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerPrefs {

    private final Logger logger;
    private final File file;
    private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> onboardingSeen = ConcurrentHashMap.newKeySet();
    private final Object io = new Object();

    public PlayerPrefs(final JavaPlugin plugin) {
        this(new File(plugin.getDataFolder(), "player-toggles.yml"), plugin.getLogger());
    }

    PlayerPrefs(final File file, final Logger logger) {
        this.file = file;
        this.logger = logger;
        load();
    }

    public boolean isEnabled(final UUID uuid) {
        return !this.disabled.contains(uuid);
    }

    public boolean toggle(final UUID uuid) {
        synchronized (this.io) {
            final boolean enabled;
            if (this.disabled.remove(uuid)) {
                enabled = true;
            } else {
                this.disabled.add(uuid);
                enabled = false;
            }
            saveLocked();
            return enabled;
        }
    }

    public boolean markOnboardingShown(final UUID uuid) {
        synchronized (this.io) {
            if (!this.onboardingSeen.add(uuid)) {
                return false;
            }
            saveLocked();
            return true;
        }
    }

    public boolean hasSeenOnboarding(final UUID uuid) {
        return this.onboardingSeen.contains(uuid);
    }

    public void clearPlayer(final UUID uuid) {
        synchronized (this.io) {
            if (this.disabled.remove(uuid)) {
                saveLocked();
            }
        }
    }

    public void clear() {
        synchronized (this.io) {
            this.disabled.clear();
            saveLocked();
        }
    }

    private void load() {
        synchronized (this.io) {
            this.disabled.clear();
            this.onboardingSeen.clear();
            if (!this.file.isFile()) {
                return;
            }
            final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.file);
            loadIds(yaml.getStringList("disabled"), this.disabled, "toggle");
            loadIds(yaml.getStringList("onboarding-seen"), this.onboardingSeen, "onboarding");
        }
    }

    private void saveLocked() {
        final YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("disabled", sortedIds(this.disabled));
        yaml.set("onboarding-seen", sortedIds(this.onboardingSeen));
        try {
            yaml.save(this.file);
        } catch (final IOException e) {
            this.logger.log(Level.WARNING, "Could not save player preferences", e);
        }
    }

    private void loadIds(final List<String> rawIds, final Set<UUID> target, final String kind) {
        for (final String raw : rawIds) {
            try {
                target.add(UUID.fromString(raw));
            } catch (final IllegalArgumentException ignored) {
                this.logger.warning("Ignoring invalid " + kind + " UUID: " + raw);
            }
        }
    }

    private static List<String> sortedIds(final Set<UUID> source) {
        final List<String> ids = new ArrayList<>(source.size());
        for (final UUID id : source) {
            ids.add(id.toString());
        }
        Collections.sort(ids);
        return ids;
    }
}
