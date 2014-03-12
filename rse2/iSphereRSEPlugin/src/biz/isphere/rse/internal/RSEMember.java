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

import biz.isphere.internal.Member;

import com.ibm.etools.iseries.rse.ui.IBMiRSEPlugin;
import com.ibm.etools.iseries.rse.ui.resources.QSYSTempFileListener;
import com.ibm.etools.iseries.rse.ui.resources.TemporaryQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.resources.IQSYSTemporaryStorage;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;


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
	
	public String getConnection() {
		return _editableMember.getISeriesConnection().getConnectionName();
	}

	public String getLibrary() {
		return _member.getLibrary();
	}

	public String getSourceFile() {
		return _member.getFile();
	}

	public String getMember() {
		return _member.getName();
	}

	public boolean exists() throws Exception {
		return _editableMember.exists();
	}

	public void download(IProgressMonitor monitor) throws Exception {
		_editableMember.download(monitor);		
	}

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
		}
		catch (SystemMessageException sme) {
			IBMiRSEPlugin.logError("Error uploading member", sme);
			DisplaySystemMessageAction msgAction = new DisplaySystemMessageAction(sme.getSystemMessage());
			Display.getDefault().syncExec(msgAction);
		}
	}

	public IFile getLocalResource() {
		return _editableMember.getLocalResource();
	}

	public void openStream() throws Exception {
		_editableMember.openStream();
	}

	public void closeStream() throws Exception {
		_editableMember.closeStream();
	}

	public void addIgnoreFile() {
		QSYSTempFileListener.getListener().addIgnoreFile(_editableMember.getLocalResource());
	}

	public void removeIgnoreFile() {
		QSYSTempFileListener.getListener().removeIgnoreFile(_editableMember.getLocalResource());
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public String getArchiveLibrary() {
		return archiveLibrary;
	}

	public void setArchiveLibrary(String archiveLibrary) {
		this.archiveLibrary = archiveLibrary;
	}

	public String getArchiveFile() {
		return archiveFile;
	}

	public void setArchiveFile(String archiveFile) {
		this.archiveFile = archiveFile;
	}

	public String getArchiveMember() {
		return archiveMember;
	}

	public void setArchiveMember(String archiveMember) {
		this.archiveMember = archiveMember;
	}

	public String getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(String archiveTime) {
		this.archiveTime = archiveTime;
	}
	
}
