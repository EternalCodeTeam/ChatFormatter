package com.eternalcode.formatter.legacy;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Legacy {


    public static final char AMPERSAND = '&';
    public static final char SECTION = '\u00A7';
    public static final String SHADOW = "<ampersand>";

    public static final Pattern ALL_PATTERN = Pattern.compile(".*");
    public static final Pattern AMPERSAND_PATTERN = Pattern.compile("(?i)" + AMPERSAND + "([0-9A-FK-ORX#])");
    public static final Pattern SHADOW_PATTERN = Pattern.compile("(?i)" + SHADOW + "[0-9A-FK-ORX#]");
    public static final Pattern HEX_PATTERN = Pattern.compile("(?i)" + AMPERSAND + "#([0-9A-F]{6})");
    public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?i)&x(&[0-9A-F]){6}");

    public static final Map<String, String> codeTranslations = new ImmutableMap.Builder<String, String>()
            .put("0", "<black>")
            .put("1", "<dark_blue>")
            .put("2", "<dark_green>")
            .put("3", "<dark_aqua>")
            .put("4", "<dark_red>")
            .put("5", "<dark_purple>")
            .put("6", "<gold>")
            .put("7", "<gray>")
            .put("8", "<dark_gray>")
            .put("9", "<blue>")
            .put("a", "<green>")
            .put("b", "<aqua>")
            .put("c", "<red>")
            .put("d", "<light_purple>")
            .put("e", "<yellow>")
            .put("f", "<white>")
            .put("k", "<obfuscated>")
            .put("l", "<bold>")
            .put("m", "<strikethrough>")
            .put("n", "<underlined>")
            .put("o", "<italic>")
            .put("r", "<reset>")
            .build();

    private Legacy() {
    }

    public static final LegacyComponentSerializer LEGACY_AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .character('&')
        .hexCharacter('#')
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public static String clearSection(String text) {
        return text.replace(SECTION, AMPERSAND);
    }

    public static String ampersandToPlaceholder(String text) {
        StringBuilder builder = new StringBuilder(text);
        Matcher colorMatcher = AMPERSAND_PATTERN.matcher(builder.toString());

        int matched = 0;
        while (colorMatcher.find()) {
            String color = colorMatcher.group(0);

            builder.replace(colorMatcher.start() + matched, colorMatcher.end() + matched, SHADOW + color.charAt(1));
            matched += SHADOW.length() - 1;
        }

        return builder.toString();
    }

    static Component placeholderToAmpersand(Component component) {
        return component.replaceText(shadowBuilder -> shadowBuilder
            .match(ALL_PATTERN)
            .replacement((matchResult, builder) -> Component.text(Legacy.placeholderToAmpersand(matchResult.group()))));
    }

    static String placeholderToAmpersand(String text) {
        Matcher matcher = SHADOW_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        int matched = 0;
        while (matcher.find()) {
            int length = (matcher.end() - matcher.start()) - 1;
            builder.replace(matcher.start() + matched, matcher.end() + matched - 1, String.valueOf(AMPERSAND));
            matched -= length;
            matched += 1;
        }

        return builder.toString();
    }

    public static String legacyToAdventure(String input) {
        String result = HEX_COLOR_PATTERN.matcher(input).replaceAll(matchResult -> {
            String hexColor = matchResult.group().replace("&x", "").replace("&", "");
            return "<#" + hexColor + ">";
        });

        result = HEX_PATTERN.matcher(result).replaceAll(matchResult -> {
            String hex = matchResult.group(1);
            return "<#" + hex + ">";
        });

        result = AMPERSAND_PATTERN.matcher(result).replaceAll(matchResult -> {
            String color = matchResult.group(1);
            String adventure = codeTranslations.get(color.toLowerCase());

            if (adventure == null) {
                return matchResult.group();
            }

            return adventure;
        });

        return result;
    }
}
