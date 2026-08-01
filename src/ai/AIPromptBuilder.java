package ai;
import java.util.List;
public final class AIPromptBuilder {
	private static final int MAX_DIFF_LENGTH = 6000;
	private AIPromptBuilder() {
	}
	public static String buildCommitPrompt(List<String> stagedFiles, String diff) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Generate a concise Conventional Commit message ");
		prompt.append("for the following staged changes.\n\n");
		prompt.append("Files changed:\n");
		for (String file : stagedFiles) {
			prompt.append("- ").append(file).append("\n");
		}
		prompt.append("\nDiff:\n");
		prompt.append(truncate(diff));
		prompt.append("\n\nRules:\n");
		prompt.append("- Use the format type(scope): summary\n");
		prompt.append("- Keep the summary line under 72 characters\n");
		prompt.append("- Add a short body only if it adds real value\n");
		prompt.append("- Do not include markdown, code fences, or explanations\n");
		return prompt.toString();
	}
	private static String truncate(String diff) {
		if (diff == null) {
			return "";
		}
		if (diff.length() <= MAX_DIFF_LENGTH) {
			return diff;
		}
		return diff.substring(0, MAX_DIFF_LENGTH) + "\n...(diff truncated)...";
	}
}
