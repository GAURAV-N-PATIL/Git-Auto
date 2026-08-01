package ai;
import java.io.IOException;
import java.util.List;
public class AIManager {
    private final AIConfigManager configManager;
    private final AISetupWizard setupWizard;
    private final AIClient aiClient;
    public AIManager() {
        configManager = new AIConfigManager();
        setupWizard = new AISetupWizard();
        aiClient = new AIClient();
    }
    public String getApiKey() throws IOException {
        if (configManager.hasApiKey()) {
            return configManager.getApiKey();
        }
        return setupWizard.start();
    }
    public String generateCommitMessage(List<String> stagedFiles, String diff)
            throws IOException, AIException {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        String prompt = AIPromptBuilder.buildCommitPrompt(stagedFiles, diff);
        return aiClient.generateCommitMessage(apiKey, prompt);
    }
}
