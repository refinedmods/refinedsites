package com.refinedmods.refinedsites.model.release;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class Stats {
    private final List<SourceDownloads> downloads;
    private final long totalDownloads;

    public Stats(final List<SourceDownloads> downloads, final long totalDownloads) {
        this.downloads = downloads;
        this.totalDownloads = totalDownloads;
    }

    public static Stats of(final List<? extends AbstractSourceData> sourceData) {
        return new Stats(
            sourceData.stream().map(source -> new SourceDownloads(source.getSource(), source.getDownloads())).toList(),
            sourceData.stream().mapToLong(AbstractSourceData::getDownloads).sum()
        );
    }

    public static Stats of(final Releases releases) {
        final Map<String, Long> downloadsBySource = new HashMap<>();
        for (final Release release : releases.getReleases()) {
            for (final SourceDownloads downloads : release.getStats().getDownloads()) {
                downloadsBySource.put(
                    downloads.source(),
                    downloadsBySource.getOrDefault(downloads.source(), 0L) + downloads.downloads()
                );
            }
        }
        return new Stats(
            downloadsBySource.entrySet().stream().map(entry -> new SourceDownloads(entry.getKey(), entry.getValue()))
                .toList(),
            releases.getReleases().stream().mapToLong(r -> r.getStats().getTotalDownloads()).sum()
        );
    }

    public static Stats of(final Collection<Releases> releases) {
        final Map<String, Long> downloadsBySource = new HashMap<>();
        for (final Releases release : releases) {
            for (final SourceDownloads downloads : release.getStats().getDownloads()) {
                downloadsBySource.put(
                    downloads.source(),
                    downloadsBySource.getOrDefault(downloads.source(), 0L) + downloads.downloads()
                );
            }
        }
        return new Stats(
            downloadsBySource.entrySet().stream().map(entry -> new SourceDownloads(entry.getKey(), entry.getValue()))
                .toList(),
            releases.stream().mapToLong(r -> r.getStats().getTotalDownloads()).sum()
        );
    }

    public record SourceDownloads(String source, long downloads) {
    }
}
