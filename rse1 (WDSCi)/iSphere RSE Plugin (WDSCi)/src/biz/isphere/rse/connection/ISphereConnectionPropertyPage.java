/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.connection.rse.ConnectionProperties;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.core.ui.propertypages.SystemBasePropertyPage;
import com.ibm.etools.systems.model.SystemConnection;

// See:
// com.ibm.etools.systems.core.ui.propertypages.SystemConnectionPropertyPage
// c:\Programme
// x86\IBM\SDP_Shared\plugins\com.ibm.etools.systems.core_7.1.1\plugin.xml
public class ISphereConnectionPropertyPage extends SystemBasePropertyPage {

    private String iSphereLibrary;
    private Validator validatorLibrary;

    private Text textISphereLibrary;
    private Label textISphereLibraryVersion;
    private Button checkBoxUseConnectionSpecificSettings;

    private ConnectionManager manager;

    /**
     * Constructor for SamplePropertyPage.
     */
    public ISphereConnectionPropertyPage() {
        super();

        manager = ConnectionManager.getInstance();
    }

    private void addLibrarySection(Composite container) {

        Composite parent = createDefaultComposite(container, ""); //$NON-NLS-1$

        // Label for Library field
        Label labelISphereLibrary = new Label(parent, SWT.NONE);
        labelISphereLibrary.setLayoutData(createLabelLayoutData());
        labelISphereLibrary.setText(Messages.iSphere_library_colon);

        // Library text field
        textISphereLibrary = WidgetFactory.createUpperCaseText(parent);
        textISphereLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                iSphereLibrary = textISphereLibrary.getText().toUpperCase().trim();
                if (StringHelper.isNullOrEmpty(iSphereLibrary) || !validatorLibrary.validate(iSphereLibrary)) {
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

    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    public boolean performOk() {

        performSave();

        return true;
    }

    @Override
    protected Control createContentArea(Composite container) {

        Composite parent = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);

        checkBoxUseConnectionSpecificSettings = WidgetFactory.createCheckbox(parent);
        checkBoxUseConnectionSpecificSettings.setText(biz.isphere.rse.Messages.Use_connection_specific_settings);
        checkBoxUseConnectionSpecificSettings.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        addSeparator(parent);
        addLibrarySection(parent);

        setScreenToValues();

        setControlEnablement();

        return parent;
    }

    protected void setScreenToValues() {

        ConnectionProperties connectionProperties = manager.getConnectionProperties(getConnectionName());

        iSphereLibrary = connectionProperties.getISphereLibraryName();
        checkBoxUseConnectionSpecificSettings.setSelection(connectionProperties.useISphereLibraryName());

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        iSphereLibrary = Preferences.getInstance().getDefaultISphereLibrary();
        checkBoxUseConnectionSpecificSettings.setSelection(false);

        setScreenValues();
    }

    protected void setScreenValues() {

        textISphereLibrary.setText(iSphereLibrary);
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    }

    private void updateISphereLibraryVersion() {
        String text = getISphereLibraryVersion(getConnectionName(), textISphereLibrary.getText());
        if (text == null) {
            return;
        }

        textISphereLibraryVersion.setText(text);
    }

    private String getISphereLibraryVersion(String connectionName, String library) {

        if (StringHelper.isNullOrEmpty(connectionName) || StringHelper.isNullOrEmpty(library)) {
            return Messages.not_found;
        }

        String version;

        try {

            AS400 as400 = ISeriesConnection.getConnection(connectionName).getAS400ToolboxObject(null);
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

    private void performSave() {

        ConnectionProperties connectionProperties = manager.getConnectionProperties(getConnectionName());

        connectionProperties.setISphereLibraryName(iSphereLibrary);
        connectionProperties.setUseISphereLibraryName(checkBoxUseConnectionSpecificSettings.getSelection());

        manager.saveConnectionProperties(getConnectionName());
    }

    @Override
    protected boolean verifyPageContents() {
        return true;
    }

    private void setControlEnablement() {

        if (checkBoxUseConnectionSpecificSettings.getSelection()) {
            textISphereLibrary.setEnabled(true);
        } else {
            textISphereLibrary.setEnabled(false);
        }

    }

    private String getConnectionName() {

        Object element = getElement();
        if (element instanceof SystemConnection) {
            SystemConnection host = (SystemConnection)element;
            return ConnectionManager.getConnectionName(host);
        }

        return null;
    }
}