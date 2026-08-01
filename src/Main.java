import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import git.GitService;
import logger.Logger;
import model.GitFile;
import util.Messages;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Logger.info("Starting SYNCAUTO...");
        String configPath = args.length > 0
                ? args[0]
                : System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "config"
                + File.separator + "gitauto.properties";
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
    private static void printModifiedFiles(List<GitFile> files) {
        System.out.println();
        System.out.println(Messages.MODIFIED_FILES_HEADER);
        System.out.println(Messages.DIVIDER);
        int index = 1;
        for (GitFile file : files) {
            System.out.printf("%d. %s%n",
                    index++,
                    file.getPath());
        }
    }
    private static void printStagedFiles(List<String> stagedFiles) {
        System.out.println();
        System.out.println(Messages.STAGED_FILES_HEADER);
        System.out.println(Messages.DIVIDER);
        for (String file : stagedFiles) {
            System.out.println(file);
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
}




            
