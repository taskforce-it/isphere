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

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.spooledfiles.ConfirmDeletionSpooledFiles;
import biz.isphere.core.spooledfiles.SpooledFile;

import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.model.ISystemRemoteChangeEvents;
import com.ibm.etools.systems.model.SystemRegistry;

public class SpooledFileDeleteAction extends AbstractSpooledFileAction {

	private ArrayList<SpooledFileResource> spooledFileResources;
	
	public void init() {
		spooledFileResources = new ArrayList<SpooledFileResource>();
	}

	public String execute(SpooledFileResource spooledFileResource) {

		spooledFileResource.getSpooledFile().setData(spooledFileResource);
		
		spooledFileResources.add(spooledFileResource);
		
		return null;
		
	}

	public String finish() {
	    
	    ArrayList<SpooledFile> spooledFiles = new ArrayList<SpooledFile>();
	    for (SpooledFileResource resource : spooledFileResources) {
            spooledFiles.add(resource.getSpooledFile());
        }
	    
        ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), spooledFiles.toArray(new SpooledFile[spooledFiles.size()]));
        if (dialog.open() == Dialog.OK) {

            DeletePostRun postRun = new DeletePostRun();
            postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

            new DeleteExec().execute(spooledFileResources, postRun);
        }
		
//      TODO: Remove disabled statements 
//		SpooledFile[] _spooledFiles = new SpooledFile[spooledFiles.size()];
//		spooledFiles.toArray(_spooledFiles);
//		
//		ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), _spooledFiles);
//		if (dialog.open() == Dialog.OK) {
//			
//			for (int index = 0; index < _spooledFiles.length; index++) {
//				
//				String message = _spooledFiles[index].delete();
//				
//				if (message == null) {
//					SystemRegistry sr = SystemPlugin.getDefault().getSystemRegistry();
//					Vector<SpooledFileResource> spooledFileVector = new Vector<SpooledFileResource>();
//					spooledFileVector.addElement((SpooledFileResource)_spooledFiles[index].getData());
//					sr.fireRemoteResourceChangeEvent(
//							ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_CREATED, 
//							spooledFileVector, 
//							null, 
//							null, 
//							null, 
//							null);
//				}
//				else {
//
//					return message;
//					
//				}
//				
//			}
//			
//		}
		
		return null;
		
	}

}