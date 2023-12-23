package com.refinedmods.refinedsites.playbook;

import com.refinedmods.refinedsites.model.Component;
import com.refinedmods.refinedsites.model.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class LocalComponentFactory implements ComponentFactory {
    private static final Gson GSON = new GsonBuilder().create();

    private final Path docsPath;
    private final String name;
    private final boolean root;
    private final Version version;

    LocalComponentFactory(final Path path,
                          final String name,
                          final boolean root,
                          final Version version) {
        this.docsPath = path.resolve("docs/");
        this.name = name;
        this.root = root;
        this.version = version;
    }

    @Override
    public Stream<Component> getComponents() {
        if (!Files.exists(docsPath)) {
            log.warn("No docs found for component {} {}", name, version);
            return Stream.empty();
        }
        try {
            final Path pagesPath = docsPath.resolve("pages/");
            final List<Path> pages = Files.walk(pagesPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".adoc"))
                .toList();
            final List<NavigationItemConfig> navItems = getNavigationItems();
            return Stream.of(Component.builder()
                .name(name)
                .rootPath(docsPath)
                .pagesPath(pagesPath)
                .changelogPath(docsPath.resolve("../CHANGELOG.md"))
                .assetsPath(docsPath.resolve("assets/"))
                .root(root)
                .version(version)
                .navigationItems(navItems.stream().map(item -> item.toNavigationItem(pagesPath)).toList())
                .pages(pages)
                .build());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<NavigationItemConfig> getNavigationItems() throws IOException {
        final Path navigationConfig = docsPath.resolve("nav.json");
        if (!navigationConfig.toFile().exists()) {
            return Collections.emptyList();
        }
        return GSON.fromJson(
            Files.readString(navigationConfig),
            new TypeToken<ArrayList<NavigationItemConfig>>() {
            }.getType()
        );
    }
}
