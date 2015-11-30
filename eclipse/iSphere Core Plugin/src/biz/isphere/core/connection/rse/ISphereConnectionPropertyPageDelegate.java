/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.connection.rse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.IDisplayErrorDialog;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.internal.handler.TransferLibraryHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;

public class ISphereConnectionPropertyPageDelegate {

    private IDisplayErrorDialog parent;
    private String connectionName;

    private String iSphereLibrary;
    private Validator validatorLibrary;

    private Button checkBoxUseConnectionSpecificSettings;
    private Text textFtpHostName;
    private Text textFtpPortNumber;
    private Text textISphereLibrary;
    private Label textISphereLibraryVersion;
    private Button buttonTransfer;

    /**
     * Constructor for SamplePropertyPage.
     */
    public ISphereConnectionPropertyPageDelegate(IDisplayErrorDialog parent, String connectionName) {
        super();

        this.parent = parent;
        this.connectionName = connectionName;
    }

    public void performDefaults() {
        setScreenToDefaultValues();
    }

    public boolean performOk() {
        return true;
    }

    public Control createContentArea(Composite container) {

        Composite parent = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);

        checkBoxUseConnectionSpecificSettings = WidgetFactory.createCheckbox(parent);
        checkBoxUseConnectionSpecificSettings.setText(Messages.Use_connection_specific_settings);
        checkBoxUseConnectionSpecificSettings.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setControlEnablement();
                updateISphereLibraryVersion();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        addSeparator(parent);
        addLibrarySection(parent);

