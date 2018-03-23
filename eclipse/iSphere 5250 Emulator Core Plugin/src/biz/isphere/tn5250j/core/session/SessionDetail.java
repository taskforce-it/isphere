/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.session;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.tn5250j.SessionConfig;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.transport.SocketConnector;
import org.tn5250j.interfaces.ConfigureFactory;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.tn5250j.core.DialogActionTypes;
import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.preferences.Preferences;

public class SessionDetail {

    private Preferences preferences;
    private Text textName;
    private Text textDevice;
    private Text textPort;
    private CCombo comboCodePage;
    private CCombo comboSSLType;
    private Button buttonScreenSize24_80;
    private Button buttonScreenSize27_132;
    // private Button buttonEnhancedMode;
    private Button buttonView;
    private Button buttonEditor;
    private Combo comboTheme;
    private Text textUser;
    private Text textPassWord;
    private Text textProgram;
    private Combo textLibrary;
    private Text textMenu;
    private StatusLineManager statusLineManager;
    private Shell shell;
    private String sessionDirectory;
    private int actionType;
    private Session session;
    private String[] codePages = { "37", "37PT", "273", "280", "284", "285", "277-dk", "277-no", "278", "297", "424", "500-ch", "870-pl", "870-sk",
        "871", "875", "1025-r", "1026", "1112", "1141", "1140", "1147", "1148" };

    public SessionDetail(Shell shell, String sessionDirectory, int actionType, Session session) {
        this.shell = shell;
        this.sessionDirectory = sessionDirectory;
        this.actionType = actionType;
        this.session = session;
        preferences = Preferences.getInstance();
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        // Action

        final Label labelAction = new Label(container, SWT.CENTER | SWT.BORDER);
        labelAction.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        if (actionType == DialogActionTypes.DELETE) {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
        }
        labelAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelAction.setText(DialogActionTypes.getText(actionType));

        // General

        final Composite compositeGeneral = new Composite(container, SWT.NONE);
        compositeGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayoutCompositeGeneral = new GridLayout();
        gridLayoutCompositeGeneral.numColumns = 2;
        compositeGeneral.setLayout(gridLayoutCompositeGeneral);

        // General : Connection

        final Label labelConnection = new Label(compositeGeneral, SWT.NONE);
        labelConnection.setText(Messages.Connection_colon);

        final Text textConnection = new Text(compositeGeneral, SWT.BORDER);
        textConnection.setText(session.getConnection());
        textConnection.setEditable(false);
        textConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // General : Name

        final Label labelName = new Label(compositeGeneral, SWT.NONE);
        labelName.setText(Messages.Name_colon);

        textName = new Text(compositeGeneral, SWT.BORDER);
        textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        if (actionType == DialogActionTypes.CREATE) {
            textName.setText(session.getName());
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textName.setText(session.getName());
        }
        if ((actionType == DialogActionTypes.CREATE && !session.getName().equals("")) || actionType == DialogActionTypes.CHANGE
            || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textName.setEnabled(false);
        }

        // General : Device

        final Label labelDevice = new Label(compositeGeneral, SWT.NONE);
        labelDevice.setText(Messages.Device_colon);

        textDevice = new Text(compositeGeneral, SWT.BORDER);
        textDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textDevice.setTextLimit(10);
        if (actionType == DialogActionTypes.CREATE) {
            textDevice.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textDevice.setText(session.getDevice());
        }
        if (textName.getText().equals(ISession.DESIGNER) || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textDevice.setEnabled(false);
        }

        // SSL

        final Label labelSSLType = new Label(compositeGeneral, SWT.NONE);
        labelSSLType.setText(Messages.SSL_type_colon);

        comboSSLType = new CCombo(compositeGeneral, SWT.BORDER | SWT.READ_ONLY);
        comboSSLType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboSSLType.setTextLimit(10);
        comboSSLType.setItems(preferences.getSSLTypeOptions());

        if (actionType == DialogActionTypes.CREATE) {
            comboSSLType.setText(preferences.getSSLType());
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboSSLType.setText(session.getSSLType());
        }

        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboSSLType.setEnabled(false);
        }

