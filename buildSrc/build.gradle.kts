plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.21")
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.2.2")
    implementation("net.minecrell:plugin-yml:0.6.0")
}
