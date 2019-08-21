/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.TransferISphereLibrary;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereLibrary extends PreferencePage implements IWorkbenchPreferencePage {

    private Text textISphereLibrary;
    private String iSphereLibrary;
    private Validator validatorLibrary;
    private Text textHostName;
    private Text textFtpPortNumber;

    public ISphereLibrary() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        Label labelHostName = new Label(container, SWT.NONE);
        labelHostName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelHostName.setText(Messages.Host_name_colon);

        textHostName = WidgetFactory.createText(container);
        textHostName.setText(Messages.Host_name_colon);
        textHostName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label labelFtpPortNumber = new Label(container, SWT.NONE);
        labelFtpPortNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelFtpPortNumber.setText(Messages.FTP_port_number_colon);

        textFtpPortNumber = WidgetFactory.createIntegerText(container);
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getDefaultFtpPortNumber()));
        textFtpPortNumber.setTextLimit(5);
        textFtpPortNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        final Label labelISphereLibrary = new Label(container, SWT.NONE);
        labelISphereLibrary.setText(Messages.iSphere_library_colon);

        textISphereLibrary = WidgetFactory.createText(container);
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
        textISphereLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textISphereLibrary.setTextLimit(10);

        validatorLibrary = Validator.getLibraryNameInstance();

        Button buttonTransfer = WidgetFactory.createPushButton(container);
        buttonTransfer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String hostName = textHostName.getText();
                int ftpPort = IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber());
                TransferISphereLibrary statusDialog = new TransferISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
                    .getDisplay(), SWT.APPLICATION_MODAL | SWT.SHELL_TRIM, iSphereLibrary, hostName, ftpPort);
                if (statusDialog.connect()) {
                    statusDialog.open();
                }
            }
        });
        buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonTransfer.setText(Messages.Transfer_iSphere_library);

        setScreenToValues();

        return container;
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

        Preferences.getInstance().setISphereLibrary(iSphereLibrary);
        Preferences.getInstance().setHostName(textHostName.getText());
        Preferences.getInstance().setFtpPortNumber(IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber()));

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();
        iSphereLibrary = Preferences.getInstance().getISphereLibrary();
        textHostName.setText(Preferences.getInstance().getHostName());
        textFtpPortNumber.setText("" + Preferences.getInstance().getFtpPortNumber());

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        iSphereLibrary = Preferences.getInstance().getDefaultISphereLibrary();
        textHostName.setText(Preferences.getInstance().getDefaultHostName());
        textFtpPortNumber.setText("" + Preferences.getInstance().getDefaultFtpPortNumber());

        setScreenValues();

    }

    protected void setScreenValues() {

        textISphereLibrary.setText(iSphereLibrary);

    }

    public void init(IWorkbench workbench) {
    }

}