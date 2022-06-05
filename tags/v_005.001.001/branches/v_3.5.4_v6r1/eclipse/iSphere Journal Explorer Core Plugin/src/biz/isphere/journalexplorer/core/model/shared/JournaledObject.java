/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;

public class JournaledObject {

    private String connectionName;
    private ObjectDescription objectDescription;

    private boolean isJournaled;
    private Journal journal;

    public JournaledObject(String connectionName, String libraryName, String objectName, String objectType) {
        this(connectionName, new QSYSObjectPathName(libraryName, objectName, getObjectType(objectType)));
    }

    protected JournaledObject(String connectionName, QSYSObjectPathName objectPathName) {

        this.connectionName = connectionName;
        this.objectDescription = new ObjectDescription(IBMiHostContributionsHandler.getSystem(connectionName), objectPathName.getPath());

        setJournalAttributes(connectionName, objectDescription);
    }

    protected static String getObjectType(String objectType) {

        if (objectType.startsWith("*")) {
            return objectType.substring(1);
        }

        return objectType;
    }

    private void setJournalAttributes(String connectionName, ObjectDescription objectDescription) {

        try {

            this.journal = null;

            QSYSObjectPathName journalPathName = new QSYSObjectPathName(objectDescription.getValueAsString(ObjectDescription.JOURNAL));
            this.isJournaled = (Boolean)objectDescription.getValue(ObjectDescription.JOURNAL_STATUS);
            if (isJournaled) {
                String journalName = journalPathName.getObjectName();
                String libraryName = journalPathName.getLibraryName();
                journal = new Journal(connectionName, libraryName, journalName);
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

    public Journal getJournal() {
        return journal;
    }

    public String getQualifiedJournalName() {
        return QualifiedName.getName(connectionName, getJournal().getLibrary(), getJournal().getName());
    }

    public String getQualifiedName() {
        return QualifiedName.getName(connectionName, objectDescription.getLibrary(), objectDescription.getName());
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
