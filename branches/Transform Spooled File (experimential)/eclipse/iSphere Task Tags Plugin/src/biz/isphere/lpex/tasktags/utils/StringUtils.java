package biz.isphere.lpex.tasktags.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

public final class StringUtils {

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

    public static boolean isNullOrEmpty(String aValue) {
        if (aValue == null || aValue.length() == 0) {
            return true;
        }
        return false;
    }

}
