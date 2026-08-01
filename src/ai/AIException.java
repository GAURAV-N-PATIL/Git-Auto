package ai;
public class AIException extends Exception {
    public AIException(String message) {
        super(message);
    }
    public AIException(String message, Throwable cause) {
        super(message, cause);
    }
}
