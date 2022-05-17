package com.eternalcode.formatter.template;

import panda.std.Result;
import panda.utilities.text.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

    private static final String TEMPLATE = "(%s) -> \"%s\"";
    private static final String ARGUMENT = "$%s";
    private static final String SEPARATOR = ", ";

    private static final Pattern TEMPLATE_REGEX = Pattern.compile("^\\((\\$\\w+(?:,\\s*\\$\\w+)*)\\)\\s*->\\s*\"(.*)\"$", Pattern.CASE_INSENSITIVE);

    private final List<String> arguments;
    private final String content;

    private Template(List<String> arguments, String content) {
        this.arguments = arguments;
        this.content = content;
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public String getContent() {
        return content;
    }

    public static Result<Template, String> parse(String text) {
        Matcher matcher = TEMPLATE_REGEX.matcher(text);

        if (!matcher.matches()) {
            return Result.error("Invalid syntax: " + text);
        }

        List<String> arguments = Template.parseArguments(matcher.group(1));
        String content = matcher.group(2);

        return Result.ok(new Template(arguments, content));
    }

    private static List<String> parseArguments(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        int last = text.indexOf("$");

        if (last == - 1) {
            return Collections.emptyList();
        }

        int normalSeparator = text.indexOf(",", last);
        int spaceSeparator = text.indexOf(" ", last);

        int lastSeparatorMin = Math.min(normalSeparator, spaceSeparator);
        int lastSeparatorMax = Math.max(normalSeparator, spaceSeparator);

        String argument = lastSeparatorMin == - 1
            ? text.substring(last + 1)
            : text.substring(last + 1, lastSeparatorMin);

        List<String> arguments = new ArrayList<>();

        arguments.add(argument);

        if (lastSeparatorMax != - 1) {
            arguments.addAll(parseArguments(text.substring(lastSeparatorMax + 1)));
        }

        return arguments;
    }

    @Override
    public String toString() {
        return String.format(TEMPLATE, Joiner.on(SEPARATOR).join(this.arguments, arg -> String.format(ARGUMENT, arg)), this.content);
    }

}
