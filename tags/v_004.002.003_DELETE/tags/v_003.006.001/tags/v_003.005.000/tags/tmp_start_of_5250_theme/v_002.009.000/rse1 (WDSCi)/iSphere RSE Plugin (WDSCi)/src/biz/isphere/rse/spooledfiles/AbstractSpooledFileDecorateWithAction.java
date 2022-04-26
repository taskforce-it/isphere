/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;

import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;

import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;

public abstract class AbstractSpooledFileDecorateWithAction extends ISeriesSystemBaseAction {

    private String decorationStyleKey;

    public AbstractSpooledFileDecorateWithAction(String decorationStyleKey, String label) {
        super(label, null);

        this.decorationStyleKey = decorationStyleKey;

        // allowOnMultipleSelection(false);
    }

    @Override
    public void run() {

         Object[] selection = getSelectedRemoteObjects();
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] instanceof ISpooledFileSubSystem) {
                ISpooledFileSubSystem spooledFileSubsystem = (ISpooledFileSubSystem)selection[i];
                SpooledFileTextDecoration decorationStyle = SpooledFileTextDecoration.getDecorationStyleByKey(decorationStyleKey);
                spooledFileSubsystem.setDecorationTextStyle(decorationStyle);
            }
        }
    }

    @Override
    public boolean isEnabled() {

         if (getSelectedRemoteObjects().length == 1) {
         return true;
         }

        return false;
    }

    public Object getFirstSelectedRemoteObject() {
        Object[] objects = getSelectedRemoteObjects();
        if (objects.length==0) {
            return null;
        }

        return objects[0];
    }

    public Object[] getSelectedRemoteObjects() {

        Object[] selectedObjects;
        IStructuredSelection structuredSelection = getSelection();
        if (structuredSelection != null) {
            selectedObjects = new Object[structuredSelection.size()];
        } else {
            selectedObjects = new Object[0];
        }

        if (structuredSelection == null) {
            return selectedObjects;
        }

        Iterator<?> i = structuredSelection.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Object object = i.next();
            if (object instanceof ISpooledFileSubSystem) {
                selectedObjects[(idx++)] = object;
            }
        }

        return selectedObjects;
    }
}