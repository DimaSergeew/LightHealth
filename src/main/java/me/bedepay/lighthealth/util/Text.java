package me.bedepay.lighthealth.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class Text {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Text() {
    }

    public static MiniMessage mini() {
        return MM;
    }

    public static Component mm(final String input, final TagResolver... resolvers) {
        return MM.deserialize(input, resolvers);
    }
}
