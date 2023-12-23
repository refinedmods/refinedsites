package com.refinedmods.refinedsites.model.release.curseforge;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CurseForgeRelease {
    private long id;
    private Date dateCreated;
    private Date dateModified;
    private String displayName;
    private long fileLength;
    private String fileName;
    private int status;
    private List<String> gameVersions;
    private List<String> gameVersionTypeIds;
    private int releaseType;
    private long totalDownloads;
    private CurseForgeUser user;
    private long additionalFilesCount;
    private boolean hasServerPack;
    private long additionalServerPackFilesCount;
    private boolean isEarlyAccessContent;
}
