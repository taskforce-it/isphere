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

    private QualifiedName objectName;
    private String objectType;

    private boolean isJournaled;
    private Journal journal;

    private transient ObjectDescription journalObjectDescription;
    private transient String journalName;

    public JournaledObject(String connectionName, String libraryName, String objectName, String objectType) {
        this.objectName = new QualifiedName(connectionName, libraryName, objectName);
        this.objectType = objectType;
    }

    public String getConnectionName() {
        return objectName.getConnectionName();
    }

    public String getObjectName() {
        return objectName.getObjectName();
    }

    public String getLibraryName() {
        return objectName.getLibraryName();
    }

    public boolean isJournaled() {

        if (getJournal() != null) {
            return isJournaled;
        } else {
            return false;
        }
    }

    public Journal getJournal() {
        return resolveJournal();
    }

    public String getQualifiedJournalName() {

        if (journalName == null && getJournal() != null) {
            journalName = new QualifiedName(getConnectionName(), getJournal().getLibrary(), getJournal().getName()).getQualifiedName();
        }

        return journalName;
    }

    public String getQualifiedName() {
        return objectName.getQualifiedName();
    }

    private Journal resolveJournal() {

        if (journalObjectDescription == null) {

            journal = null;
            isJournaled = false;

            try {

                String qsysObjectPath = new QSYSObjectPathName(objectName.getLibraryName(), objectName.getObjectName(), objectType).getPath();
                this.journalObjectDescription = new ObjectDescription(IBMiHostContributionsHandler.getSystem(getConnectionName()), qsysObjectPath);

                QSYSObjectPathName journalPathName = new QSYSObjectPathName(journalObjectDescription.getValueAsString(ObjectDescription.JOURNAL));
                this.isJournaled = (Boolean)journalObjectDescription.getValue(ObjectDescription.JOURNAL_STATUS);
                if (isJournaled) {
                    String journalName = journalPathName.getObjectName();
                    String libraryName = journalPathName.getLibraryName();
                    journal = new Journal(getConnectionName(), libraryName, journalName);
                }

            } catch (Throwable e) {
                // e.printStackTrace();
            }
        }

        return journal;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
