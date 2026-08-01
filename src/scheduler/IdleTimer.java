package scheduler;
public class IdleTimer {
    private long lastActivityMillis;
    public IdleTimer() {
        lastActivityMillis = System.currentTimeMillis();
    }
    public void recordActivity() {
        lastActivityMillis = System.currentTimeMillis();
    }
    public long secondsSinceLastActivity() {
        return (System.currentTimeMillis() - lastActivityMillis) / 1000;
    }
    public boolean isIdleFor(int thresholdSeconds) {
        return secondsSinceLastActivity() >= thresholdSeconds;
    }
}
