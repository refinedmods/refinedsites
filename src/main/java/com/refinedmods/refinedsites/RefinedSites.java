package com.refinedmods.refinedsites;

import com.refinedmods.refinedsites.model.Site;
import com.refinedmods.refinedsites.playbook.SiteFactory;
import com.refinedmods.refinedsites.render.Renderer;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefinedSites {
    private RefinedSites() {
    }

    public static void main(final String[] args) {
        log.info("Loading playbook from {}", args[0]);
        final Path rootPath = Paths.get(args[0]);
        final SiteFactory siteFactory = new SiteFactory(rootPath);
        final Site site = siteFactory.getSite();
        log.info("Loaded site {}", site);
        final Renderer renderer = new Renderer(rootPath, rootPath.resolve("output/"));
        renderer.render(site);
        log.info("Done!");
    }
}
