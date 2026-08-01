package git;
import model.GitFile;
import java.util.ArrayList;
import java.util.List;
public class GitService{
	private final GitCommandRunner runner=new GitCommandRunner();
	public boolean isGitRepository(){
		GitResult result=runner.run("git","rev-parse","--is-inside-work-tree");
		return result.isSuccess() && result.getOutput().trim().equals("true");
	}
	public List<GitFile> getModifiedFiles(){
		List<GitFile> files=new ArrayList<>();
		GitResult result=runner.run("git","status","--short");
		if (!result.isSuccess()){
			return files;
		}
		String[] lines=result.getOutput().split("\n");
		for (String line: lines){
			if (line.isBlank())
				continue;
			String status=line.substring(0,2).trim();
			String path = line.substring(3).trim();
			files.add(new GitFile(status, path));
		}
		return files;
	}
}
