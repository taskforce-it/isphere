/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

public class CustomExpandItem extends ExpandItem {

    public static final int TYPE_FIX_HEIGHT = -1;
    public static final int TYPE_PACK_HEIGHT = -2;
    public static final int TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_FIX_HEIGHT = -3;
    public static final int TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT = -4;

    private int type;
    private int minHeight;
    private int tempType;

    public CustomExpandItem(ExpandBar parent, int style, int type, int minHeight) {
        super(parent, style);
        this.type = type;
        this.minHeight = minHeight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMinimumHeight() {
        return minHeight;
    }

    public void setMinimumHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getTemporaryType() {
        return tempType;
    }

    public void setTemporaryType(int tempType) {
        this.tempType = tempType;
    }

    @Override
    public void setControl(Control control) {
        super.setControl(control);
        if (type == TYPE_PACK_HEIGHT || type == TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT) {
            minHeight = control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        }
    }

    @Override
    protected void checkSubclass() {
    }

}
