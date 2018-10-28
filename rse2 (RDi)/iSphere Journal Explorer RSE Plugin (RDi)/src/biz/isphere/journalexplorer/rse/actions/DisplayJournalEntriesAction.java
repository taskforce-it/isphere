/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler.DisplayJournalEntriesHandler;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler.SelectedFile;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteMember;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemotePhysicalFile;

public class DisplayJournalEntriesAction implements IObjectActionDelegate {

    private Shell shell;

    protected IStructuredSelection structuredSelection;

    public DisplayJournalEntriesAction() {
        return;
    }

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            List<ISelectedFile> selectedFiles = new ArrayList<ISelectedFile>();

            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {

                Object _object = iterator.next();

                ISelectedFile selectedFile = null;

                if (_object instanceof QSYSRemoteMember) {

                    QSYSRemoteMember member = (QSYSRemoteMember)_object;
                    String connectionName = member.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
                    String libraryName = member.getLibrary();
                    String fileName = member.getFile();
                    String memberName = member.getName();

                    selectedFile = new SelectedFile(connectionName, libraryName, fileName, memberName);

                } else if (_object instanceof QSYSRemotePhysicalFile) {

                    QSYSRemotePhysicalFile file = (QSYSRemotePhysicalFile)_object;
                    String connectionName = file.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
                    String libraryName = file.getLibrary();
                    String fileName = file.getName();
                    String memberName = "*FIRST"; //$NON-NLS-1$

                    selectedFile = new SelectedFile(connectionName, libraryName, fileName, memberName);
                }

                if (selectedFile != null) {
                    selectedFiles.add(selectedFile);
                }
            }

            if (!selectedFiles.isEmpty()) {
                DisplayJournalEntriesHandler.handleDisplayFileJournalEntries(selectedFiles.toArray(new ISelectedFile[selectedFiles.size()]));
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
