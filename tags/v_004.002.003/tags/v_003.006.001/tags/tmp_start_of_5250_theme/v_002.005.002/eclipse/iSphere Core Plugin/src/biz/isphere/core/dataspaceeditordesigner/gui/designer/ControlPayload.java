/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.designer;

import java.util.EventListener;

import org.eclipse.swt.widgets.Control;

import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;

/**
 * Instances of this class are attached to controls that display the content of
 * a DEditor widget. Objects of this class are used to store information about
 * the data that is displayed in the control. For now that is the widget that is
 * the source of the control. The widget is required, in order to know where the
 * data comes from.
 */
public class ControlPayload {

    private AbstractDWidget widget;
    private Control control;
    private boolean locked;
    private EventListener listener;
    private boolean invalidDataWarning;
    private boolean invalidDataError;

    public ControlPayload(AbstractDWidget widget, Control control) {
        this.widget = widget;
        this.control = control;
        this.locked = false;
        this.listener = null;
    }

    public AbstractDWidget getWidget() {
        return widget;
    }

    public Control getControl() {
        return control;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the 'locked' status of the control. Data of a locked control cannot
     * be modified.
     * 
     * @param locked - locked status of the control
     * @param listener - listener that ensures the locked status
     */
    public void setLocked(boolean locked, EventListener listener) {
        this.locked = locked;
        this.listener = listener;
    }

    public EventListener getLockedListener() {
        return listener;
    }

    public void setInvalidDataWarning(boolean invalidDataWarning) {
        this.invalidDataWarning = invalidDataWarning;
    }

    public boolean hasInvalidDataWarning() {
        return invalidDataWarning;
    }

    public void setInvalidDataError(boolean invalidDataWarning) {
        this.invalidDataError = invalidDataWarning;
    }

    public boolean hasInvalidDataError() {
        return invalidDataError;
    }

}
