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

import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.ui.PlatformUI;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.systems.editor.SystemTextEditor;

import de.taskforce.isphere.internal.IEditor;

public class Editor implements IEditor {

	public void openEditor(Object connection, String library, String file, String member, int statement, String mode) {

		if (connection instanceof IBMiConnection) {

			IBMiConnection _connection = (IBMiConnection)connection;
			
			IQSYSMember _member = null;
			try {
				_member = _connection.getMember(library, file, member, null);
			} 
			catch (SystemMessageException e) {
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (member != null) {
				
				QSYSEditableRemoteSourceFileMember mbr = null;
				try {
					mbr = new QSYSEditableRemoteSourceFileMember(_member);
				} 
				catch (SystemMessageException e) {
					e.printStackTrace();
				}

				if (mbr != null) {
					
					if (PlatformUI.getWorkbench().getEditorRegistry().findEditor("com.ibm.etools.systems.editor") != null) {

						if (mode.equals("*OPEN")) {
							mbr.open("com.ibm.etools.systems.editor", false, null);
						}
						else if (mode.equals("*BROWSE")) {
							mbr.open("com.ibm.etools.systems.editor", true, null);
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
