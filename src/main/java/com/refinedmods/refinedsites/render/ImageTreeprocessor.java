package com.refinedmods.refinedsites.render;

import com.refinedmods.refinedsites.model.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.asciidoctor.ast.Cell;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.Row;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.ast.Table;
import org.asciidoctor.extension.Treeprocessor;
import org.jruby.RubyHash;

@RequiredArgsConstructor
public class ImageTreeprocessor extends Treeprocessor {
    private final Component component;
    private final Map<Path, Path> sourceToDestinationAssets;
    private final Function<Path, Path> pagePathToOutputPathResolver;

    @Override
    public Document process(final Document document) {
        final RubyHash attributes = (RubyHash) document.getOptions().get("attributes");
        final String path = (String) attributes.get("docfile");
        final Path currentPageSourcePath =
            component.getRootPath().resolve(component.getRootPath().toAbsolutePath().relativize(Path.of(path)));
        processBlock(document, currentPageSourcePath);
        return document;
    }

    private void processBlock(final StructuralNode block, final Path currentPageSourcePath) {
        final List<StructuralNode> blocks = block.getBlocks();
        for (final StructuralNode currentBlock : blocks) {
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
            } else if ("table".equals(currentBlock.getContext())) {
                processTable((Table) currentBlock, currentPageSourcePath);
            } else {
                processBlock(currentBlock, currentPageSourcePath);
            }
        }
    }

    private void processTable(final Table table, final Path currentPageSourcePath) {
        for (final Row row : table.getBody()) {
            for (final Cell cell : row.getCells()) {
                cell.setSource(replaceInlineImages(cell.getSource(), currentPageSourcePath));
            }
        }
    }

    private String replaceInlineImages(final String content, final Path currentPageSourcePath) {
        final Pattern pattern = Pattern.compile("image:([^\\[]+)\\[");
        final Matcher matcher = pattern.matcher(content);
        final StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            final String imagePath = matcher.group(1);
            final Path sourcePath = currentPageSourcePath.getParent().resolve(imagePath).normalize();
            final Path destinationPath = sourceToDestinationAssets.get(sourcePath);
            if (destinationPath == null) {
                throw new RuntimeException("Inline asset " + sourcePath + " not found");
            }
            final Path relativePath = pagePathToOutputPathResolver.apply(currentPageSourcePath)
                .getParent().relativize(destinationPath);
            final String replacement = "image:" + relativePath.toString().replace("\\", "/") + "[";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
