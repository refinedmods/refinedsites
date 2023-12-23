package com.refinedmods.refinedsites.model;

import java.nio.file.Path;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NavigationItem {
    private final Path path;
    private final List<NavigationItem> children;
    @Setter
    private String name;
    @Setter
    private String url;
    private String icon;
}
