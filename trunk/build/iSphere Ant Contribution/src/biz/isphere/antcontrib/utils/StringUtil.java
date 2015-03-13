package biz.isphere.antcontrib.utils;

public final class StringUtil {

    private static final String EMPTY_STRING = "";
    private static final String QUESTION_MARK = "?";
    private static final String ASTERISK = "*";

    public static void main(String[] args) {
        StringUtil main = new StringUtil();
        main.run(args);
    }

    /**
     * Procedure with test pattern for {@link #matchWildcard(String, String)}
     * <p>
     * Set VM arguments to <b>-enableassertions</b> or <b>-ea</b> to enable Java
     * assertions.
     * 
     * @param args
     */
    private void run(String[] args) {

        /*
         * Test matchWildcardString()
         */

        // Positive null values Tests
        assert matchWildcard(null, null);
        assert matchWildcard(null, "");
        assert matchWildcard("", "");
        assert matchWildcard(null, "abc");
        assert matchWildcard("", " ");
        assert matchWildcard(null, " ");

        // Negative null values Tests
        assert !matchWildcard("*?", null);
        assert !matchWildcard(" ", "");
        assert !matchWildcard(" ", null);

        // Positive Tests
        assert matchWildcard("*", "");
        assert matchWildcard("?", " ");
        assert matchWildcard("*", "a");
        assert matchWildcard("*", "ab");
        assert matchWildcard("?", "a");
        assert matchWildcard("*?", "abc");
        assert matchWildcard("?*", "abc");
        assert matchWildcard("*abc", "abc");
        assert matchWildcard("*abc*", "abc");
        assert matchWildcard("*a*bc*", "aXXXbc");

        // Negative Tests
        assert !matchWildcard("*a", "");
        assert !matchWildcard("a*", "");
        assert !matchWildcard("?", "");
        assert !matchWildcard("*b*", "a");
        assert !matchWildcard("b*a", "ab");
        assert !matchWildcard("??", "a");
        assert !matchWildcard("*?", "");
        assert !matchWildcard("??*", "a");
        assert !matchWildcard("*abc", "abX");
        assert !matchWildcard("*abc*", "Xbc");
        assert !matchWildcard("*a*bc*", "ac");
    }

    /**
     * Checks a given string for the <code>null</code> value and <i>empty</i>. A
     * string is considered to be empty if its length is zero. A string of
     * spaces is not empty.
     * 
     * @param string - string that is checked for the null value and
     *        <i>empty</i>
     * @return <code>true</code> if the string is null or empty, else
     *         <code>false</code>.
     */
    public static boolean isNullOrEmpty(String string) {

        if (string == null) {
            return true;
        }

        if (string.length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Removes the specified character from both ends of a given string.
     * 
     * @param string - string that is trimmed
     * @param character - character or string that is removed from both ends of
     *        <i>string</i>
     * @return the trimmed input string
     */
    public static String trim(String string, String character) {

        int startOffset = 0;
        while (startOffset < string.length() && string.substring(startOffset, startOffset + character.length()).equals(character)) {
            startOffset += character.length();
        }

        int endOffset = string.length();
        while (endOffset > character.length() - 1 && string.substring(endOffset - character.length(), endOffset).equals(character)) {
            endOffset -= character.length();
        }

        if (startOffset > endOffset) {
            return EMPTY_STRING;
        }

        return string.substring(startOffset, endOffset);
    }

    /**
     * Return <code>true</code>, if a given string matches the specified
     * wildcard pattern.
     * 
     * @param pattern - wildcard pattern. Character '*' matches any string and
     *        '?' matches any character
     * @param string - string that is compared to the pattern
     * @return <code>true</code> if the string matches the pattern, else
     *         <code>false</code>.
     * @see <a
     *      href="http://www.codeproject.com/Tips/57304/Use-wildcard-characters-and-to-compare-strings"
     *      a>www.codeproject.com</a>
     */
    public static boolean matchWildcard(String pattern, String string) {
        return matchWildcard(pattern, string, false);
    }

    /**
     * Return <code>true</code>, if a given string matches the specified
     * wildcard pattern.
     * 
     * @param tPattern - wildcard pattern. Character '*' matches any string and
     *        '?' matches any character
     * @param tString - string that is compared to the pattern
     * @param ignoreCase - specifies whether case is ignored
     * @return <code>true</code> if the string matches the pattern, else
     *         <code>false</code>.
     * @see <a
     *      href="http://www.codeproject.com/Tips/57304/Use-wildcard-characters-and-to-compare-strings"
     *      a>www.codeproject.com</a>
     */
    public static boolean matchWildcard(String pattern, String string, boolean ignoreCase) {

        String tPattern = pattern;
        if (tPattern != null && ignoreCase) {
            tPattern = tPattern.toLowerCase();
        }

        String tString = string;
        if (tString != null && ignoreCase) {
            tString = tString.toLowerCase();
        }

        if (isNullOrEmpty(tPattern)) {
            return true;
        } else if (tString == null) {
            return matchWildcard(tPattern, EMPTY_STRING);
        } else if (tString.compareTo(tPattern) == 0) {
            return true;
        } else if (isNullOrEmpty(tString)) {
            if (isNullOrEmpty(trim(tPattern, ASTERISK))) {
                return true;
            } else {
                return false;
            }
        } else if (tPattern.length() == 0) {
            return true;
        } else if (tPattern.length() == 0) {
            return false;
        } else if (tPattern.startsWith(QUESTION_MARK)) {
            return matchWildcard(tPattern.substring(1), tString.substring(1));
        } else if (tPattern.endsWith(QUESTION_MARK)) {
            return matchWildcard(tPattern.substring(0, tPattern.length() - 1), tString.substring(0, tString.length() - 1));
        } else if (tPattern.startsWith(ASTERISK)) {
            if (matchWildcard(tPattern.substring(1), tString)) {
                return true;
            } else {
                return matchWildcard(tPattern, tString.substring(1));
            }
        } else if (tPattern.endsWith(ASTERISK)) {
            if (matchWildcard(tPattern.substring(0, tPattern.length() - 1), tString)) {
                return true;
            } else {
                return matchWildcard(tPattern, tString.substring(0, tString.length() - 1));
            }
        } else if (tPattern.substring(0, 1).equals(tString.substring(0, 1))) {
            return matchWildcard(tPattern.substring(1), tString.substring(1));
        }

        return false;
    }

}