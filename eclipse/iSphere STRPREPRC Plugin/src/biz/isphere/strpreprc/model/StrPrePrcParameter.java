/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import biz.isphere.core.clcommands.CLParameter;
import biz.isphere.core.clcommands.CLParser;

public class StrPrePrcParameter {

    private static final String OPEN_PARENTHESIS = "("; //$NON-NLS-1$
    private static final String CLOSE_PARENTHESIS = ")"; //$NON-NLS-1$

    private StrPrePrcParameterType type;
    private String keyword;
    private String value;

    int startLine;
    int endLine;

    private StrPrePrcParameter(String keyword, String value, StrPrePrcParameterType type) {
        this(keyword, value, type, -1, -1);
    }

    private StrPrePrcParameter(String keyword, String value, StrPrePrcParameterType type, int startLine, int endLine) {

        this.type = type;
        this.keyword = keyword;
        this.value = value;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public static StrPrePrcParameter createBaseParameter(String parameterString) {
        CLParser parser = new CLParser();
        CLParameter parameter = parser.parseParameter(parameterString);
        return new StrPrePrcParameter(parameter.getKeyword(), parameter.getValue(), StrPrePrcParameterType.BASE);
    }

    public static StrPrePrcParameter createCompileParameter(String parameterString, int startLine, int endLine) {
        CLParser parser = new CLParser();
        CLParameter parameter = parser.parseParameter(parameterString);
        return new StrPrePrcParameter(parameter.getKeyword(), parameter.getValue(), StrPrePrcParameterType.COMPILE, startLine, endLine);
    }

    public static StrPrePrcParameter createLinkParameter(String parameterString, int startLine, int endLine) {
        CLParser parser = new CLParser();
        CLParameter parameter = parser.parseParameter(parameterString);
        return new StrPrePrcParameter(parameter.getKeyword(), parameter.getValue(), StrPrePrcParameterType.LINK, startLine, endLine);
    }

    public void setType(StrPrePrcParameterType type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StrPrePrcParameterType getType() {
        return type;
    }

    public String getParameter() {
        return keyword + "(" + value + ")";
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(type.getType());
        buffer.append(": ");
        buffer.append(getParameter());
        return buffer.toString();
    }
}
