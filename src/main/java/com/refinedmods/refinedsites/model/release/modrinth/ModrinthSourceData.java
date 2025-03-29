package com.refinedmods.refinedsites.model.release.modrinth;

import com.refinedmods.refinedsites.model.release.AbstractSourceData;
import com.refinedmods.refinedsites.model.release.Platform;

import java.util.List;
import javax.annotation.Nullable;

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
        super("modrinth", release.getName(),
            "https://api.modrinth.com/v2/project/" + projectSlug + "/version/" + release.getId(), getPlatform(release));
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
        // using the version number in the ID will conflict when there are 2 versions with the same version number
        // (for example for neoforge and fabric) -> so use the real ID (non-conflicting) one
        this.htmlUrl = "https://modrinth.com/mod/" + projectSlug + "/version/" + release.getId();
    }

    @Nullable
    private static Platform getPlatform(final ModrinthRelease release) {
        if (release.getLoaders().contains("neoforge")) {
            return Platform.NEOFORGE;
        }
        if (release.getLoaders().contains("forge")) {
            return Platform.FORGE;
        }
        return release.getLoaders().contains("fabric") ? Platform.FABRIC : null;
    }
}
