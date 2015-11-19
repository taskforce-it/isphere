/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;

public class PreferencePage1 extends PreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage1() {
		super();
		noDefaultAndApplyButton();
	}
	
	public Control createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		final Label labelPoweredBy = new Label(container, SWT.NONE);
		final GridData gd_labelPoweredBy = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		labelPoweredBy.setLayoutData(gd_labelPoweredBy);
		labelPoweredBy.setText(Messages.getString("iSphere_5250_Emulator_is_based_on_TN5250j_Emulator"));
		
		final Label labelTN5250JImage = new Label(container, SWT.NONE);
		labelTN5250JImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelTN5250JImage.setImage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_TN5250JSPLASH));
				
		return container;
		
	}

	public void init(IWorkbench workbench) {
	}

}
