package git;
import java.util.ArrayList;
import java.util.List;
public final class Commands {
    private Commands(){
    }
    public static String[] status() {
        return new String[]{"git","status","--porcelain"};
    }
    public static String[] stagedFiles() {
        return new String[]{"git","diff","--cached","--name-only"};
    }
    public static String[] addAll() {
        return new String[]{"git","add","."};
    }
    public static String[] addFiles(List<String> files) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("add");
        command.addAll(files);
        return command.toArray(new String[0]);
    }
    public static String[] checkRepository() {
        return new String[]{"git","rev-parse","--is-inside-work-tree"};
    }
    public static String[] currentBranch() {
        return new String[]{"git","branch","--show-current"};
    }
    public static String[] fullStatus() {
        return new String[]{"git","status"};
    }
}
