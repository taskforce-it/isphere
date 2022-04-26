/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.handlers;

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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dialog.ConfirmErrorsDialog;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.externalapi.Access;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;
import biz.isphere.journalexplorer.core.ui.dialogs.LoadJournalEntriesDialog;

import com.ibm.as400.access.AS400;

public class DisplayJournalEntriesHandler {

    private static final String MIN_OS_RELEASE = "V5R4M0"; //$NON-NLS-1$

    private SelectionCriteria selectionCriteria;

    /**
     * Displays the journal entries of a given list of objects.
     * 
     * @param selectedObjects - list of objects
     */
    public void handleDisplayJournalEntries(ISelectedJournal... selectedJournals) throws Exception {

        LoadJournalEntriesDialog dialog = new LoadJournalEntriesDialog(getShell());
        if (dialog.open() == LoadJournalEntriesDialog.OK) {
            selectionCriteria = dialog.getSelectionCriteria();
        } else {
            return;
        }

        for (ISelectedJournal selectedJournal : selectedJournals) {
            Access.openJournalExplorerView(getShell(), selectedJournal.getConnectionName(), selectedJournal.getLibrary(), selectedJournal.getName(),
                selectionCriteria, true);
        }
    }

    /**
     * Displays the journal entries of a given list of objects.
     * 
     * @param selectedObjects - list of objects
     */
    public void handleDisplayJournalEntries(ISelectedObject... selectedObjects) throws Exception {

        selectionCriteria = null;

        if (selectedObjects.length == 0) {
            return;
        }

        Map<String, List<ISelectedObject>> objectsByConnection = groupObjectsByConnection(selectedObjects);

        for (String connectionName : objectsByConnection.keySet()) {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            if (system == null) {
                return;
            }

            if (!ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), system)) {
                return;
            }

            List<ISelectedObject> objects = objectsByConnection.get(connectionName);
            if (!handleDisplayJournalEntries(connectionName, objects)) {
                return;
            }
        }
    }

    /**
     * Groups the selected objects by the journals they are attached to.
     * Displays an error message for objects that are not journaled.
     * 
     * @param connectionName - name of the RSE connection
     * @param selectedObjects - list of selected objects
     * @throws Exception
     */
    private boolean handleDisplayJournalEntries(String connectionName, List<ISelectedObject> selectedObjects) throws Exception {

        Map<Journal, List<ISelectedObject>> objectsByJournal = groupObjectsByJournal(connectionName, selectedObjects);

        List<ISelectedObject> objectNotJournaled = objectsByJournal.get(null);
        if (objectNotJournaled != null) {

            List<String> objects = new LinkedList<String>();

            Iterator<ISelectedObject> it = objectNotJournaled.iterator();
            while (it.hasNext()) {
                ISelectedObject object = it.next();
                objects.add(object.getQualifiedName());
            }

            if (!ConfirmErrorsDialog.openConfirm(getShell(), Messages.bind(Messages.Title_Connection_A, connectionName),
                Messages.Error_The_following_objects_are_not_journaled_Continue_anyway, objects.toArray(new String[objects.size()]))) {
                return true;
            }

            objectsByJournal.remove(null);
        }

        if (objectsByJournal.isEmpty()) {
            MessageDialog.openInformation(getShell(), Messages.bind(Messages.Title_Connection_A, connectionName), Messages.Error_No_object_selected);
            return true;
        }

        LoadJournalEntriesDialog dialog = new LoadJournalEntriesDialog(getShell(),
            selectedObjects.toArray(new ISelectedObject[selectedObjects.size()]));
        if (dialog.open() == LoadJournalEntriesDialog.OK) {
            selectionCriteria = dialog.getSelectionCriteria();
        } else {
            return true;
        }

        for (Journal journal : objectsByJournal.keySet()) {
            List<ISelectedObject> journaledObjects = objectsByJournal.get(journal);
            if (!handleDisplayJournalEntries(journal, journaledObjects, selectionCriteria)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a tab per journal to and loads all journal entries in that tab.
     * 
     * @param journal - journal , the objects are attached to
     * @param journaledObjects - list of journaled objects
     * @param selectionCriteria - selection criteria
     * @throws Exception
     */
    private boolean handleDisplayJournalEntries(Journal journal, List<ISelectedObject> journaledObjects, SelectionCriteria selectionCriteria)
        throws Exception {

        if (IBMiHostContributionsHandler.getSystem(journal.getConnectionName()) == null) {
            return false;
        }

        String osRelease = ISpherePlugin.getDefault().getIBMiRelease(journal.getConnectionName());
        if (MIN_OS_RELEASE.compareTo(osRelease) > 0) {
            throw new Exception(Messages.bind(Messages.Error_Cannot_perform_action_OS400_must_be_at_least_at_level_A, MIN_OS_RELEASE));
        }

        Access.openJournalExplorerView(getShell(), journal.getConnectionName(), journal.getLibrary(), journal.getName(),
            journaledObjects.toArray(new JournaledObject[journaledObjects.size()]), selectionCriteria, true);

        return true;
    }

    /**
     * Groups a list of objects by the RSE connection they came from.
     * 
     * @param selectedObjects - list of selected objects of various RSE
     *        connections
     * @return map with an entry per connection and the associated objects
     */
    private Map<String, List<ISelectedObject>> groupObjectsByConnection(ISelectedObject[] selectedObjects) {

        Map<String, List<ISelectedObject>> objectsByConnection = new HashMap<String, List<ISelectedObject>>();

        for (ISelectedObject object : selectedObjects) {
            String connectionName = object.getConnectionName();
            List<ISelectedObject> objectsOfConnection = objectsByConnection.get(connectionName);
            if (objectsOfConnection == null) {
                objectsOfConnection = new ArrayList<ISelectedObject>();
                objectsByConnection.put(connectionName, objectsOfConnection);
            }
            objectsOfConnection.add(object);
        }

        return objectsByConnection;
    }

    /**
     * Creates a map per journal with the objects attached to it.
     * 
     * @param connectionName - connection name
     * @param selectedObjects - selected objects attached to various journals
     * @return map with an entry per journal and the objects attached to it
     */
    private Map<Journal, List<ISelectedObject>> groupObjectsByJournal(String connectionName, List<ISelectedObject> selectedObjects) {

        Map<Journal, List<ISelectedObject>> objectsByJournal = new HashMap<Journal, List<ISelectedObject>>();

        for (ISelectedObject object : selectedObjects) {

            Journal journal;
            JournaledObject journaledObject;

            if (object instanceof JournaledObject) {
                journaledObject = (JournaledObject)object;
            } else {

                String libraryName = object.getLibrary();
                String objectName = object.getName();

                if (object instanceof ISelectedFile) {
                    ISelectedFile file = (ISelectedFile)object;
                    String memberName = file.getMember();
                    journaledObject = new JournaledFile(connectionName, libraryName, objectName, memberName);
                } else {
                    String objType = object.getObjectType();
                    journaledObject = new JournaledObject(connectionName, libraryName, objectName, objType);
                }

            }
            journal = journaledObject.getJournal();

            List<ISelectedObject> objectsOfJournal = objectsByJournal.get(journal);
            if (objectsOfJournal == null) {
                objectsOfJournal = new ArrayList<ISelectedObject>();
                objectsByJournal.put(journal, objectsOfJournal);
            }

            if (journaledObject != null) {
                objectsOfJournal.add(journaledObject);
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
