package ai;
import java.io.IOException;
public class AIManager {
    private final AIConfigManager configManager;
    private final AISetupWizard setupWizard;
    public AIManager() {
        configManager = new AIConfigManager();
        setupWizard = new AISetupWizard();
    }
    public String getApiKey() throws IOException {
        if (configManager.hasApiKey()) {
            return configManager.getApiKey();
        }
        return setupWizard.start();
    }
}
