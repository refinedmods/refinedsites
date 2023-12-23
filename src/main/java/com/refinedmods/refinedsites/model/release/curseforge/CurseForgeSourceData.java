package com.refinedmods.refinedsites.model.release.curseforge;

import com.refinedmods.refinedsites.model.release.AbstractSourceData;

import java.util.Date;
import java.util.List;

import lombok.Getter;

@Getter
public class CurseForgeSourceData extends AbstractSourceData {
    private final long id;
    private final Date dateModified;
    private final long fileLength;
    private final String fileName;
    private final CurseForgeFileStatus status;
    private final List<String> gameVersions;
    private final CurseForgeReleaseType releaseType;
    private final transient long totalDownloads;
    private final CurseForgeUser user;
    private final long additionalFilesCount;
    private final boolean hasServerPack;
    private final long additionalServerPackFilesCount;
    private final boolean isEarlyAccessContent;
    private final String downloadUrl;
    private final String htmlUrl;

    CurseForgeSourceData(final String projectId, final String projectSlug, final CurseForgeRelease release) {
        super("curseforge", release.getDisplayName(), "https://www.curseforge.com/api/v1/mods/" + projectId + "/files/" + release.getId());
        this.id = release.getId();
        this.createdAt = release.getDateCreated();
        this.dateModified = release.getDateModified();
        this.fileLength = release.getFileLength();
        this.fileName = release.getFileName();
        this.status = CurseForgeFileStatus.fromId(release.getStatus());
        this.gameVersions = release.getGameVersions();
        this.releaseType = CurseForgeReleaseType.fromId(release.getReleaseType());
        this.totalDownloads = release.getTotalDownloads();
        this.user = release.getUser();
        this.additionalFilesCount = release.getAdditionalFilesCount();
        this.hasServerPack = release.isHasServerPack();
        this.additionalServerPackFilesCount = release.getAdditionalServerPackFilesCount();
        this.isEarlyAccessContent = release.isEarlyAccessContent();
        this.downloadUrl = "https://www.curseforge.com/minecraft/mc-mods/" + projectSlug + "/download/" + release.getId();
        this.htmlUrl = "https://www.curseforge.com/minecraft/mc-mods/" + projectSlug + "/files/" + release.getId();
    }

    @Override
    public long getDownloads() {
        return totalDownloads;
    }
}
