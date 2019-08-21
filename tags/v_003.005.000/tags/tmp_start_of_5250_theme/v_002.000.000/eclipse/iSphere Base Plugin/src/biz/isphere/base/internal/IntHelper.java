package biz.isphere.base.internal;

public final class IntHelper {

    /**
     * Parses an Integer value from a given text.
     * 
     * @param someText Text representing an integer value.
     * @return Integer on success, else null.
     */
    public static Integer tryParseInt(String someText) {
        try {
            return Integer.parseInt(someText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses an Integer value from a given text.
     * 
     * @param someText Text representing an integer value.
     * @param defaultValue Default value that is returned on invalid input
     *        values.
     * @return Integer on success, else the specified default value.
     */
    public static Integer tryParseInt(String someText, Integer defaultValue) {
        Integer integer = tryParseInt(someText);
        if (integer == null) {
            return defaultValue;
        }
        return integer;
    }

    /**
     * Parses an <code>int</code> value from a given text.
     * 
     * @param someText Text representing an <code>int</code> value.
     * @param defaultValue Default value that is returned on invalid input
     *        values.
     * @return <code>int</code> value on success, else the specified default
     *         value.
     */
    public static Integer tryParseInt(String someText, int defaultValue) {
        Integer integer = tryParseInt(someText);
        if (integer == null) {
            return new Integer(defaultValue);
        }
        return integer;
    }

}
