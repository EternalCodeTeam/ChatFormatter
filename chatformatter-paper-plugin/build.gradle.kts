import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    id("eternalcode.java")
    id("de.eldoria.plugin-yml.bukkit")
    id("com.gradleup.shadow")
}

bukkit {
    main = "com.eternalcode.formatter.paper.ChatFormatterPaperPlugin"
    apiVersion = "1.13"
    prefix = "ChatFormatter"
    author = "EternalCodeTeam"
    name = "ChatFormatter"
    softDepend = listOf("PlaceholderAPI", "Vault")
    version = "${project.version}"

    commands {
        register("chatformatter") {
            description = "Reloads the configs for ChatFormatter"
            permission = "chatformatter.reload"
            usage = "/chatformatter reload"
        }
    }

    permissions {
        register("chatformatter.*") {
            children = listOf(
                "chatformatter.decorations.*",
                "chatformatter.color.*",
                "chatformatter.receiveupdates",
                "chatformatter.reset",
                "chatformatter.gradient",
                "chatformatter.hover",
                "chatformatter.click",
                "chatformatter.insertion",
                "chatformatter.font",
                "chatformatter.transition",
                "chatformatter.translatable",
                "chatformatter.selector",
                "chatformatter.keybind",
                "chatformatter.newline",
                "chatformatter.rainbow"
            )
            default = Default.OP
        }


        register("chatformatter.decorations.*") {
            children = listOf(
                "chatformatter.decorations.bold",
                "chatformatter.decorations.italic",
                "chatformatter.decorations.underlined",
                "chatformatter.decorations.strikethrough",
                "chatformatter.decorations.obfuscated"
            )
            default = Default.OP
        }

        register("chatformatter.reset") { default = Default.OP }
        register("chatformatter.gradient") { default = Default.OP }
        register("chatformatter.hover") { default = Default.OP }
        register("chatformatter.click") { default = Default.OP }
        register("chatformatter.insertion") { default = Default.OP }
        register("chatformatter.font") { default = Default.OP }
        register("chatformatter.transition") { default = Default.OP }
        register("chatformatter.translatable") { default = Default.OP }
        register("chatformatter.selector") { default = Default.OP }
        register("chatformatter.keybind") { default = Default.OP }
        register("chatformatter.newline") { default = Default.OP }
        register("chatformatter.rainbow") { default = Default.OP }

        register("chatformatter.color.*") {
            children = listOf(
                "chatformatter.color.black",
                "chatformatter.color.dark_blue",
                "chatformatter.color.dark_green",
                "chatformatter.color.dark_aqua",
                "chatformatter.color.dark_red",
                "chatformatter.color.dark_purple",
                "chatformatter.color.gold",
                "chatformatter.color.gray",
                "chatformatter.color.dark_gray",
                "chatformatter.color.blue",
                "chatformatter.color.green",
                "chatformatter.color.aqua",
                "chatformatter.color.red",
                "chatformatter.color.light_purple",
                "chatformatter.color.yellow",
                "chatformatter.color.white"
            )
            default = Default.OP
        }

        register("chatformatter.receiveupdates") {
            default = Default.OP
        }
    }
}

dependencies {
    compileOnly(project(":chatformatter-core"))
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    archiveFileName.set("chatformatter-paper-plugin-${version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
}
