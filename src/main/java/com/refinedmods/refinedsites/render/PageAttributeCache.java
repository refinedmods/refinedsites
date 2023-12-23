package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Document;

@RequiredArgsConstructor
public class PageAttributeCache {
    private final Map<Path, PageAttributes> attributes = new HashMap<>();

    public String getName(final Asciidoctor asciidoctor, final Path path) {
        return getAttributes(asciidoctor, path).name;
    }

    public Optional<String> getIcon(final Asciidoctor asciidoctor, final Path path) {
        return getAttributes(asciidoctor, path).icon;
    }

    public PageAttributes getAttributes(final Asciidoctor asciidoctor, final Path path) {
        return attributes.computeIfAbsent(path, p -> doLoadAttributes(asciidoctor, path));
    }

    private PageAttributes doLoadAttributes(final Asciidoctor asciidoctor, final Path path) {
        final Document document = asciidoctor.loadFile(path.toFile(), Options.builder().build());
        return new PageAttributes(
            document.getDoctitle(),
            Optional.ofNullable(document.getAttribute("icon")).map(Object::toString)
        );
    }

    public record PageAttributes(String name, Optional<String> icon) {
    }
}
