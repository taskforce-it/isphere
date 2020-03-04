/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

public class SearchArgument {

    private int operator;
    private String string;
    private int fromColumn;
    private int toColumn;
    private String caseSensitive; // TODO: change to boolean -> FNDSTR.RPGLE

    public SearchArgument(String aString, int aFromColumn, int aToColumn, String aCaseSensitive) {
        this(aString, aFromColumn, aToColumn, aCaseSensitive, SearchOptions.CONTAINS);
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
            caseSensitive = SearchOptions.CASE_MATCH;
        } else {
            caseSensitive = SearchOptions.CASE_IGNORE;
        }
    }

    public SearchArgument(String aString, boolean anIsCaseSensitive, int anOperator) {
        this(aString, -1, -1, anIsCaseSensitive, anOperator);
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

    public void setRange(int aFromColumn, int aToColumn) {
        fromColumn = aFromColumn;
        toColumn = aToColumn;
    }

}
