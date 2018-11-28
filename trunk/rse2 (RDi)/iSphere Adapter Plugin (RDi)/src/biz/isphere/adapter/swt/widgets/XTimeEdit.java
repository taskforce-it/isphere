/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;

import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;

public class XTimeEdit implements ITimeEdit, SelectionListener {

    private DateTime dateTime;
    private List<ModifyListener> modifyListeners;

    public XTimeEdit(Composite parent, int style) {
        this.dateTime = new DateTime(parent, style | SWT.TIME | SWT.MEDIUM | SWT.DROP_DOWN);
        this.modifyListeners = new ArrayList<ModifyListener>();
    }

    public void setLayoutData(Object layoutData) {
        dateTime.setLayoutData(layoutData);
    }

    public void addModifyListener(ModifyListener listener) {
        if (listener == null) {
            return;
        }
        if (modifyListeners.size() == 0) {
            dateTime.addSelectionListener(this);
        }
        modifyListeners.add(listener);
    }

    public void removeModifyListener(ModifyListener listener) {
        if (listener == null) {
            return;
        }
        modifyListeners.remove(listener);
        if (modifyListeners.size() == 0) {
            dateTime.removeSelectionListener(this);
        }
    }

    public void setEnabled(boolean enabled) {
        dateTime.setEnabled(enabled);
    }

    public void setToolTipText(String text) {
        dateTime.setToolTipText(text);
    }

    public void setTime(int hours, int minutes, int seconds) {
        dateTime.setTime(hours, minutes, seconds);
    }

    public int getHours() {
        return dateTime.getHours();
    }

    public int getMinutes() {
        return dateTime.getMinutes();
    }

    public int getSeconds() {
        return dateTime.getSeconds();
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
        notifyModifyListeners(arg0);
    }

    public void widgetSelected(SelectionEvent arg0) {
        notifyModifyListeners(arg0);
    }

    private void notifyModifyListeners(SelectionEvent selectionEvent) {
        Event event = new Event();
        event.widget = dateTime;
        ModifyEvent modifyEvent = new ModifyEvent(event);
        for (ModifyListener listener : modifyListeners) {
            listener.modifyText(modifyEvent);
        }
    }
}