        return parent;
    }

    public void performSave(ConnectionProperties connectionProperties) {

        connectionProperties.setFtpHostName(textFtpHostName.getText());
        connectionProperties
            .setFtpPortNumber(IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber()));
        connectionProperties.setISphereLibraryName(iSphereLibrary);
        connectionProperties.setUseISphereLibraryName(checkBoxUseConnectionSpecificSettings.getSelection());
    }

    public boolean verifyPageContents() {
        return true;
    }

    public void setControlEnablement() {

        if (checkBoxUseConnectionSpecificSettings.getSelection()) {
            textFtpHostName.setEnabled(true);
            textFtpPortNumber.setEnabled(true);
            textISphereLibrary.setEnabled(true);
            buttonTransfer.setEnabled(true);
        } else {
            textFtpHostName.setEnabled(false);
            textFtpPortNumber.setEnabled(false);
            textISphereLibrary.setEnabled(false);
            buttonTransfer.setEnabled(false);
        }

    }

    public void setScreenToValues(ConnectionProperties connectionProperties) {

        textFtpHostName.setText(connectionProperties.getFtpHostName());
        textFtpPortNumber.setText(Integer.toString(connectionProperties.getFtpPortNumber()));
        iSphereLibrary = connectionProperties.getISphereLibraryName();
        checkBoxUseConnectionSpecificSettings.setSelection(connectionProperties.useISphereLibraryName());

        setScreenValues();
    }

    private void setScreenToDefaultValues() {

        textFtpHostName.setText(Preferences.getInstance().getDefaultHostName());
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getDefaultFtpPortNumber()));
        iSphereLibrary = Preferences.getInstance().getDefaultISphereLibrary();
        checkBoxUseConnectionSpecificSettings.setSelection(false);

        setScreenValues();
    }

    private void setScreenValues() {

        textISphereLibrary.setText(iSphereLibrary);
    }

    private void addLibrarySection(Composite container) {

        Composite parent = createDefaultComposite(container, ""); //$NON-NLS-1$

        Label labelHostName = new Label(parent, SWT.NONE);
        labelHostName.setLayoutData(createLabelLayoutData());
        labelHostName.setText(Messages.Host_name_colon);

        textFtpHostName = WidgetFactory.createText(parent);
        textFtpHostName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateISphereLibraryVersion();
            }
        });
        textFtpHostName.setLayoutData(createTextLayoutData());

        Label labelFtpPortNumber = new Label(parent, SWT.NONE);
        labelFtpPortNumber.setLayoutData(createLabelLayoutData());
        labelFtpPortNumber.setText(Messages.FTP_port_number_colon);

        textFtpPortNumber = WidgetFactory.createIntegerText(parent);
        textFtpPortNumber.setTextLimit(5);
        textFtpPortNumber.setLayoutData(createTextLayoutData());

        Label labelISphereLibrary = new Label(parent, SWT.NONE);
        labelISphereLibrary.setLayoutData(createLabelLayoutData());
        labelISphereLibrary.setText(Messages.iSphere_library_colon);

        textISphereLibrary = WidgetFactory.createUpperCaseText(parent);
        textISphereLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                iSphereLibrary = textISphereLibrary.getText().toUpperCase().trim();
                if (!validateISphereLibraryName()) {
                    setErrorMessage(Messages.The_value_in_field_iSphere_library_is_not_valid);
                    setValid(false);
                } else {
                    clearErrorMessage();
                    setValid(true);
                }
            }
        });
        textISphereLibrary.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateISphereLibraryVersion();
            }
        });
        textISphereLibrary.setLayoutData(createTextLayoutData());
        textISphereLibrary.setTextLimit(10);

        Label labelIShereLibraryVersion = new Label(parent, SWT.NONE);
        labelIShereLibraryVersion.setLayoutData(createLabelLayoutData());
        labelIShereLibraryVersion.setText("Version:");

        textISphereLibraryVersion = new Label(parent, SWT.NONE);
        textISphereLibraryVersion.setLayoutData(createTextLayoutData());

        buttonTransfer = WidgetFactory.createPushButton(parent);
        buttonTransfer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String hostName = textFtpHostName.getText();
                int ftpPort = IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber());
                TransferLibraryHandler handler = new TransferLibraryHandler(hostName, ftpPort, iSphereLibrary);
                try {
                    handler.execute(null);
                } catch (Throwable e) {
                    ISpherePlugin.logError("Failed to transfer iSphere library.", e); //$NON-NLS-1$
                }
            }
        });
        buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonTransfer.setText(Messages.Transfer_iSphere_library);

        validatorLibrary = Validator.getLibraryNameInstance();

    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    private Composite createDefaultComposite(Composite parent, String text) {

        Group group = new Group(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        group.setText(text);

        return group;
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    }

    private void updateISphereLibraryVersion() {
        String text = getISphereLibraryVersion(connectionName, textISphereLibrary.getText());
        if (text == null) {
            return;
        }

        textISphereLibraryVersion.setText(text);
    }

    private String getISphereLibraryVersion(String connectionName, String library) {

        if (!checkBoxUseConnectionSpecificSettings.getSelection()) {
            return ""; //$NON-NLS-1$
        }

        if (StringHelper.isNullOrEmpty(connectionName) || StringHelper.isNullOrEmpty(library)) {
            return Messages.not_found;
        }

        String version;

        try {

            AS400 as400 = IBMiHostContributionsHandler.getSystem(connectionName);
            if (as400 == null) {
                return Messages.bind(Messages.Host_A_not_found, connectionName);
            }

            version = ISphereHelper.getISphereLibraryVersion(as400, library);
            if (version == null) {
                return Messages.not_found;
            }

            String buildDate = ISphereHelper.getISphereLibraryBuildDate(as400, library);
            if (StringHelper.isNullOrEmpty(buildDate)) {
                return version;
            }

            DateFormat dateFormatter = Preferences.getInstance().getDateFormatter();
            DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
            return version + " - " + dateFormatter.format(dateParser.parse(buildDate)); //$NON-NLS-1$

        } catch (Throwable e) {
            return null;
        }
    }

    private boolean validateISphereLibraryName() {
        if (StringHelper.isNullOrEmpty(iSphereLibrary) || !validatorLibrary.validate(iSphereLibrary)) {
            return false;
        } else {
            return true;
        }
    }

    private void setErrorMessage(String message) {
        parent.setErrorMessage(message);
    }

    private void clearErrorMessage() {
        parent.clearErrorMessage();
    }

    private void setValid(boolean valid) {
        parent.setValid(valid);
    }
}