package com.refinedmods.refinedsites.model.release.modrinth;

import com.refinedmods.refinedsites.model.release.AbstractSourceData;

import java.util.List;

import lombok.Getter;

@Getter
public class ModrinthSourceData extends AbstractSourceData {
    private final String id;
    private final String projectId;
    private final String authorId;
    private final String versionNumber;
    private final String changelog;
    private final transient long downloads;
    private final String versionType;
    private final String status;
    private final List<ModrinthFile> files;
    private final List<String> gameVersions;
    private final List<String> loaders;
    private final String htmlUrl;

    ModrinthSourceData(final String projectSlug, final ModrinthRelease release) {
        super("modrinth", release.getName(), "https://api.modrinth.com/v2/project/" + projectSlug + "/version/" + release.getId());
        this.id = release.getId();
        this.createdAt = release.getDatePublished();
        this.projectId = release.getProjectId();
        this.authorId = release.getAuthorId();
        this.versionNumber = release.getVersionNumber();
        this.changelog = release.getChangelog();
        this.downloads = release.getDownloads();
        this.versionType = release.getVersionType();
        this.status = release.getStatus();
        this.files = release.getFiles();
        this.gameVersions = release.getGameVersions();
        this.loaders = release.getLoaders();
        this.htmlUrl = "https://modrinth.com/mod/" + projectSlug + "/version/" + release.getVersionNumber();
    }
}
