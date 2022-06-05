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

package de.taskforce.isphere.rse.spooledfiles;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.systems.core.messages.SystemMessage;
import com.ibm.etools.systems.core.ui.SystemWidgetHelpers;
import com.ibm.etools.systems.filters.ui.SystemFilterStringEditPane;

import de.taskforce.isphere.spooledfiles.SpooledFileBaseFilterStringEditPane;

public class SpooledFileFilterStringEditPane extends SystemFilterStringEditPane {
		
	private SpooledFileBaseFilterStringEditPane base = new SpooledFileBaseFilterStringEditPane();

	public SpooledFileFilterStringEditPane(Shell shell) {
		super(shell);
	}
	
	public Control createContents(Composite parent) {
		
		int nbrColumns = 2;
		Composite composite_prompts = SystemWidgetHelpers.createComposite(parent, nbrColumns);	
		((GridLayout)composite_prompts.getLayout()).marginWidth = 0;
		
		ModifyListener keyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
					validateStringInput();
			}
		};
		
		base.createContents(composite_prompts, keyListener, inputFilterString);
		
		return composite_prompts;
		
	}
	
	public Control getInitialFocusControl() {
		return base.getInitialFocusControl();
	}	
	
	protected void doInitializeFields() {
		base.doInitializeFields(inputFilterString);
	}
	
	protected void resetFields() {
		base.resetFields();
	}
	
	protected boolean areFieldsComplete() {
		return base.areFieldsComplete();
	}
	
	public String getFilterString() {
		return base.getFilterString();
	}	
	
	public SystemMessage verify() {
		return null;
	}

}
