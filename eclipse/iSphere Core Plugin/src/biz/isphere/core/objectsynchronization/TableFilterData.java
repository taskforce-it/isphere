/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

/**
 * Class that provides the selection settings for the table filter.
 */
public class TableFilterData {

    private boolean copyRight;
    private boolean copyLeft;
    private boolean noCopy;
    private boolean equal;
    private boolean singles;
    private boolean duplicates;

    public boolean isCopyRight() {
        return copyRight;
    }

    public void setCopyRight(boolean copyRight) {
        this.copyRight = copyRight;
    }

    public boolean isCopyLeft() {
        return copyLeft;
    }

    public void setCopyLeft(boolean copyLeft) {
        this.copyLeft = copyLeft;
    }

    public boolean isCopyNotEqual() {
        return noCopy;
    }

    public void setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean eEqual) {
        this.equal = eEqual;
    }

    public boolean isSingles() {
        return singles;
    }

    public void setSingles(boolean singles) {
        this.singles = singles;
    }

    public boolean isDuplicates() {
        return duplicates;
    }

    public void setDuplicates(boolean duplicates) {
        this.duplicates = duplicates;
    }
}
