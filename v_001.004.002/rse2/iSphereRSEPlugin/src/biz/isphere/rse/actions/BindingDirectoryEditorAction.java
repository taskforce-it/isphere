/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.bindingdirectoryeditor.BindingDirectoryEditor;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;


public class BindingDirectoryEditorAction implements IObjectActionDelegate {

	protected IStructuredSelection structuredSelection;
	protected Shell shell;
	
	public void run(IAction arg0) {
		
		if (structuredSelection != null && 
				!structuredSelection.isEmpty()) {
			
			Object object = structuredSelection.getFirstElement();

			if (object instanceof QSYSRemoteObject) {

				QSYSRemoteObject remoteObject = (QSYSRemoteObject)object;
				
				String profil = remoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getSystemProfileName();
				String connection = remoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();
				String host = remoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHost().getName();
				
				if (remoteObject.getType().equals("*BNDDIR")) {
					
					String library = remoteObject.getLibrary();
					String bindingDirectory = remoteObject.getName();

					IBMiConnection ibmiConnection = IBMiConnection.getConnection(profil, connection);
					
					if (ibmiConnection != null) {
						
						AS400 as400 = null;
						try {
							as400 = ibmiConnection.getAS400ToolboxObject();
						} 
						catch (SystemMessageException e) {
						}
						
						Connection jdbcConnection = null;
						try {
							jdbcConnection = ibmiConnection.getJDBCConnection(null, false);
						} 
						catch (SQLException e1) {
						}
						
						if (as400 != null && jdbcConnection != null) {

							BindingDirectoryEditor.openEditor(
									as400, 
									jdbcConnection, 
									host, 
									library, 
									bindingDirectory,
									"*EDIT");
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			structuredSelection = ((IStructuredSelection)selection);
		}
		else {
			structuredSelection = null;	
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
		shell = workbenchPart.getSite().getShell();		
	}

}
