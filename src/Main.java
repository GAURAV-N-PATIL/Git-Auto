import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import logger.Logger;
import java.io.IOException;
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
			Logger.success("Loading loaded successfully.");
			System.out.println();
			System.out.println(config);
			Logger.success("Initialization complete.");
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
