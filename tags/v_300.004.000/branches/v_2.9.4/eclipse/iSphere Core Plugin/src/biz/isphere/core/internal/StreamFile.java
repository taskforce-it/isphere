/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class StreamFile {

    public abstract String getConnection();

    public abstract String getDirectory();

    public abstract String getStreamFile();

    public abstract boolean exists();

    public abstract boolean download(IProgressMonitor monitor) throws Exception;

    public abstract String upload(IProgressMonitor monitor) throws Exception;

    public abstract IFile getLocalResource();

    public abstract String getLabel();

    public abstract void setLabel(String label);

    public abstract boolean isArchive();

    public abstract void setArchive(boolean archive);

    public abstract String getArchiveDirectory();

    public abstract void setArchiveDirectory(String archiveDirectory);

    public abstract String getArchiveStreamFile();

    public abstract void setArchiveStreamFile(String archiveStreamFile);

    public abstract String getArchiveDate();

    public abstract void setArchiveDate(String archiveDate);

    public abstract String getArchiveTime();

    public abstract void setArchiveTime(String archiveTime);

    public void openStream() throws Exception {
    }

    public void closeStream() throws Exception {
    }

    public void addIgnoreFile() {
    }

    public void removeIgnoreFile() {
    }

}
