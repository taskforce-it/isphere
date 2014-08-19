/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.actions.DisplaySystemMessageAction;
import org.eclipse.swt.widgets.Display;

import biz.isphere.core.internal.Member;

import com.ibm.etools.iseries.rse.ui.IBMiRSEPlugin;
import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.rse.ui.resources.QSYSTempFileListener;
import com.ibm.etools.iseries.rse.ui.resources.TemporaryQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.resources.IQSYSTemporaryStorage;

public class RSEMember extends Member {

    private IQSYSMember _member;
    private QSYSEditableRemoteSourceFileMember _editableMember;
    private String label;
    private boolean archive;
    private String archiveLibrary;
    private String archiveFile;
    private String archiveMember;
    private String archiveDate;
    private String archiveTime;

    public RSEMember(IQSYSMember _member) throws Exception {
        super();
        this._member = _member;
        if (_member != null) {
            _editableMember = new QSYSEditableRemoteSourceFileMember(_member);
        }
        label = null;
        archive = false;
        archiveLibrary = null;
        archiveFile = null;
        archiveMember = null;
        archiveDate = null;
        archiveTime = null;
    }

    public IBMiConnection getRSEConnection() {
        return _editableMember.getISeriesConnection();
    }

    @Override
    public String getConnection() {
        return _editableMember.getISeriesConnection().getConnectionName();
    }

    @Override
    public String getLibrary() {
        return _member.getLibrary();
    }

    @Override
    public String getSourceFile() {
        return _member.getFile();
    }

    @Override
    public String getMember() {
        return _member.getName();
    }

    @Override
    public boolean exists() {
        if (_editableMember == null) {
            return false;
        }
        return _editableMember.exists();
    }

    @Override
    public void download(IProgressMonitor monitor) throws Exception {
        _editableMember.download(monitor);
    }

    @Override
    public void upload(IProgressMonitor monitor) throws Exception {
        // _editableMember.upload(monitor);
        IQSYSTemporaryStorage storage = new TemporaryQSYSMember(_editableMember);
        try {
            if (storage.create()) {
                if (storage.uploadToISeries(monitor)) {
                    if (storage.copyToMember(_editableMember.getMember().getName())) {
                    }
                }
                storage.delete();
            }
        } catch (SystemMessageException sme) {
            IBMiRSEPlugin.logError("Error uploading member", sme);
            DisplaySystemMessageAction msgAction = new DisplaySystemMessageAction(sme.getSystemMessage());
            Display.getDefault().syncExec(msgAction);
        }
    }

    @Override
    public IFile getLocalResource() {
        return _editableMember.getLocalResource();
    }

    @Override
    public void openStream() throws Exception {
        _editableMember.openStream();
    }

    @Override
    public void closeStream() throws Exception {
        _editableMember.closeStream();
    }

    @Override
    public void addIgnoreFile() {
        QSYSTempFileListener.getListener().addIgnoreFile(_editableMember.getLocalResource());
    }

    @Override
    public void removeIgnoreFile() {
        QSYSTempFileListener.getListener().removeIgnoreFile(_editableMember.getLocalResource());
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
    public String getArchiveLibrary() {
        return archiveLibrary;
    }

    @Override
    public void setArchiveLibrary(String archiveLibrary) {
        this.archiveLibrary = archiveLibrary;
    }

    @Override
    public String getArchiveFile() {
        return archiveFile;
    }

    @Override
    public void setArchiveFile(String archiveFile) {
        this.archiveFile = archiveFile;
    }

    @Override
    public String getArchiveMember() {
        return archiveMember;
    }

    @Override
    public void setArchiveMember(String archiveMember) {
        this.archiveMember = archiveMember;
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

}
