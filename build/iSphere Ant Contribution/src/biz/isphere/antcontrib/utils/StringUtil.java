/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.utils;

public final class StringUtil {

    private static final String EMPTY_STRING = "";
    private static final String QUESTION_MARK = "?";
    private static final String ASTERISK = "*";

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
     * Returns <code>true</code>, if a given string matches the specified
     * wildcard pattern. If the pattern is <code>null</code> or empty, the
     * result is always <code>true</code>.
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
     * Returns <code>true</code>, if a given string matches the specified
     * wildcard pattern. If the pattern is <code>null</code> or empty, the
     * result is always <code>true</code>.
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

        if (pattern == null || pattern.length() == 0) {
            return true;
        }

        String tPattern = pattern;
        if (tPattern != null && ignoreCase) {
            tPattern = tPattern.toLowerCase();
        }

        String tString = string;
        if (tString != null && ignoreCase) {
            tString = tString.toLowerCase();
        }

        return matchWildcardInternally(tPattern, tString, ignoreCase);
    }

    private static boolean matchWildcardInternally(String tPattern, String tString, boolean ignoreCase) {

        if (tPattern == null) {
            return true;
        } else if (tString == null) {
            return matchWildcardInternally(tPattern, EMPTY_STRING, ignoreCase);
        } else if (tString.compareTo(tPattern) == 0) {
            return true;
        } else if (tString.length() == 0) {
            if (isNullOrEmpty(trim(tPattern, ASTERISK))) {
                return true;
            } else {
                return false;
            }
        } else if (tPattern.length() == 0) {
            if (tString.length() == 0) {
                return true;
            } else {
                return false;
            }
        } else if (tPattern.startsWith(QUESTION_MARK)) {
            return matchWildcardInternally(tPattern.substring(1), tString.substring(1), ignoreCase);
        } else if (tPattern.endsWith(QUESTION_MARK)) {
            return matchWildcardInternally(tPattern.substring(0, tPattern.length() - 1), tString.substring(0, tString.length() - 1), ignoreCase);
        } else if (tPattern.startsWith(ASTERISK)) {
            if (matchWildcardInternally(tPattern.substring(1), tString, ignoreCase)) {
                return true;
            } else {
                return matchWildcard(tPattern, tString.substring(1));
            }
        } else if (tPattern.endsWith(ASTERISK)) {
            if (matchWildcardInternally(tPattern.substring(0, tPattern.length() - 1), tString, ignoreCase)) {
                return true;
            } else {
                return matchWildcardInternally(tPattern, tString.substring(0, tString.length() - 1), ignoreCase);
            }
        } else if (tPattern.substring(0, 1).equals(tString.substring(0, 1))) {
            return matchWildcardInternally(tPattern.substring(1), tString.substring(1), ignoreCase);
        }

        return false;
    }

}