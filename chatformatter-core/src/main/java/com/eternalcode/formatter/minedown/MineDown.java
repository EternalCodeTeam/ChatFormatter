package com.eternalcode.formatter.minedown;

import com.eternalcode.formatter.adventure.Urls;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Translates MineDown syntax (https://github.com/Phoenix616/MineDown) into MiniMessage tags,
 * so it can be mixed freely with MiniMessage and legacy colors.
 * <p>
 * A MineDown element is translated only when every MiniMessage tag it needs is present in the given resolver,
 * so MineDown uses exactly the same permissions as MiniMessage. Anything else is left untouched as plain text.
 */
public final class MineDown {

    private static final List<SimpleFormat> SIMPLE_FORMATS = List.of(
        new SimpleFormat("**", "bold"),
        new SimpleFormat("##", "italic"),
        new SimpleFormat("__", "underlined"),
        new SimpleFormat("~~", "strikethrough"),
        new SimpleFormat("??", "obfuscated")
    );

    private static final List<String> DECORATIONS = List.of("bold", "italic", "underlined", "strikethrough", "obfuscated");

    private static final Pattern COLOR_PATTERN = Pattern.compile("&(#[0-9a-fA-F]{6}(?:-#[0-9a-fA-F]{6})*|[a-zA-Z_]{2,})&");
    private static final Pattern HEX_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[([^\\[\\]]+)]\\(([^()]+)\\)");
    private static final Pattern PROPERTY_START_PATTERN = Pattern.compile("(?:^|\\s)(\\w+)=");
    // Private use characters, so no formatting syntax can match them or the index between them.
    private static final char LINK_TAG_START = '\uE003';
    private static final char LINK_TAG_END = '\uE004';
    private static final Pattern LINK_TAG_PATTERN = Pattern.compile(LINK_TAG_START + "(\\d+)" + LINK_TAG_END);
    private static final Pattern KEY_PATTERN = Pattern.compile("[a-z0-9_.-]+(?::[a-z0-9_./-]+)?");

    private MineDown() {
    }

    public static String mineDownToAdventure(String input, TagResolver permittedTags) {
        List<String> linkTags = new ArrayList<>();

        String result = Urls.preserving(input, text -> translateColors(text, permittedTags));
        result = translateLinks(result, permittedTags, linkTags);
        result = Urls.preserving(result, text -> translateSimpleFormats(text, permittedTags));

        return restoreLinkTags(result, linkTags);
    }

    private static String translateSimpleFormats(String input, TagResolver permittedTags) {
        String result = input;

        for (SimpleFormat format : SIMPLE_FORMATS) {
            result = format.pattern().matcher(result).replaceAll(match -> {
                if (!permittedTags.has(format.tag())) {
                    return Matcher.quoteReplacement(match.group());
                }

                return Matcher.quoteReplacement("<" + format.tag() + ">" + match.group(1) + "</" + format.tag() + ">");
            });
        }

        return result;
    }

    private static String translateColors(String input, TagResolver permittedTags) {
        return COLOR_PATTERN.matcher(input).replaceAll(match -> {
            String color = match.group(1).toLowerCase(Locale.ROOT);

            Optional<String> tag = color.contains("-")
                ? gradientTag(color, permittedTags)
                : colorTag(color, permittedTags);

            return Matcher.quoteReplacement(tag.orElse(match.group()));
        });
    }

    private static Optional<String> gradientTag(String colors, TagResolver permittedTags) {
        if (!permittedTags.has("gradient")) {
            return Optional.empty();
        }

        return Optional.of("<gradient:" + colors.replace('-', ':') + ">");
    }

    private static Optional<String> colorTag(String color, TagResolver permittedTags) {
        if (color.equals("rainbow")) {
            return permittedTags.has("rainbow") ? Optional.of("<rainbow>") : Optional.empty();
        }

        boolean isColor = HEX_PATTERN.matcher(color).matches() || NamedTextColor.NAMES.value(color) != null;

        if (!isColor || !permittedTags.has(color)) {
            return Optional.empty();
        }

        return Optional.of("<" + color + ">");
    }

    /**
     * Opening tags are hidden behind {@link #LINK_TAG_PATTERN} markers, so their quoted arguments
     * (hover text, URLs, commands) are not changed by the simple formats translated afterwards.
     */
    private static String translateLinks(String input, TagResolver permittedTags, List<String> linkTags) {
        return LINK_PATTERN.matcher(input).replaceAll(match -> {
            Optional<List<String>> tags = parseLinkDefinition(match.group(2).trim(), permittedTags);

            if (tags.isEmpty() || tags.get().isEmpty()) {
                return Matcher.quoteReplacement(match.group());
            }

            StringBuilder builder = new StringBuilder();

            for (String tag : tags.get()) {
                linkTags.add("<" + tag + ">");
                builder.append(LINK_TAG_START).append(linkTags.size() - 1).append(LINK_TAG_END);
            }

            builder.append(match.group(1));

            for (int i = tags.get().size() - 1; i >= 0; i--) {
                builder.append("</").append(tagName(tags.get().get(i))).append('>');
            }

            return Matcher.quoteReplacement(builder.toString());
        });
    }

    private static String restoreLinkTags(String input, List<String> linkTags) {
        return LINK_TAG_PATTERN.matcher(input).replaceAll(match -> {
            int index = Integer.parseInt(match.group(1));
            return Matcher.quoteReplacement(index < linkTags.size() ? linkTags.get(index) : match.group());
        });
    }

    /**
     * Parses the part in parentheses of {@code [text](definition)}.
     * Returns empty when any part of the definition is unknown or not permitted, so the link stays as plain text.
     */
    private static Optional<List<String>> parseLinkDefinition(String definition, TagResolver permittedTags) {
        List<String> tags = new ArrayList<>();

        List<MatchResult> properties = PROPERTY_START_PATTERN.matcher(definition).results().toList();
        int propertiesStart = properties.isEmpty() ? definition.length() : properties.get(0).start();

        for (String shorthand : definition.substring(0, propertiesStart).trim().split("\\s+")) {
            if (shorthand.isEmpty()) {
                continue;
            }

            Optional<String> tag = shorthandTag(shorthand, permittedTags);

            if (tag.isEmpty()) {
                return Optional.empty();
            }

            tags.add(tag.get());
        }

        for (int i = 0; i < properties.size(); i++) {
            String key = properties.get(i).group(1).toLowerCase(Locale.ROOT);
            int valueEnd = i + 1 < properties.size() ? properties.get(i + 1).start() : definition.length();
            String value = definition.substring(properties.get(i).end(), valueEnd).trim();

            Optional<List<String>> propertyTags = propertyTags(key, value, permittedTags);

            if (propertyTags.isEmpty()) {
                return Optional.empty();
            }

            tags.addAll(propertyTags.get());
        }

        return Optional.of(tags);
    }

    private static Optional<String> shorthandTag(String shorthand, TagResolver permittedTags) {
        if (Urls.URL_PATTERN.matcher(shorthand).matches()) {
            return clickTag("open_url", shorthand, permittedTags);
        }

        if (shorthand.startsWith("/")) {
            return clickTag("run_command", shorthand, permittedTags);
        }

        String lowerCase = shorthand.toLowerCase(Locale.ROOT);

        if (DECORATIONS.contains(lowerCase)) {
            return permittedTags.has(lowerCase) ? Optional.of(lowerCase) : Optional.empty();
        }

        return colorTag(lowerCase, permittedTags).map(MineDown::stripBrackets);
    }

    private static Optional<List<String>> propertyTags(String key, String value, TagResolver permittedTags) {
        if (value.isEmpty()) {
            return Optional.empty();
        }

        switch (key) {
            case "color":
            case "colour":
                return colorTag(value.toLowerCase(Locale.ROOT), permittedTags).map(tag -> List.of(stripBrackets(tag)));
            case "format":
                List<String> decorations = new ArrayList<>();

                for (String decoration : value.toLowerCase(Locale.ROOT).split("\\s*,\\s*")) {
                    if (!DECORATIONS.contains(decoration) || !permittedTags.has(decoration)) {
                        return Optional.empty();
                    }

                    decorations.add(decoration);
                }

                return Optional.of(decorations);
            case "font":
                String font = value.toLowerCase(Locale.ROOT);

                if (!KEY_PATTERN.matcher(font).matches() || !permittedTags.has("font")) {
                    return Optional.empty();
                }

                return Optional.of(List.of("font:" + font));
            case "hover":
                return permittedTags.has("hover")
                    ? Optional.of(List.of("hover:show_text:" + quote(value)))
                    : Optional.empty();
            case "insert":
                return permittedTags.has("insert")
                    ? Optional.of(List.of("insert:" + quote(value)))
                    : Optional.empty();
            case "open_url":
            case "run_command":
            case "suggest_command":
            case "copy_to_clipboard":
                return clickTag(key, value, permittedTags).map(List::of);
            default:
                return Optional.empty();
        }
    }

    private static Optional<String> clickTag(String action, String value, TagResolver permittedTags) {
        if (!permittedTags.has("click")) {
            return Optional.empty();
        }

        return Optional.of("click:" + action + ":" + quote(value));
    }

    private static String quote(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    private static String stripBrackets(String tag) {
        return tag.substring(1, tag.length() - 1);
    }

    private static String tagName(String tag) {
        int separator = tag.indexOf(':');
        return separator == -1 ? tag : tag.substring(0, separator);
    }

    private record SimpleFormat(String marker, String tag, Pattern pattern) {

        SimpleFormat(String marker, String tag) {
            this(marker, tag, Pattern.compile(Pattern.quote(marker) + "(.+?)" + Pattern.quote(marker)));
        }

    }

}