        comboSSLType.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (TN5250jConstants.SSL_TYPE_NONE.equals(comboSSLType.getText())) {
                    textPort.setText(TN5250jConstants.PORT_NUMBER);
                } else {
                    textPort.setText(TN5250jConstants.SSL_PORT_NUMBER);
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        // General : Port

        final Label labelPort = new Label(compositeGeneral, SWT.NONE);
        labelPort.setText(Messages.Port_colon);

        textPort = new Text(compositeGeneral, SWT.BORDER);
        textPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPort.setTextLimit(5);
        if (actionType == DialogActionTypes.CREATE) {
            textPort.setText(Integer.toString(preferences.getSessionPortNumber()));
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textPort.setText(session.getPort());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textPort.setEnabled(false);
        }

        // General : Codepage

        final Label labelCodePage = new Label(compositeGeneral, SWT.NONE);
        labelCodePage.setText(Messages.Codepage_colon);

        comboCodePage = new CCombo(compositeGeneral, SWT.BORDER);
        comboCodePage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboCodePage.setTextLimit(10);
        for (int idx = 0; idx < codePages.length; idx++) {
            comboCodePage.add(codePages[idx]);
        }
        if (actionType == DialogActionTypes.CREATE) {
            comboCodePage.setText(preferences.getSessionCodepage());
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboCodePage.setText(session.getCodePage());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboCodePage.setEnabled(false);
        }

        // General : Screen size

        final Label labelScreenSize = new Label(compositeGeneral, SWT.NONE);
        labelScreenSize.setText(Messages.Screensize_colon);

        final Group groupScreenSize = new Group(compositeGeneral, SWT.NONE);
        final GridLayout gridLayoutScreenSize = new GridLayout();
        gridLayoutScreenSize.numColumns = 2;
        groupScreenSize.setLayout(gridLayoutScreenSize);

        buttonScreenSize24_80 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize24_80.setText("24*80");

        buttonScreenSize27_132 = new Button(groupScreenSize, SWT.RADIO);
        buttonScreenSize27_132.setText("27*132");

        if (actionType == DialogActionTypes.CREATE) {
            if (ISession.SIZE_132.equals(preferences.getSessionScreenSize())) {
                buttonScreenSize27_132.setSelection(true);
            } else {
                buttonScreenSize24_80.setSelection(true);
            }
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            if (ISession.SIZE_132.equals(session.getScreenSize())) {
                buttonScreenSize27_132.setSelection(true);
            } else {
                buttonScreenSize24_80.setSelection(true);
            }
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            buttonScreenSize24_80.setEnabled(false);
            buttonScreenSize27_132.setEnabled(false);
        }

        // General : Enhanced mode
        /*
         * final Label labelEnhancedMode = new Label(compositeGeneral,
         * SWT.NONE); labelEnhancedMode.setText(Messages.Enhanced_mode") + ":");
         * buttonEnhancedMode = new Button(compositeGeneral, SWT.CHECK); if
         * (actionType == DialogActionTypes.CREATE) { if
         * (store.getString("BIZ.ISPHERE.TN5250J.ENHANCEDMODE").equals("Y")) {
         * buttonEnhancedMode.setSelection(true); } else {
         * buttonEnhancedMode.setSelection(false); } } else if (actionType ==
         * DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE ||
         * actionType == DialogActionTypes.DISPLAY) { if
         * (session.getEnhancedMode().equals("Y")) {
         * buttonEnhancedMode.setSelection(true); } else {
         * buttonEnhancedMode.setSelection(false); } } if (actionType ==
         * DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
         * buttonEnhancedMode.setEnabled(false); }
         */
        // General : Area
        final Label labelArea = new Label(compositeGeneral, SWT.NONE);
        labelArea.setText(Messages.Area_colon);

        final Group groupArea = new Group(compositeGeneral, SWT.NONE);
        final GridLayout gridLayoutArea = new GridLayout();
        gridLayoutArea.numColumns = 2;
        groupArea.setLayout(gridLayoutArea);

        buttonView = new Button(groupArea, SWT.RADIO);
        buttonView.setText(Messages.View);

        buttonEditor = new Button(groupArea, SWT.RADIO);
        buttonEditor.setText(Messages.Editor);

        if (actionType == DialogActionTypes.CREATE) {
            if (ISession.AREA_VIEW.equals(preferences.getSessionArea())) {
                buttonView.setSelection(true);
            } else {
                buttonEditor.setSelection(true);
            }
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            if (ISession.AREA_VIEW.equals(session.getArea())) {
                buttonView.setSelection(true);
            } else {
                buttonEditor.setSelection(true);
            }
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            buttonView.setEnabled(false);
            buttonEditor.setEnabled(false);
        }

        // Theme combo

        final Label labelTheme = new Label(compositeGeneral, SWT.NONE);
        labelTheme.setText(Messages.Theme_colon);

        final Composite groupTheme = new Composite(compositeGeneral, SWT.NONE);
        final GridLayout gridLayoutGroupTheme = new GridLayout();
        gridLayoutGroupTheme.numColumns = 2;
        gridLayoutGroupTheme.marginWidth = 0;
        groupTheme.setLayout(gridLayoutGroupTheme);
        groupTheme.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        comboTheme = new Combo(groupTheme, SWT.DROP_DOWN);
        GridData gridDataComboTheme = new GridData(GridData.FILL_HORIZONTAL);
        // gridDataComboTheme.minimumWidth = 300;
        comboTheme.setLayoutData(gridDataComboTheme);
        comboTheme.setItems(SessionConfig.loadThemes());

        if (actionType == DialogActionTypes.CREATE) {
            selectTheme(SessionConfig.THEME_NONE);
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            selectTheme(session.getTheme());
        }

        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboTheme.setEnabled(false);
        }

