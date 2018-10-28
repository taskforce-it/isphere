/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.handlers.contributions.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dialog.ConfirmErrorsDialog;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.ui.dialogs.LoadJournalEntriesDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.IDisplayJournalEntriesContributions;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;

public class DisplayJournalEntriesHandler implements IDisplayJournalEntriesContributions {

    private static final String MIN_OS_RELEASE = "V5R4M0"; //$NON-NLS-1$

    LoadJournalEntriesDialog.SelectionCriterias selectionCriterias = null;

    public void handleDisplayFileJournalEntries(ISelectedFile... selectedFiles) {

        if (selectedFiles.length == 0) {
            return;
        }

        try {

            Map<String, List<ISelectedFile>> filesByConnection = groupFilesByConnection(selectedFiles);

            for (String connectionName : filesByConnection.keySet()) {
                List<ISelectedFile> files = filesByConnection.get(connectionName);
                handleDisplayFileJournalEntries(connectionName, files);
            }

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method DisplayJournalEntriesHandler.handleDisplayFileJournalEntries() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private void handleDisplayFileJournalEntries(String connectionName, List<ISelectedFile> selectedFiles) throws Exception {

        Map<Journal, List<JournaledFile>> objectsByJournal = groupObjectsByJournal(connectionName, selectedFiles);

        List<JournaledFile> objectNotJournaled = objectsByJournal.get(null);
        if (objectNotJournaled != null) {

            List<String> files = new LinkedList<String>();

            Iterator<JournaledFile> it = objectNotJournaled.iterator();
            while (it.hasNext()) {
                JournaledFile file = it.next();
                files.add(file.getQualifiedName());
            }

            if (!ConfirmErrorsDialog.openConfirm(getShell(), Messages.bind(Messages.Title_Connection_A, connectionName),
                Messages.Error_The_following_objects_are_not_journaled_Continue_anyway, files.toArray(new String[files.size()]))) {
                return;
            }

            objectsByJournal.remove(null);
        }

        if (selectionCriterias == null) {
            LoadJournalEntriesDialog dialog = new LoadJournalEntriesDialog(getShell());
            if (dialog.open() == LoadJournalEntriesDialog.OK) {
                selectionCriterias = dialog.getSelectionCriterias();
            }
        }

        for (Journal journal : objectsByJournal.keySet()) {
            List<JournaledFile> journaledObjects = objectsByJournal.get(journal);
            handleDisplayFileJournalEntries(journal, journaledObjects, selectionCriterias);
        }
    }

    private void handleDisplayFileJournalEntries(Journal journal, List<JournaledFile> journaledFiles,
        LoadJournalEntriesDialog.SelectionCriterias selectionCriterias) throws Exception {

        String osRelease = ISpherePlugin.getDefault().getIBMiRelease(journal.getConnectionName());
        if (MIN_OS_RELEASE.compareTo(osRelease) > 0) {
            throw new Exception(Messages.bind(Messages.Error_Cannot_perform_action_OS400_must_be_at_least_at_level_A, MIN_OS_RELEASE));
        }

        JrneToRtv tJrneToRtv = new JrneToRtv(journal);

        tJrneToRtv.setFromTime(selectionCriterias.getStartDate());
        tJrneToRtv.setToTime(selectionCriterias.getEndDate());

        if (selectionCriterias.isRecordsOnly()) {
            tJrneToRtv.setEntTyp(JrneToRtv.ENTTYP_RCD);
        } else {
            tJrneToRtv.setEntTyp(JrneToRtv.ENTTYP_ALL);
        }

        tJrneToRtv.setNullIndLen(JrneToRtv.NULLINDLEN_VARLEN);
        tJrneToRtv.setNbrEnt(selectionCriterias.getMaxItemsToRetrieve());

        for (JournaledFile journaledFile : journaledFiles) {
            tJrneToRtv.addFile(journaledFile.getLibraryName(), journaledFile.getObjectName(), journaledFile.getMemberName());
        }

        JournalExplorerView view = (JournalExplorerView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .showView(JournalExplorerView.ID);
        view.createJournalTab(tJrneToRtv);
    }

    private Map<String, List<ISelectedFile>> groupFilesByConnection(ISelectedFile[] selectedFiles) {

        Map<String, List<ISelectedFile>> filesByConnection = new HashMap<String, List<ISelectedFile>>();

        for (ISelectedFile file : selectedFiles) {
            String connectionName = file.getConnectionName();
            List<ISelectedFile> filesOfConnection = filesByConnection.get(connectionName);
            if (filesOfConnection == null) {
                filesOfConnection = new ArrayList<ISelectedFile>();
                filesByConnection.put(connectionName, filesOfConnection);
            }
            filesOfConnection.add(file);
        }

        return filesByConnection;
    }

    private Map<Journal, List<JournaledFile>> groupObjectsByJournal(String connectionName, List<ISelectedFile> selectedFiles) {

        Map<Journal, List<JournaledFile>> objectsByJournal = new HashMap<Journal, List<JournaledFile>>();

        for (ISelectedFile file : selectedFiles) {

            String libraryName = file.getLibrary();
            String fileName = file.getName();
            String memberName = file.getMember();

            JournaledFile journaledObject = new JournaledFile(connectionName, libraryName, fileName, memberName);

            Journal journal = journaledObject.getJournal();
            List<JournaledFile> filesOfJournal = objectsByJournal.get(journal);
            if (filesOfJournal == null) {
                filesOfJournal = new ArrayList<JournaledFile>();
                objectsByJournal.put(journal, filesOfJournal);
            }
            filesOfJournal.add(journaledObject);
        }

        return objectsByJournal;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
