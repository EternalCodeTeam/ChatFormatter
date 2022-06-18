package com.eternalcode.formatter.legacy;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Legacy {

    public static final char AMPERSAND = '&';
    public static final char COLOR_CHAR = '\u00A7';

    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile( "(?i)" + COLOR_CHAR + "[0-9A-FK-ORX]" );
    public static final Pattern STRIP_AMPERSAND_COLOR = Pattern.compile( "(?i)" + AMPERSAND + "[0-9A-FK-ORX]" );
    public static final Pattern SHADOW_COLOR_PATTERN = Pattern.compile( "(?i)" + AMPERSAND + "+[0-9A-FK-ORX]" );


    private Legacy() {
    }

    public final static LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public static String deColor(String text) {
        Matcher matcher = STRIP_COLOR_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        while (matcher.find()) {
            String color = matcher.group(0);

            builder.replace(matcher.start(), matcher.end(), String.valueOf( AMPERSAND ) + color.charAt(1));
        }

        return builder.toString();
    }

    public static String shadow(String text) {
        Matcher matcher = STRIP_AMPERSAND_COLOR.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        int matched = 0;
        while (matcher.find()) {
            String color = matcher.group(0);

            builder.replace(matcher.start() + matched, matcher.end() + matched, String.valueOf( AMPERSAND ) + AMPERSAND + color.charAt(1));
            matched++;
        }

        return builder.toString();
    }

    public static String colorShadow(String text) {
        Matcher matcher = SHADOW_COLOR_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        int removed = 0;
        while (matcher.find()) {
            String color = matcher.group(0);

            if (color.length() != 2) {
                builder.replace(matcher.start() - removed, matcher.end() - removed, color.substring(1));
                removed++;
                continue;
            }

            builder.replace(matcher.start() - removed, matcher.end() - removed, String.valueOf( COLOR_CHAR ) + color.charAt(1));
        }

        return builder.toString();
    }

}
