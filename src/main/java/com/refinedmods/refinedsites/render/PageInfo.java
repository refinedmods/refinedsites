package com.refinedmods.refinedsites.render;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.Builder;

@Builder
record PageInfo(String type,
                String title,
                String description,
                String parsedContent,
                String relativePath,
                String icon,
                Optional<LocalDate> date,
                IconReferences iconReferences,
                List<TableOfContents> tableOfContents,
                Path pageOutputPath) {
}
