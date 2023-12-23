package com.refinedmods.refinedsites.playbook;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class PlaybookConfig {
    private final String name;
    private final String url;
    private final List<ComponentConfig> components;
    private final Map<String, ReleaseConfig> releases;
}
