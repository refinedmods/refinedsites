package com.refinedmods.refinedsites.model.release.curseforge;

public enum CurseForgeFileStatus {
    // CurseForge core docs:
    // https://docs.curseforge.com/#tocS_FileStatus

    PROCESSING(1),
    CHANGES_REQUIRED(2),
    UNDER_REVIEW(3),
    APPROVED(4),
    REJECTED(5),
    MALWARE_DETECTED(6),
    DELETED(7),
    ARCHIVED(8),
    TESTING(9),
    RELEASED(10),
    READY_FOR_REVIEW(11),
    DEPRECATED(12),
    BAKING(13),
    AWAITING_PUBLISHING(14),
    FAILED_PUBLISHING(15);

    private final int id;

    CurseForgeFileStatus(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static CurseForgeFileStatus fromId(final int id) {
        for (final CurseForgeFileStatus status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }
}
