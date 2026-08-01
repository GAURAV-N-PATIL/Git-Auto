import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import git.GitService;
import logger.Logger;
import model.GitFile;
import java.io.IOException;
import java.util.List;
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
			int index=1;
			for(GitFile file:files){
				System.out.printf("%d. [%s] %s%n",index++,file.getStatus(),file.getPath());}
			Logger.success("Initialization complete(phase 2.1).");
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
