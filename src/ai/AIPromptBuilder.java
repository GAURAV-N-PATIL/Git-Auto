package ai;
import java.util.List;
public final class AIPromptBuilder {
	private static final int MAX_DIFF_LENGTH = 6000;
	private AIPromptBuilder() {
	}
	public static String buildCommitPrompt(List<String> stagedFiles, String diff) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Generate a git commit message ");
		prompt.append("for the following staged changes.\n\n");
		prompt.append("Files changed:\n");
		for (String file : stagedFiles) {
			prompt.append("- ").append(file).append("\n");
		}
		prompt.append("\nDiff:\n");
		prompt.append(truncate(diff));
		prompt.append("\n\nFormat (strict):\n");
		prompt.append("<type>: <short description>\n\n");
		prompt.append("Allowed types (pick exactly one, based on the diff):\n");
		prompt.append("- feat: a new feature\n");
		prompt.append("- fix: a bug fix\n");
		prompt.append("- docs: documentation changes\n");
		prompt.append("- chore: maintenance, config changes\n");
		prompt.append("- refactor: code restructuring without behavior change\n");
		prompt.append("- ci: changes to CI/CD configuration\n");
		prompt.append("\nRules:\n");
		prompt.append("- Use exactly one of the six types above, lowercase, followed by a colon and a space\n");
		prompt.append("- Do not invent other types (no 'style', 'test', 'perf', 'build', etc.)\n");
		prompt.append("- Do not add a scope in parentheses\n");
		prompt.append("- Keep the whole line under 72 characters\n");
		prompt.append("- Description should be lowercase, imperative mood (e.g. \"add\", not \"added\")\n");
		prompt.append("- Output only that single line, nothing else\n");
		prompt.append("- No body, no markdown, no code fences, no explanations\n");
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
