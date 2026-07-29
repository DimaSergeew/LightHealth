package me.bedepay.lighthealth.config;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
        plugin.reloadConfig();
        final FileConfiguration c = plugin.getConfig();

        final Style style = parseStyle(c.getString("style", "bar"));

        final String customHolo = c.getString("format.hologram",
                "<bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
        final String customDmg = c.getString("format.damage", "<#FF5252>-<amount></#FF5252>");
        final String customAb = c.getString("format.actionbar",
                "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
        final String customBb = c.getString("format.bossbar",
                "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");

        final String fmtHolo;
        final String fmtAb;
        final String fmtBb;
        final String fmtDmg = customDmg;

        switch (style) {
            case HEARTS -> {
                fmtHolo = c.getString("styles.hearts.hologram",
                        "<hearts> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = c.getString("styles.hearts.actionbar",
                        "<white><name></white> <hearts> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = c.getString("styles.hearts.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
            }
            case BAR -> {
                fmtHolo = c.getString("styles.bar.hologram",
                        "<bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = c.getString("styles.bar.actionbar",
                        "<white><name></white> <bar> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = c.getString("styles.bar.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
            }
            case NUMERIC -> {
                fmtHolo = c.getString("styles.numeric.hologram",
                        "<red>❤</red> <white><health></white><dark_gray>/</dark_gray><gray><max></gray>");
                fmtAb = c.getString("styles.numeric.actionbar",
                        "<white><name></white> <red>❤</red> <white><health></white><dark_gray>/</dark_gray><gray><max></gray> <red>-<amount></red>");
                fmtBb = c.getString("styles.numeric.bossbar",
                        "<white><name></white>  <white><health></white><dark_gray>/</dark_gray><gray><max></gray>  <red>-<amount></red>");
            }
            default -> {
                fmtHolo = customHolo;
                fmtAb = customAb;
                fmtBb = customBb;
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
                loadDamageTiers(c),
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
                clampPercent(c.getDouble("bossbar.high-percent", 50.0)),
                clampPercent(c.getDouble("bossbar.mid-percent", 25.0)),
                fmtHolo,
                fmtDmg,
                fmtAb,
                fmtBb,
                c.getString("styles.hearts.full", "❤"),
                c.getString("styles.hearts.empty", "❤"),
                Math.max(0.1, c.getDouble("styles.hearts.hp-per-heart", 2.0)),
                Math.max(1, c.getInt("styles.hearts.max-icons", 10)),
                c.getString("styles.bar.filled", "█"),
                c.getString("styles.bar.empty", "░"),
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

    private static List<DamageTier> loadDamageTiers(final FileConfiguration c) {
        final List<DamageTier> tiers = new ArrayList<>();
        final List<java.util.Map<?, ?>> maps = c.getMapList("damage-numbers.tiers");
        if (!maps.isEmpty()) {
            for (final java.util.Map<?, ?> map : maps) {
                final Object maxObj = map.get("max");
                final Object fmtObj = map.get("format");
                if (maxObj == null || fmtObj == null) {
                    continue;
                }
                final double max = maxObj instanceof Number n ? n.doubleValue() : Double.parseDouble(maxObj.toString());
                tiers.add(new DamageTier(max, fmtObj.toString()));
            }
        } else {
            final ConfigurationSection section = c.getConfigurationSection("damage-numbers.tiers");
            if (section != null) {
                for (final String key : section.getKeys(false)) {
                    final ConfigurationSection tier = section.getConfigurationSection(key);
                    if (tier == null) {
                        continue;
                    }
                    tiers.add(new DamageTier(tier.getDouble("max", 99999), tier.getString("format", "<red>-<amount></red>")));
                }
            }
        }
        tiers.sort(Comparator.comparingDouble(DamageTier::max));
        return tiers;
    }

    private static Style parseStyle(final String raw) {
        try {
            return Style.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (final Exception e) {
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
