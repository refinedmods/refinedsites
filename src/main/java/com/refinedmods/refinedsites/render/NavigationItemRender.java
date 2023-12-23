package com.refinedmods.refinedsites.render;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NavigationItemRender {
    private final String name;
    private final String nameSlug;
    private final String url;
    private final List<NavigationItemRender> children;
    private final String key;
    private final String icon;
    private final boolean active;
}
