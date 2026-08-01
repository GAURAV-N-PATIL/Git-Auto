public class GitFile{
	private String status;
	private String path;
	public GitFile(String status,String path){
		this.status=status;
		this.path=path;
	}
	public String getStatus(){
		return status;
	}
	public String getPath(){
		return path;
	}
}
