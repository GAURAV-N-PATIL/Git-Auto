package git;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class GitCommandRunner{
	public GitResult run(String... command){
		StringBuilder output=new StringBuilder();
		try{
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			Process process=builder.start();
			BufferReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line=reader.readLine())!=null){
				output.append(line).append("\n");
			}
			int exitCode=process.waitFor();
			return new GitResult(exitCode==0,output.toString());
		} catch(Exception e){
			return new GitResult(false, e.getMessage());
		}
	}
}
