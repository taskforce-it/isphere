/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalCode;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.ui.dialogs.LoadJournalEntriesDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.IDisplayJournalEntriesContributions;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedJournal;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedObject;

import com.ibm.as400.access.AS400;

public class DisplayJournalEntriesHandler implements IDisplayJournalEntriesContributions {

    private static final String MIN_OS_RELEASE = "V5R4M0"; //$NON-NLS-1$

    private LoadJournalEntriesDialog.SelectionCriterias selectionCriterias;

    /**
     * Displays the journal entries of a given list of journals.
     * 
     * @param selectedJournals - list of journal objects
     */
    public void handleDisplayJournalEntries(ISelectedJournal... selectedJournals) {

        selectionCriterias = null;

        if (selectedJournals.length == 0) {
            return;
        }

        try {

            for (ISelectedJournal selectedJournal : selectedJournals) {

                String connectionName = selectedJournal.getConnectionName();
                String library = selectedJournal.getLibrary();
                String name = selectedJournal.getName();
                Journal journal = new Journal(connectionName, library, name);

                handleDisplayFileJournalEntries(journal, new ArrayList<JournaledFile>(), selectionCriterias);
            }

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method DisplayJournalEntriesHandler.handleDisplayFileJournalEntries() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    /**
     * Displays the journal entries of a given list of physical files.
     * 
     * @param selectedFiles - list of file objects
     */
    public void handleDisplayFileJournalEntries(ISelectedFile... selectedFiles) {

        selectionCriterias = null;

        if (selectedFiles.length == 0) {
            return;
        }

        try {

            Map<String, List<ISelectedFile>> filesByConnection = groupObjectsByConnection(selectedFiles);

            for (String connectionName : filesByConnection.keySet()) {

                AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
                if (!ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), system)) {
                    return;
                }

                List<ISelectedFile> files = filesByConnection.get(connectionName);
                handleDisplayFileJournalEntries(connectionName, files);
            }

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method DisplayJournalEntriesHandler.handleDisplayFileJournalEntries() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    /**
     * Groups the selected files by the journals they are attached to. Displays
     * an error message for the files that are not journaled.
     * 
     * @param connectionName - name of the RSE connection
     * @param selectedFiles - list of selected files
     * @throws Exception
     */
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

        if (objectsByJournal.isEmpty()) {
            MessageDialog.openInformation(getShell(), Messages.bind(Messages.Title_Connection_A, connectionName), Messages.Error_No_object_selected);
            return;
        }

        if (selectionCriterias == null) {
            LoadJournalEntriesDialog dialog = new LoadJournalEntriesDialog(getShell(), selectedFiles);
            if (dialog.open() == LoadJournalEntriesDialog.OK) {
                selectionCriterias = dialog.getSelectionCriterias();
            } else {
                return;
            }
        }

        for (Journal journal : objectsByJournal.keySet()) {
            List<JournaledFile> journaledObjects = objectsByJournal.get(journal);
            handleDisplayFileJournalEntries(journal, journaledObjects, selectionCriterias);
        }
    }

    /**
     * Creates a tab for the journal the files are attached to and loads all
     * journal entries in that tab. entries.
     * 
     * @param journal - journal the files are attached to
     * @param journaledFiles - list of files
     * @param selectionCriterias - selection criterias
     * @throws Exception
     */
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
            tJrneToRtv.setJrnCde(JournalCode.R);
            tJrneToRtv.setEntTyp(selectionCriterias.getJournalEntryTypes());
        } else {
            tJrneToRtv.setJrnCde(JrneToRtv.JRNCDE_ALL);
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

    /**
     * Groups a list of objects by the RSE connection they came from. The object
     * could be a file or a journal.
     * 
     * @param selectedObjects - list of selected objects of various RSE
     *        connections
     * @return map with an entry per connection and the associated files
     */
    private Map<String, List<ISelectedFile>> groupObjectsByConnection(ISelectedFile[] selectedObjects) {

        Map<String, List<ISelectedFile>> filesByConnection = new HashMap<String, List<ISelectedFile>>();

        for (ISelectedFile file : selectedObjects) {
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

    /**
     * Creates a map per journal with the files attached to it.
     * 
     * @param connectionName - connection name
     * @param selectedFiles - selected files attached to various journals
     * @return map with an entry per journal and the files attached to it
     */
    private Map<Journal, List<JournaledFile>> groupObjectsByJournal(String connectionName, List<ISelectedFile> selectedFiles) {

        Map<Journal, List<JournaledFile>> objectsByJournal = new HashMap<Journal, List<JournaledFile>>();

        for (ISelectedObject object : selectedFiles) {

            // TODO: probably remove that
            if (object instanceof ISelectedFile) {
                ISelectedFile file = (ISelectedFile)object;

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
        }

        return objectsByJournal;
    }

    /**
     * Returns the current shell.
     * 
     * @return current shell
     */
    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
