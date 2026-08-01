package ai;
import java.io.IOException;
public interface AIProvider{
	String getProviderName();
	boolean validateApiKey(String apiKey) throws IOException, InterruptedException;
}
