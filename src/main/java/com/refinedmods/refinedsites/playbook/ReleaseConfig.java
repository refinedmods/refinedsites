package com.refinedmods.refinedsites.playbook;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReleaseConfig {
    private final List<String> github;
    private final CurseForgeReleaseConfig curseforge;
    private final String modrinth;
}
