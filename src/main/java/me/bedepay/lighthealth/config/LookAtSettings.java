package me.bedepay.lighthealth.config;

import org.bukkit.configuration.file.FileConfiguration;

public record LookAtSettings(
        boolean enabled,
        int range,
        int intervalTicks,
        boolean hologram,
        boolean actionbar,
        boolean bossbar
) {

    public static LookAtSettings load(final FileConfiguration c) {
        return new LookAtSettings(
                c.getBoolean("look-at.enabled", true),
                Math.max(1, c.getInt("look-at.range", 12)),
                Math.max(1, c.getInt("look-at.interval-ticks", 4)),
                c.getBoolean("look-at.show.hologram", true),
                c.getBoolean("look-at.show.actionbar", true),
                c.getBoolean("look-at.show.bossbar", false)
        );
    }

    public boolean anyChannel() {
        return hologram || actionbar || bossbar;
    }
}
