<div align="center">

![Readme Banner](assets/img/chatformatter.png)

[![Available on Paper](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/spigot_vector.svg)](https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/supported/paper_vector.svg)
[![Available on modrinth](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/plugin/chatformatter)

[![Patreon](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/donate/patreon-plural_vector.svg)](https://www.patreon.com/eternalcode)
[![Website](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/documentation/website_vector.svg)](https://eternalcode.pl/)
[![Discord](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/FQ7jmGBd6c)

[![Gradle](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/built-with/gradle_vector.svg)](https://gradle.org/)
[![Java](https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/built-with/java17_vector.svg)](https://www.java.com/)

</div>

> ⚠ **This plugin requires PlaceholderAPI and Vault to run!**

## Preview:
![1](assets/gif/ChatFormatterHoverPlayerInfo.gif)
![2](assets/gif/ChatFormatterClickableMessage2.gif)

## Features:

- PlaceholderAPI Support
- [MiniMessages Support](https://docs.adventure.kyori.net/minimessage/format.html) with Legacy Colors Support!
- Template System
- Custom Placeholders System
- Per-permission miniMessages Tags (see permissions below)

## Useful links:

- [Web UI](https://webui.adventure.kyori.net)
- [MiniMessages Format](https://docs.adventure.kyori.net/minimessage/format.html)

## Permissions:

- `chatformatter.decorations.*` gives access to `<bold>`, `<italic>`, `<underlined>`, `<strikethrough>`, and `<obfuscated>` tags.
- `chatformatter.decorations.bold` - `<bold>`
- `chatformatter.decorations.italic` - `<italic>`
- `chatformatter.decorations.underlined` - `<underlined>`
- `chatformatter.decorations.strikethrough` - `<strikethrough>`
- `chatformatter.decorations.obfuscated` - `<obfuscated>`
- `chatformatter.reset` - `<reset>`
- `chatformatter.gradient` - `<gradient>`
- `chatformatter.hover` - `<hover>`
- `chatformatter.click` - `<click>`
- `chatformatter.insertion` - `<insertion>`
- `chatformatter.font` - `<font>`
- `chatformatter.transition` - `<transition>`
- `chatformatter.translatable` - `<lang>`
- `chatformatter.selector` - `<selector>`
- `chatformatter.keybind` - `<key>`
- `chatformatter.newline` - `<newline>`
- `chatformatter.reload` - reloads the plugin with `/chatformatter reload`
- `chatformatter.receiveupdates` - receive update announcements for this plugin

### Additional color permissions:

- `chatformatter.color.*` gives access to `<red>`, `<blue>`, etc. tags.
- `chatformatter.legacycolor` - Allows the use of legacy color codes, such as `&c`, `&4`, `&l`, etc.
- `chatformatter.color.black` - `<black>`
- `chatformatter.color.dark_blue` - `<dark_blue>`
- `chatformatter.color.dark_green` - `<dark_green>`
- `chatformatter.color.dark_aqua` - `<dark_aqua>`
- `chatformatter.color.dark_red` - `<dark_red>`
- `chatformatter.color.dark_purple` - `<dark_purple>`
- `chatformatter.color.gold` - `<gold>`
- `chatformatter.color.gray` - `<gray>`
- `chatformatter.color.dark_gray` - `<dark_gray>`
- `chatformatter.color.blue` - `<blue>`
- `chatformatter.color.green` - `<green>`
- `chatformatter.color.aqua` - `<aqua>`
- `chatformatter.color.red` - `<red>`
- `chatformatter.color.light_purple` - `<light_purple>`
- `chatformatter.color.yellow` - `<yellow>`
- `chatformatter.color.white` - `<white>`

### config.yml

```yaml
#  
#    ____ _           _   _____      ChatFormatter       _   _            
#   / ___| |__   __ _| |_|  ___|__  _ __ _ __ ___   __ _| |_| |_ ___ _ __ 
#  | |   | '_ \ / _` | __| |_ / _ \| '__| '_ ` _ \ / _` | __| __/ _ \ '__|
#  | |___| | | | (_| | |_|  _| (_) | |  | | | | | | (_| | |_| ||  __/ |   
#   \____|_| |_|\__,_|\__|_|  \___/|_|  |_| |_| |_|\__,_|\__|\__\___|_|   
#  
#  
# Do you want to receive updates about new versions of ChatFormatter?
receiveUpdates: true
#  
# Chat format for ranks (Vault) Support mini-messages and legacy colors
# We're recommending to use webui for mini-messages: https://webui.adventure.kyori.net/
# documentation is here: https://docs.adventure.kyori.net/minimessage/format.html
defaultFormat: '{displayname} {arrow_right} {message}'
# Example usages:
# 
# Hover message:
# One line: <hover:show_text:'<red>test'>TEST
# Multiple lines: <hover:show_text:'<red>test'>Test<newline>Test2</hover>
# 
# Click message:
# Open URL: <click:open_url:'https://webui.adventure.kyori.net'>TEST</click>
# Run command: <click:run_command:/say hello>Click</click> to say hello
# Suggest command: <click:suggest_command:'/msg {displayname}'></click>
# 
# RGB and gradient message usage: 
# <color:#ff00ee>Example message</color>
# <gradient:#ff00ee:#f79459>Example message</gradient>
#  
format:
  default: '{member} &f{displayname} &8{arrow_right} {message} '
  admin: $template({admin}, &c)
#  
# It is used to shorten the text even more and keep the clean file!
templates:
  - $template($rank, $color) -> '$rank $color{displayname} &8{arrow_right} $color{message}'
#  
# Placeholders, it allows you to make a shorter text, you can use some prefixes, characters etc. 
placeholders:
  '{displayname}': <displayname>
  '{message}': <message>
  '{prefix}': <b><gradient:#29fbff:#38b3ff>ChatFormatter</gradient></b>
  '{member}': <b><color:#6e6764>Member</color></b>
  '{admin}': <b><color:#ff4400>Admin</color></b>
  '{arrow_right}': »
  '{arrow_left}': «
```



