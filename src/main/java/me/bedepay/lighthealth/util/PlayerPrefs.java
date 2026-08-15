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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerPrefs {

    private record Snapshot(List<String> disabled, List<String> onboardingSeen) {
    }

    private final Logger logger;
    private final File file;
    private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> onboardingSeen = ConcurrentHashMap.newKeySet();
    private final Object io = new Object();
    private final Executor writer;
    private final ExecutorService ownedWriter;
    private final AtomicReference<Snapshot> pending = new AtomicReference<>();

    public PlayerPrefs(final JavaPlugin plugin) {
        this(new File(plugin.getDataFolder(), "player-toggles.yml"), plugin.getLogger(), newWriterThread());
    }

    PlayerPrefs(final File file, final Logger logger) {
        this(file, logger, null);
    }

    private PlayerPrefs(final File file, final Logger logger, final ExecutorService ownedWriter) {
        this.file = file;
        this.logger = logger;
        this.ownedWriter = ownedWriter;
        // Tests and offline use write inline; the plugin hands off to a daemon thread so a
        // first-hit onboarding flag never blocks the region thread handling the damage event.
        this.writer = ownedWriter != null ? ownedWriter : Runnable::run;
        load();
    }

    private static ExecutorService newWriterThread() {
        return Executors.newSingleThreadExecutor(runnable -> {
            final Thread thread = new Thread(runnable, "LightHealth-prefs");
            thread.setDaemon(true);
            return thread;
        });
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
            scheduleSave();
            return enabled;
        }
    }

    public boolean markOnboardingShown(final UUID uuid) {
        synchronized (this.io) {
            if (!this.onboardingSeen.add(uuid)) {
                return false;
            }
            scheduleSave();
            return true;
        }
    }

    public boolean hasSeenOnboarding(final UUID uuid) {
        return this.onboardingSeen.contains(uuid);
    }

    public void reload() {
        load();
    }

    public void shutdown() {
        if (this.ownedWriter == null) {
            return;
        }
        this.ownedWriter.shutdown();
        try {
            if (!this.ownedWriter.awaitTermination(5, TimeUnit.SECONDS)) {
                this.logger.warning("Timed out while saving player preferences");
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
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

    private void scheduleSave() {
        this.pending.set(new Snapshot(sortedIds(this.disabled), sortedIds(this.onboardingSeen)));
        try {
            this.writer.execute(this::drain);
        } catch (final RejectedExecutionException e) {
            drain();
        }
    }

    private void drain() {
        final Snapshot snapshot = this.pending.getAndSet(null);
        if (snapshot == null) {
            return;
        }
        final YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("disabled", snapshot.disabled());
        yaml.set("onboarding-seen", snapshot.onboardingSeen());
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
