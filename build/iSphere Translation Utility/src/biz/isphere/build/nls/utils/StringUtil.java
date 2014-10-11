package biz.isphere.build.nls.utils;

public final class StringUtil {

    /**
     * Removes trailing spaces from a specified input string.
     * 
     * @param aString that is trimmed right
     * @return string without trailing spaces
     */
    public static String trimR(String aString) {
        return aString.replaceAll("\\s+$", "");
    }

    /**
     * Removes leading spaces from a specified input string.
     * 
     * @param aString that is trimmed left
     * @return string without leading spaces
     */
    public static String trimL(String aString) {
        return aString.replaceAll("^\\s+", "");
    }

}
