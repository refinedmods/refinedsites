package com.refinedmods.refinedsites.model.release.curseforge;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CurseForgeResponse {
    private List<CurseForgeRelease> data;
    private CurseForgeResponsePagination pagination;
}
