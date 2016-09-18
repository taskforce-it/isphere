/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.files.ui.resources.SystemEditableRemoteFile;
import org.eclipse.rse.files.ui.resources.SystemUniversalTempFileListener;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;

import biz.isphere.core.internal.StreamFile;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSEStreamFile extends StreamFile {

    private IRemoteFile _streamFile;
    private SystemEditableRemoteFile _editableStreamFile;
    private String label;
    private boolean archive;
    private String archiveDirectory;
    private String archiveStreamFile;
    private String archiveDate;
    private String archiveTime;

    public RSEStreamFile(IRemoteFile _streamFile) throws Exception {
        super();
        this._streamFile = _streamFile;
        if (_streamFile != null) {
            _editableStreamFile = new SystemEditableRemoteFile(_streamFile);
        }
        label = null;
        archive = false;
        archiveDirectory = null;
        archiveStreamFile = null;
        archiveDate = null;
        archiveTime = null;
    }

    public IBMiConnection getRSEConnection() {
        return IBMiConnection.getConnection(_editableStreamFile.getSubSystem().getHost());
    }

    @Override
    public String getConnection() {
        return IBMiConnection.getConnection(_editableStreamFile.getSubSystem().getHost()).getConnectionName();
    }

    @Override
    public String getDirectory() {
        return _streamFile.getParentPath();
    }

    @Override
    public String getStreamFile() {
        return _streamFile.getName();
    }

    @Override
    public boolean exists() {
        if (_editableStreamFile == null) {
            return false;
        }
        return _editableStreamFile.exists();
    }

    @Override
    public boolean download(IProgressMonitor monitor) throws Exception {
        return _editableStreamFile.download(monitor);
    }

    @Override
    public String upload(IProgressMonitor monitor) throws Exception {
        _editableStreamFile.doImmediateSaveAndUpload();
        return null;
    }

    @Override
    public IFile getLocalResource() {
        return _editableStreamFile.getLocalResource();
    }

    @Override
    public void openStream() throws Exception {
//        _editableStreamFile.openStream();
    }

    @Override
    public void closeStream() throws Exception {
//        _editableStreamFile.closeStream();
    }

    @Override
    public void addIgnoreFile() {
        SystemUniversalTempFileListener.getListener().addIgnoreFile(_editableStreamFile.getLocalResource());
    }

    @Override
    public void removeIgnoreFile() {
        SystemUniversalTempFileListener.getListener().removeIgnoreFile(_editableStreamFile.getLocalResource());
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isArchive() {
        return archive;
    }

    @Override
    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    @Override
    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    @Override
    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    @Override
    public String getArchiveStreamFile() {
        return archiveStreamFile;
    }

    @Override
    public void setArchiveStreamFile(String archiveStreamFile) {
        this.archiveStreamFile = archiveStreamFile;
    }

    @Override
    public String getArchiveDate() {
        return archiveDate;
    }

    @Override
    public void setArchiveDate(String archiveDate) {
        this.archiveDate = archiveDate;
    }

    @Override
    public String getArchiveTime() {
        return archiveTime;
    }

    @Override
    public void setArchiveTime(String archiveTime) {
        this.archiveTime = archiveTime;
    }

    @Override
    public String toString() {

        if (_streamFile == null) {
            return super.toString();
        }

        if (getConnection() != null) {
            return getConnection() + ":" + _streamFile.toString();
        } else {
            return _streamFile.toString();
        }
    }

}
