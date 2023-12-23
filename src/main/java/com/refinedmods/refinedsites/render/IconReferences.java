package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.asciidoctor.Asciidoctor;

class IconReferences {
    private final Map<Path, IconReference> icons = new HashMap<>();

    public Function<Path, Optional<UUID>> createResolver(final Asciidoctor asciidoctor,
                                                         final PageAttributeCache pageAttributeCache) {
        return path -> resolve(asciidoctor, pageAttributeCache, path);
    }

    private Optional<UUID> resolve(final Asciidoctor asciidoctor,
                                   final PageAttributeCache pageAttributeCache,
                                   final Path path) {
        if (icons.containsKey(path)) {
            return Optional.of(icons.get(path).key);
        }
        final Optional<String> icon = pageAttributeCache.getIcon(asciidoctor, path);
        return icon.map(i -> addReference(path, i));
    }

    private UUID addReference(final Path path, final String icon) {
        final UUID key = UUID.randomUUID();
        icons.put(path, new IconReference(key, icon));
        return key;
    }

    public String getHtml(final Path componentAssetsOutputPath, final Path pageOutputPath) {
        if (icons.isEmpty()) {
            return "";
        }
        final StringBuilder result = new StringBuilder("<div class=\"d-none\">");
        icons.values().forEach(iconReference -> {
            result.append("<div id=\"icon-").append(iconReference.key).append("\">");
            final Path assetPath = componentAssetsOutputPath.resolve(iconReference.icon);
            result.append("<img width=\"32\" height=\"32\" src=\"")
                .append(pageOutputPath.getParent().relativize(assetPath))
                .append("\">");
            result.append("</div>");
        });
        result.append("</div>");
        return result.toString();
    }

    record IconReference(UUID key, String icon) {
    }
}
