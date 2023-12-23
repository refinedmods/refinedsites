package com.refinedmods.refinedsites.model.release.github;

import com.refinedmods.refinedsites.model.release.AbstractSourceData;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import org.kohsuke.github.GHRelease;

@Getter
public class GitHubSourceData extends AbstractSourceData {
    private final long id;
    private final String nodeId;
    private final String htmlUrl;
    private final String assetsUrl;
    private final List<GitHubAsset> assets;
    private final String uploadUrl;
    private final String tagName;
    private final String targetCommitish;
    private final String body;
    private final boolean draft;
    private final boolean prerelease;
    private final Date updatedAt;
    private final Date publishedAt;
    private final String tarballUrl;
    private final String zipballUrl;
    private final String discussionUrl;

    public GitHubSourceData(final GHRelease release) {
        super("github", release.getName(), release.getUrl().toString());
        try {
            this.id = release.getId();
            this.nodeId = release.getNodeId();
            this.htmlUrl = release.getHtmlUrl().toString();
            this.assetsUrl = release.getAssetsUrl();
            this.uploadUrl = release.getUploadUrl();
            this.tagName = release.getTagName();
            this.targetCommitish = release.getTargetCommitish();
            this.body = release.getBody();
            this.draft = release.isDraft();
            this.prerelease = release.isPrerelease();
            this.createdAt = release.getCreatedAt();
            this.updatedAt = release.getUpdatedAt();
            this.publishedAt = release.getPublished_at();
            this.tarballUrl = release.getTarballUrl();
            this.zipballUrl = release.getZipballUrl();
            this.discussionUrl = release.getDiscussionUrl();
            this.assets = release.listAssets().toList().stream().map(GitHubAsset::new).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getDownloads() {
        return assets.stream().mapToLong(GitHubAsset::getDownloadCount).sum();
    }
}
