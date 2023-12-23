package com.refinedmods.refinedsites.playbook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class GitHubConfig {
    private final String organization;
    private final String repository;
    private final String minimumVersion;
    private final String snapshotBranch;

    public String getFullRepository() {
        return organization + "/" + repository;
    }
}
