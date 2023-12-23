package com.refinedmods.refinedsites.render.release;

import com.refinedmods.refinedsites.model.release.Release;
import com.refinedmods.refinedsites.model.release.ReleaseType;
import com.refinedmods.refinedsites.model.release.Releases;
import com.refinedmods.refinedsites.model.release.Stats;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.github.slugify.Slugify;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ProjectReleasesIndex {
    private final String url;
    private final String name;
    private final Date indexedAt;
    private final List<Item> releases;
    private final Stats stats;

    public ProjectReleasesIndex(final String url,
                                final Date indexedAt,
                                final Slugify slugify,
                                final Releases releases) {
        final String componentUrl = url + "/" + slugify.slugify(releases.getComponentName());
        this.url = componentUrl + "/releases.json";
        this.name = releases.getComponentName();
        this.indexedAt = indexedAt;
        this.releases = releases.getReleases().stream().map(release -> new Item(componentUrl, release)).toList();
        this.stats = Stats.of(releases);
    }

    @RequiredArgsConstructor
    @Getter
    public static class Item {
        private final String name;
        private final ReleaseType type;
        private final String url;
        private final Set<String> sources;
        private final Date createdAt;

        public Item(final String url, final Release release) {
            this.name = release.getName();
            this.type = release.getType();
            this.url = url + "/releases/" + name + ".json";
            this.sources = release.getSources();
            this.createdAt = release.getCreatedAt();
        }
    }
}
