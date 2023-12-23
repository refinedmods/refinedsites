package com.refinedmods.refinedsites.playbook;

import com.refinedmods.refinedsites.model.NavigationItem;

import java.nio.file.Path;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class NavigationItemConfig {
    private final String title;
    private final String icon;
    private final String ref;
    private final List<NavigationItemConfig> children;

    public NavigationItem toNavigationItem(final Path pagesPath) {
        return NavigationItem.builder()
            .name(title)
            .path(ref != null ? pagesPath.resolve(ref) : null)
            .children(children == null
                ? null
                : children.stream().map(child -> child.toNavigationItem(pagesPath)).toList())
            .icon(icon)
            .build();
    }
}
