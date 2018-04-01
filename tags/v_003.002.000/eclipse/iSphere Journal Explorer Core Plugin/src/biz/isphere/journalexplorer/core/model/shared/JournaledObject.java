/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import java.util.Date;

import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

public class JournaledObject {

    private String connectionName;
    private ObjectDescription objectDescription;

    private boolean isJournaled;
    private String journalName;
    private String journalLibraryName;
    private Date startingDate;
    private Date endingDate;
    private boolean recordsOnly;

    public JournaledObject(String connectionName, ObjectDescription objectDescription) {

        this.connectionName = connectionName;
        this.objectDescription = objectDescription;

        setJournalAttributes(objectDescription);
        setStartingDate(DateTimeHelper.getStartOfDay().getTime());
        setStartingDate(DateTimeHelper.getEndOfDay().getTime());
        setRecordsOnly(true);
    }

    private void setJournalAttributes(ObjectDescription objectDescription) {

        try {

            this.journalName = null;
            this.journalLibraryName = null;

            QSYSObjectPathName journal = new QSYSObjectPathName(objectDescription.getValueAsString(ObjectDescription.JOURNAL));
            this.isJournaled = (Boolean)objectDescription.getValue(ObjectDescription.JOURNAL_STATUS);
            if (isJournaled) {
                this.journalName = journal.getObjectName();
                this.journalLibraryName = journal.getLibraryName();
            }

        } catch (Throwable e) {
            this.isJournaled = false;
        }
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getObjectName() {
        return objectDescription.getName();
    }

    public String getLibraryName() {
        return objectDescription.getLibrary();
    }

    public boolean isJournaled() {

        if (isJournaled) {
            return isJournaled;
        } else {
            return false;
        }
    }

    public String getJournalName() {
        return journalName;
    }

    public String getJournalLibraryName() {
        return journalLibraryName;
    }

    public String getQualifiedName() {
        return QualifiedName.getName(connectionName, objectDescription.getLibrary(), objectDescription.getName());
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public boolean isRecordsOnly() {
        return recordsOnly;
    }

    public void setRecordsOnly(boolean recordsOnly) {
        this.recordsOnly = recordsOnly;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
