/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec.preferencepages;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.rexec.preferences.Preferences;

public class ISphereRexec extends PreferencePage implements IWorkbenchPreferencePage {

    private final static String REMOTE_USER = "biz.isphere.rexec.remote.user"; //$NON-NLS-1$ 
    private final static String REMOTE_PASSWORD = "biz.isphere.rexec.remote.password"; //$NON-NLS-1$ 

    private Button checkboxServerEnabled;
    private Text textServerListenerPort;
    private Text textSocketReadTimeout;
    private Button checkboxCaptureOutput;

    private Text textRmtUser;
    private Text textRmtPassword;

    public ISphereRexec() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createSectionCommon(main);
        createSectionServerSettings(main);
        createSectionSecuritySettings(main);

        setScreenToValues();

        return main;
    }

    private void createSectionCommon(Composite parent) {

        checkboxServerEnabled = WidgetFactory.createCheckbox(parent);
        checkboxServerEnabled.setToolTipText("Specifies whether or not iSphere listens for incoming remote commands.");
        checkboxServerEnabled.setLayoutData(createLayoutData(1));
        checkboxServerEnabled.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (validateServerEnabled()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        Label labelUseParameterSections = new Label(parent, SWT.NONE);
        labelUseParameterSections.setLayoutData(createLabelLayoutData());
        labelUseParameterSections.setText("Enable");
        labelUseParameterSections.setToolTipText("Specifies whether or not iSphere listens for incoming remote commands.");
    }

    private void createSectionServerSettings(Composite parent) {

        Group groupAddHeader = new Group(parent, SWT.NONE);
        groupAddHeader.setLayout(new GridLayout(2, false));
        groupAddHeader.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupAddHeader.setText("");

        Label labelServerListenerPort = new Label(groupAddHeader, SWT.NONE);
        labelServerListenerPort.setLayoutData(createLabelLayoutData());
        labelServerListenerPort.setText("Port number:");
        labelServerListenerPort.setToolTipText("Specifies the port number the Rexec server listen on for incoming requests.");

        textServerListenerPort = WidgetFactory.createIntegerText(groupAddHeader);
        textServerListenerPort.setToolTipText("Specifies the port number the Rexec server listen on for incoming requests.");
        GridData sourceServerListenerPortLayoutData = createLayoutData(1);
        sourceServerListenerPortLayoutData.widthHint = 50;
        textServerListenerPort.setLayoutData(sourceServerListenerPortLayoutData);
        textServerListenerPort.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateListenerPortNumber()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        Label labelSocketReadTimeout = new Label(groupAddHeader, SWT.NONE);
        labelSocketReadTimeout.setLayoutData(createLabelLayoutData());
        labelSocketReadTimeout.setText("Socket timeout:");
        labelSocketReadTimeout.setToolTipText("Specifies the timeout in milliseconds the socket waits for data.");

        textSocketReadTimeout = WidgetFactory.createIntegerText(groupAddHeader);
        textSocketReadTimeout.setToolTipText("Specifies the timeout in milliseconds the socket waits for data.");
        GridData SocketReadTimeoutLayoutData = createLayoutData(1);
        SocketReadTimeoutLayoutData.widthHint = 50;
        textSocketReadTimeout.setLayoutData(SocketReadTimeoutLayoutData);
        textSocketReadTimeout.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSocketReadTimeout()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        Label labelCaptureOutput = new Label(groupAddHeader, SWT.NONE);
        labelCaptureOutput.setLayoutData(createLabelLayoutData());
        labelCaptureOutput.setText("Capture output");
        labelCaptureOutput.setToolTipText("Specifies whether or not the command output is redirected to a text file and send back to the client.");

        checkboxCaptureOutput = WidgetFactory.createCheckbox(groupAddHeader);
        checkboxCaptureOutput.setToolTipText("Specifies whether or not the command output is redirected to a text file and send back to the client.");
        checkboxCaptureOutput.setLayoutData(createLayoutData(1));
        checkboxCaptureOutput.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (validateCaptureOutput()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

    }

    private void createSectionSecuritySettings(Composite parent) {

        Group groupAddHeader = new Group(parent, SWT.NONE);
        groupAddHeader.setLayout(new GridLayout(2, false));
        groupAddHeader.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupAddHeader.setText("Security settings");

        Label labelRmtUser = new Label(groupAddHeader, SWT.NONE);
        labelRmtUser.setLayoutData(createLabelLayoutData());
        labelRmtUser.setText("Remote user:");
        labelRmtUser.setToolTipText("Specifies the name of the user profile that is allowed to send remote commands.");

        textRmtUser = WidgetFactory.createNameText(groupAddHeader);
        textRmtUser.setToolTipText("Specifies the name of the user profile that is allowed to send remote commands.");
        textRmtUser.setLayoutData(createFillLayoutData(1));
        textRmtUser.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateListenerPortNumber()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        Label labelRmtPassword = new Label(groupAddHeader, SWT.NONE);
        labelRmtPassword.setLayoutData(createLabelLayoutData());
        labelRmtPassword.setText("Remote password:");
        labelRmtPassword.setToolTipText("Specifies the password that is used to validate the specified remote user.");

        textRmtPassword = WidgetFactory.createPassword(groupAddHeader);
        textRmtPassword.setToolTipText("Specifies the password that is used to validate the specified remote user.");
        textRmtPassword.setLayoutData(createFillLayoutData(1));
        textRmtPassword.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateListenerPortNumber()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        super.performApply();
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

        preferences.setServerEnabled(checkboxServerEnabled.getSelection());
        preferences.setListenerPort(IntHelper.tryParseInt(textServerListenerPort.getText(), preferences.getInitialListenerPort()));
        preferences.setSocketReadTimeout(IntHelper.tryParseInt(textSocketReadTimeout.getText(), preferences.getInitialSocketReadTimeout()));
        preferences.setCaptureOutput(checkboxCaptureOutput.getSelection());

        setSecureValue(REMOTE_USER, textRmtUser.getText());
        setSecureValue(REMOTE_PASSWORD, textRmtPassword.getText());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        checkboxServerEnabled.setSelection(preferences.isServerEnabled());
        textServerListenerPort.setText(Integer.toString(preferences.getListenerPort()));
        textSocketReadTimeout.setText(Integer.toString(preferences.getSocketReadTimeout()));
        checkboxCaptureOutput.setSelection(preferences.isCaptureOutput());

        textRmtUser.setText(getSecureValue(REMOTE_USER, ""));
        textRmtPassword.setText(getSecureValue(REMOTE_PASSWORD, ""));

        validateAll();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxServerEnabled.setSelection(preferences.getInitialServerEnabled());
        textServerListenerPort.setText(Integer.toString(preferences.getInitialListenerPort()));
        textSocketReadTimeout.setText(Integer.toString(preferences.getInitialSocketReadTimeout()));
        checkboxCaptureOutput.setSelection(preferences.getInitialCaptureOutput());

        textRmtUser.setText(""); //$NON-NLS-1$
        textRmtPassword.setText(""); //$NON-NLS-1$

        validateAll();
        setControlsEnablement();
    }

    private boolean validateServerEnabled() {

        return true;
    }

    private boolean validateListenerPortNumber() {

        return true;
    }

    private boolean validateSocketReadTimeout() {

        return true;
    }

    private boolean validateCaptureOutput() {

        return true;
    }

    private boolean validateAll() {

        if (!validateServerEnabled()) {
            return false;
        }

        if (!validateListenerPortNumber()) {
            return false;
        }

        if (!validateSocketReadTimeout()) {
            return false;
        }

        if (!validateCaptureOutput()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

        if (checkboxServerEnabled.getSelection()) {
            textServerListenerPort.setEnabled(true);
            textSocketReadTimeout.setEnabled(true);
            checkboxCaptureOutput.setEnabled(true);
        } else {
            textServerListenerPort.setEnabled(false);
            textSocketReadTimeout.setEnabled(false);
            checkboxCaptureOutput.setEnabled(false);
        }
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

    private GridData createLabelLayoutData() {
        return createLayoutData(1);
    }

    private GridData createButtonLayoutData(int horizontalSpan) {
        GridData gd = createLayoutData(horizontalSpan);
        gd.widthHint = 120;
        return gd;
    }

    private GridData createLayoutData(int horizontalSpan) {
        return new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1);
    }

    private GridData createFillLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.BEGINNING, true, false, horizontalSpan, 1);
    }

    private String getSecureValue(String key, String def) {

        ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault();
        String valueString = null;

        try {
            valueString = securePreferences.get(key, def);
        } catch (StorageException e) {
        }

        return valueString;
    }

    public void setSecureValue(String key, String value) {

        String valueString;
        if (StringHelper.isNullOrEmpty(value)) {
            valueString = ""; //$NON-NLS-1$
        } else {
            valueString = value;
        }

        ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault();

        try {
            securePreferences.put(key, valueString, true);
        } catch (StorageException e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }
    }
}
