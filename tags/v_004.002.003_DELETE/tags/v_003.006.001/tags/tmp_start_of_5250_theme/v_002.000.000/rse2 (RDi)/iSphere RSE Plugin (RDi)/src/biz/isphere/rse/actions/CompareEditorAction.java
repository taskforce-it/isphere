/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.compareeditor.CompareAction;

import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;

public class CompareEditorAction implements IObjectActionDelegate {

	protected IStructuredSelection structuredSelection;
	protected Shell shell;
	
	public void run(IAction arg0) {
		
		if (structuredSelection != null && 
				!structuredSelection.isEmpty()) {
			
			Object object = structuredSelection.getFirstElement();

			if (object instanceof QSYSRemoteSourceMember) {

				QSYSRemoteSourceMember member = (QSYSRemoteSourceMember)object;
				
				try {
					
					RSEMember rseLeftMember = new RSEMember(member);
					
					RSECompareDialog dialog = new RSECompareDialog(shell, true, rseLeftMember);
					
					if (dialog.open() == Dialog.OK) {
						
						boolean editable = dialog.isEditable();
						boolean considerDate = dialog.isConsiderDate();
						boolean threeWay = dialog.isThreeWay();
						
						RSEMember rseAncestorMember = null;

						if (threeWay) {
							
							IQSYSMember ancestorMember = dialog.getAncestorConnection().getMember(
									dialog.getAncestorLibrary(), 
									dialog.getAncestorFile(), 
									dialog.getAncestorMember(),
									null);
							
							if (ancestorMember != null) {
								rseAncestorMember = new RSEMember(ancestorMember);
							}
							
						}

						RSEMember rseRightMember = null;
						
						IQSYSMember rightMember = dialog.getRightConnection().getMember(
								dialog.getRightLibrary(), 
								dialog.getRightFile(), 
								dialog.getRightMember(),
								null);
						
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
