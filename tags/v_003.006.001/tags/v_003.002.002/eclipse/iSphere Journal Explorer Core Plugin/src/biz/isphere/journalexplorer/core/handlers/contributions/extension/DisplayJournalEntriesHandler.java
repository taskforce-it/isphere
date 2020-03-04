/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.handlers.contributions.extension;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;
import biz.isphere.journalexplorer.core.ui.dialogs.LoadJournalEntriesDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.IDisplayJournalEntriesContributions;

public class DisplayJournalEntriesHandler implements IDisplayJournalEntriesContributions {

    public void handleDisplayFileJournalEntries(String connectionName, String libraryName, String fileName, String memberName) {

        JournalExplorerView view;
        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            QSYSObjectPathName pathName = new QSYSObjectPathName(libraryName, fileName, "FILE"); //$NON-NLS-1$
            ObjectDescription objectDescription = new ObjectDescription(system, pathName.getPath());

            JournaledObject journaledObject = new JournaledFile(connectionName, objectDescription, memberName);
            if (!journaledObject.isJournaled()) {

                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Error_Object_A_B_is_not_journaled, libraryName, fileName));

            } else {

                LoadJournalEntriesDialog dialog = new LoadJournalEntriesDialog(getShell());
                if (dialog.open() == LoadJournalEntriesDialog.OK) {

                    journaledObject.setStartingDate(dialog.getStartDate());
                    journaledObject.setEndingDate(dialog.getEndDate());
                    journaledObject.setRecordsOnly(dialog.isRecordsOnly());

                    view = (JournalExplorerView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(JournalExplorerView.ID);
                    view.createJournalTab(journaledObject);
                }
            }

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
