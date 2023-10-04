plugins {
    `java-library`
    checkstyle
}

group = "com.eternalcode"
version = "1.0.7"

checkstyle {
    toolVersion = "10.12.3"

    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")

    maxErrors = 0
    maxWarnings = 0
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.eternalcode.pl/releases") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
