/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;

import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;

public abstract class AbstractSpooledFileDecorateWithAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    protected String decorationStyleKey;

    public AbstractSpooledFileDecorateWithAction(String decorationStyleKey) {
        this.decorationStyleKey = decorationStyleKey;
    }

    @Override
    public void run(IAction action) {
        super.run(action);
    }

    @Override
    public void run() {

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

    @Override
    public void selectionChanged(IAction action, ISelection sel) {
        super.selectionChanged(action, sel);

        if (!(getFirstSelectedRemoteObject() instanceof ISpooledFileSubSystem)) {
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

}