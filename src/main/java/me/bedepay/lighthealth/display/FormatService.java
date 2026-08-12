package me.bedepay.lighthealth.display;

import me.bedepay.lighthealth.config.PluginConfig;
import me.bedepay.lighthealth.util.Text;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;

public final class FormatService {

    private final PluginConfig config;

    public FormatService(final PluginConfig config) {
        this.config = config;
    }

    public Component hologram(final LivingEntity entity, final double health, final double max, final double amount) {
        return render(config.formatHologram(), entity, health, max, amount, false);
    }

    public Component damage(
            final LivingEntity entity,
            final double health,
            final double max,
            final double amount,
            final boolean critical
    ) {
        return render(config.damageFormat(amount, critical), entity, health, max, amount, critical);
    }

    public Component actionbar(
            final LivingEntity entity,
            final double health,
            final double max,
            final double amount,
            final boolean critical
    ) {
        final String template = amount > 0.0 ? config.formatActionbar() : config.formatLookAtActionbar();
        return render(template, entity, health, max, amount, critical);
    }

    public Component bossbar(
            final LivingEntity entity,
            final double health,
            final double max,
            final double amount,
            final boolean critical
    ) {
        final String template = amount > 0.0 ? config.formatBossbar() : config.formatLookAtBossbar();
        return render(template, entity, health, max, amount, critical);
    }

    public BossBar.Color bossBarColor(final double health, final double max) {
        return config.healthBossColor(health, max);
    }

    public static double maxHealth(final LivingEntity entity) {
        final AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            return Math.max(0.1, attr.getValue());
        }
        return Math.max(0.1, entity.getHealth());
    }

    private Component render(
            final String template,
            final LivingEntity entity,
            final double health,
            final double max,
            final double amount,
            final boolean critical
    ) {
        final double clampedHealth = Math.max(0.0, health);
        final double clampedMax = Math.max(0.1, max);
        final int percent = (int) Math.round((clampedHealth / clampedMax) * 100.0);

        final String color = config.healthColorTag(clampedHealth, clampedMax);
        final String hearts = buildHearts(clampedHealth, clampedMax, color);
        final String bar = buildBar(clampedHealth, clampedMax, color);
        final String symbol = critical ? config.critSymbol() : "";
        final String amountText = amount > 0.0 ? formatNum(amount) : "";

        final TagResolver resolvers = TagResolver.resolver(
                Placeholder.unparsed("health", formatNum(clampedHealth)),
                Placeholder.unparsed("max", formatNum(clampedMax)),
                Placeholder.unparsed("percent", Integer.toString(percent)),
                Placeholder.unparsed("amount", amountText),
                Placeholder.unparsed("name", entityName(entity)),
                Placeholder.unparsed("symbol", symbol),
                Placeholder.parsed("hearts", hearts),
                Placeholder.parsed("bar", bar)
        );
        return Text.mm(template, resolvers);
    }

    private String buildHearts(final double health, final double max, final String colorTag) {
        final double per = config.hpPerHeart();
        final int total = Math.min(config.maxHeartIcons(), Math.max(1, (int) Math.ceil(max / per)));
        final int filled = Math.min(total, (int) Math.ceil(health / per));
        final String full = stripColor(config.heartFull());
        final String empty = stripColor(config.heartEmpty());
        final StringBuilder sb = new StringBuilder(total * 24);
        if (filled > 0) {
            sb.append(colorTag);
            for (int i = 0; i < filled; i++) {
                sb.append(full);
            }
            sb.append(closeTag(colorTag));
        }
        if (filled < total) {
            sb.append("<dark_gray>");
            for (int i = filled; i < total; i++) {
                sb.append(empty);
            }
            sb.append("</dark_gray>");
        }
        return sb.toString();
    }

    private String buildBar(final double health, final double max, final String colorTag) {
        final int len = config.barLength();
        final int filled = (int) Math.round((health / max) * len);
        final int clampedFilled = Math.max(0, Math.min(len, filled));
        final String fillChar = stripColor(config.barFilled());
        final String emptyChar = stripColor(config.barEmpty());
        final StringBuilder sb = new StringBuilder(len * 24 + 32);
        sb.append("<dark_gray>[</dark_gray>");
        if (clampedFilled > 0) {
            sb.append(colorTag);
            sb.append(fillChar.repeat(clampedFilled));
            sb.append(closeTag(colorTag));
        }
        if (clampedFilled < len) {
            sb.append("<dark_gray>");
            sb.append(emptyChar.repeat(len - clampedFilled));
            sb.append("</dark_gray>");
        }
        sb.append("<dark_gray>]</dark_gray>");
        return sb.toString();
    }

    private static String stripColor(final String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input
                .replaceAll("(?i)</?#[0-9a-f]{6}>", "")
                .replaceAll("(?i)</?(?:red|green|yellow|gold|white|gray|dark_gray|bold|italic)>", "");
    }

    private static String closeTag(final String openTag) {
        if (openTag.startsWith("<#") && openTag.endsWith(">")) {
            return "</" + openTag.substring(1);
        }
        if (openTag.startsWith("<") && openTag.endsWith(">")) {
            return "</" + openTag.substring(1);
        }
        return "";
    }

    private static String formatNum(final double value) {
        if (Math.abs(value - Math.rint(value)) < 0.05) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format(Locale.US, "%.1f", value);
    }

    private static String entityName(final LivingEntity entity) {
        if (entity.customName() != null) {
            return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(entity.customName());
        }
        final String key = entity.getType().getKey().getKey().replace('_', ' ');
        if (key.isEmpty()) {
            return entity.getType().name();
        }
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }
}
