/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

public class AbstractSearchOptionConfiguration {

    private String label;
    private boolean isRegularExpression;
    private boolean isCaseSensitive;
    private boolean isCondition;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRegularExpression() {
        return isRegularExpression;
    }

    public void setRegularExpression(boolean isRegularExpression) {
        this.isRegularExpression = isRegularExpression;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public boolean isCondition() {
        return isCondition;
    }

    public void setCondition(boolean isCondition) {
        this.isCondition = isCondition;
    }

    public boolean validate(String value) {
        return true;
    }
}
