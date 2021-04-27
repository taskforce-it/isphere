/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.json.JsonSerializable;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.helpers.TimeTaken;
import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;

import com.google.gson.annotations.Expose;

/**
 * Class to hold the {@link JournalEntry} as received from a journal or a
 * journal output file. A journal entry can be serialized in Json format.
 * 
 * <pre>
 * JournalEntries
 *    |
 *    +-- JournalEntry[]
 * </pre>
 * 
 * Classes {@link JournalEntries} and {@link JournalEntry} are the data model,
 * whereas {@link JournalProperties}, {@link JournalProperty} and
 * {@link JOESDProperty} build the GUI model.
 */
public class JournalEntries implements JsonSerializable {

    @Expose(serialize = true, deserialize = true)
    private List<JournalEntry> journalEntries;
    @Expose(serialize = true, deserialize = true)
    private String connectionName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileLibraryName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileMemberName;
    @Expose(serialize = true, deserialize = true)
    private int numAvailableRows;
    @Expose(serialize = true, deserialize = true)
    private List<IBMiMessage> messages;
    @Expose(serialize = true, deserialize = true)
    private boolean isCanceled;

    // Transient values
    private transient List<JournalEntry> filteredJournalEntries;
    private transient HashSet<JournaledObject> journaledObjects;
    private transient OutputFile outputFile;
    private transient boolean isOverflow;

    /**
     * Produces a JournalEntries object without an output file name and hence
     * without output format information. This constructor is used by the Json
     * importer. The output file is set from the Json data or (for iSphere < 4.0
     * Json files) at the end of the import operation by calling
     * {@link #finalizeJsonLoading(String, SQLWhereClause)}. .
     */
    public JournalEntries() {
        this(null, 0);
    }

    /**
     * Produces a JournalEntries object for a given output file and initial
     * capacity.
     * 
     * @param outputFile - output file.
     * @param initialCapacity - initial capacity.
     */
    public JournalEntries(OutputFile outputFile, int initialCapacity) {

        if (outputFile != null) {
            this.connectionName = outputFile.getConnectionName();
            this.outputFileName = outputFile.getFileName();
            this.outputFileLibraryName = outputFile.getLibraryName();
            this.outputFileMemberName = outputFile.getMemberName();
        }

        this.journalEntries = new ArrayList<JournalEntry>(initialCapacity);
        this.isOverflow = false;
        this.numAvailableRows = -1;

        // Transient values
        this.filteredJournalEntries = null;
        this.journaledObjects = new HashSet<JournaledObject>();
        this.outputFile = null;
    }

    public String getConnectionName() {
        return connectionName;
    }

