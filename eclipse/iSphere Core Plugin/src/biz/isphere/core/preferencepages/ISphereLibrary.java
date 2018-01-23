/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.as400.access.AS400;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.internal.handler.TransferLibraryHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereLibrary extends PreferencePage implements IWorkbenchPreferencePage {

    private String iSphereLibrary;
    private Validator validatorLibrary;

    private Text textHostName;
    private Text textFtpPortNumber;
    private Text textISphereLibrary;
    private Label textISphereLibraryVersion;
    private Button buttonUpdateISphereLibraryVersion;
    private Button buttonTransfer;
    private Button chkboxUseISphereJdbc;

    private boolean updateISphereLibraryVersion;

    public ISphereLibrary() {
        super();

        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();

        this.updateISphereLibraryVersion = false;
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        container.setLayout(gridLayout);

        Label labelHostName = new Label(container, SWT.NONE);
        labelHostName.setLayoutData(createLabelLayoutData());
        labelHostName.setText(Messages.Host_name_colon);

        textHostName = WidgetFactory.createText(container);
        textHostName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateISphereLibraryVersion();
            }
        });
        textHostName.setLayoutData(createTextLayoutData());

        Label labelFtpPortNumber = new Label(container, SWT.NONE);
        labelFtpPortNumber.setLayoutData(createLabelLayoutData());
        labelFtpPortNumber.setText(Messages.FTP_port_number_colon);

        textFtpPortNumber = WidgetFactory.createIntegerText(container);
        textFtpPortNumber.setTextLimit(5);
        textFtpPortNumber.setLayoutData(createTextLayoutData());

        Label labelISphereLibrary = new Label(container, SWT.NONE);
        labelISphereLibrary.setLayoutData(createLabelLayoutData());
        labelISphereLibrary.setText(Messages.iSphere_library_colon);

        textISphereLibrary = WidgetFactory.createUpperCaseText(container);
        textISphereLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                iSphereLibrary = textISphereLibrary.getText().toUpperCase().trim();
                if (iSphereLibrary.equals("") || !validatorLibrary.validate(iSphereLibrary)) {
                    setErrorMessage(Messages.The_value_in_field_iSphere_library_is_not_valid);
                    setValid(false);
                } else {
                    setErrorMessage(null);
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

        validatorLibrary = Validator.getLibraryNameInstance();

        Label labelIShereLibraryVersion = new Label(container, SWT.NONE);
        labelIShereLibraryVersion.setLayoutData(createLabelLayoutData());
        labelIShereLibraryVersion.setText(Messages.Version_colon);

        textISphereLibraryVersion = new Label(container, SWT.NONE);
        textISphereLibraryVersion.setLayoutData(createTextLayoutData(1));

        buttonUpdateISphereLibraryVersion = WidgetFactory.createPushButton(container);
        buttonUpdateISphereLibraryVersion.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REFRESH));
        buttonUpdateISphereLibraryVersion.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                updateISphereLibraryVersion = true;
                updateISphereLibraryVersion();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        buttonTransfer = WidgetFactory.createPushButton(container);
        buttonTransfer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String hostName = textHostName.getText();
                int ftpPort = IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber());
                TransferLibraryHandler handler = new TransferLibraryHandler(hostName, ftpPort, iSphereLibrary);
                try {
                    handler.execute(null);
                } catch (Throwable e) {
                    ISpherePlugin.logError("Failed to transfer iSphere library.", e); //$NON-NLS-1$
                }
            }
        });
        buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        buttonTransfer.setText(Messages.Transfer_iSphere_library);

        createJdbcSection(container);

        setScreenToValues();

        return container;
    }

    private void createJdbcSection(Composite parent) {

        Group groupJdbcProperties = new Group(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        groupJdbcProperties.setLayoutData(gridData);
        groupJdbcProperties.setLayout(new GridLayout(1, false));
        groupJdbcProperties.setText(Messages.JDBC_Properties);

        chkboxUseISphereJdbc = WidgetFactory.createCheckbox(groupJdbcProperties, Messages.Use_iSphere_connection_manager);
        chkboxUseISphereJdbc.setToolTipText(Messages.Tooltip_Use_iSphere_connection_manager);
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

    private void setControlEnablement() {

        if (updateISphereLibraryVersion) {
            buttonUpdateISphereLibraryVersion.setEnabled(false);
        } else {
            buttonUpdateISphereLibraryVersion.setEnabled(true);
        }
    }

    protected void setStoreToValues() {

        Preferences.getInstance().setISphereLibrary(iSphereLibrary);
        Preferences.getInstance().setHostName(textHostName.getText());
        Preferences.getInstance()
            .setFtpPortNumber(IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber()));
        Preferences.getInstance().setUseISphereJdbcConnectionManager(chkboxUseISphereJdbc.getSelection());

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();
        textHostName.setText(Preferences.getInstance().getHostName());
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getFtpPortNumber()));
        iSphereLibrary = Preferences.getInstance().getISphereLibrary(); // CHECKED
        chkboxUseISphereJdbc.setSelection(Preferences.getInstance().isISphereJdbcConnectionManager());

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        textHostName.setText(Preferences.getInstance().getDefaultHostName());
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getDefaultFtpPortNumber()));
        iSphereLibrary = Preferences.getInstance().getDefaultISphereLibrary();
        chkboxUseISphereJdbc.setSelection(Preferences.getInstance().getDefaultUseISphereJdbcConnectionManager());

        setScreenValues();
    }

    protected void setScreenValues() {

        textISphereLibrary.setText(iSphereLibrary);
    }

    public void init(IWorkbench workbench) {
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
        String text = getISphereLibraryVersion(textHostName.getText(), textISphereLibrary.getText());
        if (text == null) {
            return;
        }

        textISphereLibraryVersion.setText(text);

        setControlEnablement();
    }

    private String getISphereLibraryVersion(String hostName, String library) {

        if (!updateISphereLibraryVersion) {
            return ""; //$NON-NLS-1$
        }

        if (StringHelper.isNullOrEmpty(hostName) || StringHelper.isNullOrEmpty(library)) {
            updateISphereLibraryVersion = false;
            return Messages.not_found;
        }

        String version;

        try {

            AS400 as400 = IBMiHostContributionsHandler.findSystem(hostName);
            if (as400 == null) {
                updateISphereLibraryVersion = false;
                return Messages.bind(Messages.Host_A_not_found_or_connected, hostName);
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
}