import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

subprojects {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
        maven { url = uri("https://jitpack.io") }
    }

    group = "com.eternalcode.formatter"
    version = "1.0.1"

    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.main.get().java.setSrcDirs(listOf("src/"))

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

project(":chat-formatter") {
    apply(plugin = "net.minecrell.plugin-yml.bukkit")
    apply(plugin = "xyz.jpenilla.run-paper")

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
        compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

        // Kyori Adventure & MiniMessage
        implementation("net.kyori:adventure-platform-bukkit:4.1.1")
        implementation("net.kyori:adventure-text-minimessage:4.11.0")

        // LiteCommands & CDN
        implementation("dev.rollczi.litecommands:bukkit:2.4.0")
        implementation("net.dzikoysk:cdn:1.13.22")

        // bStats
        implementation("org.bstats:bstats-bukkit:3.0.0")

        // PlaceholderAPI & Vault
        compileOnly("me.clip:placeholderapi:2.11.1")
        compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    }

    tasks.withType <ShadowJar> {
        archiveFileName.set("ChatFormatter v${project.version}.jar")

        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("META-INF/**")
        exclude("javax/**")

        mergeServiceFiles()
        minimize()

        relocate("net.dzikoysk", "com.eternalcode.formatter.libs.net.dzikoysk")
        relocate("dev.rollczi", "com.eternalcode.formatter.libs.dev.rollczi")
        relocate("panda", "com.eternalcode.formatter.libs.org.panda")
        relocate("org.panda_lang", "com.eternalcode.formatter.libs.org.panda")
        relocate("net.kyori", "com.eternalcode.formatter.libs.net.kyori")
        relocate("org.bstats", "com.eternalcode.formatter.libs.org.bstats")
    }

    tasks {
        runServer {
            minecraftVersion("1.18.2")
        }
    }
}

project(":paper-support") {
    apply(plugin = "net.minecrell.plugin-yml.bukkit")
    apply(plugin = "xyz.jpenilla.run-paper")

    bukkit {
        main = "com.eternalcode.formatter.paper.ChatFormatterPaperSupportPlugin"
        apiVersion = "1.13"
        prefix = "ChatFormatter-PaperSupport"
        author = "EternalCodeTeam"
        name = "ChatFormatter-PaperSupport"
        version = "${project.version}"
        depend = listOf("ChatFormatter")
    }

    dependencies {
        compileOnly(project(":chat-formatter"))
        compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    }

    tasks.withType <ShadowJar> {
        archiveFileName.set("ChatFormatter-PaperSupport v${project.version}.jar")

        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("META-INF/**")
        exclude("javax/**")

        mergeServiceFiles()
        minimize()
    }

    tasks {
        runServer {
            minecraftVersion("1.18.2")
        }
    }
}

project(":chat-formatter-test") {
    sourceSets.main.get().java.setSrcDirs(listOf("none/"))
    sourceSets.test.get().java.setSrcDirs(listOf("src/"))

    dependencies {
        testImplementation(project(":chat-formatter"))

        testImplementation("net.kyori:adventure-platform-bukkit:4.1.1")
        testImplementation("net.kyori:adventure-text-minimessage:4.11.0")
        testImplementation("net.dzikoysk:cdn:1.13.22")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testImplementation("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}
