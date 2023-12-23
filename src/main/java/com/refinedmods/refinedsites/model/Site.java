package com.refinedmods.refinedsites.model;

import com.refinedmods.refinedsites.model.release.Releases;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vdurmont.semver4j.Semver;
import lombok.Getter;
import lombok.ToString;

@ToString(exclude = "componentsByName")
public class Site {
    @Getter
    private final String name;
    @Getter
    private final String url;
    @Getter
    private final List<Component> components;
    private final Map<String, List<Component>> componentsByName;
    private final Map<String, Releases> releasesByComponentName;

    public Site(final String name,
                final String url,
                final List<Component> components,
                final Map<String, List<Component>> componentsByName,
                final Map<String, Releases> releasesByComponentName) {
        this.name = name;
        this.url = url;
        this.components = components;
        this.componentsByName = componentsByName;
        componentsByName.forEach((componentName, componentsWithSameName) -> sortComponents(componentsWithSameName));
        this.releasesByComponentName = releasesByComponentName;
    }

    public List<Component> getComponents(final Component component) {
        return componentsByName.get(component.getName());
    }

    public Releases getReleases(final Component component) {
        return releasesByComponentName.get(component.getName());
    }

    public Collection<Releases> getReleases() {
        return releasesByComponentName.values();
    }

    private void sortComponents(final List<Component> componentsWithSameName) {
        // sort componentsWithSameName based on NEWEST-first semver version
        componentsWithSameName.sort((component1, component2) -> {
            if (component1.getVersion().snapshot()) {
                return -1;
            }
            final Semver semver1 = new Semver(component1.getVersion().name());
            final Semver semver2 = new Semver(component2.getVersion().name());
            return semver2.compareTo(semver1);
        });
        componentsWithSameName.get(0).setLatest(true);
    }
}
