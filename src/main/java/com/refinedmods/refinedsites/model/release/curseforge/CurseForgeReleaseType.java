package com.refinedmods.refinedsites.model.release.curseforge;

enum CurseForgeReleaseType {
    RELEASE(1),
    BETA(2),
    ALPHA(3);

    private final int id;

    CurseForgeReleaseType(final int id) {
        this.id = id;
    }

    public static CurseForgeReleaseType fromId(final int id) {
        for (final CurseForgeReleaseType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
