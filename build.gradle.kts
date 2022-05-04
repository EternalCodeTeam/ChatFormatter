import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "com.eternalcode.formatter"
version = "1.0.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // spigot api
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
    implementation("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT")

    // LiteCommands
    implementation("dev.rollczi.litecommands:bukkit:1.9.1")

    // cdn configs
    implementation("net.dzikoysk:cdn:1.13.22")

    // placeholderapi
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

bukkit {
    main = "pl.eternalmc.chat.ChatFormatterPlugin"
    apiVersion = "1.13"
    prefix = "EternalMC-ChatFormatter"
    author = "EternalMC"
    name = "EternalMC-ChatFormatter"
    version = "${project.version}"
    depend = listOf("PlaceholderAPI")
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}

tasks.withType <ShadowJar> {
    archiveFileName.set("EternalMC-ChatFormatter v${project.version}.jar")

    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")
    exclude("META-INF/**")
    exclude("javax/**")


    mergeServiceFiles()
    minimize()

    relocate("net.dzikoysk", "com.eternalcode.core.libs.net.dzikoysk")
    relocate("dev.rollczi", "com.eternalcode.core.libs.dev.rollczi")
    relocate("net.kyori", "com.eternalcode.core.libs.net.kyori")
}
