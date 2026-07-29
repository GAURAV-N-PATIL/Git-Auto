import config.ConfigManager;
import config.GitAutoConfig;
import logger.Logger;
public class Main{
	public static void main(String arg[]){
		Logger.info("Starting SYNCAUTO...");
		ConfigManager configManager=new ConfigManager();
		try{
			logger.info("Loading configurattion...");
			GitAutoConfig config=configManager.load("config/gitauto.properties");
			Logger.info("Loading loaded successfully.");
			System.out.println();
			System.out.println(config);
			Logger.info("Initialization complete.");
		} catch (Exception e){
			Logger.error("Failed to load configuration.");
			e.printStackTrace();
		}
	}
}
