package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.AbstractSourceData;
import com.refinedmods.refinedsites.model.release.ReleaseType;
import com.refinedmods.refinedsites.model.release.Stats;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProjectRelease {
    private final String url;
    private final String name;
    private final Date indexedAt;
    private final ReleaseType type;
    private final List<AbstractSourceData> sources;
    private final Stats stats;
}
