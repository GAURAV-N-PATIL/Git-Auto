package ai;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
public class AIClient {
    private static final String BASE_URL =
            "https://api.cerebras.ai/v1/chat/completions";
    private final HttpClient client;
    public AIClient() {
        client = HttpClient.newHttpClient();
    }
    public boolean validateApiKey(String apiKey)
            throws AIException{
        // Will be used to send a small request to Cerebras.
        return true;
    }
    public String generateCommitMessage(
            String apiKey,
            String prompt)
            throws AIException {
        // later to be used
        return "";
    }
}
