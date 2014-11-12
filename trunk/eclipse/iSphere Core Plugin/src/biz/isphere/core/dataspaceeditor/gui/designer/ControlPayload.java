/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.gui.designer;

import org.eclipse.swt.widgets.Control;

import biz.isphere.core.dataspaceeditor.model.AbstractDWidget;

public class ControlPayload {

    private AbstractDWidget widget;
    private Control control;

    public ControlPayload(AbstractDWidget widget, Control control) {
        this.widget = widget;
        this.control = control;
    }

    public AbstractDWidget getWidget() {
        return widget;
    }

    public Control getControl() {
        return control;
    }
    
}
