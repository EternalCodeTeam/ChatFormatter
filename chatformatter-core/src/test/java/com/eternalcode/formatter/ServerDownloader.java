package com.eternalcode.formatter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerDownloader {

    private static final String PAPER_API_URL = "https://api.papermc.io/v2/projects/paper";

    public static Path downloadPaperServer(String mcVersion, String outputDir) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Pobieranie informacji dla wersji: " + mcVersion);

        HttpRequest versionRequest = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/versions/%s", PAPER_API_URL, mcVersion)))
            .build();

        HttpResponse<String> versionResponse = client.send(versionRequest, HttpResponse.BodyHandlers.ofString());

        if (versionResponse.statusCode() != 200) {
            throw new IOException("Nie znaleziono wersji serwera: " + mcVersion + ". Status: " + versionResponse.statusCode());
        }

        String responseBody = versionResponse.body();
        String buildsArray = responseBody.substring(responseBody.indexOf("\"builds\":[") + 9);
        buildsArray = buildsArray.substring(0, buildsArray.indexOf("]"));
        String[] builds = buildsArray.split(",");
        String latestBuild = builds[builds.length - 1];

        System.out.println("Znaleziono najnowszy build: " + latestBuild);

        String fileName = String.format("paper-%s-%s.jar", mcVersion, latestBuild);
        String downloadUrl = String.format("%s/versions/%s/builds/%s/downloads/%s",
            PAPER_API_URL, mcVersion, latestBuild, fileName);

        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath);
        Path filePath = outputPath.resolve(fileName);

        if (Files.exists(filePath)) {
            System.out.println("Plik serwera juź istnieje: " + filePath.toAbsolutePath());
            return filePath;
        }

        System.out.println("Pobieranie pliku z: " + downloadUrl);

        HttpRequest downloadRequest = HttpRequest.newBuilder()
            .uri(URI.create(downloadUrl))
            .build();

        HttpResponse<Path> downloadResponse = client.send(downloadRequest, HttpResponse.BodyHandlers.ofFile(filePath));

        if (downloadResponse.statusCode() != 200) {
            throw new IOException("Pobieranie pliku nie powiodło się! Status: " + downloadResponse.statusCode());
        }

        System.out.println("Serwer został pomyślnie pobrany do: " + filePath.toAbsolutePath());
        return filePath;
    }

    public Path download() {
        try {
            String minecraftVersion = "1.21";
            String destinationDirectory = "run";

            return downloadPaperServer(minecraftVersion, destinationDirectory);

        } catch (IOException | InterruptedException e) {
            System.err.println("Wystąpił błąd podczas pobierania serwera:");
            e.printStackTrace();
        }
        return null;
    }
}
