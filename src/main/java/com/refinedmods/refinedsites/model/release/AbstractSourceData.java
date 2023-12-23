package com.refinedmods.refinedsites.model.release;

import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AbstractSourceData {
    protected final String source;
    protected final transient String name;
    protected final String url;
    protected Date createdAt;

    public abstract long getDownloads();
}
