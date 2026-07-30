package config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ConfigManager {
    public GitAutoConfig load(String path)
            throws IOException, InvalidConfigException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        }
        GitAutoConfig config = new GitAutoConfig();
        config.setWatchPath(properties.getProperty("watch.path"));
        config.setIdleTime(Integer.parseInt(properties.getProperty("idle.time")));
        config.setAutoCommit(Boolean.parseBoolean(properties.getProperty("auto.commit")));
        config.setAutoPush(Boolean.parseBoolean(properties.getProperty("auto.push")));
        config.setGitBranch(properties.getProperty("git.branch"));
        config.setLogLevel(properties.getProperty("log.level"));
        ConfigValidator.validate(config);
        return config;
    }
}
