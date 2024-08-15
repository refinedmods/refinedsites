package com.refinedmods.refinedsites.render;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArticleRender {
    private final String title;
    private final String description;
    private final String url;
    private final LocalDate date;
}
