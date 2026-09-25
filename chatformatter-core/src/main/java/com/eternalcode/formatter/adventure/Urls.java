package com.eternalcode.formatter.adventure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Urls {

    public static final Pattern URL_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_\\+.~#?&\\/\\/=]*)");

    // Private use characters, so no formatting syntax can match them or the index between them.
    private static final char MASK_START = '';
    private static final char MASK_END = '';
    private static final Pattern MASK_PATTERN = Pattern.compile(MASK_START + "(\\d+)" + MASK_END);

    private Urls() {
    }

    /**
     * Applies the transformation to the input while keeping every URL in it unchanged,
     * so formatting syntax such as {@code &b} or {@code __} inside a URL is not translated.
     */
    public static String preserving(String input, UnaryOperator<String> transformation) {
        List<String> urls = new ArrayList<>();

        String masked = URL_PATTERN.matcher(input).replaceAll(match -> {
            urls.add(match.group());
            return MASK_START + String.valueOf(urls.size() - 1) + MASK_END;
        });

        String transformed = transformation.apply(masked);

        return MASK_PATTERN.matcher(transformed).replaceAll(match -> {
            int index = Integer.parseInt(match.group(1));
            return Matcher.quoteReplacement(index < urls.size() ? urls.get(index) : match.group());
        });
    }

}
