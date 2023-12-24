package com.refinedmods.refinedsites.model;

import java.nio.file.Path;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class Component {
    private final String name;
    private final boolean root;
    private final Version version;
    private final List<Path> pages;
    private final List<NavigationItem> navigationItems;
    private final Path rootPath;
    private final Path pagesPath;
    private final Path assetsPath;
    private final Path changelogPath;
    @Setter
    private boolean latest;
    @Setter
    private String slug;

    public String getAssetsOutputPath() {
        return slug + "." + version.friendlyName();
    }

    public String getRelativePagePath(final Path from, final Path to) {
        return from.relativize(to).toString().replace(".adoc", ".html");
    }
}
