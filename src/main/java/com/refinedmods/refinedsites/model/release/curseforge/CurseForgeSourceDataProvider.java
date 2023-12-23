package com.refinedmods.refinedsites.model.release.curseforge;

import com.refinedmods.refinedsites.model.release.SourceDataProvider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CurseForgeSourceDataProvider implements SourceDataProvider<CurseForgeSourceData> {
    private static final Gson GSON = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .setPrettyPrinting()
        .create();

    private final String projectId;
    private final String projectSlug;

    @Override
    public List<CurseForgeSourceData> getSourceData() {
        int currentPage = 0;
        int totalPages;
        final List<CurseForgeSourceData> result = new ArrayList<>();
        do {
            try {
                final String url = "https://www.curseforge.com/api/v1/mods/" + projectId + "/files?pageIndex=" + currentPage + "&pageSize=50&sort=dateCreated&sortDescending=true&removeAlphas=false";
                final HttpResponse<String> rawResponse = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build(), HttpResponse.BodyHandlers.ofString());
                if (rawResponse.statusCode() != 200) {
                    throw new RuntimeException(rawResponse.body());
                }
                final CurseForgeResponse response = GSON.fromJson(rawResponse.body(), CurseForgeResponse.class);
                totalPages = response.getPagination().getTotalCount() / response.getPagination().getPageSize();
                log.info("Retrieved CurseForge page {} of {}", currentPage + 1, totalPages + 1);
                response.getData().forEach(release -> {
                    log.info("Found release {} for {}", release.getDisplayName(), projectId);
                    result.add(new CurseForgeSourceData(projectId, projectSlug, release));
                });
                currentPage++;
            } catch (final IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (currentPage <= totalPages);
        return result;
    }
}
