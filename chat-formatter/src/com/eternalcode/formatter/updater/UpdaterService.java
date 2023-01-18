package com.eternalcode.formatter.updater;

import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class UpdaterService {

    public CompletableFuture<Boolean> isUpToDate(Plugin plugin) {
        GitCheck gitCheck = new GitCheck();
        GitRepository repository = GitRepository.of("EternalCodeTeam", "ChatFormatter");

        return CompletableFuture.supplyAsync(() -> {
            GitCheckResult result = gitCheck.checkRelease(repository, GitTag.of(plugin.getDescription().getVersion()));

            return result.isUpToDate();
        });
    }

}
