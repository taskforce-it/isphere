/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

public class SearchArgument {

    public static final int CONTAINS = 1;
    public static final int CONTAINS_NOT = -1;

    public static final String CASE_MATCH = "*MATCH";
    public static final String CASE_IGNORE = "*IGNORE";

    private int operator;
    private String string;
    private int fromColumn;
    private int toColumn;
    private String caseSensitive; // TODO: change to boolean -> SNDSTR.RPGLE

    public SearchArgument(String aString, int aFromColumn, int aToColumn, String aCaseSensitive) {
        this(aString, aFromColumn, aToColumn, aCaseSensitive, CONTAINS);
    }

    public SearchArgument(String aString, int aFromColumn, int aToColumn, String aCaseSensitive, int anOperator) {
        operator = anOperator;
        string = aString;
        fromColumn = aFromColumn;
        toColumn = aToColumn;
        caseSensitive = aCaseSensitive;
    }

    public SearchArgument(String aString, int aFromColumn, int aToColumn, boolean anIsCaseSensitive, int anOperator) {
        operator = anOperator;
        string = aString;
        fromColumn = aFromColumn;
        toColumn = aToColumn;
        if (anIsCaseSensitive) {
            caseSensitive = SearchArgument.CASE_MATCH;
        } else {
            caseSensitive = SearchArgument.CASE_IGNORE;
        }
    }

    public int getOperator() {
        return operator;
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

    public static String getCaseMatch() {
        return CASE_MATCH;
    }

    public static String getCaseIgnore() {
        return CASE_IGNORE;
    }

}
