package biz.isphere.base.internal;

import java.util.ArrayList;
import java.util.StringTokenizer;

public final class StringHelper {

    /**
     * Splitts a given string of tokens into pieces using a given seperator.
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

}
