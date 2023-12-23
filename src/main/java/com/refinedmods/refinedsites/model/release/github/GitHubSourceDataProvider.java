package com.refinedmods.refinedsites.model.release.github;

import com.refinedmods.refinedsites.model.release.SourceDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;

@RequiredArgsConstructor
@Slf4j
public class GitHubSourceDataProvider implements SourceDataProvider<GitHubSourceData> {
    private final String repositoryName;
    private final String token;

    @Override
    public List<GitHubSourceData> getSourceData() {
        try {
            final GitHub gitHub = GitHub.connectUsingOAuth(token);
            final List<GitHubSourceData> result = new ArrayList<>();
            for (final GHRelease ghRelease : gitHub.getRepository(repositoryName).listReleases()) {
                log.info("Retrieved GitHub release {}", ghRelease.getName());
                result.add(new GitHubSourceData(ghRelease));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
