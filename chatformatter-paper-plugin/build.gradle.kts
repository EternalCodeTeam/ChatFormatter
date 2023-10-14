import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins{
    id("eternalcode.java")
    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper") version "2.2.0"
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
    compileOnly(project(":chatformatter-core"))
    compileOnly("com.eternalcode:eternalcombat-api:1.1.1")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("chatformatter-paper-plugin-${version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
}

tasks.create("shadowAll") {
    val projects = listOf(
            project(":chatformatter-core"),
            project(":chatformatter-paper-plugin")
    )

    for (project in projects) {
        dependsOn(project.tasks.shadowJar)
    }

    doLast {
        merge("ChatFormatter v${project.version}.jar", projects)
    }
}

fun merge(archiveFileName: String, projects: List<Project>) {
    val outputFile = File(project.layout.buildDirectory.asFile.get(), "libs/${archiveFileName}")
    val outputDir = outputFile.parentFile ?: throw RuntimeException("Could not get output directory")

    if (!outputDir.exists() && !outputDir.mkdirs()) {
        throw RuntimeException("Could not create output directory")
    }

    if (outputFile.exists()) {
        outputFile.delete()
    }

    if (!outputFile.createNewFile()) {
        throw RuntimeException("Could not find output file to merge")
    }

    JarOutputStream(FileOutputStream(outputFile)).use { outputJar ->
        for (project in projects) {
            val shadowJar = project.tasks.shadowJar.get()

            for (file in shadowJar.outputs.files.files) {
                JarFile(file).use { jarFile ->
                    for (entry in jarFile.entries()) {
                        if (entry.isDirectory) {
                            continue
                        }

                        val bytes = jarFile.getInputStream(entry).readBytes()
                        val newEntry = JarEntry(entry.name)

                        newEntry.setTime(System.currentTimeMillis())
                        newEntry.setSize(bytes.size.toLong())

                        try {
                            outputJar.putNextEntry(newEntry)
                            outputJar.write(bytes)
                            outputJar.closeEntry()
                        }
                        catch (exception: IOException) {
                            if (exception.message?.contains("duplicate entry: ") == true) {
                                continue
                            }

                            exception.printStackTrace()
                        }
                    }
                }
            }
        }
    }

}

tasks.runServer {
    minecraftVersion("1.19.3")
    downloadPlugins {
        val outputFile = File(project.layout.buildDirectory.asFile.get(), "libs/ChatFormatter v${project.version}.jar")

        downloadPlugins.url(outputFile.toURL()!!.toString())
    }
}
