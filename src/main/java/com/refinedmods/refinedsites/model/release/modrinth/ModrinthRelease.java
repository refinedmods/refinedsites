package com.refinedmods.refinedsites.model.release.modrinth;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ModrinthRelease {
    private String id;
    private String projectId;
    private String authorId;
    private boolean featured;
    private String name;
    private String versionNumber;
    private String changelog;
    private Date datePublished;
    private int downloads;
    private String versionType;
    private String status;
    private List<ModrinthFile> files;
    private List<String> gameVersions;
    private List<String> loaders;
}
