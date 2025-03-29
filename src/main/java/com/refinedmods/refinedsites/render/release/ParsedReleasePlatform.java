package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.Platform;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParsedReleasePlatform {
    private final Platform platform;
    @Nullable
    private final String curseforgeUrl;
    @Nullable
    private final String modrinthUrl;
}
