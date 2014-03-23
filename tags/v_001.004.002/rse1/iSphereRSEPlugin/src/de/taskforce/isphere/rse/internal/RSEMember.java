/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package de.taskforce.isphere.rse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.etools.iseries.core.ISeriesTempFileListener;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.resources.ISeriesEditableSrcPhysicalFileMember;

import de.taskforce.isphere.internal.Member;

public class RSEMember extends Member {

	private ISeriesMember _member;
	private ISeriesEditableSrcPhysicalFileMember _editableMember;
	private String label;
	private boolean archive;
	private String archiveLibrary; 
	private String archiveFile; 
	private String archiveMember;
	private String archiveDate;
	private String archiveTime;
	
	public RSEMember(ISeriesMember _member) throws Exception {
		super();
		this._member = _member;
		if (_member != null) {
			_editableMember = new ISeriesEditableSrcPhysicalFileMember(_member);
		}
		label = null;
		archive = false;
		archiveLibrary = null; 
		archiveFile = null; 
		archiveMember = null;
		archiveDate = null;
		archiveTime = null;
	}

	public ISeriesConnection getRSEConnection() {
		return _member.getISeriesConnection();
	}
	
	public String getConnection() {
		return _member.getISeriesConnection().getConnectionName();
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
		_editableMember.upload(monitor);		
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
		ISeriesTempFileListener.getListener().addIgnoreFile(_editableMember.getLocalResource());
	}

	public void removeIgnoreFile() {
		ISeriesTempFileListener.getListener().removeIgnoreFile(_editableMember.getLocalResource());
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
