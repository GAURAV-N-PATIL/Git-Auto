package ui;
public final class ConsoleUI {
    private static final String LINE ="========================================================";
    private static final String SECTION ="--------------------------------------------------------";
    private ConsoleUI() {
    }
    public static void banner() {
        System.out.println();
        System.out.println(LINE);
        System.out.printf("%30s%n", "SYNCAUTO");
        System.out.println(LINE);
        System.out.println();
    }
    public static void section(String title) {
        System.out.println();
        System.out.println(SECTION);
        System.out.println(title);
        System.out.println(SECTION);
        System.out.println();
    }
    public static void footer() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("Ready for Commit");
        System.out.println(LINE);
    }
}
