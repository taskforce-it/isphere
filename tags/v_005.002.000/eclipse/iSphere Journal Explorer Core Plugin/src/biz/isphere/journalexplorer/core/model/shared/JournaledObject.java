/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.journalexplorer.core.handlers.ISelectedObject;
import biz.isphere.journalexplorer.core.internals.JournalExplorerHelper;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;

public class JournaledObject implements ISelectedObject {

    private QualifiedName qualifiedObjectName;
    private String objectType;

    private transient boolean isJournaled;
    private transient Journal journal;
    private transient ObjectDescription journalObjectDescription;

    public JournaledObject(String connectionName, String libraryName, String objectName, String objectType) {

        checksObjectType(objectType);

        this.qualifiedObjectName = new QualifiedName(connectionName, libraryName, objectName);
        this.objectType = objectType;
    }

    private void checksObjectType(String objectType) {

        if (JournalExplorerHelper.isValidObjectType(objectType)) {
            return;
        }

        throw new IllegalArgumentException("Invalid object type: " + objectType);
    }

    public String getConnectionName() {
        return qualifiedObjectName.getConnectionName();
    }

    public String getName() {
        return qualifiedObjectName.getObjectName();
    }

    public String getLibrary() {
        return qualifiedObjectName.getLibraryName();
    }

    public String getMember() {
        throw new IllegalAccessError();
    }

    public String getObjectType() {
        return objectType;
    }

    public boolean isFile() {
        return JournalExplorerHelper.isFile(objectType);
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

    public String getQualifiedName() {
        return qualifiedObjectName.getQualifiedName();
    }

    private Journal resolveJournal() {

        if (journalObjectDescription == null) {

            journal = null;
            isJournaled = false;

            try {

                String qsysObjectPath = new QSYSObjectPathName(qualifiedObjectName.getLibraryName(), qualifiedObjectName.getObjectName(),
                    getObjectType(objectType)).getPath();
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

    private String getObjectType(String objectType) {

        if (!StringHelper.isNullOrEmpty(objectType)) {
            if (objectType.startsWith("*")) {
                return objectType.substring(1);
            }
        }

        return objectType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((qualifiedObjectName == null) ? 0 : qualifiedObjectName.hashCode());
        result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JournaledObject other = (JournaledObject)obj;
        if (qualifiedObjectName == null) {
            if (other.qualifiedObjectName != null) return false;
        } else if (!qualifiedObjectName.equals(other.qualifiedObjectName)) return false;
        if (objectType == null) {
            if (other.objectType != null) return false;
        } else if (!objectType.equals(other.objectType)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getQualifiedName(), getObjectType());
    }
}