        // Signon mask

        final Group groupSignOnMask = new Group(container, SWT.NONE);
        groupSignOnMask.setText(Messages.Signon_mask);
        groupSignOnMask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        groupSignOnMask.setLayout(gridLayout_1);

        // Signon mask : User

        final Label labelUser = new Label(groupSignOnMask, SWT.NONE);
        labelUser.setText(Messages.User_colon);

        textUser = new Text(groupSignOnMask, SWT.BORDER);
        textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textUser.setTextLimit(128);
        if (actionType == DialogActionTypes.CREATE) {
            textUser.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textUser.setText(session.getUser());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textUser.setEnabled(false);
        }

        // Signon mask : Password

        final Label labelPassWord = new Label(groupSignOnMask, SWT.NONE);
        labelPassWord.setText(Messages.Password_colon);

        textPassWord = new Text(groupSignOnMask, SWT.PASSWORD | SWT.BORDER);
        textPassWord.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPassWord.setTextLimit(128);
        if (actionType == DialogActionTypes.CREATE) {
            textPassWord.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            if (session.getPassword().equals("")) {
                textPassWord.setText("");
            } else {
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(TN5250JCorePlugin.BASIC);
                String decryptedPassword;
                try {
                    decryptedPassword = textEncryptor.decrypt(session.getPassword());
                } catch (EncryptionOperationNotPossibleException exeption) {
                    decryptedPassword = "";
                }
                textPassWord.setText(decryptedPassword);
            }
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textPassWord.setEnabled(false);
        }

        // Signon mask : Program

        final Label labelProgram = new Label(groupSignOnMask, SWT.NONE);
        labelProgram.setText(Messages.Program_colon);

        textProgram = new Text(groupSignOnMask, SWT.BORDER);
        textProgram.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textProgram.setTextLimit(10);
        if (actionType == DialogActionTypes.CREATE) {
            textProgram.setText(session.getProgram());
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textProgram.setText(session.getProgram());
        }
        if (textName.getText().equals(ISession.DESIGNER) || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textProgram.setEnabled(false);
        }

        // Signon mask : Library

        final Label labelLibrary = new Label(groupSignOnMask, SWT.NONE);
        labelLibrary.setText(Messages.Library_colon);

        textLibrary = new Combo(groupSignOnMask, SWT.BORDER);
        textLibrary.setItems(new String[] { ISession.ISPHERE_PRODUCT_LIBRARY });
        textLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textLibrary.setTextLimit(10);
        if (actionType == DialogActionTypes.CREATE) {
            textLibrary.setText(session.getLibrary());
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textLibrary.setText(session.getLibrary());
        }
        if (textName.getText().equals(ISession.DESIGNER) || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textLibrary.setEnabled(false);
        }

        // Signon mask : Menu

        final Label labelMenu = new Label(groupSignOnMask, SWT.NONE);
        labelMenu.setText(Messages.Menu_colon);

        textMenu = new Text(groupSignOnMask, SWT.BORDER);
        textMenu.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMenu.setTextLimit(10);
        if (actionType == DialogActionTypes.CREATE) {
            textMenu.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMenu.setText(session.getMenu());
        }
        if (textName.getText().equals(ISession.DESIGNER) || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMenu.setEnabled(false);
        }

