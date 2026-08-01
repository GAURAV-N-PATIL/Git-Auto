import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import git.GitService;
import logger.Logger;
import model.GitFile;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
public class Main{
	public static void main(String[] args){
		Logger.info("Starting SYNCAUTO...");
		String configPath=args.length>0
			?args[0]
			:"/home/gauravpatil/Documents/SYNC/src/config/gitauto.properties";
		ConfigManager configManager=new ConfigManager();
		try{
			Logger.info("Loading configurattion...");
			GitAutoConfig config=configManager.load(configPath);
			Logger.success("Configuration loaded successfully.");
			GitService gitService = new GitService();
			Logger.info("Checking Git repository....");
			if(!gitService.isGitRepository()){
				Logger.error("Current directory is not a Git repository.");
				return;}
			Logger.success("Git repository detected.");
			Logger.info("Scanning modified files...");
			List<GitFile> files=gitService.getModifiedFiles();
			if(files.isEmpty()){
				Logger.info("No modified files found.");
				return;}
			System.out.println();
			System.out.println("Modified Files");
			System.out.println("-------------------------------");
			int index = 1;
			for(GitFile file : files){System.out.printf("%d. %s%n",index++,file.getPath());}
			System.out.println();
			System.out.println("Enter '.' to stage all");
			System.out.println("or file numbers separated by commas.");
			System.out.print("> ");
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine().trim();
			boolean staged = false;
			if(input.equals(".")) {
    				staged = gitService.stageAll();} 
			else{
    				String[] selections = input.split(",");
    				List<String> selectedFiles = new ArrayList<>();
    				for (String selection : selections) {
        				int selectedIndex =Integer.parseInt(selection.trim())-1;
        				if (selectedIndex >= 0&&selectedIndex < files.size()){
            					selectedFiles.add(files.get(selectedIndex).getPath());
					}
				}
				staged = gitService.stageFiles(selectedFiles);
			}
			if (!staged){
    				Logger.error("Failed to stage files.");
    				return;
			}
			Logger.success("Files staged successfully.");
			List<String> stagedFiles=gitService.getStagedFiles();
			if (stagedFiles.isEmpty()){
    				Logger.error("No staged files found.");
    				return;
			}
			System.out.println();
			System.out.println("Staged Files");
			System.out.println("------------------------");
			for (String file : stagedFiles){
    			System.out.println(file);
			}
			Logger.success("Initialization complete).");
		} catch (InvalidConfigException e){
			Logger.error("Failed to load configuration.");
			Logger.error(e.getMessage());
		}
		catch (IOException e){
			Logger.error("Unable to read configuration files.");
			Logger.error(e.getMessage());
		}
		catch (NumberFormatException e) {
        	    	Logger.error("Configuration contains invalid numbers.");
         	   	Logger.error(e.getMessage());
        	}
        	catch (Exception e) {
            		Logger.error("Unexpected Error");
            		e.printStackTrace();
       	 	}
	}
}
