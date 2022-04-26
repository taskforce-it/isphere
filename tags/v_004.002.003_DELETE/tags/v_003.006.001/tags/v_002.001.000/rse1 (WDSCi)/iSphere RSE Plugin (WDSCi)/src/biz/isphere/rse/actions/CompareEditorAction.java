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

import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.compareeditor.CompareAction;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

public class CompareEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

	protected ArrayList arrayListSelection;

	public CompareEditorAction() {
		super(Messages.iSphere_Compare_Editor, "", null);
		arrayListSelection = new ArrayList();
		setContextMenuGroup("additions");
		allowOnMultipleSelection(true);
		setHelp("");
		setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COMPARE));
	}

	public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
		setShell(shell);
		menu.add("additions", this);
	}

	public void run() {
		if (arrayListSelection.size() > 0) {
			for (Iterator iterMembers = arrayListSelection.iterator(); iterMembers.hasNext();) {
				DataElement dataElement = (DataElement)iterMembers.next();
				ISeriesMember member = new ISeriesMember(dataElement);
				if (member != null) {
					
					try {
						
						RSEMember rseLeftMember = new RSEMember(member);
						
						RSECompareDialog dialog = new RSECompareDialog(getShell(), true, rseLeftMember);
						
						if (dialog.open() == Dialog.OK) {
							
							boolean editable = dialog.isEditable();
							boolean considerDate = dialog.isConsiderDate();
							boolean threeWay = dialog.isThreeWay();
							
							RSEMember rseAncestorMember = null;

							if (threeWay) {
								
								ISeriesMember ancestorMember = dialog.getAncestorConnection().getISeriesMember(
										getShell(), 
										dialog.getAncestorLibrary(), 
										dialog.getAncestorFile(), 
										dialog.getAncestorMember());
								
								if (ancestorMember != null) {
									rseAncestorMember = new RSEMember(ancestorMember);
								}
								
							}

							RSEMember rseRightMember = null;
							
							ISeriesMember rightMember = dialog.getRightConnection().getISeriesMember(
									getShell(), 
									dialog.getRightLibrary(), 
									dialog.getRightFile(), 
									dialog.getRightMember());
							
							if (rightMember != null) {
								rseRightMember = new RSEMember(rightMember);
							}

							CompareAction action = new CompareAction(editable, considerDate, threeWay, rseAncestorMember, rseLeftMember, rseRightMember, null);
							action.run();
							
						}

					} catch (Exception e) {
					}
					
				}
			}
		}
	}

	public boolean supportsSelection(IStructuredSelection selection) {

		this.arrayListSelection.clear();

		ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

		for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
			Object objSelection = iterSelection.next();
			if (objSelection instanceof DataElement) {
				DataElement dataElement = (DataElement)objSelection;
				ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
				if (type.isSourceMember()) {
					arrayListSelection.add(dataElement);
				}
			}
		}
		
		if (arrayListSelection.isEmpty() || arrayListSelection.size() != 1) {
			return false;
		}
		
		this.arrayListSelection = arrayListSelection;
		return true;
		
	}

}