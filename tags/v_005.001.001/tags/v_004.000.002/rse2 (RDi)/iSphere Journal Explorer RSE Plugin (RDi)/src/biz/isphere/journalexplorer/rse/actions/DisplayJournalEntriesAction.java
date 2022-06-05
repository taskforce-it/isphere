/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;
import biz.isphere.journalexplorer.core.handlers.DisplayJournalEntriesHandler;
import biz.isphere.journalexplorer.core.handlers.ISelectedJournal;
import biz.isphere.journalexplorer.core.handlers.ISelectedObject;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteMember;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemotePhysicalFile;

/**
 * This action is used when the user requests loading journal entries from a
 * file or member.
 */
public class DisplayJournalEntriesAction implements IObjectActionDelegate {

    private Shell shell;

    protected IStructuredSelection structuredSelection;

    public DisplayJournalEntriesAction() {
        return;
    }

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            List<ISelectedObject> selectedObjects = new ArrayList<ISelectedObject>();
            List<ISelectedJournal> selectedJournals = new ArrayList<ISelectedJournal>();

            Iterator<?> iterator = structuredSelection.iterator();
            while (iterator.hasNext()) {

                Object _object = iterator.next();

                ISelectedObject selectedObject = null;
                ISelectedJournal selectedJournal = null;

                if (_object instanceof QSYSRemoteMember) {

                    QSYSRemoteMember member = (QSYSRemoteMember)_object;
                    String connectionName = member.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
                    String libraryName = member.getLibrary();
                    String fileName = member.getFile();
                    String memberName = member.getName();

                    selectedObject = new JournaledFile(connectionName, libraryName, fileName, memberName);

                } else if (_object instanceof QSYSRemotePhysicalFile) {

                    QSYSRemotePhysicalFile file = (QSYSRemotePhysicalFile)_object;
                    String connectionName = file.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
                    String libraryName = file.getLibrary();
                    String fileName = file.getName();
                    String memberName = "*ALL"; //$NON-NLS-1$

                    selectedObject = new JournaledFile(connectionName, libraryName, fileName, memberName);

                } else if (_object instanceof QSYSRemoteObject) {

                    QSYSRemoteObject remoteObject = (QSYSRemoteObject)_object;
                    String connectionName = remoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
                    String libraryName = remoteObject.getLibrary();
                    String objectName = remoteObject.getName();
                    String objectType = remoteObject.getType();

                    if (ISeries.JRN.equals(remoteObject.getType())) {
                        selectedJournal = new Journal(connectionName, libraryName, objectName);
                    } else if (ISeries.DTAARA.equals(remoteObject.getType())) {
                        selectedObject = new JournaledObject(connectionName, libraryName, objectName, objectType);
                    } else if (ISeries.DTAQ.equals(remoteObject.getType())) {
                        selectedObject = new JournaledObject(connectionName, libraryName, objectName, objectType);
                    }
                }

                if (selectedObject != null) {
                    selectedObjects.add(selectedObject);
                }

                if (selectedJournal != null) {
                    selectedJournals.add(selectedJournal);
                }
            }

            if (!selectedObjects.isEmpty()) {
                try {

                    DisplayJournalEntriesHandler handler = new DisplayJournalEntriesHandler();
                    handler.handleDisplayJournalEntries(selectedObjects.toArray(new ISelectedObject[selectedObjects.size()]));

                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not open journal exploer view ***", e);
                    MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                }
            }

            if (!selectedJournals.isEmpty()) {
                try {

                    DisplayJournalEntriesHandler handler = new DisplayJournalEntriesHandler();
                    handler.handleDisplayJournalEntries(selectedJournals.toArray(new ISelectedJournal[selectedObjects.size()]));

                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not open journal exploer view ***", e);
                    MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                }
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = (IStructuredSelection)selection;
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }
}
