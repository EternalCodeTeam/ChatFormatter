import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins{
    id("eternalcode.java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "3.0.1"
}

tasks.create("shadowAll") {
    group = "shadow"

    val projects = listOf(
        project(":chatformatter-core"),
        project(":chatformatter-paper-plugin")
    )

    for (project in projects) {
        dependsOn(project.name + ":shadowJar")
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

runPaper {
    disablePluginJarDetection()
}

tasks.runServer {
    minecraftVersion("1.21.9")
    dependsOn("shadowAll")
    pluginJars = files("/build/libs/ChatFormatter v${project.version}.jar")
    // We need to start the server with Java 21, but jar is built with Java 17
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
    downloadPlugins.modrinth("luckperms", "v5.5.0-bukkit")
    downloadPlugins.modrinth("VaultUnlocked", "2.16.0")
}
