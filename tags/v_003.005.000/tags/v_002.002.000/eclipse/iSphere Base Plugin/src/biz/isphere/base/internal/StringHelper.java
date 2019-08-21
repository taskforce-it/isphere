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
}
