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
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;

public class Contributors extends PreferencePage implements IWorkbenchPreferencePage {

    public Contributors() {
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
        cN1.setText(Messages.getString("Phone") + ": +49 23 09-60 93 01");

        final Label cN2 = new Label(compositeNumbers, SWT.NONE);
        cN2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        cN2.setText(Messages.getString("Fax") + ": +49 23 09-40 97 68");

        final Composite compositeInternet = new Composite(container, SWT.NONE);
        compositeInternet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeInternet.setLayout(new GridLayout());

        final Label cI1 = new Label(compositeInternet, SWT.NONE);
        cI1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        cI1.setText(Messages.getString("E-Mail") + ": info@taskforce-it.de");

        final Link cI2 = new Link(compositeInternet, SWT.NONE);
        cI2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        cI2.setText(Messages.getString("Internet") + ": <a href=\"www.taskforce-it.de\">www.taskforce-it.de</a>");
        cI2.addSelectionListener(new LinkSelectionListener());

        final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator1.setLayoutData(gd_labelSeparator1);
        
        final Label labelTools400Image = new Label(container, SWT.NONE);
        labelTools400Image.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTools400Image.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_TOOLS400));
        
        final Label labelTools400Name = new Label(container, SWT.NONE);
        labelTools400Name.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTools400Name.setText("Thomas Raddatz");

        final Composite compositeInternetTools400 = new Composite(container, SWT.NONE);
        compositeInternetTools400.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeInternetTools400.setLayout(new GridLayout());

        final Label labelTools400Email = new Label(compositeInternetTools400, SWT.NONE);
        labelTools400Email.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTools400Email.setText(Messages.getString("E-Mail") + ": thomas.raddatz@tools400.de");

        final Link labelTools400Internet = new Link(compositeInternetTools400, SWT.NONE);
        labelTools400Internet.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTools400Internet.setText(Messages.getString("Internet") + ": <a href=\"www.tools400.de\">www.tools400.de</a>");
        labelTools400Internet.addSelectionListener(new LinkSelectionListener());
        
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
