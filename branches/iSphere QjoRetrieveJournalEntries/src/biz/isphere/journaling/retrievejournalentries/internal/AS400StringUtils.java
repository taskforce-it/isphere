package biz.isphere.journaling.retrievejournalentries.internal;

public final class AS400StringUtils {

    /**
     * Pad space to the left of the input string s, so that the length of s is
     * n.
     * 
     * @param s - string that is padded
     * @param n - padded string length
     * @return padded string
     */
    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * Pad space to the right of the input string s, so that the length of s is
     * n.
     * 
     * @param s - string that is padded
     * @param n - padded string length
     * @return padded string
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

}
