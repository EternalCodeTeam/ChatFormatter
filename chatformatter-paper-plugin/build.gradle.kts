plugins{
    id("eternalcode.java")
    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
}

bukkit {
    main = "com.eternalcode.formatter.paper.ChatFormatterPaperPlugin"
    apiVersion = "1.13"
    prefix = "ChatFormatter"
    author = "EternalCodeTeam"
    name = "ChatFormatter"
    depend = listOf("PlaceholderAPI", "Vault")
    version = "${project.version}"
}

dependencies {
    compileOnly(project(":chatformatter-core"))
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    archiveFileName.set("chatformatter-paper-plugin-${version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
}
