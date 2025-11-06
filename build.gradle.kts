import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    id("eternalcode.java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

tasks.register<ShadowJar>("shadowChatFormatter") {
    this.group = "build"

    val targetProjects = listOf(
        project(":chatformatter-core"),
        project(":chatformatter-paper-plugin")
    )

    for (targetProject in targetProjects) {
        this.dependsOn("${targetProject.name}:shadowJar")
    }

    this.doLast {
        this@register.mergeJars(
            "ChatFormatter v${project.version}.jar",
            targetProjects
        )
    }
}

fun ShadowJar.mergeJars(archiveFileName: String, projects: List<Project>) {
    val outputFile = File(
        this.project.layout.buildDirectory.asFile.get(),
        "libs/$archiveFileName"
    )
    val outputDir = outputFile.parentFile
        ?: throw IllegalStateException("Cannot find output directory")

    if (!outputDir.exists() && !outputDir.mkdirs()) {
        throw IllegalStateException("Failed to create directory: ${outputDir.absolutePath}")
    }

    if (outputFile.exists() && !outputFile.delete()) {
        throw IllegalStateException("Cannot delete existing file: ${outputFile.absolutePath}")
    }

    if (!outputFile.createNewFile()) {
        throw IllegalStateException("Cannot create output file: ${outputFile.absolutePath}")
    }

    mergeShadowJarsIntoOutput(outputFile, projects)
}

private fun mergeShadowJarsIntoOutput(
    outputFile: File,
    projects: List<Project>
) {
    JarOutputStream(FileOutputStream(outputFile)).use { outputJar ->
        val processedEntries = mutableSetOf<String>()

        for (targetProject in projects) {
            val shadowJarTask = targetProject.tasks.named("shadowJar", ShadowJar::class.java).get()

            for (jarFile in shadowJarTask.outputs.files.files) {
                processJarFile(jarFile, outputJar, processedEntries)
            }
        }
    }
}

private fun processJarFile(
    jarFile: File,
    outputJar: JarOutputStream,
    processedEntries: MutableSet<String>
) {
    JarFile(jarFile).use { sourceJar ->
        for (entry in sourceJar.entries()) {
            if (entry.isDirectory || processedEntries.contains(entry.name)) {
                continue
            }

            try {
                copyJarEntry(sourceJar, entry, outputJar)
                processedEntries.add(entry.name)
            } catch (exception: IOException) {
                if (exception.message?.contains("duplicate entry:") != true) {
                    throw exception
                }
            }
        }
    }
}

private fun copyJarEntry(
    sourceJar: JarFile,
    entry: JarEntry,
    outputJar: JarOutputStream
) {
    val entryBytes = sourceJar.getInputStream(entry).use { it.readBytes() }
    val newEntry = JarEntry(entry.name).apply {
        this.time = System.currentTimeMillis()
        this.size = entryBytes.size.toLong()
    }

    outputJar.putNextEntry(newEntry)
    outputJar.write(entryBytes)
    outputJar.closeEntry()
}

runPaper {
    this.disablePluginJarDetection()
}

tasks.runServer {
    minecraftVersion("1.21.10")
    dependsOn("shadowChatFormatter")
    pluginJars.from(layout.buildDirectory.file("libs/ChatFormatter v${project.version}.jar"))

    javaLauncher.set(
        javaToolchains.launcherFor {
            this.languageVersion.set(JavaLanguageVersion.of(21))
        }
    )

    downloadPlugins {
        this.modrinth("luckperms", "v5.5.0-bukkit")
        this.modrinth("VaultUnlocked", "2.16.0")
    }
}