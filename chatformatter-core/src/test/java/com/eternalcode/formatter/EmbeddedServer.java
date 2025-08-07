package com.eternalcode.formatter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class EmbeddedServer {

    private static final Logger LOGGER = Logger.getLogger(EmbeddedServer.class.getName());
    private final Path serverJarPath;
    private Thread serverThread;
    private URLClassLoader serverClassLoader;
    private ClassLoader bukkitClassLoader;

    private AtomicReference<CountDownLatch> serverReadyLatch;


    public EmbeddedServer(Path serverJarPath) {
        if (!serverJarPath.toFile().exists()) {
            throw new IllegalArgumentException("Plik JAR serwera nie istnieje: " + serverJarPath);
        }
        this.serverJarPath = serverJarPath;
    }

    public void start(Runnable runnable) throws Exception {
        String mainClassName = getMainClassFromManifest(serverJarPath);
        LOGGER.info("Uruchamianie wbudowanego serwera z pliku: " + serverJarPath.getFileName());

        this.serverClassLoader = new URLClassLoader(new URL[]{serverJarPath.toUri().toURL()}, EmbeddedServer.class.getClassLoader().getParent());

        serverReadyLatch = new AtomicReference<>(new CountDownLatch(1));

        this.serverThread = new Thread(() -> {
            try {
                Thread.currentThread().setContextClassLoader(serverClassLoader);


                // Przekierowanie strumieni wyjścia i błędów
                System.setOut(new java.io.PrintStream(new LogRedirector(System.out, serverReadyLatch.get()), true));
                System.setErr(new java.io.PrintStream(new LogRedirector(System.err, null), true));

                LOGGER.info("Główna klasa serwera: " + mainClassName);

                Class<?> mainClass = serverClassLoader.loadClass(mainClassName);
                Method mainMethod = mainClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{"--nogui"});

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Błąd podczas uruchamiania wątku serwera.", e);
            }
        });

        serverThread.setName("Embedded-Bukkit-Server-Thread");
        serverThread.start();

        LOGGER.info("Oczekiwanie na pełne załadowanie serwera...");
        if (!serverReadyLatch.get().await(20, TimeUnit.MINUTES)) {
            throw new RuntimeException("Serwer nie uruchomił się w wyznaczonym czasie.");
        }

        LOGGER.info("Oczekiwanie na wątek serwera...");
        bukkitClassLoader = waitForServerClassLoader();

        ClassLoader testClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(bukkitClassLoader);
        runnable.run();
        Thread.currentThread().setContextClassLoader(testClassLoader);


        LOGGER.info("Serwer jest gotowy i działa w tle.");
    }

    private ClassLoader waitForServerClassLoader() throws InterruptedException {
        List<String> potentialMinecraftThreadNames = List.of("Paper", "Craft");
        while (true) {
            Optional<ClassLoader> classLoader = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> potentialMinecraftThreadNames.stream().anyMatch(potential -> thread.getName().contains(potential)))
                .map(thread -> thread.getContextClassLoader())
                .filter(loader -> isBukkitLoader(loader))
                .findFirst();
            if (classLoader.isPresent()) {
                return classLoader.get();
            }
            Thread.sleep(100);
        }
    }

    private static boolean isBukkitLoader(ClassLoader loader) {
        try {
            loader.loadClass("org.bukkit.Bukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void stop()  {
        try {
            Class<?> bukkitClass = bukkitClassLoader.loadClass("org.bukkit.Bukkit");
            Object server = bukkitClass.getMethod("getServer").invoke(null);

            if (server != null) {
                LOGGER.info("Zatrzymywanie serwera...");
                Method shutdownMethod = server.getClass().getMethod("shutdown");
                shutdownMethod.invoke(server);
            }

            if (serverThread != null && serverThread.isAlive()) {
                serverThread.join(10000);
            }
            LOGGER.info("Serwer zatrzymany.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Błąd podczas zatrzymywania serwera.", e);
        }
    }

    private String getMainClassFromManifest(Path jarPath) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                Attributes mainAttributes = manifest.getMainAttributes();
                String mainClass = mainAttributes.getValue(Attributes.Name.MAIN_CLASS);
                if (mainClass != null) {
                    return mainClass;
                }
            }
            return "org.bukkit.craftbukkit.Main";
        }
    }

    private static class LogRedirector extends java.io.OutputStream {
        private final java.io.PrintStream original;
        private final CountDownLatch latch;
        private final StringBuilder lineBuilder = new StringBuilder();

        public LogRedirector(java.io.PrintStream original, CountDownLatch latch) {
            this.original = original;
            this.latch = latch;
        }

        @Override
        public void write(int b) throws IOException {
            original.write(b);
            if (b == '\n') {
                String line = lineBuilder.toString();
                if (latch != null && line.contains("Done (")) {
                    latch.countDown();
                }
                lineBuilder.setLength(0);
            } else {
                lineBuilder.append((char) b);
            }
        }
    }
}