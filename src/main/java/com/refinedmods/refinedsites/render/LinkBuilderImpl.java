package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.Map;

import lombok.Setter;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.ILinkBuilder;

public class LinkBuilderImpl implements ILinkBuilder {
    private final Path outputPath;
    @Setter
    private Path currentPageOutputPath;

    public LinkBuilderImpl(final Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String getName() {
        return "refinedsites";
    }

    @Override
    public Integer getOrder() {
        return 1;
    }

    @Override
    public String buildLink(
        final IExpressionContext context,
        final String base,
        final Map<String, Object> parameters
    ) {
        final Path assetLocation = outputPath.resolve(base);
        return currentPageOutputPath.getParent().relativize(assetLocation).toString();
    }
}
