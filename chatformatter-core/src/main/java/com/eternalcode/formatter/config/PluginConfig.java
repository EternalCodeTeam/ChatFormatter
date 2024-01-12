package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import com.eternalcode.formatter.template.TemplateRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.eternalcode.formatter.ChatSettings;
import com.eternalcode.formatter.placeholder.PlaceholderStack;
import net.dzikoysk.cdn.entity.Description;

import java.util.List;
import java.util.Map;

public class PluginConfig implements ChatSettings, PlaceholderStack, TemplateRepository {

    @Description(" ")
    @Description("#    ____ _           _   _____      ChatFormatter       _   _            ")
    @Description("#   / ___| |__   __ _| |_|  ___|__  _ __ _ __ ___   __ _| |_| |_ ___ _ __ ")
    @Description("#  | |   | '_ \\ / _` | __| |_ / _ \\| '__| '_ ` _ \\ / _` | __| __/ _ \\ '__|")
    @Description("#  | |___| | | | (_| | |_|  _| (_) | |  | | | | | | (_| | |_| ||  __/ |   ")
    @Description("#   \\____|_| |_|\\__,_|\\__|_|  \\___/|_|  |_| |_| |_|\\__,_|\\__|\\__\\___|_|   ")
    @Description(" ")

    @Description({ " ", "# Do you want to receive updates about new versions of ChatFormatter?" })
    public boolean receiveUpdates = true;


    @Description({ " ", "# Chat format for ranks (Vault) Support mini-messages and legacy colors" })
    @Description({ " ", "# We're recommending to use webui for mini-messages: https://webui.adventure.kyori.net/" })
    @Description({ " ", "# documentation is here: https://docs.adventure.kyori.net/minimessage/format.html" })

    @Description({
        "# Example usages:",
        "# ",
        "# Hover message:",
        "# One line: <hover:show_text:'<red>test'>TEST",
        "# Multiple lines: <hover:show_text:'<red>test'>Test<newline>Test2</hover>",
        "# ",
        "# Click message:",
        "# Open URL: <click:open_url:'https://webui.adventure.kyori.net'>TEST</click>",
        "# Run command: <click:run_command:/say hello>Click</click> to say hello",
        "# Suggest command: <click:suggest_command:'/msg {displayname}'></click>",
        "# ",
        "# RGB and gradient message usage: ",
        "# <color:#ff00ee>Example message</color>",
        "# <gradient:#ff00ee:#f79459>Example message</gradient>",
        " "
    })
    public String defaultFormat = "{displayname} {arrow_right} {message}";
    public Map<String, String> format = new ImmutableMap.Builder<String, String>()
        .put("default", "{member} &f{displayname} &8{arrow_right} {message} ")
        .put("admin", "$template({admin}, &c)")
        .build();


    @Description({ " ", "# It is used to shorten the text even more and keep the clean file!" })
    public List<Template> templates = new ImmutableList.Builder<Template>()
        .add(Template.of("template", List.of("rank", "color"), "$rank $color{displayname} &8{arrow_right} $color{message}"))
        .build();

    @Description({ " ", "# Placeholders, it allows you to make a shorter text, you can use some prefixes, characters etc. " })
    public Map<String, String> placeholders = new ImmutableMap.Builder<String, String>()
        .put("{displayname}", "<displayname>")
        .put("{name}", "<name>")
        .put("{message}", "<message>")
        .put("{prefix}", "<b><gradient:#29fbff:#38b3ff>ChatFormatter</gradient></b>")
        .put("{member}", "<b><color:#6e6764>Member</color></b>")
        .put("{admin}", "<b><color:#ff4400>Admin</color></b>")
        .put("{arrow_right}", "»")
        .put("{arrow_left}", "«")
        .build();


    @Override
    public boolean isReceiveUpdates() {
        return this.receiveUpdates;
    }

    @Override
    public String getRawFormat(String rank) {
        return this.format.getOrDefault(rank, this.defaultFormat);
    }

    @Override
    public String apply(String text) {
        String value = text;

        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        return value;
    }

    @Override
    public List<Template> getTemplates() {
        return this.templates;
    }
}
