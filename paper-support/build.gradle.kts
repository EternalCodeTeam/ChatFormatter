import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins{
    id("net.minecrell.plugin-yml.bukkit")
    id("xyz.jpenilla.run-paper")
}

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
    compileOnly(project(":core"))
    implementation(project(":paper-multi-version-support"))
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("ChatFormatter-PaperSupport v${project.version}.jar")

    exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
            "javax/**"
    )

    mergeServiceFiles()
    minimize()
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}