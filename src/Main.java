import config.ConfigManager;
import config.GitAutoConfig;
import config.InvalidConfigException;
import git.GitService;
import logger.Logger;
import model.GitFile;
import java.io.File;
import java.io.IOException;
import java.util.*;
public class Main{
    public static void main(String[] args) {
        Logger.info("Starting SYNCAUTO...");
        String configPath=args.length>0 
                ?args[0] 
                :System.getProperty("user.dir")+File.separator+"src"+File.separator+"config"+File.separator+"gitauto.properties";
        ConfigManager configManager=new ConfigManager();
        try{
            Logger.info("Loading configuration...");
            GitAutoConfig config=configManager.load(configPath);
            Logger.success("Configuration loaded successfully.");
            GitService gitService=new GitService();
            Logger.info("Checking Git repository...");
            if (!gitService.isGitRepository()){ 
                Logger.error("Current directory is not a Git repository.");
                return;
            }
            Logger.success("Git repository detected.");
            Logger.info("Scanning modified files...");
            List<GitFile> files=gitService.getModifiedFiles();
            if (files.isEmpty()){
                Logger.info("No modified files found.");
                return;
            }
            System.out.println();
            System.out.println("Modified Files");
            System.out.println("-------------------------------");
            int index=1;
            for(GitFile file:files){
                System.out.printf("%d. %s%n", index++, file.getPath());
            }
            Scanner scanner=new Scanner(System.in);
            boolean staged=false;
            while (true) {
                System.out.println();
                System.out.println("Enter '.' to stage all");
                System.out.println("or file numbers separated by commas.");
                System.out.print("> ");
                String input=scanner.nextLine().trim();
                if (input.equals(".")){
                    staged=gitService.stageAll();
                    break;
                } else{
                    String[] selections=input.split(",");
                    List<String> selectedFiles=new ArrayList<>();
                    boolean validInput=true;
                    for (String selection:selections){
                        selection=selection.trim();
                        try{
                            int selectedIndex=Integer.parseInt(selection)-1;
                            if (selectedIndex<0||selectedIndex>=files.size()){
                                Logger.error("Invalid file number: "+(selectedIndex+1));
                                validInput=false;
                                break;
                            }
                            selectedFiles.add(files.get(selectedIndex).getPath());
                        } catch(NumberFormatException e){
                            Logger.error("'"+selection+"' is not a valid file number.");
                            validInput=false;
                            break;
                        }
                    }
                    if(!validInput){
                        Logger.info("Please try again.");
                        continue;
                    }
                    staged=gitService.stageFiles(selectedFiles);
                    break;
                }
            }
            if (!staged){
                Logger.error("Failed to stage files.");
                return;
            }
            Logger.success("Files staged successfully.");
            List<String> stagedFiles=gitService.getStagedFiles();
            if (stagedFiles.isEmpty()){
                Logger.error("No staged files found.");
                return;
            }
            System.out.println();
            System.out.println("Staged Files");
            System.out.println("------------------------");
            for (String file:stagedFiles){
                System.out.println(file);
            }
            Logger.success("Initialization complete.");
        } catch (InvalidConfigException e){
            Logger.error("Failed to load configuration.");
            Logger.error(e.getMessage());
        } catch(IOException e){
            Logger.error("Unable to read configuration files.");
            Logger.error(e.getMessage());
        } catch(Exception e){
            Logger.error("Unexpected Error");
            e.printStackTrace();
        }
    }
}
