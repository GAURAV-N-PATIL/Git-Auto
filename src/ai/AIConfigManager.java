package ai;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
public class AIConfigManager{
    private static final String CONFIG_DIRECTORY =
            System.getProperty("user.home")
                    + File.separator
                    + ".gitauto";
    private static final String CONFIG_FILE =
            CONFIG_DIRECTORY
                    + File.separator
                    + "ai.properties";
    public boolean configExists() {
        return new File(CONFIG_FILE).exists();
    }
    public AIConfig load() throws IOException {
        if (!configExists()) {
            return null;
        }
        Properties properties = new Properties();
        try (FileInputStream input =
                     new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        }
        String apiKey = properties.getProperty("apiKey");
        return new AIConfig(apiKey);
    }
    public void save(AIConfig config) throws IOException {
        File directory = new File(CONFIG_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Properties properties = new Properties();
        Properties.setProperty(
                "apiKey",
                config.getApiKey());
        try (FileOutputStream output =
                     new FileOutputStream(CONFIG_FILE)) {
            properties.store(
                    output,
                    "GITAUTO AI Configuration");
        }
    }
    public boolean delete() {
        File file = new File(CONFIG_FILE);
        return file.exists() && file.delete();
    }
    public String getConfigPath() {
        return CONFIG_FILE;
    }
    public String getApiKey() throws IOException {
        AIConfig config = load();
        if (config == null) {
            return null;
        }
        return config.getApiKey();
    }
    public boolean hasApiKey() throws IOException {
        AIConfig config = load();
        return config != null
                && config.getApiKey() != null
                && !config.getApiKey().isBlank();
    }
}
