package util;

public class SQLQueryRuntime {
    private static long startTs = 0;
    private static long endTs = 0;
    
    public static void beginScope() {
        startTs = System.currentTimeMillis();
    }

    public static void endScope() {
        endTs = System.currentTimeMillis();
    }

    public static long measure() {
        return endTs - startTs; 
    }
}
