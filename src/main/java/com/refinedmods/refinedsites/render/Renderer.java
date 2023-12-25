package com.refinedmods.refinedsites.render;

import com.refinedmods.refinedsites.model.Component;
import com.refinedmods.refinedsites.model.NavigationItem;
import com.refinedmods.refinedsites.model.Site;
import com.refinedmods.refinedsites.model.release.Release;
import com.refinedmods.refinedsites.model.release.Releases;
import com.refinedmods.refinedsites.model.release.curseforge.CurseForgeSourceData;
import com.refinedmods.refinedsites.model.release.github.GitHubSourceData;
import com.refinedmods.refinedsites.model.release.modrinth.ModrinthSourceData;
import com.refinedmods.refinedsites.render.release.ParsedRelease;
import com.refinedmods.refinedsites.render.release.ProjectRelease;
import com.refinedmods.refinedsites.render.release.ProjectReleasesIndex;
import com.refinedmods.refinedsites.render.release.ReleasesIndex;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@RequiredArgsConstructor
@Slf4j
public class Renderer {
    private static final Slugify SLUGIFY = Slugify.builder().lowerCase(true).build();
    private static final Gson GSON = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .setPrettyPrinting()
        .create();

    private final TemplateEngine templateEngine;
    private final Path sourcePath;
    private final Path outputPath;
    private final LinkBuilderImpl linkBuilder;
    private final Date renderDate = new Date();
    private final String url;

