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

public final class PlayerPrefs {

    private final JavaPlugin plugin;
    private final File file;
    private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();

    public PlayerPrefs(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "player-toggles.yml");
        load();
    }

    public boolean isEnabled(final UUID uuid) {
        return !this.disabled.contains(uuid);
    }

    public boolean toggle(final UUID uuid) {
        final boolean enabled;
        if (this.disabled.remove(uuid)) {
            enabled = true;
        } else {
            this.disabled.add(uuid);
            enabled = false;
        }
        save();
        return enabled;
    }

    public void clearPlayer(final UUID uuid) {
        if (this.disabled.remove(uuid)) {
            save();
        }
    }

    public void clear() {
        this.disabled.clear();
        save();
    }

    private void load() {
        this.disabled.clear();
        if (!this.file.isFile()) {
            return;
        }
        final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.file);
        for (final String raw : yaml.getStringList("disabled")) {
            try {
                this.disabled.add(UUID.fromString(raw));
            } catch (final IllegalArgumentException ignored) {
                this.plugin.getLogger().warning("Ignoring invalid toggle UUID: " + raw);
            }
        }
    }

    private void save() {
        final YamlConfiguration yaml = new YamlConfiguration();
        final List<String> ids = new ArrayList<>(this.disabled.size());
        for (final UUID id : this.disabled) {
            ids.add(id.toString());
        }
        Collections.sort(ids);
        yaml.set("disabled", ids);
        try {
            yaml.save(this.file);
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "Could not save player toggles", e);
        }
    }
}
