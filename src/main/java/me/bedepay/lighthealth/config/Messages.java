package me.bedepay.lighthealth.config;

import me.bedepay.lighthealth.util.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Messages {

    public static final Set<String> SUPPORTED = Set.of("en", "ru", "es", "zh");

    private final JavaPlugin plugin;
    private FileConfiguration primary;
    private FileConfiguration fallback;
    private String prefix;
    private String activeLanguage = "en";

    public Messages(final JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        extractLocales();
        final String requested = plugin.getConfig().getString("language", "en");
        this.activeLanguage = normalize(requested);
        this.fallback = loadLocaleFile("en");
        this.primary = this.activeLanguage.equals("en")
                ? this.fallback
                : loadLocaleFile(this.activeLanguage);
        this.prefix = raw("prefix");
        if (this.prefix.startsWith("<red>missing:")) {
            this.prefix = "<gradient:#FF6B6B:#FFA07A><bold>LightHealth</bold></gradient> <dark_gray>»</dark_gray> ";
        }
    }

    public String language() {
        return activeLanguage;
    }

    public static String normalize(final String raw) {
        if (raw == null || raw.isBlank()) {
            return "en";
        }
        final String code = raw.trim().toLowerCase(Locale.ROOT);
        if (SUPPORTED.contains(code)) {
            return code;
        }
        if (code.startsWith("zh") || code.equals("cn") || code.equals("chinese")) {
            return "zh";
        }
        if (code.startsWith("ru") || code.equals("russian")) {
            return "ru";
        }
        if (code.startsWith("es") || code.equals("spanish") || code.equals("español")) {
            return "es";
        }
        if (code.startsWith("en") || code.equals("english")) {
            return "en";
        }
        return "en";
    }

    public static boolean isSupported(final String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        final String code = raw.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED.contains(code)
                || code.startsWith("zh") || code.equals("cn")
                || code.startsWith("ru")
                || code.startsWith("es")
                || code.startsWith("en");
    }

    private void extractLocales() {
        final File dir = new File(this.plugin.getDataFolder(), "lang");
        if (!dir.exists() && !dir.mkdirs()) {
            this.plugin.getLogger().warning("Could not create lang folder");
        }
        for (final String code : SUPPORTED) {
            final String path = "lang/" + code + ".yml";
            final File out = new File(this.plugin.getDataFolder(), path);
            if (!out.exists()) {
                this.plugin.saveResource(path, false);
            }
        }
    }

    private FileConfiguration loadLocaleFile(final String code) {
        final File file = new File(this.plugin.getDataFolder(), "lang/" + code + ".yml");
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        }
        final InputStream stream = this.plugin.getResource("lang/" + code + ".yml");
        if (stream != null) {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }
        return new YamlConfiguration();
    }

    public String raw(final String key) {
        if (this.primary != null && this.primary.contains(key)) {
            final String value = this.primary.getString(key);
            if (value != null) {
                return value;
            }
        }
        if (this.fallback != null && this.fallback.contains(key)) {
            final String value = this.fallback.getString(key);
            if (value != null) {
                return value;
            }
        }
        return "<red>missing: " + key + "</red>";
    }

    public Component get(final String key, final TagResolver... resolvers) {
        return Text.mm(raw(key), resolvers);
    }

    public Component prefixed(final String key, final TagResolver... resolvers) {
        return Text.mm(this.prefix + raw(key), resolvers);
    }

    public void send(final Audience audience, final String key, final TagResolver... resolvers) {
        audience.sendMessage(prefixed(key, resolvers));
    }

    public void send(final Audience audience, final String key, final String langPlaceholder) {
        audience.sendMessage(prefixed(key, Placeholder.unparsed("lang", langPlaceholder)));
    }

    public void sendList(final Audience audience, final String key, final TagResolver... resolvers) {
        final List<String> lines = list(key);
        for (final String line : lines) {
            audience.sendMessage(Text.mm(line, resolvers));
        }
    }

    private List<String> list(final String key) {
        if (this.primary != null && this.primary.isList(key)) {
            final List<String> lines = this.primary.getStringList(key);
            if (!lines.isEmpty()) {
                return lines;
            }
        }
        if (this.fallback != null && this.fallback.isList(key)) {
            return this.fallback.getStringList(key);
        }
        return List.of();
    }
}
