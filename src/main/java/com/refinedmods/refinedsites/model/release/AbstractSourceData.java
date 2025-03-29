package com.refinedmods.refinedsites.model.release;

import java.util.Date;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AbstractSourceData {
    protected final String source;
    protected final transient String name;
    protected final String url;
    @Nullable
    protected final Platform platform;
    protected Date createdAt;

    public abstract long getDownloads();
}
