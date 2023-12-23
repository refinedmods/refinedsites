package com.refinedmods.refinedsites.model.release.curseforge;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CurseForgeResponsePagination {
    private int index;
    private int pageSize;
    private int totalCount;
}
