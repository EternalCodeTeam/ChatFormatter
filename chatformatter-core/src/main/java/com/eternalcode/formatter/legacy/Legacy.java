package com.eternalcode.formatter.legacy;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;
import java.util.regex.Pattern;

public final class Legacy {

    private static final Pattern COLOR_LEGACY_PATTERN = Pattern.compile("(?i)&([0-9A-FK-ORX#])");
    private static final Pattern HEX_LEGACY_PATTERN = Pattern.compile("(?i)&#([0-9A-F]{6})");
    private static final Pattern HEX_LEGACY_VANILLA_PATTERN = Pattern.compile("(?i)&x(&[0-9A-F]){6}");

    private static final Map<Character, String> codeTranslations = new ImmutableMap.Builder<Character, String>()
        .put('0', "<black>")
        .put('1', "<dark_blue>")
        .put('2', "<dark_green>")
        .put('3', "<dark_aqua>")
        .put('4', "<dark_red>")
        .put('5', "<dark_purple>")
        .put('6', "<gold>")
        .put('7', "<gray>")
        .put('8', "<dark_gray>")
        .put('9', "<blue>")
        .put('a', "<green>")
        .put('b', "<aqua>")
        .put('c', "<red>")
        .put('d', "<light_purple>")
        .put('e', "<yellow>")
        .put('f', "<white>")
        .put('k', "<obfuscated>")
        .put('l', "<bold>")
        .put('m', "<strikethrough>")
        .put('n', "<underlined>")
        .put('o', "<italic>")
        .put('r', "<reset>")
        .build();

    private static final Map<Character, String> legacyCodeToPermission = new ImmutableMap.Builder<Character, String>()
        .put('0', "chatformatter.color.black")
        .put('1', "chatformatter.color.dark_blue")
        .put('2', "chatformatter.color.dark_green")
        .put('3', "chatformatter.color.dark_aqua")
        .put('4', "chatformatter.color.dark_red")
        .put('5', "chatformatter.color.dark_purple")
        .put('6', "chatformatter.color.gold")
        .put('7', "chatformatter.color.gray")
        .put('8', "chatformatter.color.dark_gray")
        .put('9', "chatformatter.color.blue")
        .put('a', "chatformatter.color.green")
        .put('b', "chatformatter.color.aqua")
        .put('c', "chatformatter.color.red")
        .put('d', "chatformatter.color.light_purple")
        .put('e', "chatformatter.color.yellow")
        .put('f', "chatformatter.color.white")
        .put('k', "chatformatter.decorations.obfuscated")
        .put('l', "chatformatter.decorations.bold")
        .put('m', "chatformatter.decorations.strikethrough")
        .put('n', "chatformatter.decorations.underlined")
        .put('o', "chatformatter.decorations.italic")
        .put('r', "chatformatter.reset")
        .build();

    private Legacy() {
    }

    public static String clearSection(String text) {
        return text.replace('§', '&');
    }

    public static String legacyToAdventure(String input) {
        return legacyToAdventure(input, permission -> true);
    }

    public static String legacyToAdventure(String input, Predicate<String> hasPermission) {
        String result = HEX_LEGACY_VANILLA_PATTERN.matcher(input).replaceAll(matchResult -> {
            String hexColor = matchResult.group().replace("&x", "").replace("&", "");
            return "<#" + hexColor + ">";
        });

        result = HEX_LEGACY_PATTERN.matcher(result).replaceAll(matchResult -> {
            String hex = matchResult.group(1);
            return "<#" + hex + ">";
        });

        result = COLOR_LEGACY_PATTERN.matcher(result).replaceAll(matchResult -> {
            char color = matchResult.group(1).toLowerCase().charAt(0);
            String adventure = codeTranslations.get(color);
            if (adventure != null && hasPermissionForLegacyCode(hasPermission, color)) {
                return adventure;
            }
            return "&" + color;
        });

        return result;
    }

    private static boolean hasPermissionForLegacyCode(Predicate<String> hasPermission, char code) {
        if (hasWildcardPermission(hasPermission)) {
            return true;
        }
        String permission = legacyCodeToPermission.get(code);
        return permission != null && hasPermission.test(permission);
    }

    private static boolean hasWildcardPermission(Predicate<String> hasPermission) {
        return hasPermission.test("chatformatter.*")
            || hasPermission.test("chatformatter.color.*")
            || hasPermission.test("chatformatter.decorations.*");
    }
}
