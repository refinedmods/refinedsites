package com.refinedmods.refinedsites.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
class Breadcrumb {
    private final String name;
    private final String url;
    @Setter
    private boolean active;
    private final String icon;
}
