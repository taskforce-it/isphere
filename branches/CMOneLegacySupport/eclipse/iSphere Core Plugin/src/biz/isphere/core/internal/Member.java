/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectLock;

public abstract class Member {

    public abstract String getConnection();

    public abstract String getLibrary();

    public abstract String getSourceFile();

    public abstract String getMember();

    public abstract boolean exists();

    public abstract boolean download(IProgressMonitor monitor) throws Exception;

    public abstract String upload(IProgressMonitor monitor) throws Exception;

    public abstract IFile downloadMember(IProgressMonitor monitor) throws Exception;

    public abstract String uploadMember(IProgressMonitor monitor, IFile localResource) throws Exception;

    public abstract IFile getLocalResource();

    public abstract String[] getContents() throws Exception;

    public abstract void setContents(String[] contents) throws Exception;

    public abstract String getDescription();

    public abstract String getSourceType();

    public abstract String getLabel();

    public abstract void setLabel(String label);

    public abstract boolean isArchive();

    public abstract void setArchive(boolean archive);

    public abstract String getArchiveLibrary();

    public abstract void setArchiveLibrary(String archiveLibrary);

    public abstract String getArchiveFile();

    public abstract void setArchiveFile(String archiveFile);

    public abstract String getArchiveMember();

    public abstract void setArchiveMember(String archiveMember);

    public abstract String getArchiveDate();

    public abstract void setArchiveDate(String archiveDate);

    public abstract String getArchiveTime();

    public abstract void setArchiveTime(String archiveTime);

    public abstract ISeriesHostObjectLock queryLocks() throws Exception;

    public abstract String getMemberLockedMessages(ISeriesHostObjectLock lock);

    public boolean hasSequenceNumbersAndDateFields() {
        return true;
    }

    public void openStream() throws Exception {
    }

    public void closeStream() throws Exception {
    }

    public void addIgnoreFile() {
    }

    public void removeIgnoreFile() {
    }

}
