package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;
import org.jruby.RubyHash;

@RequiredArgsConstructor
public class ImageTreeprocessor extends Treeprocessor {
    private final Map<Path, Path> sourceToDestinationAssets;
    private final Function<Path, Path> pagePathToOutputPathResolver;

    @Override
    public Document process(final Document document) {
        final RubyHash attributes = (RubyHash) document.getOptions().get("attributes");
        final String path = (String) attributes.get("docfile");
        final Path currentPageSourcePath = Path.of(path);
        processBlock(document, currentPageSourcePath);
        return document;
    }

    private void processBlock(final StructuralNode block, final Path currentPageSourcePath) {
        final List<StructuralNode> blocks = block.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            final StructuralNode currentBlock = blocks.get(i);
            if ("image".equals(currentBlock.getContext())) {
                final Path sourcePath = currentPageSourcePath.getParent().resolve(
                    (String) currentBlock.getAttribute("target")
                ).normalize();
                final Path destinationPath = sourceToDestinationAssets.get(sourcePath);
                if (destinationPath == null) {
                    throw new RuntimeException("Asset " + sourcePath + " not found");
                }
                final Path relativePath = pagePathToOutputPathResolver.apply(currentPageSourcePath)
                    .getParent().relativize(destinationPath);
                currentBlock.setAttribute("target", relativePath.toString(), true);
            } else {
                processBlock(currentBlock, currentPageSourcePath);
            }
        }
    }
}
