package utils;
public final class Messages {
    private Messages() {
    }
    public static String invalidFileNumber(int number){
    	return "Invalid file number: " + number;
}	
    public static String invalidSelection(String input){
    	return "'"+input+"' is not a valid file number.";
}
    public static final String STARTING ="Starting GITAUTO...";
    public static final String LOADING_CONFIG ="Loading configuration...";
    public static final String CONFIG_SUCCESS ="Configuration loaded successfully.";
    public static final String INITIALIZATION_COMPLETE ="Initialization complete.";
    public static final String CHECKING_REPOSITORY ="Checking Git repository...";
    public static final String REPOSITORY_DETECTED ="Git repository detected.";
    public static final String NOT_A_REPOSITORY ="Current directory is not a Git repository.";
    public static final String SCANNING_FILES ="Scanning modified files...";
    public static final String NO_MODIFIED_FILES ="No modified files found.";
    public static final String STAGING_SUCCESS ="Files staged successfully.";
    public static final String STAGING_FAILED ="Failed to stage files.";
    public static final String NO_STAGED_FILES ="No staged files found.";
    public static final String ENTER_STAGE_PROMPT ="Enter '.' to stage all";
    public static final String ENTER_FILE_NUMBERS="or file numbers separated by commas.";
    public static final String TRY_AGAIN ="Please try again.";
    public static final String CONFIG_LOAD_FAILED ="Failed to load configuration.";
    public static final String CONFIG_READ_FAILED ="Unable to read configuration files.";
    public static final String UNEXPECTED_ERROR ="Unexpected error.";
    public static final String MODIFIED_FILES_HEADER ="Modified Files";
    public static final String STAGED_FILES_HEADER ="Staged Files";
    public static final String DIVIDER ="-------------------------------";
    public static final String GENERATING_COMMIT_MESSAGE ="Generating commit message using AI...";
    public static final String COMMIT_MESSAGE_HEADER ="Suggested Commit Message";
    public static final String AI_UNAVAILABLE ="AI commit message generation is unavailable.";
    public static final String AI_GENERATION_FAILED ="Failed to generate commit message.";
    public static final String AI_CONFIG_READ_FAILED ="Unable to read AI configuration.";
    public static final String ENTER_CUSTOM_MESSAGE ="Enter your commit message, then leave a blank line to finish:";
    public static final String CREATING_COMMIT ="Creating commit...";
    public static final String COMMIT_SUCCESS ="Commit created successfully.";
    public static final String COMMIT_FAILED ="Failed to create commit.";
    public static final String COMMIT_CANCELLED ="Commit cancelled.";
    public static final String NO_REMOTE ="No 'origin' remote configured for this repository.";
    public static final String PUSHING ="Pushing to remote...";
    public static final String PUSH_SUCCESS ="Push completed successfully.";
    public static final String PUSH_FAILED ="Push failed.";
    public static final String PUSH_SKIPPED ="Push skipped.";
    public static String pushPrompt(String branch){
    	return "Push this commit to origin/"+branch+"? (y/n)";
    }
    public static String autoModePrompt(int idleTime, boolean autoPush){
    	return "Auto mode is enabled in your config: after " + idleTime
    		+ "s of no changes, Git-Auto can commit automatically"
    		+ (autoPush ? " and push." : " (auto.push is off, so pushing still needs your say-so).")
    		+ "\nStart in auto mode now? (y/n)";
    }
    public static final String AUTO_MODE_DECLINED ="Continuing in one-time interactive mode.";
}
