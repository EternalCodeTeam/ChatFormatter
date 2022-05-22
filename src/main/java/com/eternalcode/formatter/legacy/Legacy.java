package com.eternalcode.formatter.legacy;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;

public final class Legacy {

    private static final String AMPERSAND = "&";

    private Legacy() {
    }

    public final static LegacyComponentSerializer LEGACY_AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
        .character('&')
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public static String decolor(String text) {
        Matcher matcher = ChatColor.STRIP_COLOR_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        while (matcher.find()) {
            String color = matcher.group(0);

            builder.replace(matcher.start(), matcher.end(), AMPERSAND + color.charAt(1));
        }

        return builder.toString();
    }

}
