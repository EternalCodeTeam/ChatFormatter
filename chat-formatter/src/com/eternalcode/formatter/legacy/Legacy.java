package com.eternalcode.formatter.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Legacy {


    public static final char AMPERSAND = '&';
    public static final char SECTION = '\u00A7';
    public static final String SHADOW = "<ampersand>";

    public static final Pattern ALL_PATTERN = Pattern.compile(".*");
    public static final Pattern AMPERSAND_PATTERN = Pattern.compile( "(?i)" + AMPERSAND + "[0-9A-FK-ORX#]" );
    public static final Pattern SHADOW_PATTERN = Pattern.compile( "(?i)" + SHADOW + "[0-9A-FK-ORX#]" );

    private Legacy() {
    }

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .character('&')
        .hexCharacter('#')
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public static String clearSection(String text) {
        return text.replace(SECTION, AMPERSAND);
    }

    public static String toBukkitFormat(String text) {
        return text
            .replace("<displayname>", "%1$s")
            .replace("<message>", "%2$s");
    }

    public static String toAdventureFormat(String text) {
        return text
            .replace("%1$s", "<displayname>")
            .replace("%2$s", "<message>");
    }

    public static String shadow(String text) {
        StringBuilder builder = new StringBuilder(text);
        Matcher colorMatcher = AMPERSAND_PATTERN.matcher(builder.toString());

        int matched = 0;
        while (colorMatcher.find()) {
            String color = colorMatcher.group(0);

            builder.replace(colorMatcher.start() + matched, colorMatcher.end() + matched, SHADOW + color.charAt(1));
            matched++;
        }

        return builder.toString();
    }

    static Component deshadow(Component component) {
        return component.replaceText(shadowBuilder -> shadowBuilder
            .match(ALL_PATTERN)
            .replacement((matchResult, builder) -> Component.text(Legacy.deshadow(matchResult.group()))));
    }

    static String deshadow(String text) {
        Matcher matcher = SHADOW_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text);

        int matched = 0;
        while (matcher.find()) {
            int length = (matcher.end() - matcher.start()) - 1;
            builder.replace(matcher.start() + matched, matcher.end() + matched - 1, String.valueOf( AMPERSAND ));
            matched += length;
        }

        return builder.toString();
    }

}
