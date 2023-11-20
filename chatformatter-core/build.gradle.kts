import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("eternalcode.java")

    id("com.github.johnrengelman.shadow")
    id("net.minecrell.plugin-yml.bukkit")
}

bukkit {
    main = "com.eternalcode.formatter.ChatFormatterPlugin"
    apiVersion = "1.19"
    prefix = "ChatFormatter"
    author = "EternalCodeTeam"
    name = "ChatFormatter"
    version = "${project.version}"
    depend = listOf("PlaceholderAPI", "Vault")
}

dependencies {
    // Spigot API
    val spigotApiVersion = "1.19.3-R0.1-SNAPSHOT"
    compileOnly("org.spigotmc:spigot-api:$spigotApiVersion")
    testImplementation("org.spigotmc:spigot-api:$spigotApiVersion")

    // Kyori Adventure & MiniMessage
    val adventureVersion = "4.3.1"
    val miniMessageVersion = "4.14.0"
    implementation("net.kyori:adventure-platform-bukkit:$adventureVersion")
    implementation("net.kyori:adventure-text-minimessage:$miniMessageVersion")
    testImplementation("net.kyori:adventure-platform-bukkit:$adventureVersion")
    testImplementation("net.kyori:adventure-text-minimessage:$miniMessageVersion")

    // Okaeri Config
    val okaeriVersion = "5.0.0-beta.5"
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:$okaeriVersion")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriVersion")
    testImplementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:$okaeriVersion")
    testImplementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriVersion")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // PlaceholderAPI & Vault
    compileOnly("me.clip:placeholderapi:2.11.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // GitCheck
    implementation("com.eternalcode:gitcheck:1.0.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    shadowJar {
        archiveFileName.set("chatformatter-core-${version}.jar")

        exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
            "javax/**"
        )

        mergeServiceFiles()

        val prefix = "com.eternalcode.formatter.libs"
        listOf(
            "dev.rollczi",
            "panda",
            "net.kyori",
            "org.bstats",
            "org.json",
            "eu.okaeri",
            "org.yaml",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
