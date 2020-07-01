/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import org.eclipse.swt.widgets.Combo;
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

    private String aspGroup;
    private Validator validatorASPGroup;

    private Button checkBoxUseConnectionSpecificSettings;
    private Text textConnectionName;
    private Text textFtpPortNumber;
    private Text textISphereLibrary;
    private Combo comboASPGroup;
    private Label textISphereLibraryVersion;
    private Button buttonUpdateISphereLibraryVersion;
    private Button buttonTransfer;
    private int systemCcsid;

    private boolean updateISphereLibraryVersion;

    /**
     * Constructor for SamplePropertyPage.
     */
    public ISphereConnectionPropertyPageDelegate(IDisplayErrorDialog parent, String connectionName) {
        super();

        this.parent = parent;
        this.connectionName = connectionName;

        this.updateISphereLibraryVersion = false;
        this.systemCcsid = -1;
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

        WidgetFactory.createSeparator(parent);
        addLibrarySection(parent);

        return parent;
    }

    public void performSave(ConnectionProperties connectionProperties) {

        connectionProperties
            .setFtpPortNumber(IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber()));
        connectionProperties.setISphereLibraryName(iSphereLibrary);
        connectionProperties.setASPGroup(aspGroup);
        connectionProperties.setUseISphereLibraryName(checkBoxUseConnectionSpecificSettings.getSelection());
    }

    public boolean verifyPageContents() {
        return true;
    }

    public void setControlEnablement() {

        if (checkBoxUseConnectionSpecificSettings.getSelection()) {
            textConnectionName.setEnabled(true);
            textFtpPortNumber.setEnabled(true);
            textISphereLibrary.setEnabled(true);
            comboASPGroup.setEnabled(true);
            if (updateISphereLibraryVersion) {
                buttonUpdateISphereLibraryVersion.setEnabled(false);
            } else {
                buttonUpdateISphereLibraryVersion.setEnabled(true);
            }
            buttonTransfer.setEnabled(true);
        } else {
            textConnectionName.setEnabled(false);
            textFtpPortNumber.setEnabled(false);
            textISphereLibrary.setEnabled(false);
            comboASPGroup.setEnabled(false);
            buttonUpdateISphereLibraryVersion.setEnabled(false);
            buttonTransfer.setEnabled(false);
        }
    }

    public void setScreenToValues(ConnectionProperties connectionProperties) {

        textConnectionName.setText(connectionName);
        textFtpPortNumber.setText(Integer.toString(connectionProperties.getFtpPortNumber()));
        iSphereLibrary = connectionProperties.getISphereLibraryName();
        aspGroup = connectionProperties.getASPGroup();
        checkBoxUseConnectionSpecificSettings.setSelection(connectionProperties.useISphereLibraryName());

        setScreenValues();
    }

    private void setScreenToDefaultValues() {

        textConnectionName.setText(connectionName);
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getDefaultFtpPortNumber()));
        iSphereLibrary = Preferences.getInstance().getDefaultISphereLibrary();
        aspGroup = Preferences.getInstance().getDefaultASPGroup();
        checkBoxUseConnectionSpecificSettings.setSelection(false);

        setScreenValues();
    }

    private void setScreenValues() {

        textISphereLibrary.setText(iSphereLibrary);
        comboASPGroup.setText(aspGroup);
    }

    private void addLibrarySection(Composite container) {

        Composite parent = createDefaultComposite(container, ""); //$NON-NLS-1$

        Label labelHostName = new Label(parent, SWT.NONE);
        labelHostName.setLayoutData(createLabelLayoutData());
        labelHostName.setText(Messages.Host_name_colon);

        textConnectionName = WidgetFactory.createReadOnlyText(parent);
        textConnectionName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateISphereLibraryVersion();
            }
        });
        textConnectionName.setLayoutData(createTextLayoutData());

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

        // TODO: fix library name validator (pass CCSID) - DONE
        validatorLibrary = Validator.getLibraryNameInstance(getSystemCcsid());

        Label labelASPGroup = new Label(parent, SWT.NONE);
        labelASPGroup.setLayoutData(createLabelLayoutData());
        labelASPGroup.setText(Messages.ASP_group_colon);

        comboASPGroup = WidgetFactory.createUpperCaseCombo(parent);
        comboASPGroup.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                aspGroup = comboASPGroup.getText().toUpperCase().trim();
                if (!validateASPGroup()) {
                    setErrorMessage(Messages.The_value_in_field_ASP_group_is_not_valid);
                    setValid(false);
                } else {
                    setErrorMessage(null);
                    setValid(true);
                    updateISphereLibraryVersion();
                }
            }
        });
        comboASPGroup.setLayoutData(createTextLayoutData());
        comboASPGroup.setTextLimit(10);
        comboASPGroup.add("*NONE"); //$NON-NLS-1$

        validatorASPGroup = Validator.getNameInstance(getSystemCcsid());
        validatorASPGroup.addSpecialValue("*NONE"); //$NON-NLS-1$

        Label labelIShereLibraryVersion = new Label(parent, SWT.NONE);
        labelIShereLibraryVersion.setLayoutData(createLabelLayoutData());
        labelIShereLibraryVersion.setText(Messages.Version_colon);

        textISphereLibraryVersion = new Label(parent, SWT.NONE);
        textISphereLibraryVersion.setLayoutData(createTextLayoutData(1));

        buttonUpdateISphereLibraryVersion = WidgetFactory.createPushButton(parent);
        buttonUpdateISphereLibraryVersion.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REFRESH));
        buttonUpdateISphereLibraryVersion.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                updateISphereLibraryVersion = true;
                updateISphereLibraryVersion();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        buttonTransfer = WidgetFactory.createPushButton(parent);
        buttonTransfer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String hostName = textConnectionName.getText();
                int ftpPort = IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber());
                TransferLibraryHandler handler = new TransferLibraryHandler(hostName, ftpPort, iSphereLibrary, aspGroup);
                handler.setConnectionsEnabled(false);
                try {
                    handler.execute(null);
                } catch (Throwable e) {
                    ISpherePlugin.logError("*** Failed to transfer iSphere library ***", e); //$NON-NLS-1$
                }
            }
        });
        buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        buttonTransfer.setText(Messages.Transfer_iSphere_library);

    }

    private int getSystemCcsid() {

        if (systemCcsid == -1) {
            systemCcsid = IBMiHostContributionsHandler.getSystemCcsid(connectionName);
            if (systemCcsid < 0) {
                systemCcsid = Preferences.getInstance().getSystemCcsid();
            }
        }

        return systemCcsid;
    }

    private Composite createDefaultComposite(Composite parent, String text) {

        Group group = new Group(parent, SWT.NULL);
        GridLayout layout = new GridLayout(3, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        group.setText(text);

        return group;
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return createTextLayoutData(2);
    }

    private GridData createTextLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }

    private void updateISphereLibraryVersion() {
        String text = getISphereLibraryVersion(connectionName, textISphereLibrary.getText());
        if (text == null) {
            return;
        }

        textISphereLibraryVersion.setText(text);

        setControlEnablement();
    }

    private String getISphereLibraryVersion(String connectionName, String library) {

        if (!updateISphereLibraryVersion) {
            return ""; //$NON-NLS-1$
        }

        if (!checkBoxUseConnectionSpecificSettings.getSelection()) {
            updateISphereLibraryVersion = false;
            return ""; //$NON-NLS-1$
        }

        if (StringHelper.isNullOrEmpty(connectionName) || StringHelper.isNullOrEmpty(library)) {
            updateISphereLibraryVersion = false;
            return Messages.not_found;
        }

        String version;

        try {

            AS400 as400 = IBMiHostContributionsHandler.getSystem(connectionName);
            if (as400 == null) {
                updateISphereLibraryVersion = false;
                return Messages.bind(Messages.Connection_A_does_not_exist_or_is_currently_offline_and_cannot_be_connected, connectionName);
            }

            /*
             * From here on start updating library version automatically.
             */

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
            version = version + " - " + dateFormatter.format(dateParser.parse(buildDate)); //$NON-NLS-1$
            return version;

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

    private boolean validateASPGroup() {
        if (StringHelper.isNullOrEmpty(aspGroup) || !validatorASPGroup.validate(aspGroup)) {
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