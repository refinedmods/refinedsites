package com.refinedmods.refinedsites.model.release.github;

import lombok.Getter;
import org.kohsuke.github.GHAsset;

@Getter
public class GitHubAsset {
    private final long id;
    private final String nodeId;
    private final String name;
    private final String label;
    private final String state;
    private final String contentType;
    private final long size;
    private final long downloadCount;
    private final String downloadUrl;

    public GitHubAsset(final GHAsset asset) {
        this.id = asset.getId();
        this.nodeId = asset.getNodeId();
        this.name = asset.getName();
        this.label = asset.getLabel();
        this.state = asset.getState();
        this.contentType = asset.getContentType();
        this.size = asset.getSize();
        this.downloadCount = asset.getDownloadCount();
        this.downloadUrl = asset.getBrowserDownloadUrl();
    }
}
