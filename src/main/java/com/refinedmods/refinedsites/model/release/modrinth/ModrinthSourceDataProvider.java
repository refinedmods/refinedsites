package com.refinedmods.refinedsites.model.release.modrinth;

import com.refinedmods.refinedsites.model.release.SourceDataProvider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModrinthSourceDataProvider implements SourceDataProvider<ModrinthSourceData> {
    private static final Gson GSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    private final String projectSlug;

    @Override
    public List<ModrinthSourceData> getSourceData() {
        try {
            final String url = "https://api.modrinth.com/v2/project/" + projectSlug + "/version";
            final HttpResponse<String> rawResponse = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofString());
            if (rawResponse.statusCode() != 200) {
                throw new RuntimeException(rawResponse.body());
            }
            final ModrinthRelease[] response = GSON.fromJson(rawResponse.body(), ModrinthRelease[].class);
            return Arrays.stream(response)
                .map(release -> new ModrinthSourceData(projectSlug, release))
                .toList();
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
