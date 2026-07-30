package config;
public class GitAutoConfig{
	private String watchPath;
	private int idleTime;
	private boolean autoCommit;
	private boolean autoPush;
	private String gitBranch;
	private String logLevel;

	public String getWatchPath(){
		return watchPath;
	}
	public void setWatchPath(String watchPath){
		this.watchPath=watchPath;
	}
	public int getIdleTime(){
		return idleTime;
	}
	public void setIdleTime(int idleTime){
		this.idleTime=idleTime;
	}
	public boolean isAutoCommit(){
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit){
		this.autoCommit=autoCommit;
	}
	public boolean isAutoPush(){
		return autoPush;
	}
	public void setAutoPush(boolean autoPush){
		this.autoPush=autoPush;
	}
   	 public String getGitBranch() {
        	return gitBranch;
 	}
    	public void setGitBranch(String gitBranch) {
        	this.gitBranch = gitBranch;
   	}
    	public String getLogLevel() {
        	return logLevel;
    	}	
    	public void setLogLevel(String logLevel) {
        	this.logLevel = logLevel;
    	}
	@Override
	public String toString(){
		return """
			-----------------------------
			GitAuto Configuration
			-----------------------------
			Watch Path : %s
			Idle Time  : %d
			Auto Commit: %b
			Auto Push  : %b
			Git Branch : %s
			log Level  : %s
			-----------------------------
			""".formatted(
					watchPath,
					idleTime,
					autoCommit,
					autoPush,
					gitBranch,
					logLevel
				     );
	}
}
