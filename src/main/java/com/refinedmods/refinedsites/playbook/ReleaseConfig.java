package com.refinedmods.refinedsites.playbook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReleaseConfig {
    private final String github;
    private final CurseForgeReleaseConfig curseforge;
    private final String modrinth;
}
