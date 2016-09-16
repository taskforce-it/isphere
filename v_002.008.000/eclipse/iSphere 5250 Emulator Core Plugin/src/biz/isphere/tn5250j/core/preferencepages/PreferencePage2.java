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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;

public class PreferencePage2 extends PreferencePage implements IWorkbenchPreferencePage {

    private IPreferenceStore store;
    private Text textPort;
    private CCombo comboCodePage;
    private Button buttonScreenSize24_80;
    private Button buttonScreenSize27_132;
    // private Button buttonEnhancedMode;
    private Button buttonView;
    private Button buttonEditor;
    private String[] codePages = { "37", "37PT", "273", "280", "284", "285", "277-dk", "277-no", "278", "297", "424", "500-ch", "870-pl", "870-sk",
        "871", "875", "1025-r", "1026", "1112", "1141", "1140", "1147", "1148" };

    public PreferencePage2() {
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

        // Port

        final Label labelPort = new Label(container, SWT.NONE);
        labelPort.setText(Messages.Port_colon);

        textPort = new Text(container, SWT.BORDER);
        textPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPort.setTextLimit(5);

        // Codepage

        final Label labelCodePage = new Label(container, SWT.NONE);
        labelCodePage.setText(Messages.Codepage_colon);

        comboCodePage = new CCombo(container, SWT.BORDER);
        comboCodePage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboCodePage.setTextLimit(10);
        for (int idx = 0; idx < codePages.length; idx++) {
            comboCodePage.add(codePages[idx]);
        }

        // Screen size

        final Label labelScreenSize = new Label(container, SWT.NONE);
        labelScreenSize.setText(Messages.Screensize_colon);

        final Group groupScreenSize = new Group(container, SWT.NONE);
        final GridLayout gridLayoutScreenSize = new GridLayout();
        gridLayoutScreenSize.numColumns = 2;
        groupScreenSize.setLayout(gridLayoutScreenSize);

        buttonScreenSize24_80 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize24_80.setText("24*80");

        buttonScreenSize27_132 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize27_132.setText("27*132");

        // Enhanced mode
        /*
         * final Label labelEnhancedMode = new Label(container, SWT.NONE);
         * labelEnhancedMode.setText(Messages.getString("Enhanced_mode") + ":");
         * buttonEnhancedMode = new Button(container, SWT.CHECK);
         */
        // Area

        final Label labelArea = new Label(container, SWT.NONE);
        labelArea.setText(Messages.Area_colon);

        final Group groupArea = new Group(container, SWT.NONE);
        final GridLayout gridLayoutArea = new GridLayout();
        gridLayoutArea.numColumns = 2;
        groupArea.setLayout(gridLayoutArea);

        buttonView = new Button(groupArea, SWT.RADIO);
        buttonView.setText(Messages.View);

        buttonEditor = new Button(groupArea, SWT.RADIO);
        buttonEditor.setText(Messages.Editor);

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
        store.setValue("BIZ.ISPHERE.TN5250J.PORT", textPort.getText());
        store.setValue("BIZ.ISPHERE.TN5250J.CODEPAGE", comboCodePage.getText());
        if (buttonScreenSize27_132.getSelection()) {
            store.setValue("BIZ.ISPHERE.TN5250J.SCREENSIZE", "132");
        } else {
            store.setValue("BIZ.ISPHERE.TN5250J.SCREENSIZE", "");
        }
        // if (buttonEnhancedMode.getSelection()) {
        // store.setValue("BIZ.ISPHERE.TN5250J.ENHANCEDMODE", "Y");
        // }
        // else {
        // store.setValue("BIZ.ISPHERE.TN5250J.ENHANCEDMODE", "");
        // }
        if (buttonView.getSelection()) {
            store.setValue("BIZ.ISPHERE.TN5250J.AREA", "*VIEW");
        } else {
            store.setValue("BIZ.ISPHERE.TN5250J.AREA", "*EDITOR");
        }
    }

    protected void setStoreToDefaults() {
        store.setToDefault("BIZ.ISPHERE.TN5250J.PORT");
        store.setToDefault("BIZ.ISPHERE.TN5250J.CODEPAGE");
        store.setToDefault("BIZ.ISPHERE.TN5250J.SCREENSIZE");
        // store.setToDefault("BIZ.ISPHERE.TN5250J.ENHANCEDMODE");
        store.setToDefault("BIZ.ISPHERE.TN5250J.AREA");
    }

    protected void setScreenToValues() {
        textPort.setText(store.getString("BIZ.ISPHERE.TN5250J.PORT"));
        comboCodePage.setText(store.getString("BIZ.ISPHERE.TN5250J.CODEPAGE"));
        if (store.getString("BIZ.ISPHERE.TN5250J.SCREENSIZE").equals("132")) {
            buttonScreenSize24_80.setSelection(false);
            buttonScreenSize27_132.setSelection(true);
        } else {
            buttonScreenSize24_80.setSelection(true);
            buttonScreenSize27_132.setSelection(false);
        }
        // if (store.getString("BIZ.ISPHERE.TN5250J.ENHANCEDMODE").equals("Y"))
        // {
        // buttonEnhancedMode.setSelection(true);
        // }
        // else {
        // buttonEnhancedMode.setSelection(false);
        // }
        if (store.getString("BIZ.ISPHERE.TN5250J.AREA").equals("*VIEW")) {
            buttonView.setSelection(true);
            buttonEditor.setSelection(false);
        } else {
            buttonView.setSelection(false);
            buttonEditor.setSelection(true);
        }
    }

    public void init(IWorkbench workbench) {
    }

}