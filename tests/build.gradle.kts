dependencies {
    testImplementation(project(":core"))

    testImplementation("net.kyori:adventure-platform-bukkit:4.3.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.14.0")
    testImplementation("net.dzikoysk:cdn:1.14.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }
}