/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.events.ISystemRemoteChangeEvents;
import org.eclipse.rse.core.model.ISystemRegistry;

import biz.isphere.spooledfiles.ConfirmDeletionSpooledFiles;
import biz.isphere.spooledfiles.SpooledFile;


public class SpooledFileDeleteAction extends AbstractSpooledFileAction {

	private ArrayList<SpooledFile> spooledFiles;
	
	public void init() {
		spooledFiles = new ArrayList<SpooledFile>();
	}

	public String execute(SpooledFileResource spooledFileResource) {

		spooledFileResource.getSpooledFile().setData(spooledFileResource);
		
		spooledFiles.add(spooledFileResource.getSpooledFile());
		
		return null;
		
	}

	public String finish() {
		
		SpooledFile[] _spooledFiles = new SpooledFile[spooledFiles.size()];
		spooledFiles.toArray(_spooledFiles);
		
		ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), _spooledFiles);
		if (dialog.open() == Dialog.OK) {
			
			for (int index = 0; index < _spooledFiles.length; index++) {
				
				String message = _spooledFiles[index].delete();
				
				if (message == null) {
					ISystemRegistry sr = RSECorePlugin.getTheSystemRegistry();
					Vector<SpooledFileResource> spooledFileVector = new Vector<SpooledFileResource>();
					spooledFileVector.addElement((SpooledFileResource)_spooledFiles[index].getData());
					sr.fireRemoteResourceChangeEvent(
							ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_DELETED, 
							spooledFileVector, 
							null, 
							null, 
							null, 
							null);
				}
				else {

					return message;
					
				}
				
			}
			
		}
		
		return null;
		
	}

}