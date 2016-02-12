/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import biz.isphere.strpreprc.cl.CLParameter;
import biz.isphere.strpreprc.cl.CLParser;

public class StrPrePrcParameter extends CLParameter {

    private static final String OPEN_PARENTHESIS = "("; //$NON-NLS-1$
    private static final String CLOSE_PARENTHESIS = ")"; //$NON-NLS-1$

    private StrPrePrcParameterType type;

    private StrPrePrcParameter(String keyword, String value, StrPrePrcParameterType type) {
        super(keyword, value);

        this.type = type;
    }

    static StrPrePrcParameter createBaseParameter(String parameter) {
        CLParser parameterParser = CLParser.createParameterParser();
        parameterParser.parse(parameter);
        CLParameter clParameter = parameterParser.getParameter();
        String keyword = clParameter.getKeyword();
        String value = clParameter.getValue();
        return new StrPrePrcParameter(keyword, value, StrPrePrcParameterType.BASE);
    }

    static StrPrePrcParameter createCompileParameter(String parameter) {
        CLParser parameterParser = CLParser.createParameterParser();
        parameterParser.parse(parameter);
        CLParameter clParameter = parameterParser.getParameter();
        String keyword = clParameter.getKeyword();
        String value = clParameter.getValue();
        return new StrPrePrcParameter(keyword, value, StrPrePrcParameterType.COMPILE);
    }

    static StrPrePrcParameter createLinkParameter(String parameter) {
        CLParser parameterParser = CLParser.createParameterParser();
        parameterParser.parse(parameter);
        CLParameter clParameter = parameterParser.getParameter();
        String keyword = clParameter.getKeyword();
        String value = clParameter.getValue();
        return new StrPrePrcParameter(keyword, value, StrPrePrcParameterType.LINK);
    }

    public void setType(StrPrePrcParameterType type) {
        this.type = type;
    }

    public StrPrePrcParameterType getType() {
        return this.type;
    }

    public String getParameter() {
        return super.toString();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (type == StrPrePrcParameterType.BASE) {
            buffer.append("BASE:");
        } else if (type == StrPrePrcParameterType.COMPILE) {
            buffer.append("COMPILE:");
        } else {
            buffer.append("LINK:");
        }
        buffer.append(super.toString().trim());
        return buffer.toString();
    }
}
