package com.eternalcode.formatter.adventure;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class TextColorTagResolver implements TagResolver, SerializableResolver.Single {

    private static final String COLOR_3 = "c";
    private static final String COLOR_2 = "colour";
    private static final String COLOR = "color";

    private static final StyleClaim<TextColor> STYLE = StyleClaim.claim(COLOR, Style::color, (color, emitter) -> {
        if (color instanceof NamedTextColor) {
            emitter.tag(NamedTextColor.NAMES.key((NamedTextColor) color));
        }
    });

    private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();

    static {
        COLOR_ALIASES.put("dark_grey", NamedTextColor.DARK_GRAY);
        COLOR_ALIASES.put("grey", NamedTextColor.GRAY);
    }

    private static boolean isColorOrAbbreviation(final String name) {
        return name.equals(COLOR) || name.equals(COLOR_2) || name.equals(COLOR_3);
    }

    private final Set<TextColor> allowedColors = new HashSet<>();

    private TextColorTagResolver(Collection<NamedTextColor> allowedColors) {
        this.allowedColors.addAll(allowedColors);
    }

    @Override
    public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue args, final @NotNull Context ctx) throws ParsingException {
        if (!this.has(name)) {
            return null;
        }

        String colorName;
        if (isColorOrAbbreviation(name)) {
            colorName = args.popOr("Expected to find a color parameter: <name>|#RRGGBB").lowerValue();
        } else {
            colorName = name;
        }

        TextColor color = resolveColor(colorName, ctx);

        if (!allowedColors.contains(color)) {
            throw ctx.newException(String.format("Color '%s' is not allowed.", colorName));
        }

        return Tag.styling(color);
    }

    static @NotNull TextColor resolveColor(final @NotNull String colorName, final @NotNull Context ctx) throws ParsingException {
        TextColor textColor = COLOR_ALIASES.get(colorName);

        if (textColor != null) {
            return textColor;
        }

        textColor = NamedTextColor.NAMES.value(colorName);

        if (textColor != null) {
            return textColor;
        }

        throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colours or hex (#RRGGBB) colors.", colorName));
    }

    @Override
    public boolean has(final @NotNull String name) {
        if (isColorOrAbbreviation(name)) {
            return true;
        }

        NamedTextColor textColor = NamedTextColor.NAMES.value(name);

        if (textColor != null && allowedColors.contains(textColor)) {
            return true;
        }

        return COLOR_ALIASES.containsKey(name);
    }

    @Override
    public @Nullable StyleClaim<?> claimStyle() {
        return STYLE;
    }

    public static TextColorTagResolver of(NamedTextColor... allowedColors) {
        return new TextColorTagResolver(Arrays.asList(allowedColors));
    }

}
