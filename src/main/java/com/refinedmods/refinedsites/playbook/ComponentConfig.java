package com.refinedmods.refinedsites.playbook;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
class ComponentConfig {
    private final String name;
    private final String version;
    private final String path;
    private final GitHubConfig github;
}
