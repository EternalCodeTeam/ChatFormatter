package com.eternalcode.formatter.updater;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.concurrent.CompletableFuture;

public class UpdaterService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("EternalCodeTeam", "ChatFormatter");

    private final GitCheck gitCheck = new GitCheck();
    private final CompletableFuture<GitCheckResult> gitCheckResult;

    public UpdaterService(PluginDescriptionFile descriptionFile) {
        this.gitCheckResult = CompletableFuture.supplyAsync(() -> {
            String version = descriptionFile.getVersion();
            return this.gitCheck.checkRelease(GIT_REPOSITORY, GitTag.of("v" + version));
        });
    }

    public CompletableFuture<Boolean> isUpToDate() {
        return this.gitCheckResult.thenApply(GitCheckResult::isUpToDate);
    }

}