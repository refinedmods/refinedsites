package com.refinedmods.refinedsites.render;

import com.refinedmods.refinedsites.model.Component;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.asciidoctor.extension.Name;

@Name("xref")
@RequiredArgsConstructor
class XRefInlineMacroProcessor extends InlineMacroProcessor {
    private final Component component;
    private final Path currentPagePath;
    private final Function<Path, Optional<UUID>> iconResolver;
    private final Function<Path, String> nameResolver;

    @Override
    public Object process(final ContentNode parent, final String target, final Map<String, Object> attributes) {
        final String actualTarget;
        final String anchor;
        if (target.contains("#")) {
            actualTarget = target.substring(0, target.indexOf('#'));
            anchor = target.substring(target.indexOf('#') + 1);
        } else {
            actualTarget = target;
            anchor = null;
        }
        final Path referencedPagePath = currentPagePath.getParent().resolve(actualTarget).normalize();
        final StringBuilder result = new StringBuilder();
        result.append("<a href=\"");
        result.append(getTarget(referencedPagePath));
        if (anchor != null) {
            result.append("#").append(anchor);
        }
        result.append("\"");
        iconResolver.apply(referencedPagePath).ifPresent(iconId -> result
            .append(" data-icon-id=\"")
            .append(iconId)
            .append("\""));
        result.append(">");
        result.append(!attributes.containsKey("1")
            ? nameResolver.apply(referencedPagePath)
            : (String) attributes.get("1"));
        result.append("</a>");
        return result.toString();
    }

    private String getTarget(final Path referencedPagePath) {
        return component.getRelativePagePath(currentPagePath.getParent(), referencedPagePath);
    }
}
