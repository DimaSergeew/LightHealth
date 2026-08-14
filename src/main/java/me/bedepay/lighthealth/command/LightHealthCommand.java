package me.bedepay.lighthealth.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.bedepay.lighthealth.LightHealth;
import me.bedepay.lighthealth.config.Messages;
import me.bedepay.lighthealth.config.PluginConfig;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class LightHealthCommand {

    private final LightHealth plugin;

    public LightHealthCommand(final LightHealth plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("lighthealth")
                .executes(ctx -> help(ctx.getSource().getSender()))
                .then(Commands.literal("help")
                        .executes(ctx -> help(ctx.getSource().getSender())))
                .then(Commands.literal("toggle")
                        .executes(ctx -> toggle(ctx.getSource().getSender())))
                .then(Commands.literal("status")
                        .executes(ctx -> status(ctx.getSource().getSender())))
                .then(Commands.literal("reload")
                        .executes(ctx -> reload(ctx.getSource().getSender())))
                .then(Commands.literal("lang")
                        .executes(ctx -> langInfo(ctx.getSource().getSender()))
                        .then(Commands.argument("code", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    final String rem = builder.getRemaining().toLowerCase(Locale.ROOT);
                                    for (final String code : Messages.SUPPORTED) {
                                        if (code.startsWith(rem)) {
                                            builder.suggest(code);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> langSet(
                                        ctx.getSource().getSender(),
                                        StringArgumentType.getString(ctx, "code")))))
                .then(Commands.literal("language")
                        .executes(ctx -> langInfo(ctx.getSource().getSender()))
                        .then(Commands.argument("code", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    final String rem = builder.getRemaining().toLowerCase(Locale.ROOT);
                                    for (final String code : Messages.SUPPORTED) {
                                        if (code.startsWith(rem)) {
                                            builder.suggest(code);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> langSet(
                                        ctx.getSource().getSender(),
                                        StringArgumentType.getString(ctx, "code")))))
                .build();
    }

    private int help(final CommandSender sender) {
        plugin.messages().sendList(sender, "usage");
        return Command.SINGLE_SUCCESS;
    }

    private int status(final CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "players-only");
            return 0;
        }
        final PluginConfig config = plugin.config();
        final boolean enabled = plugin.prefs().isEnabled(player.getUniqueId());
        final boolean lookAtEnabled = config.lookAt().enabled() && config.lookAt().anyChannel();
        plugin.messages().sendList(
                player,
                "status",
                Placeholder.parsed("state", plugin.messages().raw(enabled ? "status-on" : "status-off")),
                Placeholder.unparsed("channels", displayChannels(config)),
                Placeholder.parsed(
                        "look_at_state",
                        plugin.messages().raw(lookAtEnabled ? "status-on" : "status-off")
                ),
                Placeholder.unparsed("look_at_channels", lookAtChannels(config)),
                Placeholder.unparsed("style", config.style().name().toLowerCase(Locale.ROOT))
        );
        return Command.SINGLE_SUCCESS;
    }

    private String displayChannels(final PluginConfig config) {
        final List<String> channels = new ArrayList<>();
        addIfEnabled(channels, config.hologram(), "hologram");
        addIfEnabled(channels, config.damageNumbers(), "numbers");
        addIfEnabled(channels, config.actionbar(), "actionbar");
        addIfEnabled(channels, config.bossbar(), "bossbar");
        return channelList(channels);
    }

    private String lookAtChannels(final PluginConfig config) {
        if (!config.lookAt().enabled()) {
            return plugin.messages().raw("status-none");
        }
        final List<String> channels = new ArrayList<>();
        addIfEnabled(channels, config.lookAt().hologram(), "hologram");
        addIfEnabled(channels, config.lookAt().actionbar(), "actionbar");
        addIfEnabled(channels, config.lookAt().bossbar(), "bossbar");
        return channelList(channels);
    }

    private String channelList(final List<String> channels) {
        return channels.isEmpty() ? plugin.messages().raw("status-none") : String.join(", ", channels);
    }

    private static void addIfEnabled(final List<String> channels, final boolean enabled, final String name) {
        if (enabled) {
            channels.add(name);
        }
    }

    private int toggle(final CommandSender sender) {
        if (!sender.hasPermission("lighthealth.toggle")) {
            plugin.messages().send(sender, "no-permission");
            return 0;
        }
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "players-only");
            return 0;
        }
        final boolean enabled = plugin.prefs().toggle(player.getUniqueId());
        plugin.messages().send(player, enabled ? "toggled-on" : "toggled-off");
        if (!enabled) {
            plugin.displays().clearPersonal(player.getUniqueId());
            plugin.lookAt().clearPlayer(player.getUniqueId());
        }
        return Command.SINGLE_SUCCESS;
    }

    private int reload(final CommandSender sender) {
        if (!sender.hasPermission("lighthealth.admin")) {
            plugin.messages().send(sender, "no-permission");
            return 0;
        }
        plugin.reloadAll();
        plugin.messages().send(sender, "reloaded");
        return Command.SINGLE_SUCCESS;
    }

    private int langInfo(final CommandSender sender) {
        if (!sender.hasPermission("lighthealth.admin")) {
            plugin.messages().send(sender, "no-permission");
            return 0;
        }
        plugin.messages().send(
                sender,
                "language-current",
                Placeholder.unparsed("lang", plugin.messages().language())
        );
        return Command.SINGLE_SUCCESS;
    }

    private int langSet(final CommandSender sender, final String raw) {
        if (!sender.hasPermission("lighthealth.admin")) {
            plugin.messages().send(sender, "no-permission");
            return 0;
        }
        if (!Messages.isSupported(raw)) {
            plugin.messages().send(sender, "unknown-language");
            return 0;
        }
        final String code = Messages.normalize(raw);
        PluginConfig.writeLanguage(plugin, code);
        plugin.messages().reload();
        plugin.messages().send(sender, "language-set", Placeholder.unparsed("lang", code));
        return Command.SINGLE_SUCCESS;
    }
}
