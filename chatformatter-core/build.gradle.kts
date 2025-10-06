plugins {
    id("eternalcode.java")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // Spigot API
    val spigotApiVersion = "1.19.3-R0.1-SNAPSHOT"
    compileOnly("org.spigotmc:spigot-api:$spigotApiVersion")
    testImplementation("org.spigotmc:spigot-api:$spigotApiVersion")

    // Kyori Adventure & MiniMessage
    val adventureVersion = "4.4.1"
    val miniMessageVersion = "4.25.0"
    implementation("net.kyori:adventure-api:$miniMessageVersion")
    implementation("net.kyori:adventure-text-minimessage:$miniMessageVersion")
    implementation("net.kyori:adventure-platform-bukkit:$adventureVersion")
    testImplementation("net.kyori:adventure-platform-bukkit:$adventureVersion")
    testImplementation("net.kyori:adventure-text-minimessage:$miniMessageVersion")

    // CDN Configs
    val cdnVersion = "1.14.9"
    implementation("net.dzikoysk:cdn:$cdnVersion")
    testImplementation("net.dzikoysk:cdn:$cdnVersion")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.1.0")

    // PlaceholderAPI & Vault
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // GitCheck
    implementation("com.eternalcode:gitcheck:1.0.0")

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    testImplementation("me.clip:placeholderapi:2.11.6")
    testImplementation("org.assertj:assertj-core:3.27.6")
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
            "com.eternalcode.gitcheck",
            "net.dzikoysk",
            "panda",
            "org.panda_lang",
            "net.kyori",
            "org.bstats",
            "org.json",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
