/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.swt.widgets;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

public class ButtonLockedListener implements SelectionListener {

    private boolean locked;

    public void setLockedValue(boolean locked) {
        this.locked = locked;
    }

    public void widgetSelected(SelectionEvent event) {
        restoreLockedValue(event);
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        return;
    }

    private boolean getSelection(SelectionEvent event) {
        return ((Button)event.getSource()).getSelection();
    }

    private void restoreLockedValue(SelectionEvent event) {
        if (locked != getSelection(event)) {
            ((Button)event.getSource()).setSelection(locked);
        }
    }

}
