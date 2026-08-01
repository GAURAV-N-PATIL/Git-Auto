package git;
public class GitResult{
	private final boolean success;
	private final String output;
	public GitResult(boolean success,String output){
		this.success=success;
		this.output=output;
	}
	public boolean isSuccess(){
		return success;
	}
	public String getOutput(){
		return output;
	}
}
