package biz.isphere.antcontrib.logger;

public final class Logger {
    /**
     * When things go wrong, it is useful to be able to debug the ROT.
     */
    private static final boolean DEBUG =
    // true;
    "true".equalsIgnoreCase(System.getProperty("biz.isphere.antcontrib.debug"));

    private static boolean isDebugEnabled() {
        return DEBUG;
    }

    public void logError(String message) {
        logError(message, null);
    }

    public static void logError(String message, Throwable e) {

        System.out.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static void logWarning(String message) {

        System.out.println("WARNING: " + message);

    }

    public static void logInfo(String message) {

        if (!isDebugEnabled()) {
            return;
        }

        System.out.println("INFO: " + message);
    }
}
