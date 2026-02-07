import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    id("eternalcode.java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

class JarMerger(
    private val outputFile: File,
    private val inputFiles: List<File>
) : Serializable {

    fun merge() {
        val outputDir = this.outputFile.parentFile
            ?: throw IllegalStateException("Cannot find output directory")

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw IllegalStateException("Failed to create directory: ${outputDir.absolutePath}")
        }

        if (this.outputFile.exists() && !this.outputFile.delete()) {
            throw IllegalStateException("Cannot delete existing file: ${this.outputFile.absolutePath}")
        }

        if (!this.outputFile.createNewFile()) {
            throw IllegalStateException("Cannot create output file: ${this.outputFile.absolutePath}")
        }

        JarOutputStream(FileOutputStream(this.outputFile)).use { outputJar ->
            val processedEntries = mutableSetOf<String>()

            for (jarFile in this.inputFiles) {
                this.processJarFile(jarFile, outputJar, processedEntries)
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
                    this.copyJarEntry(sourceJar, entry, outputJar)
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

    val projectVersion = project.version.toString()
    val outputFile = project.layout.buildDirectory.file("libs/ChatFormatter v${projectVersion}.jar").get().asFile
    val shadowJarFiles = targetProjects.flatMap { targetProject ->
        targetProject.tasks.named("shadowJar", ShadowJar::class.java)
            .get()
            .outputs
            .files
            .files
    }

    val merger = JarMerger(outputFile, shadowJarFiles)

    this.doLast {
        merger.merge()
    }
}

runPaper {
    this.disablePluginJarDetection()
}

tasks.runServer {
    minecraftVersion("1.21.11")
    dependsOn("shadowChatFormatter")
    pluginJars.from(layout.buildDirectory.file("libs/ChatFormatter v${project.version}.jar"))
    jvmArgs("-Dcom.mojang.eula.agree=true")

    javaLauncher.set(
        javaToolchains.launcherFor {
            this.languageVersion.set(JavaLanguageVersion.of(21))
        }
    )

    downloadPlugins {
        this.modrinth("luckperms", "v5.5.17-bukkit")
        this.modrinth("VaultUnlocked", "2.19.0")
    }
}