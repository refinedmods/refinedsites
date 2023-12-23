package com.refinedmods.refinedsites.model.release;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Releases {
    private final Date indexedAt;
    private final List<Release> releases;
    private final String componentName;
    private final Stats stats;
}
