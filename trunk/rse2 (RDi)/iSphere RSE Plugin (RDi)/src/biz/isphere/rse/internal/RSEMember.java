/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;

import biz.isphere.core.internal.Member;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectLock;
import com.ibm.etools.iseries.rse.ui.IBMiRSEPlugin;
import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.rse.ui.resources.QSYSTempFileListener;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.QSYSResources;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.resources.QSYSRemoteMemberTransfer;

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
    public boolean download(IProgressMonitor monitor) throws Exception {
        if (_editableMember.getISeriesConnection().isOffline()) {
            String connectionName = _editableMember.getISeriesConnection().getConnectionName();
            String message = Messages.bind(Messages.Connection_is_offline, connectionName);
            throw new SystemMessageException(new SystemMessage("RSE", "ISPHERE", "0", SystemMessage.ERROR, message, ""));
        }
        return _editableMember.download(monitor);
    }

    @Override
    public String upload(IProgressMonitor monitor) throws Exception {
        return uploadMember(monitor, null);
    }

    public IFile downloadMember(IProgressMonitor monitor) throws Exception {

        if (!download(monitor)) {
            return null;
        }

        return getLocalResource();
    }

    @Override
    public String uploadMember(IProgressMonitor monitor, IFile localResource) throws Exception {

        _editableMember.connect();
        if (!_editableMember.isConnected()) {
            return Messages.bind(Messages.Failed_to_connect_to_system_A, _editableMember.getISeriesConnection().getConnectionName());
        }
        _editableMember.closeStream();

        String localPath;
        if (localResource == null) {
            localPath = _editableMember.getDownloadPath();
        } else {
            localPath = localResource.getLocation().toString();
        }

        QSYSRemoteMemberTransfer.acquireLock(localPath);

        try {

            ISeriesHostObjectLock lock = queryLocks();
            if (lock != null) {
                return getMemberLockedMessages(lock);
            }
            if (monitor != null) {
                monitor.beginTask(NLS.bind(QSYSResources.MSG_UPLOAD_PROGRESS, _editableMember.getMember().getAbsoluteName()), -1);
            }
            boolean insertSequenceNumbersIfRequired = true;

            IPreferenceStore prefStore = IBMiRSEPlugin.getDefault().getPreferenceStore();
            int startSeqNum = prefStore.getInt("com.ibm.etools.systems.editor.reseq.start");
            int incrSeqNum = prefStore.getInt("com.ibm.etools.systems.editor.reseq.incr");

            QSYSRemoteMemberTransfer memberTransfer = new QSYSRemoteMemberTransfer(_editableMember.getMember(), localPath);
            memberTransfer.upload(insertSequenceNumbersIfRequired, startSeqNum, incrSeqNum);

        } finally {
            QSYSRemoteMemberTransfer.releaseLock(localPath);
        }

        return null;
    }

    public ISeriesHostObjectLock queryLocks() throws Exception {
        return _editableMember.queryLocks();
    }

    public String getMemberLockedMessages(ISeriesHostObjectLock lock) {

        if (lock != null) {
            return Messages.bind(Messages.Member_C_of_file_A_slash_B_is_locked_by_job_F_slash_E_slash_D, new Object[] { getLibrary(),
                getSourceFile(), getMember(), lock.getJobName(), lock.getJobUser(), lock.getJobNumber() });
        }

        return null;
    }

    @Override
    public IFile getLocalResource() {
        return _editableMember.getLocalResource();
    }

    @Override
    public void setContents(String[] contents) throws Exception {
        _editableMember.setContents(contents, false, null);
    }

    @Override
    public String[] getContents() throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(getLocalResource().getContents(), "UTF-8")); //$NON-NLS-1$
        List<String> contents = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
            contents.add(line.substring(12)); // strip seq. number and date
        }

        return contents.toArray(new String[contents.size()]);
    }

    public String getDescription() {
        return _editableMember.getMember().getDescription();
    }

    public String getSourceType() {
        return _editableMember.getMember().getType();
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

    @Override
    public String toString() {

        if (_member == null) {
            return super.toString();
        }

        if (getConnection() != null) {
            return getConnection() + ":" + _member.toString();
        } else {
            return _member.toString();
        }
    }

}
