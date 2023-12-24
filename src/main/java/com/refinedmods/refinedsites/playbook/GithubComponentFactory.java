package com.refinedmods.refinedsites.playbook;

import com.refinedmods.refinedsites.model.Component;
import com.refinedmods.refinedsites.model.Version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.vdurmont.semver4j.Semver;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

@Slf4j
class GithubComponentFactory implements ComponentFactory {
    private final Path rootPath;
    private final String name;
    private final List<Tag> validTags = new ArrayList<>();
    private final GHRepository repo;

    GithubComponentFactory(final Path rootPath, final GitHubConfig config, final String name, final String token) {
        try {
            this.rootPath = rootPath;
            this.name = name;
            final GitHub github = GitHub.connectUsingOAuth(token);
            this.repo = github.getRepository(config.getFullRepository());
            final Semver minVersion = new Semver(config.getMinimumVersion());
            for (final var tag : repo.listTags().toList()) {
                final String tagName = tag.getName();
                if (!tagName.startsWith("v")) {
                    log.warn("Ignoring tag {}", tagName);
                    continue;
                }
                final Semver version = new Semver(tagName.substring(1));
                if (version.isGreaterThanOrEqualTo(minVersion)) {
                    log.info("Found valid version {}", version);
                    validTags.add(new Tag(
                        tagName,
                        version.getValue(),
                        "v" + version.getValue(),
                        false
                    ));
                }
            }
            validTags.add(new Tag(
                config.getSnapshotBranch(),
                "snapshot",
                "snapshot",
                true
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Component> getComponents() {
        final List<Component> components = new ArrayList<>();
        for (final Tag validTag : validTags) {
            final String path = "gh-" + UUID.randomUUID();
            log.info("Cloning version {} into {}", validTag, path);
            try {
                Git.cloneRepository()
                    .setURI(repo.getHtmlUrl().toString())
                    .setDirectory(rootPath.resolve(path).toFile())
                    .setBranch(validTag.tagName())
                    .setDepth(1)
                    .call();
            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }
            final LocalComponentFactory factory = new LocalComponentFactory(
                rootPath.resolve(path),
                name,
                false,
                new Version(
                    validTag.version,
                    validTag.friendlyVersion,
                    validTag.snapshot
                )
            );
            components.addAll(factory.getComponents().toList());
        }
        return components.stream();
    }

    private record Tag(String tagName, String version, String friendlyVersion, boolean snapshot) {
    }
}
