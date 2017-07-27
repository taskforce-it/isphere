/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.tn5250j.TN5250JPlugin;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;

/**
 * 5250 preferences page: Welcome to TN5250J screen
 */
public class PreferencePage1 extends PreferencePage implements IWorkbenchPreferencePage {

    public PreferencePage1() {
        super();
        noDefaultAndApplyButton();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        final Label labelPoweredBy = new Label(container, SWT.NONE);
        final GridData gd_labelPoweredBy = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        labelPoweredBy.setLayoutData(gd_labelPoweredBy);
        labelPoweredBy.setText(Messages.iSphere_5250_Emulator_is_based_on_TN5250j_Emulator);

        ImageHyperlink labelTN5250JImage = new ImageHyperlink(container, SWT.NONE);
        labelTN5250JImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTN5250JImage.setImage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_TN5250JSPLASH));
        labelTN5250JImage.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                Program.launch("http://tn5250j.sourceforge.net"); //$NON-NLS-1$
            }
        });

        final Label labelVersionInfo = new Label(container, SWT.NONE);
        final GridData gd_labelVersionInfo = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        labelVersionInfo.setLayoutData(gd_labelVersionInfo);
        labelVersionInfo.setText(TN5250JPlugin.getDefault().getVersionInfo());

        return container;

    }

    public void init(IWorkbench workbench) {
    }

}
