package com.eternalcode.formatter.config;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.cdn.entity.Description;
import panda.utilities.StringUtils;
import com.eternalcode.formatter.ChatSettings;
import com.eternalcode.formatter.placeholder.PlaceholderStack;

import java.util.Map;

public class PluginConfig implements ChatSettings, PlaceholderStack {

    @Description({ StringUtils.EMPTY })
    @Description("#    ____ _           _   _____      ChatFormatter       _   _            ")
    @Description("#   / ___| |__   __ _| |_|  ___|__  _ __ _ __ ___   __ _| |_| |_ ___ _ __ ")
    @Description("#  | |   | '_ \\ / _` | __| |_ / _ \\| '__| '_ ` _ \\ / _` | __| __/ _ \\ '__|")
    @Description("#  | |___| | | | (_| | |_|  _| (_) | |  | | | | | | (_| | |_| ||  __/ |   ")
    @Description("#   \\____|_| |_|\\__,_|\\__|_|  \\___/|_|  |_| |_| |_|\\__,_|\\__|\\__\\___|_|   ")
    @Description({ StringUtils.EMPTY })

    @Description("# Do you want to use pre chat format? (Other plugins could join custom prefixes etc.)")
    @Description("# INFO: This option requires to use custom badges like {displayname} and {message} in each message.")
    public boolean preFormatting = false;

    public String defaultFormat = "{displayname} {arrow_right} {message}";

    @Description({ StringUtils.EMPTY, "# Chat format for ranks (Vault) Support mini-messages and legacy colors" })
    @Description({ StringUtils.EMPTY, "# We're recommending to use webui for mini-messages: https://webui.adventure.kyori.net/" })
    @Description({ StringUtils.EMPTY, "# documentation is here: https://docs.adventure.kyori.net/minimessage/format.html" })

    @Description({
        "# Example usages:",
        "# ",
        "# Hover message:",
        "# One line: <hover:show_text:'<red>test'>TEST",
        "# Multiple lines: <hover:show_text:'<red>test'>Test\nTest2</hover>",
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
    public Map<String, String> format = new ImmutableMap.Builder<String, String>()
        .put("default", "{member] &f{displayname} &8{arrow_right} {message} ")
        .put("admin", "{admin} &c{displayname} &8{arrow_right} {message}")
        .build();

    @Description({ StringUtils.EMPTY, "# Placeholdery, pozwala skrócić tekst, możesz tutaj użyć np. prefixów, znaczków itd. " })
    public Map<String, String> placeholders = new ImmutableMap.Builder<String, String>()
        .put("{prefix}", "<b><gradient:#29fbff:#38b3ff>ChatFormatter</gradient></b>")
        .put("{member}", "<b><color:#6e6764>Member</color></b>")
        .put("{admin}", "<b><color:#ff4400>Admin</color></b>")
        .put("{arrow_right}", "»")
        .put("{arrow_left}", "«")
        .build();

    @Override
    public boolean preFormatting() {
        return preFormatting;
    }

    @Override
    public String format(String rank) {
        return format.getOrDefault(rank, defaultFormat);
    }

    @Override
    public String apply(String text) {
        String value = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        return value;
    }

}
