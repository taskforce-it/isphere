/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import biz.isphere.core.Messages;

public class SearchArgument {

    private static String SPACE = " "; //$NON-NLS-1$
    private static String QUOTE = "'"; //$NON-NLS-1$
    private static String COMMA = ","; //$NON-NLS-1$

    private int operator;
    private String string;
    private int fromColumn;
    private int toColumn;
    private String caseSensitive; // TODO: change to boolean -> FNDSTR.RPGLE
    private String regularExpression;

    /**
     * The value specified here must match the maximum line length in FNDSTR
     * (see: LILINE).
     */
    public static final int MAX_SOURCE_FILE_SEARCH_COLUMN = 228;
    
    /**
     * The value specified here must match the maximum message text length in
     * XFNDSTR (see: LITXT).
     */
    public static int MAX_MESSAGE_FILE_SEARCH_COLUMN = 132;

    public SearchArgument(String aString, int aFromColumn, int aToColumn, String aCaseSensitive) {
        this(aString, aFromColumn, aToColumn, aCaseSensitive, SearchOptions.SEARCH_ARG_STRING, SearchOptions.CONTAINS);
    }

    public SearchArgument(String aString, int aFromColumn, int aToColumn, String aCaseSensitive, String aRegularExpression, int anOperator) {
        operator = anOperator;
        string = aString;
        fromColumn = aFromColumn;
        toColumn = aToColumn;
        caseSensitive = aCaseSensitive;
        regularExpression = aRegularExpression;
    }

    public SearchArgument(String aString, int aFromColumn, int aToColumn, boolean aCaseSensitive, boolean aRegularExpression, int anOperator) {

        operator = anOperator;
        string = aString;
        fromColumn = aFromColumn;
        toColumn = aToColumn;

        if (aCaseSensitive) {
            caseSensitive = SearchOptions.CASE_MATCH;
        } else {
            caseSensitive = SearchOptions.CASE_IGNORE;
        }

        if (aRegularExpression) {
            regularExpression = SearchOptions.SEARCH_ARG_REGEX;
        } else {
            regularExpression = SearchOptions.SEARCH_ARG_STRING;
        }
    }

    public SearchArgument(String aString, boolean anIsCaseSensitive, boolean aRegularExpression, int anOperator) {
        this(aString, -1, -1, anIsCaseSensitive, aRegularExpression, anOperator);
    }

    public int getOperator() {
        return operator;
    }

    public String getOperatorAsText() {

        if (operator == SearchOptions.CONTAINS) {
            return Messages.Contains;
        } else {
            return Messages.Contains_not;
        }
    }

    public String getString() {
        return string;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getToColumn() {
        return toColumn;
    }

    public String getCaseSensitive() {
        return caseSensitive;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRange(int aFromColumn, int aToColumn) {
        fromColumn = aFromColumn;
        toColumn = aToColumn;
    }

    public String toText() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getOperatorAsText());
        buffer.append(SPACE);
        buffer.append(QUOTE);
        buffer.append(string);
        buffer.append(QUOTE);
        buffer.append(SPACE);
        buffer.append("("); //$NON-NLS-1$
        buffer.append(Messages.Columns_colon);
        buffer.append(SPACE);
        buffer.append(getFromColumn());
        buffer.append(" - "); //$NON-NLS-1$
        buffer.append(getToColumn());
        buffer.append(COMMA);
        buffer.append(SPACE);
        buffer.append(Messages.Case_sensitive_colon);
        buffer.append(getCaseSensitive());
        buffer.append(COMMA);
        buffer.append(SPACE);
        buffer.append(getRegularExpression());
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
