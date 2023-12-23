package com.refinedmods.refinedsites.model.release;

import java.util.List;

@FunctionalInterface
public interface SourceDataProvider<T extends AbstractSourceData> {
    List<T> getSourceData();
}
