package git;
import model.GitFile;
import java.util.ArrayList;
import java.util.List;
public class GitService{
	private final GitCommandRunner runner=new GitCommandRunner();
	public boolean isGitRepository(){
		GitResult result=runner.run(Commands.checkRepository());
		return result.isSuccess() && result.getOutput().trim().equals("true");
	}
	public List<GitFile> getModifiedFiles(){
		List<GitFile> files=new ArrayList<>();
		GitResult result=runner.run(Commands.status());
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
		GitResult result=runner.run(Commands.addAll());
		return result.isSuccess();
	}
	public boolean stageFiles(List<String> files){
		List<String> command=new ArrayList<>();
		if(files==null || files.isEmpty()){
			return false;
		}
		GitResult result=runner.run(Commands.addFiles(files));
		return result.isSuccess();
	}
	public List<String> getStagedFiles(){
		List<String> stagedFiles=new ArrayList<>();
		GitResult result=runner.run(Commands.stagedFiles());
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
	public String getCurrentBranch(){
		GitResult result=runner.run(Commands.currentBranch());
		if(!result.isSuccess()){
			return "";
		}
		return result.getOutput().trim();
	}
	public String getStagedDiff(){
		GitResult result=runner.run(Commands.diffCached());
		if(!result.isSuccess()){
			return "";
		}
		return result.getOutput();
	}
	public GitResult commit(String message){
		if(message==null || message.isBlank()){
			return new GitResult(false, "Commit message cannot be empty.");
		}
		return runner.run(Commands.commit(message));
	}
	public boolean hasRemote(){
		GitResult result=runner.run(Commands.remoteList());
		return result.isSuccess() && !result.getOutput().isBlank();
	}
	public GitResult push(String remote, String branch){
		return runner.run(Commands.push(remote, branch));
	}
}
