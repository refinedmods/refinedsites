package com.refinedmods.refinedsites.model.release;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Release {
    private final String name;
    private final ReleaseType type;
    private final Set<String> sources;
    private final Date createdAt;
    private final Stats stats;
    private final List<AbstractSourceData> sourceData;

    public Release(final String name, final List<? extends AbstractSourceData> sourceData) {
        this.name = name;
        this.type = name.contains("alpha") ? ReleaseType.ALPHA
            : name.contains("beta") ? ReleaseType.BETA
            : ReleaseType.RELEASE;
        this.sourceData = sourceData.stream()
            .sorted(Comparator.comparing(AbstractSourceData::getCreatedAt))
            .collect(Collectors.toList());
        this.sources = this.sourceData.stream().map(AbstractSourceData::getUrl).collect(Collectors.toSet());
        this.createdAt = this.sourceData.get(0).getCreatedAt();
        this.stats = Stats.of(this.sourceData);
    }
}
