package config;
public class ConfigValidator{
	public static void validate(GitAutoConfig config)
		throws InvalidConfigException{
		if(config.getWatchPath()==null||config.getWatchPath().isBlank()){
			throw new InvalidConfigException("watch.path cannot be empty");
		}
		if (config.getIdleTime() <= 0) {
            		throw new InvalidConfigException("idle.time must be greater than zero.");
		}
        	if (config.getGitBranch()==null||config.getGitBranch().isBlank()) {
            		throw new InvalidConfigException("git.branch cannot be empty.");
		}
        	if (config.getLogLevel() == null ||
                	config.getLogLevel().isBlank()) {
            			throw new InvalidConfigException("log.level cannot be empty.");
			}
	}
}
