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
	public boolean stageAll(){
		GitResult result=runner.run("git","add",".");
		return result.isSuccess();
	}
	public boolean stageFiles(List<String> files){
		List<String> command=new ArrayList<>();
		command.add("git");
		command.add("add");
		command.addAll(files);
		GitResult result=runner.run(command.toArray(new String[0]));
		return result.isSuccess();
	}
	public List<String> getStagedFiles(){
		List<String> stagedFiles=new ArrayList<>();
		GitResult result=runner.run("git","diff","--cached","--name-only");
		if(!result.isSuccess()){
			return stagedFiles;
		}
		String[] lines=result.getOutput().split("\n");
		for (String line:lines){
			if(!line.isBlank()){
				stagedFiles.add(line.trim());
			}
		}
		return stagedFiles;
	}
}
