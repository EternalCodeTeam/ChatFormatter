import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit")
    id("xyz.jpenilla.run-paper")
}

bukkit {
    main = "com.eternalcode.formatter.ChatFormatterPlugin"
    apiVersion = "1.13"
    prefix = "ChatFormatter"
    author = "EternalCodeTeam"
    name = "ChatFormatter"
    version = "${project.version}"
    depend = listOf("PlaceholderAPI", "Vault")
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    // Kyori Adventure & MiniMessage
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    // LiteCommands & CDN
    implementation("dev.rollczi.litecommands:bukkit:2.8.9")
    implementation("net.dzikoysk:cdn:1.14.4")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // PlaceholderAPI & Vault
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // GitCheck
    implementation("com.eternalcode:gitcheck:1.0.0")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("ChatFormatter v${project.version}.jar")

    exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
            "javax/**"
    )

    mergeServiceFiles()
    minimize()

    val prefix = "com.eternalcode.formatter.libs"
    listOf(
            "net.dzikoysk",
            "dev.rollczi",
            "panda",
            "org.panda_lang",
            "net.kyori",
            "org.bstats",
            "org.json",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}