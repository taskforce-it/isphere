/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.util.ArrayList;
import java.util.StringTokenizer;

public final class StringHelper {

    /**
     * Splits a given string of tokens into pieces using a given separator.
     * 
     * @param aText String of tokens, separated by a separator value.
     * @param aSeparator Separator value.
     * @return Array of tokens.
     */
    public static String[] getTokens(String aText, String aSeparator) {
        StringTokenizer tTokenizer = new StringTokenizer(aText, aSeparator);
        int nTokens = tTokenizer.countTokens();
        ArrayList<String> tStringArray = new ArrayList<String>();
        String tItem;
        for (int i = 0; i < nTokens; i++) {
            tItem = tTokenizer.nextToken().trim();
            if (!isNullOrEmpty(tItem)) {
                tStringArray.add(tItem);
            }
        }
        return tStringArray.toArray(new String[tStringArray.size()]);
    }

    /**
     * Concatenates a list of token to a string value, separated by the
     * specified separator value.
     * 
     * @param aTokens String array of tokens.
     * @param aSeparator Separator used to separate the tokens.
     * @return String of tokens, separated by the specified separator.
     */
    public static String concatTokens(String[] aTokens, String aSeparator) {
        StringBuilder tList = new StringBuilder();
        for (String tItem : aTokens) {
            if (!isNullOrEmpty(tItem)) {
                if (tList.length() > 0) {
                    tList.append(aSeparator);
                }
                tList.append(tItem);
            }
        }
        return tList.toString();
    }

    /**
     * Checks a given String for null and empty.
     * 
     * @param aValue String.
     * @return <code>true</code> if the string is null or empty, else
     *         <code>false</code>.
     */
    public static boolean isNullOrEmpty(String aValue) {
        if (aValue == null || aValue.length() == 0) {
            return true;
        }
        return false;
    }

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

    /**
     * Returns a fixed length string suitable for IBM API calls.
     * 
     * @param aValue - String value that is expanded to 'aLength'.
     * @param aLength - The length the string is expanded to.
     * @return string with fixed length
     */
    public static String getFixLength(String aValue, int aLength) {
        StringBuffer fixLength = new StringBuffer(aValue);
        while (fixLength.length() < aLength) {
            fixLength.append(" ");
        }
        return fixLength.toString();
    }

    /**
     * Returns a fixed length string suitable for IBM API calls.
     * 
     * @param aValue - String value that is expanded to 'aLength'.
     * @param aLength - The length the string is expanded to.
     * @return string with fixed length
     */
    public static String getFixLengthLeading(String aValue, int aLength) {
        StringBuffer fixLength = new StringBuffer();
        while (fixLength.length() < aLength - aValue.length()) {
            fixLength.append(" ");
        }
        fixLength.append(aValue);
        return fixLength.toString();
    }

    /**
     * Reverses a given String.
     * 
     * @param aText - String that is put in reverse order.
     * @return reversed string
     */
    public static String reverse(String aText) {
        StringBuilder builder = new StringBuilder(aText);
        return builder.reverse().toString();
    }

    /**
     * <p>
     * Checks whether the character is ASCII 7 bit printable.
     * </p>
     * 
     * <pre>
     *   CharUtils.isAsciiPrintable('a')  = true
     *   CharUtils.isAsciiPrintable('A')  = true
     *   CharUtils.isAsciiPrintable('3')  = true
     *   CharUtils.isAsciiPrintable('-')  = true
     *   CharUtils.isAsciiPrintable('\n') = false
     *   CharUtils.isAsciiPrintable('&copy;') = false
     * </pre>
     * 
     * @param ch the character to check
     * @return true if between 32 and 126 inclusive
     */
    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    /**
     * Count the number of character that are included in a given string.
     * 
     * @param string - String that is search for a given character
     * @param c - character that is counted
     * @return number of occurrences of 'c'
     */
    public static int count(String string, char c) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the number of substrings that are included in a given string.
     * 
     * @param string - String that is search for a given character
     * @param substring - substring that is counted
     * @return number of occurrences of 'substring'
     */
    public static int count(String string, String substring) {
        int count = 0;
        int i = 0;
        while ((i = string.indexOf(substring, i)) >= 0) {
            count++;
            i = i + substring.length();
        }
        return count;
    }
}
