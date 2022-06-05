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

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor;
import biz.isphere.rse.Messages;

public class BindingDirectoryEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

	protected ArrayList arrayListSelection;

	public BindingDirectoryEditorAction() {
		super(Messages.iSphere_Binding_Directory_Editor, "", null);
		arrayListSelection = new ArrayList();
		setContextMenuGroup("additions");
		allowOnMultipleSelection(true);
		setHelp("");
		setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_BINDING_DIRECTORY));
	}

	public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
		setShell(shell);
		menu.add("additions", this);
	}

	public boolean supportsSelection(IStructuredSelection selection) {

		this.arrayListSelection.clear();

		ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

		for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
			Object objSelection = iterSelection.next();
			if (objSelection instanceof DataElement) {
				DataElement dataElement = (DataElement)objSelection;
				ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
				if (type.isObject()) {
					String strType = ISeriesDataElementHelpers.getType(dataElement);
					if (strType.equalsIgnoreCase("*BNDDIR")) {
						arrayListSelection.add(dataElement);
					}
				}
			}
		}
		
		if (arrayListSelection.isEmpty()) {
			return false;
		}
		
		this.arrayListSelection = arrayListSelection;
		return true;
		
	}

	public void run() {

		if (arrayListSelection.size() > 0) {
			
			for (Iterator iterObjects = arrayListSelection.iterator(); iterObjects.hasNext();) {
				
				DataElement dataElement = (DataElement)iterObjects.next();
				
				ISeriesObject object = new ISeriesObject(dataElement);
				
				if (object != null) {
					
					String library = object.getLibrary();
					String bindingDirectory = object.getName();
					
					ISeriesConnection iseriesConnection = object.getISeriesConnection();
					
					if (iseriesConnection != null) {
						
						String host = iseriesConnection.getSystemConnection().getHostName();
						
						AS400 as400 = null;
						try {
							as400 = iseriesConnection.getAS400ToolboxObject(getShell());
						} 
						catch (SystemMessageException e) {
						}
						
						Connection jdbcConnection = null;
						try {
							jdbcConnection = iseriesConnection.getJDBCConnection(null, false);
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

}