        // Status line

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
        statusLine.setLayoutData(gridDataStatusLine);

        // Set focus

        if (actionType == DialogActionTypes.CREATE) {
            if (textName.getText().equals(ISession.DESIGNER)) {
                textPort.setFocus();
            } else {
                textName.setFocus();
            }
        } else if (actionType == DialogActionTypes.CHANGE) {
            if (textName.getText().equals(ISession.DESIGNER)) {
                textPort.setFocus();
            } else {
                textDevice.setFocus();
            }
        } else {
            textConnection.setFocus();
        }

    }

    private void selectTheme(String theme) {

        int index = -1;

        if (StringHelper.isNullOrEmpty(theme)) {
            index = 0;
        } else {

            String[] themes = comboTheme.getItems();
            for (int i = 0; i < themes.length; i++) {
                if (themes[i].equalsIgnoreCase(theme)) {
                    index = i;
                    break;
                }
            }

        }

        if (index >= 0 && index < comboTheme.getItemCount()) {
            comboTheme.select(index);
        } else {
            comboTheme.setText(theme);
        }

        /*
         * Ugly hack for WDSCi to clear the selection of the text field.
         */
        new UIJob("") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                comboTheme.clearSelection();
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    protected void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            statusLineManager.setErrorMessage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_ERROR), errorMessage);
        } else {
            statusLineManager.setErrorMessage(null, null);
        }
    }

    public boolean processButtonPressed() {
        switch (actionType) {
        case DialogActionTypes.CREATE: {
            convertData();
            if (checkData()) {
                transferData();
                return true;
            }
            return false;
        }
        case DialogActionTypes.CHANGE: {
            convertData();
            if (checkData()) {
                transferData();
                return true;
            }
            return false;
        }
        case DialogActionTypes.DELETE: {
            return true;
        }
        case DialogActionTypes.DISPLAY: {
            return true;
        }
        }
        return false;
    }

    protected void convertData() {
        textName.setText(textName.getText().trim());
        textDevice.setText(textDevice.getText().toUpperCase().trim());
        textPort.setText(textPort.getText().toUpperCase().trim());
        comboCodePage.setText(comboCodePage.getText().trim());
        comboSSLType.setText(comboSSLType.getText().trim());
        comboTheme.setText(comboTheme.getText().trim());
        textUser.setText(textUser.getText().trim());
        textPassWord.setText(textPassWord.getText().trim());
        textProgram.setText(textProgram.getText().toUpperCase().trim());
        textLibrary.setText(textLibrary.getText().toUpperCase().trim());
        textMenu.setText(textMenu.getText().toUpperCase().trim());
    }

    protected boolean checkData() {

        boolean error;

        setErrorMessage(null);

        if (actionType == DialogActionTypes.CREATE) {

            // The value in field 'Name' is not valid.

            if (textName.getText().equals("")) {
                setErrorMessage(Messages.The_value_in_field_Name_is_not_valid);
                textName.setFocus();
                return false;
            }

            // The name _DESIGNER' is reserved.

            if (session.getName().equals("") && textName.getText().equals(ISession.DESIGNER)) {
                setErrorMessage(Messages.The_name_DESIGNER_is_reserved);
                textName.setFocus();
                return false;
            }

            // The name does already exist.

            if (new File(sessionDirectory + File.separator + textName.getText()).exists()) {
                setErrorMessage(Messages.The_name_does_already_exist);
                textName.setFocus();
                return false;
            }
        }

        // The value in field 'Device' is not valid.

        if (!textDevice.getText().equals("")) {
            if (textDevice.getText().equals("")) {
                setErrorMessage(Messages.The_value_in_field_Device_is_not_valid);
                textDevice.setFocus();
                return false;
            }
        }

        // The value in field 'SSL Type' is not valid.

        String sslType = comboSSLType.getText();

        String[] sslProtocols;
        if (TN5250jConstants.SSL_TYPE_NONE.equals(sslType) || TN5250jConstants.SSL_TYPE_DEFAULT.equals(sslType)) {
            sslProtocols = null;
            error = false;
        } else {
            sslProtocols = SocketConnector.getSupportedSSLProtocols();
            error = true;
        }

        if (sslProtocols != null) {
            for (int idx = 0; idx < sslProtocols.length; idx++) {
                if (sslProtocols[idx].equals(sslType)) {
                    error = false;
                    break;
                }
            }
        }

        if (error) {
            setErrorMessage(Messages.The_value_in_field_SSL_type_is_not_valid);
            comboSSLType.setFocus();
            return false;
        }

        // The value in field 'Port' is not valid.

        error = false;
        if (textPort.getText().equals("")) {
            error = true;
        } else {
            try {
                int result = Integer.parseInt(textPort.getText());
                if (result == 0) {
                    error = true;
                }
            } catch (NumberFormatException e1) {
                error = true;
            }
        }
        if (error) {
            setErrorMessage(Messages.The_value_in_field_Port_is_not_valid);
            textPort.setFocus();
            return false;
        }

        // The value in field 'Codepage' is not valid.

        error = true;
        for (int idx = 0; idx < codePages.length; idx++) {
            if (codePages[idx].equals(comboCodePage.getText())) {
                error = false;
                break;
            }
        }
        if (error) {
            setErrorMessage(Messages.The_value_in_field_Codepage_is_not_valid);
            comboCodePage.setFocus();
            return false;
        }

        // The value in field 'Theme' is not valid.

        boolean isNewTheme = true;

        String[] themes = comboTheme.getItems();
        for (String theme : themes) {
            if (theme.equalsIgnoreCase(comboTheme.getText())) {
                isNewTheme = false;
                break;
            }
        }

        if (isNewTheme) {
            if (!MessageDialog.openQuestion(shell, Messages.Session, NLS.bind(Messages.Question_Create_New_Theme, comboTheme
                .getText()))) {
                return false;
            }
        }

        // The value in field 'User' is not valid.

        if (textName.getText().equals(ISession.DESIGNER) && textUser.getText().equals("")) {
            setErrorMessage(Messages.The_value_in_field_User_is_not_valid);
            textUser.setFocus();
            return false;
        }

        // The value in field 'Password' is not valid.

        if (textName.getText().equals(ISession.DESIGNER) && textPassWord.getText().equals("")) {
            setErrorMessage(Messages.The_value_in_field_Password_is_not_valid);
            textPassWord.setFocus();
            return false;
        }

        // The value in field 'Program' is not valid.

        if (!textProgram.getText().equals("")) {
            error = false;
            if (error) {
                setErrorMessage(Messages.The_value_in_field_Program_is_not_valid);
                textProgram.setFocus();
                return false;
            }
        }

        // The value in field 'Library' is not valid.

        if (!textLibrary.getText().equals("")) {
            error = false;
            if (error) {
                setErrorMessage(Messages.The_value_in_field_Library_is_not_valid);
                textLibrary.setFocus();
                return false;
            }
        }

        // The value in field 'Menu' is not valid.

        if (!textMenu.getText().equals("")) {
            error = false;
            if (error) {
                setErrorMessage(Messages.The_value_in_field_Menu_is_not_valid);
                textMenu.setFocus();
                return false;
            }
        }

        // Everything is alright
        return true;
    }

    protected void transferData() {
        session.setName(textName.getText());
        session.setDevice(textDevice.getText());
        session.setPort(textPort.getText());
        session.setCodePage(comboCodePage.getText());
        session.setSSLType(comboSSLType.getText());
        if (buttonScreenSize27_132.getSelection()) {
            session.setScreenSize(ISession.SIZE_132);
        } else {
            session.setScreenSize(ISession.SIZE_80);
        }
        // if (buttonEnhancedMode.getSelection()) {
        // session.setEnhancedMode("Y");
        // }
        // else {
        // session.setEnhancedMode("");
        // }
        if (buttonView.getSelection()) {
            session.setArea(ISession.AREA_VIEW);
        } else {
            session.setArea(ISession.AREA_EDITOR);
        }

        session.setTheme(comboTheme.getText());

        session.setUser(textUser.getText());

        if (textPassWord.getText().equals("")) {
            session.setPassword("");
        } else {
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(TN5250JCorePlugin.BASIC);
            String encryptedPassword = textEncryptor.encrypt(textPassWord.getText());
            session.setPassword(encryptedPassword);
        }

        session.setProgram(textProgram.getText());
        session.setLibrary(textLibrary.getText());
        session.setMenu(textMenu.getText());
    }

}
