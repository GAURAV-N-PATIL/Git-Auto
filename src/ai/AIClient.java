package ai;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class AIClient {
    private static final String API_URL =
            "https://api.cerebras.ai/v1/chat/completions";
    private static final String MODEL ="gpt-oss-120b";
    private final HttpClient httpClient;
    private final Gson gson;
    public AIClient() {
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }
    public String generateCommitMessage(
            String apiKey,
            String prompt)
            throws AIException {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", MODEL);
            JsonArray messages = new JsonArray();
            JsonObject system = new JsonObject();
            system.addProperty(
                    "role",
                    "system");
            system.addProperty(
                    "content",
                    "You generate concise Conventional Commit messages. "
                            + "Return ONLY the commit message.");
            JsonObject user = new JsonObject();
            user.addProperty(
                    "role",
                    "user");
            user.addProperty(
                    "content",
                    prompt);
            messages.add(system);
            messages.add(user);
            body.add("messages", messages);
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey)
                            .header(
                                    "Content-Type",
                                    "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    gson.toJson(body)))
                            .build();
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        } catch (IOException e) {
            throw new AIException(
                    "Unable to connect to Cerebras.",
                    e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AIException(
                    "Request interrupted.",
                    e);
        }
    }
    private String handleResponse(
            HttpResponse<String> response)
            throws AIException {
        int status = response.statusCode();
        switch (status) {
            case 200:
                return parseCommitMessage(
                        response.body());
            case 401:
            case 403:
                throw new AIException(
                        "Invalid API key.");
            case 429:
                throw new AIException(
                        "Rate limit exceeded.");
            default:
                if (status >= 500) {
                    throw new AIException(
                            "Cerebras service unavailable.");
                }
                throw new AIException(
                        "Request failed.\n"
                                + response.body());
        }
    }
    private String parseCommitMessage(String json)
            throws AIException {
        try {
            JsonObject root =
                    gson.fromJson(json, JsonObject.class);
            return root
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString()
                    .trim();
        } catch (Exception e) {
            throw new AIException(
                    "Failed to parse AI response.",
                    e);
        }
    }
}
