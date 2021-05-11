/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.session.SessionDetailDialog;
import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;
import biz.isphere.tn5250j.rse.subsystems.TN5250JSubSystem;

public class NewSessionAction implements IObjectActionDelegate {

    private ArrayList<TN5250JSubSystem> selectedSubSystems;

    public NewSessionAction() {
        selectedSubSystems = new ArrayList<TN5250JSubSystem>();
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
    }

    public void run(IAction action) {
        for (int idx = 0; idx < selectedSubSystems.size(); idx++) {
            Session session = new Session(TN5250JRSEPlugin.getRSESessionDirectory(selectedSubSystems.get(idx).getSystemProfileName(),
                selectedSubSystems.get(idx).getHostAliasName()));
            session.setConnection(selectedSubSystems.get(idx).getSystemProfileName() + "-" + selectedSubSystems.get(idx).getHostAliasName());
            SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                TN5250JRSEPlugin.getRSESessionDirectory(selectedSubSystems.get(idx).getSystemProfileName(), selectedSubSystems.get(idx)
                    .getHostAliasName()), DialogActionTypes.CREATE, session);
            if (sessionDetailDialog.open() == Dialog.OK) {
                RSESession rseSession = new RSESession(selectedSubSystems.get(idx), session.getName(), session);
                rseSession.create(selectedSubSystems.get(idx));
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        selectedSubSystems.clear();
        Iterator<?> theSet = ((IStructuredSelection)selection).iterator();
        while (theSet.hasNext()) {
            Object object = theSet.next();
            if (object instanceof TN5250JSubSystem) {
                selectedSubSystems.add((TN5250JSubSystem)object);
            }
        }
    }

}
