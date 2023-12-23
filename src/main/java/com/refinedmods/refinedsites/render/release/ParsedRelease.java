package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.Release;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParsedRelease {
    private final Release release;
    private final String slug;
    @Nullable
    private final String curseforgeUrl;
    @Nullable
    private final String githubUrl;
    @Nullable
    private final String modrinthUrl;
    @Nullable
    private final String changelogHtml;
}
