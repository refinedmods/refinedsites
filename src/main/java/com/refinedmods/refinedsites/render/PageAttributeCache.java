package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
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
            Optional.ofNullable(document.getAttribute("type")).map(Object::toString).orElse("page"),
            StringEscapeUtils.unescapeHtml4(document.getDoctitle()),
            Optional.ofNullable(document.getAttribute("description"))
                .map(Object::toString)
                .map(StringEscapeUtils::unescapeHtml4)
                .orElse(""),
            Optional.ofNullable(document.getAttribute("date"))
                .map(Object::toString)
                .map(LocalDate::parse),
            Optional.ofNullable(document.getAttribute("icon")).map(Object::toString)
        );
    }

    public record PageAttributes(String type,
                                 String name,
                                 String description,
                                 Optional<LocalDate> date,
                                 Optional<String> icon) {
    }
}
