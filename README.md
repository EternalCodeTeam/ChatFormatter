<div align="center">
    
![Readme Banner](https://i.imgur.com/AUI4Rnu.png)

[![Discord](https://img.shields.io/discord/889460117953720351?color=%237289DA&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/FQ7jmGBd6c)
[![Website](https://img.shields.io/badge/-website-orange?style=for-the-badge&logo=internet-explorer&logoColor=white)](https://eternalcode.pl/)

![Percentage of issues still open](https://img.shields.io/github/issues/EternalCodeTeam/ChatFormatter?style=for-the-badge)
[![GitHub forks](https://img.shields.io/github/forks/EternalCodeTeam/ChatFormatter?style=for-the-badge)](https://github.com/EternalCodeTeam/ChatFormatter/network)
[![GitHub stars](https://img.shields.io/github/stars/EternalCodeTeam/ChatFormatter?style=for-the-badge)](https://github.com/EternalCodeTeam/ChatFormatter/stargazers)
[![GitHub license](https://img.shields.io/github/license/EternalCodeTeam/ChatFormatter?style=for-the-badge)](https://github.com/EternalCodeTeam/ChatFormatter/blob/master/LICENSE)    
    
</div>

##### WARRNING! This plugin requires PlaceholderAPI and Vault to run!

### Features:
- PlaceholderAPI Support
- [MiniMessages Support](https://docs.adventure.kyori.net/minimessage/format.html) with Legacy Colors Support!
- Template System
- Custom Placeholders System

### Useful links:
- [Web UI](https://webui.adventure.kyori.net)
- [MiniMessages Format](https://docs.adventure.kyori.net/minimessage/format.html) 


### config.yml
```yaml
#    ____ _           _   _____      ChatFormatter       _   _            
#   / ___| |__   __ _| |_|  ___|__  _ __ _ __ ___   __ _| |_| |_ ___ _ __ 
#  | |   | '_ \ / _` | __| |_ / _ \| '__| '_ ` _ \ / _` | __| __/ _ \ '__|
#  | |___| | | | (_| | |_|  _| (_) | |  | | | | | | (_| | |_| ||  __/ |   
#   \____|_| |_|\__,_|\__|_|  \___/|_|  |_| |_| |_|\__,_|\__|\__\___|_|   

# Do you want to use pre chat format? (Other plugins could join custom prefixes etc.)
# INFO: This option requires to use custom badges like {displayname} and {message} in each message.
preFormatting: false
defaultFormat: "{displayname} {arrow_right} {message}"

# Chat format for ranks (Vault) Support mini-messages and legacy colors

# We're recommending to use webui for mini-messages: https://webui.adventure.kyori.net/

# documentation is here: https://docs.adventure.kyori.net/minimessage/format.html
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

format:
  default: "{member} &f{displayname} &8{arrow_right} {message} "
  admin: "$template({admin}, &c)"

# It is used to shorten the text even more and keep the clean file!
templates:
  - "$template($rank, $color) -> '$rank $color{displayname} &8{arrow_right} $color{message}'"

# Placeholders, it allows you to make a shorter text, you can use some prefixes, characters etc. 
placeholders:
  {displayname}: "<displayname>"
  {message}: "<message>"
  {prefix}: "<b><gradient:#29fbff:#38b3ff>ChatFormatter</gradient></b>"
  {member}: "<b><color:#6e6764>Member</color></b>"
  {admin}: "<b><color:#ff4400>Admin</color></b>"
  {arrow_right}: "»"
  {arrow_left}: "«"
```



