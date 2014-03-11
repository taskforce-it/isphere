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

package biz.isphere.info;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;


public class InfoDetail {

	public InfoDetail() {
	}
	
	public void createContents(Composite parent) {
		
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite container = new Composite(sc, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		final Label labelISphere = new Label(container, SWT.NONE);
		labelISphere.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelISphere.setText("iSphere " + ISpherePlugin.getDefault().getVersion());
		
		final Label labelTaskForceImage = new Label(container, SWT.NONE);
		labelTaskForceImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelTaskForceImage.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_TASKFORCE));
		
		final Composite compositeAdress = new Composite(container, SWT.NONE);
		compositeAdress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeAdress.setLayout(new GridLayout());

		final Label cA1 = new Label(compositeAdress, SWT.NONE);
		cA1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA1.setText("Task Force IT-Consulting GmbH");

		final Label cA2 = new Label(compositeAdress, SWT.NONE);
		cA2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA2.setText("Im Eickel 77");

		final Label cA3 = new Label(compositeAdress, SWT.NONE);
		cA3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA3.setText("45731 Waltrop");

		final Label cA4 = new Label(compositeAdress, SWT.NONE);
		cA4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA4.setText("Deutschland / Germany");

		final Composite compositeNumbers = new Composite(container, SWT.NONE);
		compositeNumbers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeNumbers.setLayout(new GridLayout());

		final Label cN1 = new Label(compositeNumbers, SWT.NONE);
		cN1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cN1.setText(Messages.getString("Telefon") + ": +49 23 09-60 93 01");

		final Label cN2 = new Label(compositeNumbers, SWT.NONE);
		cN2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cN2.setText(Messages.getString("Telefax") + ": +49 23 09-40 97 68");

		final Composite compositeInternet = new Composite(container, SWT.NONE);
		compositeInternet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeInternet.setLayout(new GridLayout());

		final Label cI1 = new Label(compositeInternet, SWT.NONE);
		cI1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cI1.setText(Messages.getString("E-Mail") + ": info@taskforce-it.de");

		final Label cI2 = new Label(compositeInternet, SWT.NONE);
		cI2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cI2.setText(Messages.getString("Internet") + ": www.taskforce-it.de");

		final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator1.setLayoutData(gd_labelSeparator1);
		
		final Label labelFeature11 = new Label(container, SWT.NONE);
		labelFeature11.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature11.setText("Message File Editor");
		
		final Label labelFeature12 = new Label(container, SWT.NONE);
		labelFeature12.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature12.setText("developed by Task Force IT-Consulting GmbH");
		
		final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator2.setLayoutData(gd_labelSeparator2);
		
		final Label labelFeature21 = new Label(container, SWT.NONE);
		labelFeature21.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature21.setText("Binding Directory Editor");
		
		final Label labelFeature22 = new Label(container, SWT.NONE);
		labelFeature22.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature22.setText("developed by Task Force IT-Consulting GmbH");

		final Label labelSeparator3 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator3.setLayoutData(gd_labelSeparator3);
		
		final Label labelFeature31 = new Label(container, SWT.NONE);
		labelFeature31.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature31.setText("Compare/Merge Editor");
		
		final Label labelFeature32 = new Label(container, SWT.NONE);
		labelFeature32.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature32.setText("originally developed by Softlanding Systems Inc. (RSE Extensions)");
		
		final Label labelFeature33 = new Label(container, SWT.NONE);
		labelFeature33.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature33.setText("enhanced and integrated into iSphere by Task Force IT-Consulting GmbH");
		
		final Label labelSeparator4 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator4 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator4.setLayoutData(gd_labelSeparator4);
		
		final Label labelFeature41 = new Label(container, SWT.NONE);
		labelFeature41.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature41.setText("Source File Search");
		
		final Label labelFeature42 = new Label(container, SWT.NONE);
		labelFeature42.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature42.setText("developed by Task Force IT-Consulting GmbH");
		
		final Label labelSeparator5 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator5 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator5.setLayoutData(gd_labelSeparator5);
		
		final Label labelFeature51 = new Label(container, SWT.NONE);
		labelFeature51.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature51.setText("Message File Search");
		
		final Label labelFeature52 = new Label(container, SWT.NONE);
		labelFeature52.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature52.setText("developed by Task Force IT-Consulting GmbH");
		
		final Label labelSeparator6 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator6 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator6.setLayoutData(gd_labelSeparator6);
		
		final Label labelFeature61 = new Label(container, SWT.NONE);
		labelFeature61.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature61.setText("Spooled Files Subsystem");
		
		final Label labelFeature62 = new Label(container, SWT.NONE);
		labelFeature62.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature62.setText("originally developed by Softlanding Systems Inc. (RSE Extensions)");
		
		final Label labelFeature63 = new Label(container, SWT.NONE);
		labelFeature63.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelFeature63.setText("enhanced and integrated into iSphere by Task Force IT-Consulting GmbH");
		
		final Label labelSeparator7 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator7 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator7.setLayoutData(gd_labelSeparator7);
		
		final Label labelCMOneImage = new Label(container, SWT.NONE);
		labelCMOneImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelCMOneImage.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CMONE));
		
		// Compute size
		Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
	    // Set the child as the scrolled content of the ScrolledComposite
	    sc.setContent(container);

	    // Set the minimum size
	    sc.setMinSize(point.x, point.y);

	    // Expand both horizontally and vertically
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
		
	}
	
	public boolean processButtonPressed() {	
		return true;
	}

}
