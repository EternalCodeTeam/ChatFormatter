package com.eternalcode.formatter.template;

import panda.std.Result;
import panda.utilities.text.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

    private static final String TEMPLATE_FORMAT = "$%s(%s) -> '%s'";
    private static final String ARGUMENT_FORMAT = "$%s";
    private static final String SEPARATOR_FORMAT = ", ";

    public static final Pattern TEMPLATE_INVOKE_PATTERN = Pattern.compile("\\$(\\w+)\\(([^()]+(?:,\\s*[^()]+){0,250})\\)");
    private static final Pattern TEMPLATE_PARSE_PATTERN = Pattern.compile("^\\$(\\w+)\\((\\$\\w+(?:,\\s*\\$\\w+){0,250})\\)\\s*->\\s*'(.*)'$", Pattern.CASE_INSENSITIVE);

    private final String name;
    private final List<String> arguments;
    private final String content;
    
    private Template(String name, List<String> arguments, String content) {
        this.name = name;
        this.arguments = arguments;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public String getContent() {
        return content;
    }

    public String apply(String text) {
        Matcher matcher = Template.TEMPLATE_INVOKE_PATTERN.matcher(text);

        while (matcher.find()) {
            String currentName = matcher.group(1);

            if (!this.name.equals(currentName)) {
                continue;
            }

            List<String> parsedArguments = Template.parseArguments(matcher.group(2), "");
            String template = this.injectArguments(parsedArguments);

            text = text.substring(0, matcher.start()) + template + text.substring(matcher.end());
        }

        return text;
    }

    private String injectArguments(List<String> toInject) {
        if (toInject.size() != this.arguments.size()) {
            throw new IllegalArgumentException("Incorrect arguments in template " + this.name);
        }

        String template = this.content;

        for (int index = 0; index < this.arguments.size(); index++) {
            String key = this.arguments.get(index);
            String value = toInject.get(index);

            template = template.replace(ARGUMENT_FORMAT.formatted(key), value);
        }

        return template;
    }

    public static Result<Template, String> parse(String text) {
        Matcher matcher = TEMPLATE_PARSE_PATTERN.matcher(text);

        if (!matcher.matches()) {
            return Result.error("Invalid syntax: " + text);
        }

        String name = matcher.group(1);
        List<String> arguments = Template.parseArguments(matcher.group(2), "$");
        String content = matcher.group(3);

        return Result.ok(new Template(name, arguments, content));
    }

    public static Template of(String name, List<String> arguments, String content) {
        return new Template(name, new ArrayList<>(arguments), content);
    }

    private static List<String> parseArguments(String text, String before) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        int last = text.indexOf(before);

        if (last == - 1) {
            return Collections.emptyList();
        }

        int normalSeparator = text.indexOf(",", last);
        int spaceSeparator = text.indexOf(" ", last);

        int lastSeparatorMin = Math.min(normalSeparator, spaceSeparator);
        int lastSeparatorMax = Math.max(normalSeparator, spaceSeparator);

        String argument = lastSeparatorMin == - 1
            ? text.substring(last + before.length())
            : text.substring(last + before.length(), lastSeparatorMin);

        List<String> arguments = new ArrayList<>();

        arguments.add(argument);

        if (lastSeparatorMax != - 1) {
            arguments.addAll(parseArguments(text.substring(lastSeparatorMax + 1), before));
        }

        return arguments;
    }

    @Override
    public String toString() {
        return String.format(TEMPLATE_FORMAT, name, Joiner.on(SEPARATOR_FORMAT).join(this.arguments, arg -> String.format(ARGUMENT_FORMAT, arg)), this.content);
    }

}
