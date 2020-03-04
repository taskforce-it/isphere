/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.preferencepages;

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

import biz.isphere.base.ISphereBasePlugin;

public class General extends PreferencePage implements IWorkbenchPreferencePage {

    public General() {
        super();
        noDefaultAndApplyButton();

    }

    @Override
    public Control createContents(Composite parent) {
        Composite _container = new Composite(parent, SWT.NONE);
        _container.setLayout(new FillLayout(SWT.VERTICAL));

        ScrolledComposite sc = new ScrolledComposite(_container, SWT.H_SCROLL | SWT.V_SCROLL);

        Composite container = new Composite(sc, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        final Label labelTaskForceImage = new Label(container, SWT.NONE);
        labelTaskForceImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTaskForceImage.setImage(ISphereBasePlugin.getDefault().getImageRegistry().get(ISphereBasePlugin.IMAGE_ISPHERE));

        final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator1.setLayoutData(gd_labelSeparator1);

        final Label labelISphere = new Label(container, SWT.NONE);
        labelISphere.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelISphere.setText("iSphere Version " + ISphereBasePlugin.getDefault().getVersion()); //$NON-NLS-1$

        final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator2.setLayoutData(gd_labelSeparator2);

        final Label labelFeature0 = new Label(container, SWT.NONE);
        labelFeature0.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelFeature0.setText("iSphere provides the following features:"); //$NON-NLS-1$

        createFeatureLabel(container, "Binding Directory Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "Data Area Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "User Space Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "Data Queue Viewer"); //$NON-NLS-1$
        createFeatureLabel(container, "Message File Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "Message File Compare Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "Message File Search"); //$NON-NLS-1$
        createFeatureLabel(container, "Source File Search"); //$NON-NLS-1$
        createFeatureLabel(container, "Source Compare/Merge Editor"); //$NON-NLS-1$
        createFeatureLabel(container, "Spooled Files Subsystem"); //$NON-NLS-1$
        createFeatureLabel(container, "Lpex Task Tags"); //$NON-NLS-1$
        createFeatureLabel(container, "Host Object Decorator"); //$NON-NLS-1$
        createFeatureLabel(container, "RSE Filter Manager"); //$NON-NLS-1$
        createFeatureLabel(container, "Messages Subsystem"); //$NON-NLS-1$
        createFeatureLabel(container, "Job Log Explorer"); //$NON-NLS-1$

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

    private void createFeatureLabel(Composite parent, String label) {

        final Label featureLabel = new Label(parent, SWT.NONE);
        featureLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        featureLabel.setText(label);
    }

    public void init(IWorkbench workbench) {
        return;
    }

}
