package com.refinedmods.refinedsites.playbook;

import com.refinedmods.refinedsites.model.Component;
import com.refinedmods.refinedsites.model.Site;
import com.refinedmods.refinedsites.model.Version;
import com.refinedmods.refinedsites.model.release.AbstractSourceData;
import com.refinedmods.refinedsites.model.release.Release;
import com.refinedmods.refinedsites.model.release.Releases;
import com.refinedmods.refinedsites.model.release.SourceDataProvider;
import com.refinedmods.refinedsites.model.release.Stats;
import com.refinedmods.refinedsites.model.release.curseforge.CurseForgeSourceDataProvider;
import com.refinedmods.refinedsites.model.release.github.GitHubSourceDataProvider;
import com.refinedmods.refinedsites.model.release.modrinth.ModrinthSourceDataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SiteFactory {
    private static final Gson GSON = new GsonBuilder().create();

    private final Path rootPath;

    public Site getSite() {
        try {
            final Path playbookPath = rootPath.resolve("playbook.json");
            final PlaybookConfig json = GSON.fromJson(Files.readString(playbookPath), PlaybookConfig.class);
            final List<Component> components = json.getComponents().stream().flatMap(component -> {
                final ComponentFactory factory = getComponentFactory(component);
                return factory.getComponents();
            }).collect(Collectors.toList());
            addRootComponent(components, json);
            final Map<String, List<Component>> componentsByName = components.stream().collect(Collectors.groupingBy(
                Component::getName
            ));
            final Map<String, Releases> releasesByComponentName = getReleases(json);
            return new Site(
                json.getName(),
                json.getUrl(),
                components,
                componentsByName,
                releasesByComponentName
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Releases> getReleases(final PlaybookConfig json) {
        final Map<String, Releases> releasesByComponentName = new HashMap<>();
        for (final var releaseEntry : json.getReleases().entrySet()) {
            final ReleaseConfig releaseConfig = releaseEntry.getValue();
            final String componentName = releaseEntry.getKey();
            final Releases releases = getReleases(componentName, releaseConfig);
            releasesByComponentName.put(componentName, releases);
        }
        return releasesByComponentName;
    }

    private Releases getReleases(final String componentName, final ReleaseConfig releaseConfig) {
        final List<SourceDataProvider<?>> sourceDataProviders = getSourceDataProviders(releaseConfig);
        final Date indexedAt = new Date();
        final List<? extends AbstractSourceData> sourceData = sourceDataProviders.stream().flatMap(
            provider -> provider.getSourceData().stream()
        ).toList();
        final Map<String, List<AbstractSourceData>> sourceDataByName = sourceData.stream().collect(
            Collectors.groupingBy(AbstractSourceData::getName)
        );
        final List<Release> releases = sourceDataByName.entrySet()
            .stream()
            .map(entry -> new Release(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(Release::getCreatedAt))
            .toList();
        return new Releases(indexedAt, releases, componentName, Stats.of(sourceData));
    }

    private List<SourceDataProvider<?>> getSourceDataProviders(final ReleaseConfig releaseConfig) {
        final List<SourceDataProvider<?>> sourceDataProviders = new ArrayList<>();
        if (releaseConfig.getGithub() != null) {
            sourceDataProviders.add(new GitHubSourceDataProvider(releaseConfig.getGithub(), getGhToken()));
        }
        if (releaseConfig.getCurseforge() != null) {
            sourceDataProviders.add(new CurseForgeSourceDataProvider(
                releaseConfig.getCurseforge().getId(),
                releaseConfig.getCurseforge().getSlug()
            ));
        }
        if (releaseConfig.getModrinth() != null) {
            sourceDataProviders.add(new ModrinthSourceDataProvider(releaseConfig.getModrinth()));
        }
        return sourceDataProviders;
    }

    private static String getGhToken() {
        final String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            throw new RuntimeException("GITHUB_TOKEN environment variable is not set");
        }
        return token;
    }

    private ComponentFactory getComponentFactory(final ComponentConfig component) {
        if (component.getGithub() != null) {
            return new GithubComponentFactory(rootPath, component.getGithub(), component.getName(), getGhToken());
        }
        if (component.getPath() != null) {
            return new LocalComponentFactory(
                rootPath.resolve(component.getPath()),
                component.getName(),
                false,
                new Version(
                    component.getVersion(),
                    "v" + component.getVersion(),
                    false
                )
            );
        }
        return () -> Stream.of(Component.builder()
            .name(component.getName())
            .rootPath(null)
            .pagesPath(null)
            .changelogPath(null)
            .assetsPath(null)
            .root(false)
            .version(new Version("0.0.0", "v0.0.0", false))
            .navigationItems(Collections.emptyList())
            .pages(Collections.emptyList())
            .build());
    }

    private void addRootComponent(final List<Component> components, final PlaybookConfig json) {
        components.addAll(new LocalComponentFactory(rootPath, json.getName(), true, new Version(
            "0.0.0",
            "v0.0.0",
            false
        )).getComponents().toList());
    }
}
