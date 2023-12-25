package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;

@RequiredArgsConstructor
public class ImageTreeprocessor extends Treeprocessor {
    private final Path currentPageSourcePath;
    private final Path currentPageOutputPath;
    private final Map<Path, Path> sourceToDestinationAssets;

    @Override
    public Document process(final Document document) {
        processBlock(document);
        return document;
    }

    private void processBlock(final StructuralNode block) {
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
                final Path relativePath = currentPageOutputPath.getParent().relativize(destinationPath);
                currentBlock.setAttribute("target", relativePath.toString(), true);
            } else {
                processBlock(currentBlock);
            }
        }
    }
}
