package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import com.eternalcode.formatter.template.TemplateRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.eternalcode.formatter.ChatSettings;
import com.eternalcode.formatter.placeholder.PlaceholderStack;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dzikoysk.cdn.entity.Description;
import org.bukkit.entity.Player;

import java.util.Collections;
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


    @Description({ " ", "# Chat rankFormat for ranks (Vault) Support mini-messages and legacy colors" })
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
        "# ",
        "# You can use three more placeholders: <displayname> <name> <message>",
        "# "
    })
    public String defaultFormat = "{displayname} » {message}";
    public Map<String, String> rankFormat = new ImmutableMap.Builder<String, String>()
            .put("default", "{member} &7$hoverName({displayname}) &8» <gradient:#d4d4d4:white>{message} ")
            .put("vip", "{vip} &f$hoverName({displayname}) <dark_gray>» <gradient:#ffd270:white>{message}")
            .put("mod", "{mod} &f$hoverName({displayname}) <dark_gray>» <gradient:#a3ff9e:white>{message}")
            .put("admin", "{admin} &f$hoverName({displayname}) <dark_gray>» <gradient:#bac8ff:white><b>{message}")
            .put("owner", "{owner} &f$hoverName({displayname}) <dark_gray>» <gradient:#ff9195:white><b>{message}")
            .build();

    @Description({ " ", "# Placeholders, it allows you to make a shorter text, you can use some prefixes, characters etc. " })
    public Map<String, String> placeholders = new ImmutableMap.Builder<String, String>()
            .put("{displayname}", "<displayname>")
            .put("{name}", "<name>")
            .put("{message}", "<message>")
            .put("{member}", "<#6e6764>Member")
            .put("{vip}", "<gold>VIP")
            .put("{mod}", "<b><#00c900>Mod</b>")
            .put("{admin}", "<b><#6e86ff>Admin</b>")
            .put("{owner}", "<b><gradient:#c40000:#e04b4b>Owner</b>")
            .put("{rankDescription}", "<dark_gray>Rank: <white>%vault_group%")
            .put("{joinDate}", "<dark_gray>Joined: <white>%player_first_join_date%")
            .put("{health}", "<dark_gray>Health: <red>%player_health%")
            .put("{lvl}", "<dark_gray>LVL: <gold>%player_level%")
            .put("{privateMessage}", "<gradient:#36ff39:#75ff75><i>Click to send private message</i></gradient>")
            .build();

    @Description({ " ", "# This section is made for experienced users" , "# It is used to shorten the text even more and keep the clean file!" })
    public List<Template> templates = new ImmutableList.Builder<Template>()
            .add(Template.of("hoverName", List.of("name"), "<hover:show_text:'<dark_gray>Name: <white>$name<br><br>{rankDescription}<br>{joinDate}<br>{health}<br>{lvl}<br><br>{privateMessage}'><click:suggest_command:'/msg {displayname} '>{displayname}</click></hover>"))
            .build();


    @Override
    public boolean isReceiveUpdates() {
        return this.receiveUpdates;
    }

    @Override
    public String getRawFormat(String rank) {
        return this.rankFormat.getOrDefault(rank, this.defaultFormat);
    }

    @Override
    public String apply(String text, Player target) {
        String value = text;

        value = PlaceholderAPI.setPlaceholders(target, value);

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