    /*
     * Returns the output file or record format (*TYPE1 - *TYPE5) of the journal
     * entry.
     */
    public OutputFile getOutputFile() {

        if (outputFile == null && (connectionName != null && outputFileLibraryName != null && outputFileName != null && outputFileMemberName != null)) {
            outputFile = new OutputFile(connectionName, outputFileLibraryName, outputFileName, outputFileMemberName);
        }

        return outputFile;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public JournaledFile[] getJournaledFiles() {

        Set<JournaledFile> journaledFiles = new HashSet<JournaledFile>();

        for (JournaledObject journaledObject : journaledObjects) {
            if (journaledObject instanceof JournaledFile) {
                JournaledFile journaledFile = (JournaledFile)journaledObject;
                String connectionName = journaledFile.getConnectionName();
                String libraryName = journaledFile.getLibraryName();
                String fileName = journaledFile.getName();
                String memberName = journaledFile.getMember();
                journaledFiles.add(new JournaledFile(connectionName, libraryName, fileName, memberName)); // $NON-NLS-1$
            }
        }

        return journaledFiles.toArray(new JournaledFile[journaledFiles.size()]);
    }

    public void applyFilter(SQLWhereClause whereClause, IProgressMonitor monitor) throws ParseException {

        if (whereClause == null || !whereClause.hasClause()) {
            removeFilter();
            return;
        }

        TimeTaken timeTaken = TimeTaken.start("Filtering journal entries"); // //$NON-NLS-1$

        filteredJournalEntries = new ArrayList<JournalEntry>(journalEntries.size());

        boolean isFound;

        int count = 0;
        for (JournalEntry journalEntry : journalEntries) {

            count++;

            if (monitor != null && count % 50 == 0) {
                monitor.setTaskName(Messages.Status_Filtering_journal_entries + "(" + count + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            RowJEP sqljep = null;
            Comparable<?>[] row = null;
            if (whereClause.hasClause()) {
                if (whereClause.hasSpecificFields() && tableDoesNotMatch(whereClause, journalEntry)) {
                    // Always not found, when the where clause includes record
                    // specific fields and the table does not match.
                    isFound = false;
                } else {
                    // Compare JO* and record specific fields
                    sqljep = new RowJEP(whereClause.getClause());
                    sqljep.parseExpression(journalEntry.getColumnMapping());
                    row = journalEntry.getRow();
                    isFound = (Boolean)sqljep.getValue(row);
                }
            } else {
                // Compare only JO* fields.
                sqljep = new RowJEP(whereClause.getClause());
                sqljep.parseExpression(JournalEntry.getBasicColumnMapping());
                row = journalEntry.getBasicRow();
                isFound = (Boolean)sqljep.getValue(row);
            }

            if (isFound) {
                filteredJournalEntries.add(journalEntry);
            }
        }

        timeTaken.stop();
    }

    private boolean tableDoesNotMatch(SQLWhereClause whereClause, JournalEntry journalEntry) {

        if (!whereClause.getFile().equals(journalEntry.getObjectName()) || !whereClause.getLibrary().equals(journalEntry.getObjectLibrary())) {
            return true;
        }

        return false;
    }

    public void removeFilter() {
        this.filteredJournalEntries = null;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public void add(JournalEntry journalEntry) {

        if (filteredJournalEntries != null) {
            throw new IllegalAccessError("Cannot add entry when filter is active."); //$NON-NLS-1$
        }

        getItems().add(journalEntry);
        addJournaledObject(journalEntry);
    }

    public List<JournalEntry> getItems() {

        if (filteredJournalEntries != null) {
            return filteredJournalEntries;
        } else {
            return journalEntries;
        }
    }

    public JournalEntry getItem(int index) {

        if (getItems().size() < 0) {
            return null;
        }

        return getItems().get(index);
    }

    public boolean isOverflow() {
        return isOverflow;
    }

    public void setOverflow(boolean isOverflow, int numAvailableRows) {

        this.isOverflow = isOverflow;
        this.numAvailableRows = numAvailableRows;
    }

    public int size() {

        return getItems().size();
    }

    public int getNumberOfRowsAvailable() {

        if (isOverflow()) {
            return numAvailableRows;
        } else {
            return getNumberOfRowsDownloaded();
        }
    }

    public int getNumberOfRowsDownloaded() {
        return journalEntries.size();
    }

    public void clear() {

        removeFilter();
        getItems().clear();
    }

    public void setMessages(List<IBMiMessage> messages) {
        this.messages = messages;
    }

    public IBMiMessage[] getMessages() {

        if (messages == null) {
            return new IBMiMessage[0];
        }

        return messages.toArray(new IBMiMessage[messages.size()]);
    }

    public void finalizeJsonLoading(String connectionName, SQLWhereClause whereClause) {

        try {

            /*
             * Apply initial SQL where clause, takes some time, because meta
             * data must be resolved
             */
            if (whereClause.hasClause()) {

                List<JournalEntry> filteredJournalEntries = new ArrayList<JournalEntry>();

                RowJEP sqljep = new RowJEP(whereClause.getClause());

                for (JournalEntry journalEntry : journalEntries) {

                    sqljep.parseExpression(journalEntry.getColumnMapping());
                    Comparable<?>[] row = journalEntry.getRow();
                    if ((Boolean)sqljep.getValue(row)) {
                        filteredJournalEntries.add(journalEntry);
                    }

                }

                journalEntries = filteredJournalEntries;
            }

        } catch (ParseException e) {
            ISpherePlugin.logError("*** Could no apply SQL where clause ***", e);
        }

        /* Build a distinct list of journaled objects */
        journaledObjects = new HashSet<JournaledObject>();
        for (JournalEntry journalEntry : journalEntries) {
            if (!StringHelper.isNullOrEmpty(connectionName)) {
                journalEntry.overwriteConnectionName(connectionName);
            }
            addJournaledObject(journalEntry);
        }

        // Overwrite connection name if specified
        if (!StringHelper.isNullOrEmpty(connectionName)) {
            setConnectionName(connectionName);
        }

        /*
         * Hack for old export files, exported prior to iSphere v4.0
         */
        // TODO: remove at the end of 2021.
        if (this.outputFileName == null && getItems().size() > 0) {
            if (this.connectionName == null) {
                this.connectionName = getItem(0).getConnectionName();
            }
            this.outputFileName = getItem(0).getOutFileName();
            this.outputFileLibraryName = getItem(0).getOutFileLibrary();
            this.outputFileMemberName = getItem(0).getOutFileMemberName();
        }
    }

    private void addJournaledObject(JournalEntry journalEntry) {

        String objectType = journalEntry.getObjectType();
        // if (JournalExplorerHelper.isValidObjectType(objectType)) {
        String connectionName = journalEntry.getConnectionName();
        String objectName = journalEntry.getObjectName();
        String libraryName = journalEntry.getObjectLibrary();
        if (journalEntry.isFile()) {
            String memberName = journalEntry.getMemberName();
            journaledObjects.add(new JournaledFile(connectionName, libraryName, objectName, memberName));
        } else {
            journaledObjects.add(new JournaledObject(connectionName, libraryName, objectName, objectType));
        }
        // }

    }
}
