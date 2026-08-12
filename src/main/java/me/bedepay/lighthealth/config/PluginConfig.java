package me.bedepay.lighthealth.config;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public final class PluginConfig {

    public enum Style {
        HEARTS, BAR, NUMERIC, CUSTOM
    }

    public record DamageTier(double max, String format) {
    }

    private final Style style;
    private final boolean hologram;
    private final boolean damageNumbers;
    private final boolean actionbar;
    private final boolean bossbar;

    private final boolean hologramOnlyWhenDamaged;
    private final int hologramHideTicks;
    private final double hologramViewDistance;
    private final double hologramYOffset;

    private final int damageDurationTicks;
    private final double damageRisePerTick;
    private final double damageViewDistance;
    private final float damageBaseScale;
    private final float damageCritScale;
    private final int damageEnvIntervalTicks;
    private final List<DamageTier> damageTiers;
    private final boolean critEnabled;
    private final String critSymbol;
    private final String critFormat;

    private final int actionbarDurationTicks;

    private final int bossbarHideTicks;
    private final double bossbarMinMaxHealth;
    private final BossBar.Color bossbarColor;
    private final BossBar.Overlay bossbarOverlay;
    private final boolean bossbarDynamicColor;
    private final double highPercent;
    private final double midPercent;

    private final String formatHologram;
    private final String formatDamage;
    private final String formatActionbar;
    private final String formatBossbar;
    private final String formatLookAtActionbar;
    private final String formatLookAtBossbar;

    private final String heartFull;
    private final String heartEmpty;
    private final double hpPerHeart;
    private final int maxHeartIcons;
    private final String barFilled;
    private final String barEmpty;
    private final int barLength;

    private final Set<EntityType> blacklistedEntities;
    private final Set<String> worlds;
    private final boolean worldsAsWhitelist;
    private final boolean showPlayers;
    private final LookAtSettings lookAt;

    private PluginConfig(
            final Style style,
            final boolean hologram,
            final boolean damageNumbers,
            final boolean actionbar,
            final boolean bossbar,
            final boolean hologramOnlyWhenDamaged,
            final int hologramHideTicks,
            final double hologramViewDistance,
            final double hologramYOffset,
            final int damageDurationTicks,
            final double damageRisePerTick,
            final double damageViewDistance,
            final float damageBaseScale,
            final float damageCritScale,
            final int damageEnvIntervalTicks,
            final List<DamageTier> damageTiers,
            final boolean critEnabled,
            final String critSymbol,
            final String critFormat,
            final int actionbarDurationTicks,
            final int bossbarHideTicks,
            final double bossbarMinMaxHealth,
            final BossBar.Color bossbarColor,
            final BossBar.Overlay bossbarOverlay,
            final boolean bossbarDynamicColor,
            final double highPercent,
            final double midPercent,
            final String formatHologram,
            final String formatDamage,
            final String formatActionbar,
            final String formatBossbar,
            final String formatLookAtActionbar,
            final String formatLookAtBossbar,
            final String heartFull,
            final String heartEmpty,
            final double hpPerHeart,
            final int maxHeartIcons,
            final String barFilled,
            final String barEmpty,
            final int barLength,
            final Set<EntityType> blacklistedEntities,
            final Set<String> worlds,
            final boolean worldsAsWhitelist,
            final boolean showPlayers,
            final LookAtSettings lookAt
    ) {
        this.style = style;
        this.hologram = hologram;
        this.damageNumbers = damageNumbers;
        this.actionbar = actionbar;
        this.bossbar = bossbar;
        this.hologramOnlyWhenDamaged = hologramOnlyWhenDamaged;
        this.hologramHideTicks = hologramHideTicks;
        this.hologramViewDistance = hologramViewDistance;
        this.hologramYOffset = hologramYOffset;
        this.damageDurationTicks = damageDurationTicks;
        this.damageRisePerTick = damageRisePerTick;
        this.damageViewDistance = damageViewDistance;
        this.damageBaseScale = damageBaseScale;
        this.damageCritScale = damageCritScale;
        this.damageEnvIntervalTicks = damageEnvIntervalTicks;
        this.damageTiers = List.copyOf(damageTiers);
        this.critEnabled = critEnabled;
        this.critSymbol = critSymbol;
        this.critFormat = critFormat;
        this.actionbarDurationTicks = actionbarDurationTicks;
        this.bossbarHideTicks = bossbarHideTicks;
        this.bossbarMinMaxHealth = bossbarMinMaxHealth;
        this.bossbarColor = bossbarColor;
        this.bossbarOverlay = bossbarOverlay;
        this.bossbarDynamicColor = bossbarDynamicColor;
        this.highPercent = highPercent;
        this.midPercent = midPercent;
        this.formatHologram = formatHologram;
        this.formatDamage = formatDamage;
        this.formatActionbar = formatActionbar;
        this.formatBossbar = formatBossbar;
        this.formatLookAtActionbar = formatLookAtActionbar;
        this.formatLookAtBossbar = formatLookAtBossbar;
        this.heartFull = heartFull;
        this.heartEmpty = heartEmpty;
        this.hpPerHeart = hpPerHeart;
        this.maxHeartIcons = maxHeartIcons;
        this.barFilled = barFilled;
        this.barEmpty = barEmpty;
        this.barLength = barLength;
        this.blacklistedEntities = blacklistedEntities;
        this.worlds = worlds;
        this.worldsAsWhitelist = worldsAsWhitelist;
        this.showPlayers = showPlayers;
        this.lookAt = lookAt;
    }

    public static PluginConfig load(final JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        mergeMissingKeys(plugin);
        plugin.reloadConfig();
        final FileConfiguration c = plugin.getConfig();

        final Style style = parseStyle(plugin, c.getString("style", "bar"));

        final String customHolo = str(c, "format.hologram",
                "<bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
        final String customDmg = str(c, "format.damage", "<#FF5252>-<amount></#FF5252>");
        final String customAb = str(c, "format.actionbar",
                "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
        final String customBb = str(c, "format.bossbar",
                "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
        final String customLookAb = str(c, "format.look-at-actionbar",
                "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
        final String customLookBb = str(c, "format.look-at-bossbar",
                "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");

        final String fmtHolo;
        final String fmtAb;
        final String fmtBb;
        final String fmtLookAb;
        final String fmtLookBb;
        final String fmtDmg = customDmg;

        switch (style) {
            case HEARTS -> {
                fmtHolo = str(c, "styles.hearts.hologram",
                        "<hearts> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = str(c, "styles.hearts.actionbar",
                        "<white><name></white> <hearts> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = str(c, "styles.hearts.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
                fmtLookAb = str(c, "styles.hearts.look-at-actionbar",
                        "<white><name></white> <hearts> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtLookBb = str(c, "styles.hearts.look-at-bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
            }
            case BAR -> {
                fmtHolo = str(c, "styles.bar.hologram",
                        "<bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = str(c, "styles.bar.actionbar",
                        "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = str(c, "styles.bar.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
                fmtLookAb = str(c, "styles.bar.look-at-actionbar",
                        "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtLookBb = str(c, "styles.bar.look-at-bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
            }
            case NUMERIC -> {
                fmtHolo = str(c, "styles.numeric.hologram",
                        "<red>❤</red> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = str(c, "styles.numeric.actionbar",
                        "<white><name></white> <red>❤</red> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = str(c, "styles.numeric.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
                fmtLookAb = str(c, "styles.numeric.look-at-actionbar",
                        "<white><name></white> <red>❤</red> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtLookBb = str(c, "styles.numeric.look-at-bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
            }
            default -> {
                fmtHolo = customHolo;
                fmtAb = customAb;
                fmtBb = customBb;
                fmtLookAb = customLookAb;
                fmtLookBb = customLookBb;
            }
        }

        final Set<EntityType> banned = new HashSet<>();
        for (final String raw : c.getStringList("blacklist.entities")) {
            try {
                banned.add(EntityType.valueOf(raw.trim().toUpperCase(Locale.ROOT)));
            } catch (final IllegalArgumentException ignored) {
                plugin.getLogger().warning("Unknown entity type in blacklist: " + raw);
            }
        }

        final Set<String> worlds = new HashSet<>();
        for (final String w : c.getStringList("blacklist.worlds")) {
            worlds.add(w.toLowerCase(Locale.ROOT));
        }

        final double[] thresholds = normalizeThresholds(
                plugin,
                c.getDouble("bossbar.high-percent", 50.0),
                c.getDouble("bossbar.mid-percent", 25.0)
        );

        return new PluginConfig(
                style,
                c.getBoolean("display.hologram", true),
                c.getBoolean("display.damage-numbers", true),
                c.getBoolean("display.actionbar", true),
                c.getBoolean("display.bossbar", true),
                c.getBoolean("hologram.only-when-damaged", true),
                Math.max(1, c.getInt("hologram.hide-after-ticks", 40)),
                Math.max(1.0, c.getDouble("hologram.view-distance", 16.0)),
                c.getDouble("hologram.y-offset", 0.35),
                Math.max(1, c.getInt("damage-numbers.duration-ticks", 28)),
                Math.max(0.0, c.getDouble("damage-numbers.rise-per-tick", 0.045)),
                Math.max(1.0, c.getDouble("damage-numbers.view-distance", 16.0)),
                (float) Math.max(0.1, c.getDouble("damage-numbers.base-scale", 1.15)),
                (float) Math.max(0.1, c.getDouble("damage-numbers.crit-scale", 1.5)),
                Math.max(0, c.getInt("damage-numbers.env-interval-ticks", 10)),
                loadDamageTiers(plugin, c),
                c.getBoolean("damage-numbers.crit.enabled", true),
                c.getString("damage-numbers.crit.symbol", "✦"),
                c.getString("damage-numbers.crit.format",
                        "<gradient:#FFE082:#FF6D00><bold><symbol> <amount></bold></gradient>"),
                Math.max(1, c.getInt("actionbar.duration-ticks", 40)),
                Math.max(1, c.getInt("bossbar.hide-after-ticks", 70)),
                Math.max(0.0, c.getDouble("bossbar.min-max-health", 0.0)),
                parseColor(c.getString("bossbar.color", "GREEN")),
                parseOverlay(c.getString("bossbar.overlay", "NOTCHED_10")),
                c.getBoolean("bossbar.dynamic-color", true),
                thresholds[0],
                thresholds[1],
                fmtHolo,
                fmtDmg,
                fmtAb,
                fmtBb,
                fmtLookAb,
                fmtLookBb,
                str(c, "styles.hearts.full", "❤"),
                str(c, "styles.hearts.empty", "❤"),
                Math.max(0.1, c.getDouble("styles.hearts.hp-per-heart", 2.0)),
                Math.max(1, c.getInt("styles.hearts.max-icons", 10)),
                str(c, "styles.bar.filled", "█"),
                str(c, "styles.bar.empty", "░"),
                Math.max(1, c.getInt("styles.bar.length", 12)),
                Set.copyOf(banned),
                Set.copyOf(worlds),
                c.getBoolean("blacklist.worlds-as-whitelist", false),
                c.getBoolean("players", false),
                LookAtSettings.load(c)
        );
    }

    private static double clampPercent(final double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }

    static double[] normalizeThresholds(final JavaPlugin plugin, final double highRaw, final double midRaw) {
        double high = clampPercent(highRaw);
        double mid = clampPercent(midRaw);
        if (mid > high) {
            if (plugin != null) {
                plugin.getLogger().warning(
                        "bossbar.mid-percent (" + midRaw + ") > high-percent (" + highRaw + "), swapping");
            }
            final double tmp = high;
            high = mid;
            mid = tmp;
        }
        return new double[] {high, mid};
    }

    private static String str(final FileConfiguration c, final String path, final String def) {
        final String value = c.getString(path, def);
        return value == null || value.isEmpty() ? def : value;
    }

    /**
     * Add keys introduced in newer plugin versions without overwriting admin values.
     */
    private static void mergeMissingKeys(final JavaPlugin plugin) {
        final InputStream stream = plugin.getResource("config.yml");
        if (stream == null) {
            return;
        }
        final YamlConfiguration defaults;
        try (stream; final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            defaults = YamlConfiguration.loadConfiguration(reader);
        } catch (final IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not read bundled config.yml", e);
            return;
        }
        final File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.isFile()) {
            return;
        }
        final YamlConfiguration disk = YamlConfiguration.loadConfiguration(file);
        boolean changed = false;
        for (final String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key)) {
                continue;
            }
            if (!disk.contains(key, true)) {
                disk.set(key, defaults.get(key));
                changed = true;
            }
        }
        if (changed) {
            try {
                disk.save(file);
                plugin.getLogger().info("Merged new config keys into config.yml");
            } catch (final IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not update config.yml with new keys", e);
            }
        }
    }

    /**
     * Persist {@code language:} without rewriting the rest of config.yml.
     */
    public static void writeLanguage(final JavaPlugin plugin, final String code) {
        final File file = new File(plugin.getDataFolder(), "config.yml");
        if (file.isFile()) {
            try {
                final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                boolean replaced = false;
                for (int i = 0; i < lines.size(); i++) {
                    final String line = lines.get(i);
                    final String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    if (trimmed.startsWith("language:")) {
                        final int idx = line.indexOf("language:");
                        lines.set(i, line.substring(0, idx) + "language: " + code);
                        replaced = true;
                        break;
                    }
                }
                if (replaced) {
                    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
                    plugin.reloadConfig();
                    return;
                }
            } catch (final IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not update language in config.yml", e);
            }
        }
        plugin.reloadConfig();
        plugin.getConfig().set("language", code);
        plugin.saveConfig();
    }

    private static List<DamageTier> loadDamageTiers(final JavaPlugin plugin, final FileConfiguration c) {
        final List<DamageTier> tiers = new ArrayList<>();
        final List<java.util.Map<?, ?>> maps = c.getMapList("damage-numbers.tiers");
        if (!maps.isEmpty()) {
            for (final java.util.Map<?, ?> map : maps) {
                final Object maxObj = map.get("max");
                final Object fmtObj = map.get("format");
                if (maxObj == null || fmtObj == null) {
                    continue;
                }
                try {
                    final double max = maxObj instanceof Number n
                            ? n.doubleValue()
                            : Double.parseDouble(maxObj.toString());
                    tiers.add(new DamageTier(max, fmtObj.toString()));
                } catch (final NumberFormatException e) {
                    plugin.getLogger().warning("Invalid damage-numbers.tiers max: " + maxObj);
                }
            }
        } else {
            final ConfigurationSection section = c.getConfigurationSection("damage-numbers.tiers");
            if (section != null) {
                for (final String key : section.getKeys(false)) {
                    final ConfigurationSection tier = section.getConfigurationSection(key);
                    if (tier == null) {
                        continue;
                    }
                    final String format = tier.getString("format", "<red>-<amount></red>");
                    tiers.add(new DamageTier(tier.getDouble("max", 99999), format == null ? "<red>-<amount></red>" : format));
                }
            }
        }
        tiers.sort(Comparator.comparingDouble(DamageTier::max));
        return tiers;
    }

    private static Style parseStyle(final JavaPlugin plugin, final String raw) {
        if (raw == null || raw.isBlank()) {
            return Style.BAR;
        }
        try {
            return Style.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (final Exception e) {
            plugin.getLogger().warning("Unknown style '" + raw + "', using bar");
            return Style.BAR;
        }
    }

    private static BossBar.Color parseColor(final String raw) {
        try {
            return BossBar.Color.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (final Exception e) {
            return BossBar.Color.GREEN;
        }
    }

    private static BossBar.Overlay parseOverlay(final String raw) {
        try {
            return BossBar.Overlay.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (final Exception e) {
            return BossBar.Overlay.NOTCHED_10;
        }
    }

    public String damageFormat(final double amount, final boolean critical) {
        if (critical && this.critEnabled) {
            return this.critFormat;
        }
        for (final DamageTier tier : this.damageTiers) {
            if (amount <= tier.max()) {
                return tier.format();
            }
        }
        return this.formatDamage;
    }

    public float damageScale(final boolean critical) {
        return critical && this.critEnabled ? this.damageCritScale : this.damageBaseScale;
    }

    public BossBar.Color healthBossColor(final double health, final double max) {
        if (!this.bossbarDynamicColor) {
            return this.bossbarColor;
        }
        final double pct = (Math.max(0.0, health) / Math.max(0.1, max)) * 100.0;
        if (pct > this.highPercent) {
            return BossBar.Color.GREEN;
        }
        if (pct > this.midPercent) {
            return BossBar.Color.YELLOW;
        }
        return BossBar.Color.RED;
    }

    public String healthColorTag(final double health, final double max) {
        final double pct = (Math.max(0.0, health) / Math.max(0.1, max)) * 100.0;
        if (pct > this.highPercent) {
            return "<#4CAF50>";
        }
        if (pct > this.midPercent) {
            return "<#FFEB3B>";
        }
        return "<#F44336>";
    }

    public Style style() {
        return style;
    }

    public boolean hologram() {
        return hologram;
    }

    public boolean damageNumbers() {
        return damageNumbers;
    }

    public boolean actionbar() {
        return actionbar;
    }

    public boolean bossbar() {
        return bossbar;
    }

    public boolean hologramOnlyWhenDamaged() {
        return hologramOnlyWhenDamaged;
    }

    public int hologramHideTicks() {
        return hologramHideTicks;
    }

    public double hologramViewDistance() {
        return hologramViewDistance;
    }

    public double hologramYOffset() {
        return hologramYOffset;
    }

    public int damageDurationTicks() {
        return damageDurationTicks;
    }

    public double damageRisePerTick() {
        return damageRisePerTick;
    }

    public double damageViewDistance() {
        return damageViewDistance;
    }

    public int damageEnvIntervalTicks() {
        return damageEnvIntervalTicks;
    }

    public String critSymbol() {
        return critSymbol;
    }

    public boolean critEnabled() {
        return critEnabled;
    }

    public int actionbarDurationTicks() {
        return actionbarDurationTicks;
    }

    public int bossbarHideTicks() {
        return bossbarHideTicks;
    }

    public double bossbarMinMaxHealth() {
        return bossbarMinMaxHealth;
    }

    public BossBar.Color bossbarColor() {
        return bossbarColor;
    }

    public BossBar.Overlay bossbarOverlay() {
        return bossbarOverlay;
    }

    public String formatHologram() {
        return formatHologram;
    }

    public String formatDamage() {
        return formatDamage;
    }

    public String formatActionbar() {
        return formatActionbar;
    }

    public String formatBossbar() {
        return formatBossbar;
    }

    public String formatLookAtActionbar() {
        return formatLookAtActionbar;
    }

    public String formatLookAtBossbar() {
        return formatLookAtBossbar;
    }

    public String heartFull() {
        return heartFull;
    }

    public String heartEmpty() {
        return heartEmpty;
    }

    public double hpPerHeart() {
        return hpPerHeart;
    }

    public int maxHeartIcons() {
        return maxHeartIcons;
    }

    public String barFilled() {
        return barFilled;
    }

    public String barEmpty() {
        return barEmpty;
    }

    public int barLength() {
        return barLength;
    }

    public boolean showPlayers() {
        return showPlayers;
    }

    public LookAtSettings lookAt() {
        return lookAt;
    }

    public boolean isEntityAllowed(final EntityType type) {
        return !this.blacklistedEntities.contains(type);
    }

    public boolean isWorldAllowed(final String worldName) {
        final String key = worldName.toLowerCase(Locale.ROOT);
        if (this.worlds.isEmpty()) {
            return true;
        }
        final boolean listed = this.worlds.contains(key);
        return this.worldsAsWhitelist == listed;
    }

    public boolean anyChannelEnabled() {
        return hologram || damageNumbers || actionbar || bossbar;
    }
}
