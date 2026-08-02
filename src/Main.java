import ai.AIException;
import ai.AIManager;
import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import git.GitResult;
import git.GitService;
import logger.Logger;
import model.GitFile;
import scheduler.CommitScheduler;
import utils.Messages;
import ui.ConsoleUI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main{
    private static final String DEFAULT_CONFIG_PATH =
            System.getProperty("user.home")
            + File.separator + ".gitauto"
            + File.separator + "gitauto.properties";
    public static void main(String[] args) {
	ConsoleUI.banner();
        Logger.info(Messages.STARTING);
        String configPath = args.length > 0 ? args[0] : DEFAULT_CONFIG_PATH;
        if (args.length == 0) {
            ensureDefaultConfigExists(configPath);
        }
        ConfigManager configManager=new ConfigManager();
        try{
            Logger.info(Messages.LOADING_CONFIG);
            GitAutoConfig config=configManager.load(configPath);
            Logger.success(Messages.CONFIG_SUCCESS);
            GitService gitService=new GitService();
            Logger.info(Messages.CHECKING_REPOSITORY);
            if (!gitService.isGitRepository()){ 
                Logger.error(Messages.NOT_A_REPOSITORY);
                return;
            }
            Logger.success(Messages.REPOSITORY_DETECTED);

            if (config.isAutoCommit() && confirmAutoMode(config)) {
                CommitScheduler scheduler = new CommitScheduler(gitService, config);
                Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));
                scheduler.start();
                return;
            }

            Logger.info(Messages.SCANNING_FILES);
            List<GitFile> files=gitService.getModifiedFiles();
            if (files.isEmpty()){
                Logger.info(Messages.NO_MODIFIED_FILES);
                return;
            }
	    printModifiedFiles(files);
            boolean staged = stageFiles(gitService, files);
            if (!staged) {
                Logger.error(Messages.STAGING_FAILED);
                return;
            }
            Logger.success(Messages.STAGING_SUCCESS);
            List<String> stagedFiles = gitService.getStagedFiles();
            if (stagedFiles.isEmpty()) {
                Logger.error(Messages.NO_STAGED_FILES);
                return;
            }
            printStagedFiles(stagedFiles);       
	    System.out.println();
	    System.out.println("Repository : "+new File(System.getProperty("user.dir")).getName());
            String currentBranch = gitService.getCurrentBranch();
            System.out.println("Branch     : "+currentBranch);
            System.out.println("Files      : "+stagedFiles.size()+" staged");

            String commitMessage = resolveCommitMessage(gitService, stagedFiles);
            if (commitMessage == null) {
                Logger.info(Messages.COMMIT_CANCELLED);
                return;
            }
            Logger.info(Messages.CREATING_COMMIT);
            GitResult commitResult = gitService.commit(commitMessage);
            if (!commitResult.isSuccess()) {
                Logger.error(Messages.COMMIT_FAILED);
                Logger.error(commitResult.getOutput());
                return;
            }
            Logger.success(Messages.COMMIT_SUCCESS);
            promptAndPush(gitService, currentBranch, config);
            Logger.success(Messages.INITIALIZATION_COMPLETE);
        } catch (InvalidConfigException e) {
            Logger.error(Messages.CONFIG_LOAD_FAILED);
            Logger.error(e.getMessage());
        } catch (IOException e) {
            Logger.error(Messages.CONFIG_READ_FAILED);
            Logger.error(e.getMessage());
        } catch (Exception e) {
            Logger.error(Messages.UNEXPECTED_ERROR);
            Logger.error(e.getMessage());
        }
    }
    private static void ensureDefaultConfigExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        String defaults = """
                # Git-Auto configuration
                # Auto-generated on first run. Edit anytime -- no rebuild needed.
                # Location: ~/.gitauto/gitauto.properties (shared by every repo)

                # Currently unused -- Git-Auto always operates on whichever
                # repo you run it from, not a fixed path.
                watch.path=unused

                # Seconds of no repo changes before an auto-commit fires
                # (auto mode only -- see auto.commit below)
                idle.time=300

                # If true, Git-Auto offers auto mode on startup in every repo.
                # Starts OFF by default since this config applies everywhere.
                auto.commit=false

                # If true, auto mode also pushes after each auto-commit.
                auto.push=false

                # Branch to push to when none is checked out
                git.branch=main

                # Log verbosity
                log.level=INFO
                """;
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(defaults);
        } catch (IOException e) {
            Logger.error("Unable to create default config at " + path);
        }
    }
    private static boolean confirmAutoMode(GitAutoConfig config) {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println(Messages.autoModePrompt(config.getIdleTime(), config.isAutoPush()));
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("y") || input.equals("yes")) {
            return true;
        }
        Logger.info(Messages.AUTO_MODE_DECLINED);
        return false;
    }
    private static void printModifiedFiles(List<GitFile> files) {
        System.out.println();
        System.out.println(Messages.MODIFIED_FILES_HEADER);
        System.out.println(Messages.DIVIDER);
        int index = 1;
        for (GitFile file : files) {
            System.out.printf("[%d] %s%n",
                    index++,
                    file.getPath());
        }
    }
    private static void printStagedFiles(List<String> stagedFiles) {
        System.out.println();
        System.out.println(Messages.STAGED_FILES_HEADER);
        System.out.println(Messages.DIVIDER);
        for (String file : stagedFiles){
		System.out.printf("✓ %s%n", file);
	}
    }
    private static boolean stageFiles(GitService gitService,List<GitFile> files) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println(Messages.ENTER_STAGE_PROMPT);
            System.out.println(Messages.ENTER_FILE_NUMBERS);
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equals(".")) {
                return gitService.stageAll();
            }
            String[] selections = input.split(",");
            List<String> selectedFiles = new ArrayList<>();
            boolean valid = true;
            for (String selection : selections) {
                selection = selection.trim();
                try {
                    int index = Integer.parseInt(selection) - 1;
                    if (index < 0 || index>=files.size()){
                        Logger.error(Messages.invalidFileNumber(index + 1));                        	      valid = false;
                        break;
                    }
                    selectedFiles.add(files.get(index).getPath());
                } catch (NumberFormatException e){
		    Logger.error(Messages.invalidSelection(selection));
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                Logger.info(Messages.TRY_AGAIN);
                continue;
            }
            return gitService.stageFiles(selectedFiles);
        }
    }
    private static String resolveCommitMessage(GitService gitService, List<String> stagedFiles) {
        Scanner scanner = new Scanner(System.in);
        AIManager aiManager = new AIManager();
        String diff = gitService.getStagedDiff();
        while (true) {
            String suggestion = null;
            Logger.info(Messages.GENERATING_COMMIT_MESSAGE);
            try {
                suggestion = aiManager.generateCommitMessage(stagedFiles, diff);
            } catch (AIException e) {
                Logger.error(Messages.AI_GENERATION_FAILED);
                Logger.error(e.getMessage());
            } catch (IOException e) {
                Logger.error(Messages.AI_CONFIG_READ_FAILED);
                Logger.error(e.getMessage());
            }
            if (suggestion == null || suggestion.isBlank()) {
                Logger.info(Messages.AI_UNAVAILABLE);
                return readCustomMessage(scanner);
            }
            System.out.println();
            System.out.println(Messages.COMMIT_MESSAGE_HEADER);
            System.out.println(Messages.DIVIDER);
            System.out.println(suggestion);
            System.out.println(Messages.DIVIDER);
            System.out.println();
            System.out.println("1. Commit with this message");
            System.out.println("2. Regenerate");
            System.out.println("3. Write my own message");
            System.out.println("4. Cancel");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return suggestion;
                case "2":
                    continue;
                case "3":
                    return readCustomMessage(scanner);
                case "4":
                    return null;
                default:
                    Logger.error("Invalid option.");
            }
        }
    }
    private static String readCustomMessage(Scanner scanner) {
        System.out.println();
        System.out.println(Messages.ENTER_CUSTOM_MESSAGE);
        StringBuilder message = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                break;
            }
            if (message.length() > 0) {
                message.append("\n");
            }
            message.append(line);
        }
        String result = message.toString().trim();
        return result.isEmpty() ? null : result;
    }
    private static void promptAndPush(GitService gitService, String branch, GitAutoConfig config) {
        Scanner scanner = new Scanner(System.in);
        if (!gitService.hasRemote()) {
            Logger.error(Messages.NO_REMOTE);
            return;
        }
        String targetBranch = (branch == null || branch.isBlank())
                ? config.getGitBranch()
                : branch;
        System.out.println();
        System.out.println(Messages.pushPrompt(targetBranch));
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (!input.equals("y") && !input.equals("yes")) {
            Logger.info(Messages.PUSH_SKIPPED);
            return;
        }
        Logger.info(Messages.PUSHING);
        GitResult result = gitService.push("origin", targetBranch);
        if (result.isSuccess()) {
            Logger.success(Messages.PUSH_SUCCESS);
        } else {
            Logger.error(Messages.PUSH_FAILED);
            Logger.error(result.getOutput());
        }
    }
}
