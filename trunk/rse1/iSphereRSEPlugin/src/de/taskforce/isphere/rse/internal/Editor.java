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

import org.eclipse.ui.PlatformUI;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.resources.ISeriesEditableSrcPhysicalFileMember;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.editor.SystemTextEditor;

import de.taskforce.isphere.internal.IEditor;

public class Editor implements IEditor {

	public void openEditor(Object connection, String library, String file, String member, int statement, String mode) {

		if (connection instanceof ISeriesConnection) {

			ISeriesConnection _connection = (ISeriesConnection)connection;
			
			ISeriesMember _member = null;
			try {
				_member = _connection.getISeriesMember(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), library, file, member);
			} 
			catch (SystemMessageException e) {
				e.printStackTrace();
			} 

			if (member != null) {
				
				ISeriesEditableSrcPhysicalFileMember mbr = null;
				try {
					mbr = new ISeriesEditableSrcPhysicalFileMember(_member);
				} 
				catch (SystemMessageException e) {
					e.printStackTrace();
				}

				if (mbr != null) {
					
					if (PlatformUI.getWorkbench().getEditorRegistry().findEditor("com.ibm.etools.systems.editor") != null) {

						if (mode.equals("*OPEN")) {
							mbr.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false, "com.ibm.etools.systems.editor");
						}
						else if (mode.equals("*BROWSE")) {
							mbr.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true, "com.ibm.etools.systems.editor");
						}
						
						if (statement != 0) {
					        if(!mbr.openIsCanceled()) {
					        	SystemTextEditor systemTextEditor = mbr.getEditor();
					            if(systemTextEditor != null) {
					            	systemTextEditor.gotoLine(statement);
					            }
					        }
						}
						
					}

				}
				
			}
			
		}
		
	}

}
