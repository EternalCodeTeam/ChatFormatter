plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("net.minecrell:plugin-yml:0.6.0")
}
