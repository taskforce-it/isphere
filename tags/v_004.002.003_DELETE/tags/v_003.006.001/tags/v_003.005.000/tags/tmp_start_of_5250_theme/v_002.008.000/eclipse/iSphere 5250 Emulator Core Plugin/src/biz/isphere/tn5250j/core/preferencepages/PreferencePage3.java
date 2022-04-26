/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferencepages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;

public class PreferencePage3 extends PreferencePage implements IWorkbenchPreferencePage {

    private IPreferenceStore store;
    private Button buttonMSActive;
    private Text textMSHorizontalSize;
    private Text textMSVerticalSize;

    public PreferencePage3() {
        super();
        setPreferenceStore(TN5250JCorePlugin.getDefault().getPreferenceStore());
        store = getPreferenceStore();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutx = new GridLayout();
        gridLayoutx.numColumns = 2;
        container.setLayout(gridLayoutx);

        // Minimal size - Active

        final Label labelMSActive = new Label(container, SWT.NONE);
        labelMSActive.setText(Messages.Active_colon);

        buttonMSActive = new Button(container, SWT.CHECK);

        // Minimal size - Horizontal size

        final Label labelMSHorizontalSize = new Label(container, SWT.NONE);
        labelMSHorizontalSize.setText(Messages.Horizontal_size_colon);

        textMSHorizontalSize = new Text(container, SWT.BORDER);
        textMSHorizontalSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(textMSHorizontalSize.getText());
                    setErrorMessage(null);
                    setValid(true);
                } catch (NumberFormatException e1) {
                    setErrorMessage(Messages.The_value_in_field_Horizontal_size_is_not_valid);
                    setValid(false);
                }
            }
        });
        textMSHorizontalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMSHorizontalSize.setTextLimit(4);

        // Minimal size - Vertical size

        final Label labelMSVerticalSize = new Label(container, SWT.NONE);
        labelMSVerticalSize.setText(Messages.Vertical_size_colon);

        textMSVerticalSize = new Text(container, SWT.BORDER);
        textMSVerticalSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(textMSVerticalSize.getText());
                    setErrorMessage(null);
                    setValid(true);
                } catch (NumberFormatException e1) {
                    setErrorMessage(Messages.The_value_in_field_Vertical_size_is_not_valid);
                    setValid(false);
                }
            }
        });
        textMSVerticalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMSVerticalSize.setTextLimit(4);

        // Miscellaneous

        setScreenToValues();

        return container;
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        setScreenToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setStoreToDefaults();
        setScreenToValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {
        if (buttonMSActive.getSelection()) {
            store.setValue("BIZ.ISPHERE.TN5250J.MSACTIVE", "Y");
        } else {
            store.setValue("BIZ.ISPHERE.TN5250J.MSACTIVE", "");
        }
        store.setValue("BIZ.ISPHERE.TN5250J.MSHSIZE", textMSHorizontalSize.getText());
        store.setValue("BIZ.ISPHERE.TN5250J.MSVSIZE", textMSVerticalSize.getText());
    }

    protected void setStoreToDefaults() {
        store.setToDefault("BIZ.ISPHERE.TN5250J.MSACTIVE");
        store.setToDefault("BIZ.ISPHERE.TN5250J.MSHSIZE");
        store.setToDefault("BIZ.ISPHERE.TN5250J.MSVSIZE");
    }

    protected void setScreenToValues() {
        if (store.getString("BIZ.ISPHERE.TN5250J.MSACTIVE").equals("Y")) {
            buttonMSActive.setSelection(true);
        } else {
            buttonMSActive.setSelection(false);
        }
        textMSHorizontalSize.setText(store.getString("BIZ.ISPHERE.TN5250J.MSHSIZE"));
        textMSVerticalSize.setText(store.getString("BIZ.ISPHERE.TN5250J.MSVSIZE"));
    }

    public void init(IWorkbench workbench) {
    }

}