    public Renderer(final Path sourcePath, final Path outputPath, final String url) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.linkBuilder = new LinkBuilderImpl(outputPath);
        final FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(sourcePath.toString() + "/");
        this.templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.setTemplateResolver(resolver);
        templateEngine.setLinkBuilder(linkBuilder);
        this.url = url;
    }

    public void render(final Site site) {
        log.info("Rendering site {}", site);
        try {
            Files.createDirectories(outputPath);
            final SitemapIndexGenerator sitemapIndex;
            try {
                sitemapIndex = new SitemapIndexGenerator(url, outputPath.resolve("sitemap_index.xml").toFile());
            } catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            }
            final Path assetsPath = copyRootAssets();
            renderReleasesIndex(site);
            for (final Component component : site.getComponents()) {
                renderComponentPre(component);
            }
            for (final Component component : site.getComponents()) {
                renderComponent(component, assetsPath, site, sitemapIndex);
            }
            sitemapIndex.write();
            Files.writeString(outputPath.resolve("robots.txt"), "Sitemap: " + url + "/sitemap_index.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderReleasesIndex(final Site site) {
        try {
            final Path releasesPath = outputPath.resolve("releases.json");
            Files.writeString(releasesPath, GSON.toJson(new ReleasesIndex(
                site.getUrl(),
                site.getReleases().stream().map(Releases::getIndexedAt).sorted().findFirst().orElse(null),
                SLUGIFY,
                site.getReleases()
            )));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path copyRootAssets() throws IOException {
        final Path assetsSourcePath = sourcePath.resolve("assets/");
        final Path assetsDestinationPath = outputPath.resolve("assets/");
        Files.walk(assetsSourcePath).forEach(path -> {
            final Path relativePath = assetsSourcePath.relativize(path);
            final Path targetPath = assetsDestinationPath.resolve(relativePath);
            try {
                Files.createDirectories(targetPath.getParent());
                Files.copy(path, targetPath);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        return assetsDestinationPath;
    }

    private void renderComponentPre(final Component component) {
        final String componentSlug = SLUGIFY.slugify(component.getName());
        component.setSlug(componentSlug);
    }

    private void renderComponent(final Component component,
                                 final Path assetsOutputPath,
                                 final Site site,
                                 final SitemapIndexGenerator sitemapIndex)
        throws IOException {
        log.info("Rendering component {}", component);
        final Path componentOutputPath = getComponentOutputPath(component);
        final String sitemapBaseUrl = component.isRoot() ? url : url + "/" + outputPath.relativize(componentOutputPath);
        final WebSitemapGenerator componentSitemap = getWebSitemapGenerator(
            component,
            sitemapBaseUrl,
            componentOutputPath
        );
        final Map<Path, Path> sourceToDestinationAssets;
        if (component.getAssetsPath() != null) {
            sourceToDestinationAssets = copyComponentAssets(component, assetsOutputPath);
        } else {
            sourceToDestinationAssets = Collections.emptyMap();
        }
        final Releases releases = site.getReleases(component);
        if (component.isLatest() && releases != null) {
            renderComponentReleases(releases, site, componentOutputPath);
        }
        final List<ParsedRelease> parsedReleases = releases == null ? Collections.emptyList() : parseReleases(releases);
        final ParsedRelease releaseMatchingComponentVersion = findReleaseMatchingComponentVersion(
            component,
            parsedReleases
        );
        final PageAttributeCache pageAttributeCache = new PageAttributeCache();
        final Map<Path, PageInfo> pageInfo = new HashMap<>();
        for (final Path pagePath : component.getPages()) {
            pageInfo.put(pagePath, renderPagePre(
                pagePath,
                component,
                pageAttributeCache,
                sourceToDestinationAssets,
                componentOutputPath
            ));
        }
        prepareNavigationItems(component.getNavigationItems(), pageInfo);
        for (final Path pagePath : component.getPages()) {
            renderPage(
                pagePath,
                component,
                componentOutputPath,
                assetsOutputPath,
                site,
                pageInfo,
                parsedReleases,
                releaseMatchingComponentVersion,
                sitemapBaseUrl,
                componentSitemap
            );
        }
        if (componentSitemap != null) {
            componentSitemap.write();
            sitemapIndex.addUrl(sitemapBaseUrl + "/sitemap.xml", renderDate);
        }
    }

    private Path getComponentOutputPath(final Component component) throws IOException {
        final Path componentOutputPath;
        if (component.isRoot()) {
            componentOutputPath = outputPath;
        } else if (component.isLatest()) {
            componentOutputPath = outputPath.resolve(component.getSlug());
            Files.createDirectories(componentOutputPath);
        } else {
            componentOutputPath = outputPath.resolve(component.getSlug()).resolve(
                component.getVersion().friendlyName()
            );
            Files.createDirectories(componentOutputPath);
        }
        return componentOutputPath;
    }

    @Nullable
    private WebSitemapGenerator getWebSitemapGenerator(final Component component,
                                                       final String sitemapBaseUrl,
                                                       final Path componentOutputPath) {
        if (component.getPages().isEmpty()) {
            return null;
        }
        if (component.isLatest()) {
            try {
                return new WebSitemapGenerator(sitemapBaseUrl, componentOutputPath.toFile());
            } catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Nullable
    private ParsedRelease findReleaseMatchingComponentVersion(final Component component,
                                                              final List<ParsedRelease> parsedReleases) {
        return parsedReleases.stream()
            .filter(release -> release.getRelease().getName().equals(component.getVersion().friendlyName()))
            .findFirst()
            .orElse(null);
    }

    private void renderComponentReleases(final Releases releases,
                                         final Site site,
                                         final Path componentOutputPath) {
        try {
            final Path releasesIndexPath = componentOutputPath.resolve("releases.json");
            Files.writeString(releasesIndexPath, GSON.toJson(new ProjectReleasesIndex(
                site.getUrl(),
                releases.getIndexedAt(),
                SLUGIFY,
                releases
            )));
            final Path releasesPath = componentOutputPath.resolve("releases/");
            Files.createDirectories(releasesPath);
            for (final Release release : releases.getReleases()) {
                final Path releasePath = releasesPath.resolve(release.getName() + ".json");
                Files.writeString(releasePath, GSON.toJson(new ProjectRelease(
                    site.getUrl() + "/" + SLUGIFY.slugify(releases.getComponentName())
                        + "/releases/" + release.getName() + ".json",
                    release.getName(),
                    releases.getIndexedAt(),
                    release.getType(),
                    release.getSourceData(),
                    release.getStats()
                )));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ParsedRelease> parseReleases(final Releases releases) {
        final Parser markdownParser = Parser.builder().build();
        final HtmlRenderer renderer = HtmlRenderer.builder().build();
        return releases.getReleases().stream().map(release -> new ParsedRelease(
            release,
            SLUGIFY.slugify(release.getName()),
            release.getSourceData().stream().filter(sd -> sd instanceof CurseForgeSourceData)
                .map(sd -> ((CurseForgeSourceData) sd).getHtmlUrl())
                .findFirst()
                .orElse(null),
            release.getSourceData().stream().filter(sd -> sd instanceof GitHubSourceData)
                .map(sd -> ((GitHubSourceData) sd).getHtmlUrl())
                .findFirst()
                .orElse(null),
            release.getSourceData().stream().filter(sd -> sd instanceof ModrinthSourceData)
                .map(sd -> ((ModrinthSourceData) sd).getHtmlUrl())
                .findFirst()
                .orElse(null),
            release.getSourceData().stream().filter(sd -> sd instanceof GitHubSourceData)
                .map(sd -> ((GitHubSourceData) sd).getBody())
                .map(body -> renderer.render(markdownParser.parse(body)))
                .findFirst()
                .orElse(null)
        )).toList();
    }

    private void prepareNavigationItems(final List<NavigationItem> navigationItems,
                                        final Map<Path, PageInfo> pageInfo) {
        navigationItems.forEach(navigationItem -> {
            if (navigationItem.getPath() != null && navigationItem.getName() == null) {
                final Path navigationItemPath = navigationItem.getPath();
                final PageInfo info = pageInfo.get(navigationItemPath);
                navigationItem.setName(info.title());
            }
            if (navigationItem.getChildren() != null) {
                prepareNavigationItems(navigationItem.getChildren(), pageInfo);
            }
        });
    }

    private Map<Path, Path> copyComponentAssets(final Component component,
                                                final Path assetsOutputPath) throws IOException {
        if (!Files.exists(component.getAssetsPath())) {
            return Collections.emptyMap();
        }
        final Path componentAssetsPath = assetsOutputPath.resolve(
            component.getAssetsOutputPath() + "/"
        );
        final Map<Path, Path> sourceToDestinationAssets = new HashMap<>();
        Files.walk(component.getAssetsPath()).forEach(path -> {
            final Path relativePath = component.getAssetsPath().relativize(path);
            final Path targetPath = componentAssetsPath.resolve(relativePath);
            try {
                Files.createDirectories(targetPath.getParent());
                Files.copy(path, targetPath);
                sourceToDestinationAssets.put(path.normalize(), targetPath);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        return sourceToDestinationAssets;
    }

    private PageInfo renderPagePre(final Path pagePath,
                                   final Component component,
                                   final PageAttributeCache attributeCache,
                                   final Map<Path, Path> sourceToDestinationAssets,
                                   final Path componentOutputPath) {
        log.info("Parsing Asciidoc for page {}", pagePath);
        final String relativePath = component.getRelativePagePath(component.getPagesPath(), pagePath);
        final Path pageOutputPath = componentOutputPath.resolve(relativePath);
        final IconReferences icons = new IconReferences();
        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
            final PageAttributeCache.PageAttributes pageAttributes = attributeCache.getAttributes(
                asciidoctor,
                pagePath
            );
            asciidoctor.javaExtensionRegistry().includeProcessor(new IncludeProcessorImpl());
            asciidoctor.javaExtensionRegistry().treeprocessor(new ImageTreeprocessor(
                pagePath,
                pageOutputPath,
                sourceToDestinationAssets
            ));
            asciidoctor.javaExtensionRegistry().inlineMacro(new XRefInlineMacroProcessor(
                component,
                pagePath,
                icons.createResolver(asciidoctor, attributeCache),
                path -> attributeCache.getName(asciidoctor, path)
            ));
            final Document document = asciidoctor.loadFile(pagePath.toFile(), Options.builder().build());
            final List<TableOfContents> toc = document.getBlocks()
                .stream()
                .filter(block -> block.getTitle() != null)
                .map(this::getTableOfContents)
                .toList();
            final String parsedContent = (String) document.getContent();
            return PageInfo.builder()
                .title(pageAttributes.name())
                .tableOfContents(toc)
                .iconReferences(icons)
                .parsedContent(parsedContent
                    .replace("<table class=\"", "<table class=\"table table-striped table-bordered "))
                .relativePath(relativePath)
                .icon(pageAttributes.icon().orElse(null))
                .pageOutputPath(pageOutputPath)
                .build();
        }
    }

    private TableOfContents getTableOfContents(final StructuralNode block) {
        final TableOfContents tableOfContents = new TableOfContents(block.getTitle(), block.getId());
        for (final StructuralNode subBlock : block.getBlocks()) {
            if (subBlock.getTitle() != null) {
                tableOfContents.getChildren().add(getTableOfContents(subBlock));
            }
        }
        return tableOfContents;
    }

    private void renderPage(final Path pagePath,
                            final Component component,
                            final Path componentOutputPath,
                            final Path assetsOutputPath,
                            final Site site,
                            final Map<Path, PageInfo> pageInfo,
                            final List<ParsedRelease> releases,
                            final ParsedRelease releaseMatchingComponentVersion,
                            final String baseUrl,
                            @Nullable final WebSitemapGenerator sitemapGenerator) throws IOException {
        log.info("Rendering page {}", pagePath);
        final Context context = new Context();
        final PageInfo info = pageInfo.get(pagePath);
        Files.createDirectories(info.pageOutputPath().getParent());
        final FileWriter fileWriter = new FileWriter(info.pageOutputPath().toFile());
        context.setVariable("title", info.title());
        context.setVariable("icon", info.icon());
        final String iconsHtml = info.iconReferences().getHtml(
            assetsOutputPath.resolve(component.getAssetsOutputPath() + "/"),
            info.pageOutputPath()
        );
        context.setVariable("toc", info.tableOfContents());
        context.setVariable("content", info.parsedContent() + iconsHtml);
        context.setVariable("currentComponent", component);
        context.setVariable("otherComponents", site.getComponents(component));
        context.setVariable("navigationItems", component.getNavigationItems().stream().map(
            item -> mapNavigationItem(item, info, pageInfo)
        ).toList());
        context.setVariable("breadcrumbs", getBreadcrumbsTopLevel(component, info, pageInfo));
        context.setVariable("currentRelease", releaseMatchingComponentVersion);
        final List<ParsedRelease> otherReleases = releases.stream().filter(r -> r != releaseMatchingComponentVersion)
            .collect(Collectors.toList());
        Collections.reverse(otherReleases);
        context.setVariable("otherReleases", otherReleases);
        linkBuilder.setCurrentPageOutputPath(info.pageOutputPath());
        final String template = getTemplate(pagePath);
        templateEngine.process(template, context, fileWriter);
        if (sitemapGenerator != null && !info.relativePath().contains("404")) {
            sitemapGenerator.addUrl(new WebSitemapUrl.Options(
                baseUrl + "/" + componentOutputPath.relativize(info.pageOutputPath())
            ).lastMod(renderDate).changeFreq(ChangeFreq.DAILY).build());
        }
    }

    private String getTemplate(final Path pagePath) {
        final Path potentialTemplateOverridePath = Path.of(
            pagePath.toString().replace(".adoc", ".html")
        );
        if (!Files.exists(potentialTemplateOverridePath)) {
            return "page.html";
        }
        return sourcePath.relativize(potentialTemplateOverridePath).toString();
    }

    private List<Breadcrumb> getBreadcrumbsTopLevel(final Component component,
                                                    final PageInfo currentPageInfo,
                                                    final Map<Path, PageInfo> pageInfo) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<>();
        for (final NavigationItem item : component.getNavigationItems()) {
            if (getBreadcrumbs(currentPageInfo, item, breadcrumbs, pageInfo)) {
                break;
            }
        }
        return breadcrumbs;
    }

    private boolean getBreadcrumbs(final PageInfo currentPageInfo,
                                   final NavigationItem item,
                                   final List<Breadcrumb> breadcrumbs,
                                   final Map<Path, PageInfo> pageInfo) {
        if (isContainedInItem(item, currentPageInfo, pageInfo)) {
            return getBreadcrumbsContained(currentPageInfo, item, breadcrumbs, pageInfo);
        }
        return false;
    }

    private boolean getBreadcrumbsContained(final PageInfo currentPageInfo,
                                            final NavigationItem item,
                                            final List<Breadcrumb> breadcrumbs,
                                            final Map<Path, PageInfo> pageInfo) {
        final PageInfo itemPageInfo = pageInfo.get(item.getPath());
        final Breadcrumb breadcrumb = new Breadcrumb(
            item.getName(),
            itemPageInfo.relativePath(),
            item.getIcon()
        );
        breadcrumbs.add(breadcrumb);
        breadcrumb.setActive(true);
        if (item.getChildren() != null) {
            for (final NavigationItem child : item.getChildren()) {
                if (getBreadcrumbs(currentPageInfo, child, breadcrumbs, pageInfo)) {
                    breadcrumb.setActive(false);
                    break;
                }
            }
        }
        return true;
    }

    private boolean isContainedInItem(final NavigationItem item,
                                      final PageInfo currentPageInfo,
                                      final Map<Path, PageInfo> pageInfo) {
        final PageInfo navigationItemPageInfo = pageInfo.get(
            item.getPath()
        );
        if (navigationItemPageInfo == currentPageInfo) {
            return true;
        }
        if (item.getChildren() != null) {
            for (final NavigationItem child : item.getChildren()) {
                if (isContainedInItem(child, currentPageInfo, pageInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private NavigationItemRender mapNavigationItem(final NavigationItem navigationItem,
                                                   final PageInfo currentPageInfo,
                                                   final Map<Path, PageInfo> pageInfo) {
        final PageInfo navigationItemPageInfo = pageInfo.get(navigationItem.getPath());
        final List<NavigationItemRender> children = navigationItem.getChildren() == null
            ? null
            : navigationItem.getChildren().stream()
            .map(nestedItem -> mapNavigationItem(nestedItem, currentPageInfo, pageInfo))
            .toList();
        final boolean active = currentPageInfo == navigationItemPageInfo
            || (children != null && children.stream().anyMatch(NavigationItemRender::isActive));
        return new NavigationItemRender(
            navigationItem.getName(),
            SLUGIFY.slugify(navigationItem.getName()),
            navigationItemPageInfo == null ? null : navigationItemPageInfo.relativePath(),
            children,
            UUID.randomUUID().toString(),
            navigationItem.getIcon() != null
                ? navigationItem.getIcon()
                : (navigationItemPageInfo == null ? null : navigationItemPageInfo.icon()),
            active
        );
    }
}
