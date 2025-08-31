plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.10")
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.1.0")
    implementation("de.eldoria.plugin-yml.bukkit:de.eldoria.plugin-yml.bukkit.gradle.plugin:0.8.0")
}
