package com.refinedmods.refinedsites.render;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TableOfContents {
    private final String title;
    private final String id;
    private final List<TableOfContents> children = new ArrayList<>();
}
