/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.ISpherePlugin;

public class General extends PreferencePage implements IWorkbenchPreferencePage {

	public General() {
		super();
		noDefaultAndApplyButton();
	}
	
	public Control createContents(Composite parent) {
		Composite _container = new Composite(parent, SWT.NULL);
		_container.setLayout(new FillLayout(SWT.VERTICAL));
		
		ScrolledComposite sc = new ScrolledComposite(_container, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite container = new Composite(sc, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		final Label labelTaskForceImage = new Label(container, SWT.NONE);
		labelTaskForceImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelTaskForceImage.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ISPHERE));

		final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator1.setLayoutData(gd_labelSeparator1);
		
		final Label labelISphere = new Label(container, SWT.NONE);
		labelISphere.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelISphere.setText("iSphere Version 1.4.2");

		final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator2.setLayoutData(gd_labelSeparator2);
		
		final Label labelFeature0 = new Label(container, SWT.NONE);
		labelFeature0.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature0.setText("iSphere provides the following features");
		
		final Label labelFeature1 = new Label(container, SWT.NONE);
		labelFeature1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature1.setText("Message File Editor");
		
		final Label labelFeature2 = new Label(container, SWT.NONE);
		labelFeature2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature2.setText("Binding Directory Editor");
		
		final Label labelFeature3 = new Label(container, SWT.NONE);
		labelFeature3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature3.setText("Compare/Merge Editor");
		
		final Label labelFeature4 = new Label(container, SWT.NONE);
		labelFeature4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature4.setText("Source File Search");
		
		final Label labelFeature5 = new Label(container, SWT.NONE);
		labelFeature5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature5.setText("Message File Search");
		
		final Label labelFeature6 = new Label(container, SWT.NONE);
		labelFeature6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature6.setText("Spooled Files Subsystem");
		
		// Compute size
		Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
	    // Set the child as the scrolled content of the ScrolledComposite
	    sc.setContent(container);

	    // Set the minimum size
	    sc.setMinSize(point.x, point.y);

	    // Expand both horizontally and vertically
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
				
		return _container;
	}

	public void init(IWorkbench workbench) {
	}

}
