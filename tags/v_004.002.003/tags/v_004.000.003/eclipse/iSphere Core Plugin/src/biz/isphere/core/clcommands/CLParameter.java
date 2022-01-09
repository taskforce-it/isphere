/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

/**
 * This class represents a CL parameter that consists of a <i>keyword</i> and a
 * <i>value</i>.
 * 
 * @author Thomas Raddatz
 */
public class CLParameter {

    private String keyword;
    private String value;

    /**
     * Produces a new CLParameter object.
     */
    public CLParameter(String keyword, String value) {
        this.keyword = keyword;
        this.value = value;
    }

    /**
     * Returns the keyword of the CL parameter.
     * 
     * @return keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the value of the CL parameter.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this parameter.
     * 
     * @param value - New parameter value.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(keyword);
        buffer.append("(");
        buffer.append(value);
        buffer.append(")");

        return buffer.toString();
    }
}
