package ai;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class AIClient {
    private static final String API_URL =
            "https://openrouter.ai/api/v1/chat/completions";
    // Any OpenRouter model ID ending in ":free" works here at no cost.
    // Check https://openrouter.ai/models?max_price=0 for the current list,
    // since free model availability rotates over time.
    private static final String MODEL =
            "openai/gpt-oss-120b:free";
    private final HttpClient httpClient;
    public AIClient() {
        httpClient = HttpClient.newHttpClient();
    }
    public String generateCommitMessage(
            String apiKey,
            String prompt)
            throws AIException {
        try {
            String requestBody = buildRequestBody(prompt);
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Content-Type", "application/json")
                            // Optional per OpenRouter docs, but recommended:
                            // identifies the app for their rankings page.
                            .header("HTTP-Referer", "https://github.com/git-auto")
                            .header("X-Title", "Git-Auto")
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                            .build();
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        } catch (IOException e) {
            throw new AIException(
                    "Unable to connect to OpenRouter.",
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AIException(
                    "Request interrupted.",
                    e);
        }
    }
    private String buildRequestBody(String prompt) {
        prompt = escapeJson(prompt);
        return """
                {
                  "model":"%s",
                  "messages":[
                    {
                      "role":"system",
                      "content":"You generate concise Conventional Commit messages. Return ONLY the commit message."
                    },
                    {
                      "role":"user",
                      "content":"%s"
                    }
                  ]
                }
                """.formatted(MODEL, prompt);
    }
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
	        .replace("\r", "");
    }
    private String handleResponse(HttpResponse<String> response)
            throws AIException {
        int status = response.statusCode();
        switch (status) {
            case 200:
                return parseCommitMessage(response.body());
            case 401:
            case 403:
                throw new AIException("Invalid API key.");
            case 429:
                throw new AIException("Rate limit exceeded.");
            default:
                if (status >= 500) {
                    throw new AIException(
                            "OpenRouter service unavailable.");
                }
                throw new AIException(
                        "Request failed:\n" + response.body());
        }
    }
    private String parseCommitMessage(String json)
            throws AIException {
        String key = "\"content\":\"";
        int start = json.indexOf(key);
        if (start == -1) {
            throw new AIException("Unable to parse AI response.");
        }
        start += key.length();
        StringBuilder message = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case 'n':
                        message.append('\n');
                        break;
                    case '"':
                        message.append('"');
                        break;
                    case '\\':
                        message.append('\\');
                        break;
                    default:
                        message.append(c);
                }
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                break;
            }
            message.append(c);
        }
        return message.toString().trim();
    }
}
