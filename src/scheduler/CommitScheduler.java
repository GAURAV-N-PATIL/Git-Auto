package scheduler;

import ai.AIException;
import ai.AIManager;
import config.GitAutoConfig;
import git.GitResult;
import git.GitService;
import logger.Logger;
import model.GitFile;
import java.io.IOException;
import java.util.List;
public class CommitScheduler {
    private static final long POLL_INTERVAL_MS = 5000;
    private final GitService gitService;
    private final AIManager aiManager;
    private final GitAutoConfig config;
    private final IdleTimer idleTimer;
    private volatile boolean running;
    public CommitScheduler(GitService gitService, GitAutoConfig config) {
        this.gitService = gitService;
        this.config = config;
        this.aiManager = new AIManager();
        this.idleTimer = new IdleTimer();
    }
    public void start() {
        running = true;
        Logger.info("Auto mode active. Watching for changes every "
                + (POLL_INTERVAL_MS / 1000) + "s. Press Ctrl+C to stop.");
        List<GitFile> lastSeen = gitService.getModifiedFiles();
        idleTimer.recordActivity();
        while (running) {
            if (!sleep(POLL_INTERVAL_MS)) {
                break;
            }
            List<GitFile> current = gitService.getModifiedFiles();
            if (!sameFiles(lastSeen, current)) {
                idleTimer.recordActivity();
                lastSeen = current;
                continue;
            }
            if (current.isEmpty()) {
                continue;
            }
            if (config.isAutoCommit() && idleTimer.isIdleFor(config.getIdleTime())) {
                runAutoCommitCycle();
                lastSeen = gitService.getModifiedFiles();
                idleTimer.recordActivity();
            }
        }
        Logger.info("Auto mode stopped.");
    }
    public void stop() {
        running = false;
    }
    private void runAutoCommitCycle() {
        Logger.info("Idle for " + config.getIdleTime()
                + "s with pending changes. Auto-committing...");
        if (!gitService.stageAll()) {
            Logger.error("Auto-stage failed.");
            return;
        }
        List<String> stagedFiles = gitService.getStagedFiles();
        if (stagedFiles.isEmpty()) {
            return;
        }
        String message = resolveCommitMessage(stagedFiles);
        GitResult commitResult = gitService.commit(message);
        if (!commitResult.isSuccess()) {
            Logger.error("Auto-commit failed.");
            Logger.error(commitResult.getOutput());
            return;
        }
        Logger.success("Auto-committed: " + message);
        if (config.isAutoPush()) {
            pushAutomatically();
        } else {
            Logger.info("auto.push is disabled — commit is local only.");
        }
    }
    private String resolveCommitMessage(List<String> stagedFiles) {
        String diff = gitService.getStagedDiff();
        try {
            String suggestion = aiManager.generateCommitMessage(stagedFiles, diff);
            if (suggestion != null && !suggestion.isBlank()) {
                return suggestion;
            }
        } catch (AIException e) {
            Logger.error("AI commit message generation failed: " + e.getMessage());
        } catch (IOException e) {
            Logger.error("Unable to read AI configuration: " + e.getMessage());
        }
        return fallbackMessage(stagedFiles);
    }
    private String fallbackMessage(List<String> stagedFiles) {
        return "chore: update " + stagedFiles.size()
                + (stagedFiles.size() == 1 ? " file" : " files");
    }
    private void pushAutomatically() {
        if (!gitService.hasRemote()) {
            Logger.error("No 'origin' remote configured; skipping push.");
            return;
        }
        String branch = gitService.getCurrentBranch();
        String targetBranch = (branch == null || branch.isBlank())
                ? config.getGitBranch()
                : branch;
        Logger.info("Auto-pushing to " + targetBranch + "...");
        GitResult result = gitService.push("origin", targetBranch);
        if (result.isSuccess()) {
            Logger.success("Auto-push complete.");
        } else {
            Logger.error("Auto-push failed.");
            Logger.error(result.getOutput());
        }
    }
    private boolean sameFiles(List<GitFile> a, List<GitFile> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            GitFile fa = a.get(i);
            GitFile fb = b.get(i);
            if (!fa.getPath().equals(fb.getPath())
                    || !fa.getStatus().equals(fb.getStatus())) {
                return false;
            }
        }
        return true;
    }
    private boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
            return false;
        }
    }
}
