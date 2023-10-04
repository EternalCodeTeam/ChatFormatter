plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

subprojects {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.eternalcode.pl/releases") }
    }

    group = "com.eternalcode.formatter"
    version = "1.0.7"

    apply(plugin = "java-library")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.main.get().java.setSrcDirs(listOf("src/"))

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}