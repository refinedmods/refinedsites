package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.Releases;
import com.refinedmods.refinedsites.model.release.Stats;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.github.slugify.Slugify;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ReleasesIndex {
    private final String url;
    private final Date indexedAt;
    private final List<Item> projects;
    private final Stats stats;

    public ReleasesIndex(final String url,
                         final Date indexedAt,
                         final Slugify slugify,
                         final Collection<Releases> projectReleases) {
        this.url = url + "/releases.json";
        this.indexedAt = indexedAt;
        this.projects = projectReleases.stream().map(release -> new Item(
            url + "/" + slugify.slugify(release.getComponentName()) + "/releases.json",
            release.getComponentName()
        )).toList();
        this.stats = Stats.of(projectReleases);
    }

    @RequiredArgsConstructor
    @Getter
    public static class Item {
        private final String url;
        private final String name;
    }
}
