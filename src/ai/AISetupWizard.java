package ai;
import java.io.IOException;
import java.util.Scanner;
public class AISetupWizard {
    private final Scanner scanner;
    private final AIConfigManager configManager;
    public AISetupWizard() {
        scanner = new Scanner(System.in);
        configManager = new AIConfigManager();
    }
    public String start() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("         AI Setup Wizard");
        System.out.println("======================================");
        System.out.println();
        System.out.println("No API key was found.");
        System.out.println("AI-generated commit messages require");
        System.out.println("a Cerebras API key.");
        System.out.println();
        while (true) {
            System.out.println("1. Show API key instructions");
            System.out.println("2. I already have an API key");
            System.out.println("3. Exit");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showInstructions();
                    break;
                case "2":
                    return setupApiKey();
                case "3":
                    return null;
                default:
                    System.out.println("Invalid option.");
                    System.out.println();
            }
        }
    }
    private void showInstructions() {
	System.out.println();
    	System.out.println("======================================");
    	System.out.println("       Cerebras API Key Setup");
    	System.out.println("======================================");
    	System.out.println();
    	System.out.println("1. Create a free Cerebras account (or sign in).");
    	System.out.println("2. Open the Cloud Console.");
    	System.out.println("3. Navigate to API Keys.");
    	System.out.println("4. Create a new API key.");
    	System.out.println("5. Copy the generated key.");
    	System.out.println("6. Return here and paste it.");
    	System.out.println();
    	System.out.println("Cloud Console:");
    	System.out.println("https://cloud.cerebras.ai");
    	System.out.println();
    	System.out.println("API Keys:");
    	System.out.println("https://cloud.cerebras.ai/platform/api-keys");
    	System.out.println();
    	System.out.println("Documentation:");
    	System.out.println("https://inference-docs.cerebras.ai/console/api-keys");
   	System.out.println();
    }
    private String setupApiKey() {
        while (true) {
            System.out.println();
            System.out.print("Paste your API key: ");
	    System.out.println("Press Enter after you've created your API key...");
            String apiKey = scanner.nextLine().trim();
            if (apiKey.isBlank()) {
                System.out.println("API key cannot be empty.");
                continue;
            }
            if (askToSave()) {
                try {
                    configManager.save(new AIConfig(apiKey));
                    System.out.println();
                    System.out.println("✓ API key saved locally.");
                    System.out.println("Location:");
                    System.out.println(configManager.getConfigPath());
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Failed to save API key.");
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println();
                System.out.println("API key will only be used for this session.");
            }
            return apiKey;
	    }
	}
    private boolean askToSave() {
        while (true) {
            System.out.println();
            System.out.println("Save this API key for future use?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return true;
                case "2":
                    return false;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
