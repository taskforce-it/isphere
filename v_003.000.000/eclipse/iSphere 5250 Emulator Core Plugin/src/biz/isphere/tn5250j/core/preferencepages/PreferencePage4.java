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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.tn5250j.core.Messages;

/**
 * 5250 preferences page: Key bindings information screen
 */
public class PreferencePage4 extends PreferencePage implements IWorkbenchPreferencePage {

    public PreferencePage4() {
        super();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutx = new GridLayout();
        container.setLayout(gridLayoutx);

        final Label labelKey1 = new Label(container, SWT.NONE);
        labelKey1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey1.setText(Messages.Eclipse_Key_Bindings_Ctrl_F12);

        final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator1.setLayoutData(gd_labelSeparator1);

        final Label labelKey2 = new Label(container, SWT.NONE);
        labelKey2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey2.setText(Messages.Eclipse_Key_Bindings_Ctrl_Alt_Arrow_Up);

        final Label labelKey3 = new Label(container, SWT.NONE);
        labelKey3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey3.setText(Messages.Eclipse_Key_Bindings_Ctrl_Alt_Arrow_Down);

        final Label labelKey4 = new Label(container, SWT.NONE);
        labelKey4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey4.setText(Messages.Eclipse_Key_Bindings_Ctrl_Alt_Arrow_Left);

        final Label labelKey5 = new Label(container, SWT.NONE);
        labelKey5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey5.setText(Messages.Eclipse_Key_Bindings_Ctrl_Alt_Arrow_Right);

        final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator2.setLayoutData(gd_labelSeparator2);

        final Label labelKey6 = new Label(container, SWT.NONE);
        labelKey6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey6.setText(Messages.Eclipse_Key_Bindings_Ctrl_Arrow_Up);

        final Label labelKey7 = new Label(container, SWT.NONE);
        labelKey7.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey7.setText(Messages.Eclipse_Key_Bindings_Ctrl_Arrow_Down);

        final Label labelSeparator3 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator3.setLayoutData(gd_labelSeparator3);

        final Label labelKey8 = new Label(container, SWT.NONE);
        labelKey8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey8.setText(Messages.Eclipse_Key_Bindings_Alt_Arrow_Up);

        final Label labelKey9 = new Label(container, SWT.NONE);
        labelKey9.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelKey9.setText(Messages.Eclipse_Key_Bindings_Alt_Arrow_Down);

        return container;
    }

    public void init(IWorkbench workbench) {
    }

}