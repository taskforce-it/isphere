/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.messagesubsystem.ISphereMessageSubsystemBasePlugin;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.preferences.Preferences;

public class MessageMonitoringPage extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo comboReplyFieldPosition;
    private Button checkboxEnableDebug; // TODO: remove debug code

    public MessageMonitoringPage() {
        super();
        setPreferenceStore(ISphereMessageSubsystemBasePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionCommon(main);
        createSectionDebug(main);

        setScreenToValues();

        return main;
    }

    private void createSectionCommon(Composite parent) {

        Label labelReplyFieldPosition = new Label(parent, SWT.NONE);
        labelReplyFieldPosition.setLayoutData(createLabelLayoutData());
        labelReplyFieldPosition.setToolTipText(Messages.bind(Messages.Reply_Field_Position_tooltip, new String[] { Messages.Reply,
            Messages.iSeries_Message }));
        labelReplyFieldPosition.setText(Messages.bind(Messages.Reply_Field_Position_colon, Messages.Reply));

        comboReplyFieldPosition = WidgetFactory.createReadOnlyCombo(parent);
        comboReplyFieldPosition.setItems(Preferences.getInstance().getReplyFieldPositions());
        comboReplyFieldPosition.setText(comboReplyFieldPosition.getItem(0));
        comboReplyFieldPosition.setToolTipText(Messages.Reply_Field_Position_tooltip);
        comboReplyFieldPosition.setLayoutData(createLayoutData(1));
        comboReplyFieldPosition.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                validateAll();
                setControlsEnablement();
            }
        });
    }

    // TODO: remove debug code
    private void createSectionDebug(Composite parent) {

        checkboxEnableDebug = WidgetFactory.createCheckbox(parent);
        checkboxEnableDebug.setText("Enable &debug log (see: Eclipse error log view)"); //$NON-NLS-1$
        checkboxEnableDebug.setLayoutData(createLayoutData(2));

        checkboxEnableDebug.setVisible(true);
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = Preferences.getInstance();

        preferences.setReplyFieldPosition(comboReplyFieldPosition.getText());

        // TODO: remove debug code
        preferences.setDebugEnabled(checkboxEnableDebug.getSelection());

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        comboReplyFieldPosition.select(comboReplyFieldPosition.indexOf(preferences.getReplyFieldPosition()));

        // TODO: remove debug code
        checkboxEnableDebug.setSelection(preferences.isDebugEnabled());

        validateAll();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        comboReplyFieldPosition.select(comboReplyFieldPosition.indexOf(preferences.getDefaultReplyFieldPosition()));

        // TODO: remove debug code
        checkboxEnableDebug.setSelection(preferences.getDefaultDebugEnabled());

        validateAll();
        setControlsEnablement();
    }

    private boolean validateAll() {

        return clearError();
    }

    private void setControlsEnablement() {
    }

    private boolean setError(String message) {
        setErrorMessage(message);
        setValid(false);
        return false;
    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

    private GridData createLayoutData(int horizontalSpan) {
        return new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1);
    }
}
