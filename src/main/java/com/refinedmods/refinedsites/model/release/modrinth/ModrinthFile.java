package com.refinedmods.refinedsites.model.release.modrinth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ModrinthFile {
    private ModrinthHashes hashes;
    private String url;
    private String filename;
    private boolean primary;
    private int size;
}
