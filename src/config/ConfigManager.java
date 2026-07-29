package config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ConfigManager{
	public GitAutoConfig load(String configPath) throws IOException{
		Properties properties = new Properties();
		try(FileInputStream fis=new FileInputStream(configPath)){
			properties.load(fis);
		}
		GitAutoConfig config=new GitAutoConfig();
		config.setWatchPath(properties.getProperty("watch.path"));
		config.setIdleTime(Integer.parseInt(properties.getProperty("idle.time")));
		config.setAutoCommit(Boolean.parseBoolean(properties.getProperty("auto.commit")));
		config.setAutoPush(Boolean.parseBoolean(properties.getProperty("auto.push")));
		config.setGitBranch(properties.getProperty("git.branch"));
		config.setLogLevel(properties.getProperty("log.level"));
		return config;
	}
}
