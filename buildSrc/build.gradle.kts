plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.0")
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.3.1")
    implementation("net.minecrell:plugin-yml:0.6.0")
}
