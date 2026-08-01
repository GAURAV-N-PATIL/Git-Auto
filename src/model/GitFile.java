package model;
public class GitFile{
	private final String status;
	private final String path;
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
	@Override
	public String toString(){
		return status+" " +path;
	}
}
