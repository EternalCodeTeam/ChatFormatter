plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("net.minecrell:plugin-yml:0.6.0")
}
