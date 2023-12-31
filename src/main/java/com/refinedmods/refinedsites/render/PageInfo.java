package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.util.List;

import lombok.Builder;

@Builder
record PageInfo(String title,
                String parsedContent,
                String relativePath,
                String icon,
                IconReferences iconReferences,
                List<TableOfContents> tableOfContents,
                Path pageOutputPath) {
}
