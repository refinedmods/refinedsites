package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.Release;

import java.util.List;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParsedRelease {
    private final Release release;
    private final String slug;
    @Nullable // COMPAT
    private final String curseforgeUrl;
    private final List<ParsedReleasePlatform> platforms;
    @Nullable
    private final String githubUrl;
    @Nullable // COMPAT
    private final String modrinthUrl;
    @Nullable
    private final String changelogHtml;
}
