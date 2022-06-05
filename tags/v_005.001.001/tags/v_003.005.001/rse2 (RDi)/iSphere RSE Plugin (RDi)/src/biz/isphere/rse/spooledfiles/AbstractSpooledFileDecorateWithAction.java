/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;

public abstract class AbstractSpooledFileDecorateWithAction implements IObjectActionDelegate {

    private IWorkbenchPart targetPart;
    private IAction action;
    private Shell shell;

    private String decorationStyleKey;
    private IStructuredSelection structuredSelection;

    public AbstractSpooledFileDecorateWithAction(String decorationStyleKey) {
        this.decorationStyleKey = decorationStyleKey;
    }

    public void run(IAction action) {

        Object[] selection = getSelectedRemoteObjects();
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] instanceof ISpooledFileSubSystem) {
                ISpooledFileSubSystem spooledFileSubsystem = (ISpooledFileSubSystem)selection[i];
                SpooledFileTextDecoration decorationStyle = SpooledFileTextDecoration.getDecorationStyleByKey(decorationStyleKey);
                spooledFileSubsystem.setDecorationTextStyle(decorationStyle);
                spooledFileSubsystem.commit();
            }
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
        this.action = action;
        this.shell = targetPart.getSite().getShell();
    }

    public void setChecked(boolean checked) {
        action.setChecked(checked);
    }

    public void setEnabled(boolean enabled) {
        action.setEnabled(enabled);
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (!action.isEnabled()) {
            return;
        }

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
            setEnabled(false);
        }

        if (!(getFirstSelectedRemoteObject() instanceof ISpooledFileSubSystem)) {
            setEnabled(false);
            return;
        }

        ISpooledFileSubSystem spooledFileSubsystem = (ISpooledFileSubSystem)getFirstSelectedRemoteObject();
        String decorationStyle = spooledFileSubsystem.getDecorationTextStyle().getKey();

        if (this.decorationStyleKey.equals(decorationStyle)) {
            setChecked(true);
        } else {
            setChecked(false);
        }
    }

    public Object getFirstSelectedRemoteObject() {
        if (structuredSelection == null) {
            return null;
        }

        return structuredSelection.getFirstElement();
    }

    public Object[] getSelectedRemoteObjects() {

        Object[] selectedObjects;
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
            selectedObjects[(idx++)] = i.next();
        }

        return selectedObjects;
    }
}