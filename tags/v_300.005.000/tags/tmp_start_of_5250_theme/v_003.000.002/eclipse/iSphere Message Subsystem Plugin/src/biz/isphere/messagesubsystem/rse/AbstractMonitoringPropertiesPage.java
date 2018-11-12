/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.MessageQueueMailMessenger;

public abstract class AbstractMonitoringPropertiesPage extends PropertyPage {

    public final static String ID = "biz.isphere.messagesubsystem.internal.MonitoringPropertiesPage"; //$NON-NLS-1$

    private IQueuedMessageSubsystem queuedMessageSubSystem;
    private MonitoringAttributes monitoringAttributes;

    private Button monitorButton;
    private Button removeButton;
    private Label removeLabel;
    private Composite prefGroup;
    private Combo inqCombo;
    private Combo infCombo;
    private Button collectMessagesButton;
    private Label collectMessagesLabel;
    private Group emailGroup;
    private Text emailText;
    private Text fromText;
    private Text hostText;
    private Text portText;
    private Button smtpCredentialsButton;
    private Text smtpUserText;
    private Text smtpPasswordText;
    private Button testButton;

    private QueuedMessageFilterStringEditPaneDelegate delegate;
    private Set<Object> controlsInError;

    public AbstractMonitoringPropertiesPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {

        queuedMessageSubSystem = (IQueuedMessageSubsystem)getElement();
        monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);
        delegate = new QueuedMessageFilterStringEditPaneDelegate();
        controlsInError = new HashSet<Object>();

        CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER);
        addMonitorTab(tabFolder);
        addFilterTab(tabFolder);

        loadSettings();

        setControlVisibility();

        tabFolder.setSelection(0);

        return tabFolder;
    }

    private void addMonitorTab(CTabFolder tabFolder) {

        CTabItem tabItem = new CTabItem(tabFolder, SWT.NULL);
        tabItem.setText(Messages.Monitor);

        Composite propsGroup = new Composite(tabFolder, SWT.NONE);
        tabItem.setControl(propsGroup);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        propsGroup.setLayout(layout);
        propsGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Composite monGroup = new Composite(propsGroup, SWT.NONE);
        GridLayout monLayout = new GridLayout();
        monLayout.numColumns = 2;
        monGroup.setLayout(monLayout);
        monGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        monitorButton = WidgetFactory.createCheckbox(monGroup);
        monitorButton.setToolTipText(Messages.Monitor_message_queue_tooltip);

        Label monitorLabel = new Label(monGroup, SWT.NONE);
        monitorLabel.setText(Messages.Monitor_message_queue);
        monitorLabel.setToolTipText(Messages.Monitor_message_queue_tooltip);

        removeButton = WidgetFactory.createCheckbox(monGroup);
        removeButton.setToolTipText(Messages.Remove_informational_messages_after_notification_tooltip);

        removeLabel = new Label(monGroup, SWT.NONE);
        removeLabel.setText(Messages.Remove_informational_messages_after_notification);
        removeLabel.setToolTipText(Messages.Remove_informational_messages_after_notification_tooltip);

        collectMessagesButton = WidgetFactory.createCheckbox(monGroup);
        collectMessagesButton.setToolTipText(Messages.Collect_informational_messages_on_startup_tooltip);

        collectMessagesLabel = new Label(monGroup, SWT.NONE);
        collectMessagesLabel.setText(Messages.Collect_informational_messages_on_startup);
        collectMessagesLabel.setToolTipText(Messages.Collect_informational_messages_on_startup_tooltip);

        prefGroup = new Composite(propsGroup, SWT.NONE);
        GridLayout prefLayout = new GridLayout();
        prefLayout.numColumns = 2;
        prefGroup.setLayout(prefLayout);
        prefGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Label inqLabel = new Label(prefGroup, SWT.NONE);
        inqLabel.setText(Messages.Inquiry_message_notification_colon);
        inqLabel.setToolTipText(Messages.Inquiry_message_notification_tooltip);

        inqCombo = WidgetFactory.createReadOnlyCombo(prefGroup);
        inqCombo.setToolTipText(Messages.Inquiry_message_notification_tooltip);
        loadNotificationTypes(inqCombo);
        GridData gd = new GridData();
        gd.widthHint = 100;
        inqCombo.setLayoutData(gd);

        Label infLabel = new Label(prefGroup, SWT.NONE);
        infLabel.setText(Messages.Informational_message_notification_colon);
        infLabel.setToolTipText(Messages.Informational_message_notification_tooltip);

        infCombo = WidgetFactory.createReadOnlyCombo(prefGroup);
        infCombo.setToolTipText(Messages.Informational_message_notification_tooltip);
        loadNotificationTypes(infCombo);
        gd = new GridData();
        gd.widthHint = 100;
        infCombo.setLayoutData(gd);

        SelectionListener listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
                setControlVisibility();
            }
        };

        monitorButton.addSelectionListener(listener);
        inqCombo.addSelectionListener(listener);
        infCombo.addSelectionListener(listener);

        createEmailGroup(propsGroup);

        smtpCredentialsButton.addSelectionListener(listener);
    }

    private void addFilterTab(CTabFolder tabFolder) {

        CTabItem tabItem = new CTabItem(tabFolder, SWT.NULL);
        tabItem.setText(Messages.Filter);

        Composite propsGroup = new Composite(tabFolder, SWT.NONE);
        tabItem.setControl(propsGroup);
        propsGroup.setLayout(new GridLayout(3, false));

        delegate.createContents(propsGroup);

        delegate.resetFields();
        delegate.doInitializeFields(null);

        ModifyListener keyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Object source = e.getSource();
                if (source instanceof Text) {
                    Text text = (Text)source;
                    validateStringInput(source, text.getText());
                }
                if (source instanceof Combo) {
                    Combo combo = (Combo)source;
                    validateStringInput(source, combo.getText());
                }
            }
        };

        delegate.addModifyListener(keyListener);
    }

    private void validateStringInput(Object control, String text) {

        if (StringHelper.isNullOrEmpty(text)) {
            setErrorMessage(Messages.Please_enter_a_value);
            controlsInError.add(control);
        } else {
            controlsInError.remove(control);
        }

        if (controlsInError.size() == 0) {
            setValid(true);
            setErrorMessage(null);
        } else {
            setValid(false);
        }
    }

    private void setSendEmailButtonEnablement() {
        if (validateEmailProperties()) {
            testButton.setEnabled(true);
        } else {
            testButton.setEnabled(false);
        }
    }

    private void createEmailGroup(Composite propsGroup) {

        emailGroup = new Group(propsGroup, SWT.NONE);
        GridLayout emailLayout = new GridLayout();
        emailLayout.numColumns = 2;
        emailGroup.setLayout(emailLayout);
        emailGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Label emailLabel = new Label(emailGroup, SWT.NONE);
        emailLabel.setText(Messages.Email_address_colon);
        emailLabel.setToolTipText(Messages.Email_address_tooltip);

        emailText = WidgetFactory.createText(emailGroup);
        emailText.setToolTipText(Messages.Email_address_tooltip);
        GridData gd = new GridData();
        gd.widthHint = 250;
        emailText.setLayoutData(gd);

        Label fromLabel = new Label(emailGroup, SWT.NONE);
        fromLabel.setText(Messages.Email_from_colon);
        fromLabel.setToolTipText(Messages.Email_from_tooltip);

        fromText = WidgetFactory.createText(emailGroup);
        fromText.setToolTipText(Messages.Email_from_tooltip);
        gd = new GridData();
        gd.widthHint = 250;
        fromText.setLayoutData(gd);

        Label hostLabel = new Label(emailGroup, SWT.NONE);
        hostLabel.setText(Messages.Email_host_colon);
        hostLabel.setToolTipText(Messages.Email_host_tooltip);

        hostText = WidgetFactory.createText(emailGroup);
        hostText.setToolTipText(Messages.Email_host_tooltip);
        gd = new GridData();
        gd.widthHint = 250;
        hostText.setLayoutData(gd);

        Label portLabel = new Label(emailGroup, SWT.NONE);
        portLabel.setText(Messages.Email_port_colon);
        portLabel.setToolTipText(Messages.Email_port_tooltip);

        portText = WidgetFactory.createIntegerText(emailGroup);
        portText.setToolTipText(Messages.Email_port_tooltip);
        gd = new GridData();
        gd.widthHint = 50;
        portText.setLayoutData(gd);
        portText.setTextLimit(4);

        smtpCredentialsButton = WidgetFactory.createCheckbox(emailGroup);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = 2;
        smtpCredentialsButton.setLayoutData(gd);
        smtpCredentialsButton.setText(Messages.SmtpLogin_credentials);
        smtpCredentialsButton.setToolTipText(Messages.SmtpLogin_credentials_tooltip);

        Label smtpUser = new Label(emailGroup, SWT.NONE);
        smtpUser.setText(Messages.SmtpUser_colon);
        smtpUser.setToolTipText(Messages.SmtpUser_tooltip);

        smtpUserText = WidgetFactory.createText(emailGroup);
        smtpUserText.setToolTipText(Messages.SmtpUser_tooltip);
        gd = new GridData();
        gd.widthHint = 250;
        smtpUserText.setLayoutData(gd);

        Label smtpPassword = new Label(emailGroup, SWT.NONE);
        smtpPassword.setText(Messages.SmtpPassword_colon);
        smtpPassword.setToolTipText(Messages.SmtpPassword_tooltip);

        smtpPasswordText = WidgetFactory.createPassword(emailGroup);
        smtpPasswordText.setToolTipText(Messages.SmtpPassword_tooltip);
        gd = new GridData();
        gd.widthHint = 250;
        smtpPasswordText.setLayoutData(gd);

        Label dummy = new Label(emailGroup, SWT.NONE); // dummy
        dummy.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER, GridData.HORIZONTAL_ALIGN_CENTER, false, false));

        testButton = WidgetFactory.createPushButton(emailGroup);
        testButton.setText(Messages.Email_send_test_message);
        testButton.setToolTipText(Messages.Email_send_test_message_tooltip);
        testButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
                testEmail();
            }
        });

        ModifyListener modListener = new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                setSendEmailButtonEnablement();
            }
        };

        emailText.addModifyListener(modListener);
        fromText.addModifyListener(modListener);
        hostText.addModifyListener(modListener);
        portText.addModifyListener(modListener);
    }

    private boolean validateEmailProperties() {

        if (StringHelper.isNullOrEmpty(emailText.getText())) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(fromText.getText())) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(hostText.getText())) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(portText.getText())) {
            return false;
        }

        return true;
    }

    private void loadNotificationTypes(Combo combo) {
        combo.add(Messages.Notification_type_Dialog);
        combo.add(Messages.Notification_type_Email);
        combo.add(Messages.Notification_type_Beep);
    }

    @Override
    public boolean performOk() {

        if (queuedMessageSubSystem.hasPendingRequest()) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Previous_request_is_still_pending);
            return false;
        }

        saveSettings();

        queuedMessageSubSystem.restartMessageMonitoring();

        return super.performOk();
    }

    @Override
    protected void performDefaults() {

        boolean isMonitoring = monitorButton.getSelection();
        monitoringAttributes.restoreToDefault();

        loadSettings();
        monitorButton.setSelection(isMonitoring);

        setControlVisibility();

        super.performDefaults();
    }

    private void loadSettings() {

        monitorButton.setSelection(monitoringAttributes.isMonitoringEnabled());
        removeButton.setSelection(monitoringAttributes.isRemoveInformationalMessages());
        inqCombo.select(inqCombo.indexOf(monitoringAttributes.getInqueryMessageNotificationTypeForGUI()));
        infCombo.select(inqCombo.indexOf(monitoringAttributes.getInformationalMessageNotificationTypeForGUI()));
        emailText.setText(monitoringAttributes.getEmail());
        fromText.setText(monitoringAttributes.getFrom());
        hostText.setText(monitoringAttributes.getHost());
        portText.setText(monitoringAttributes.getPort());
        smtpCredentialsButton.setSelection(monitoringAttributes.isSmtpLogin());
        smtpUserText.setText(monitoringAttributes.getSmtpUser());
        smtpPasswordText.setText(monitoringAttributes.getSmtpPassword());

        String filterString = monitoringAttributes.getFilterString();
        delegate.doInitializeFields(filterString);
    }

    private void saveSettings() {

        monitoringAttributes.setMonitoring(monitorButton.getSelection());
        monitoringAttributes.setRemoveInformationalMessages(removeButton.getSelection());
        monitoringAttributes.setInqueryMessageNotificationTypeFromGUI(inqCombo.getText());
        monitoringAttributes.setInformationalMessageNotificationTypeFromGUI(infCombo.getText());
        monitoringAttributes.setEmail(emailText.getText());
        monitoringAttributes.setFrom(fromText.getText());
        monitoringAttributes.setHost(hostText.getText());
        monitoringAttributes.setPort(portText.getText());
        monitoringAttributes.setSmtpLogin(smtpCredentialsButton.getSelection());
        monitoringAttributes.setSmtpUser(smtpUserText.getText());
        monitoringAttributes.setSmtpPassword(smtpPasswordText.getText());

        String filterString = delegate.getFilterString();
        monitoringAttributes.setFilterString(filterString);
    }

    private void setControlVisibility() {

        if (!monitorButton.getSelection()) {
            removeButton.setVisible(false);
            removeLabel.setVisible(false);
            collectMessagesButton.setVisible(false);
            collectMessagesLabel.setVisible(false);
            prefGroup.setVisible(false);
            emailGroup.setVisible(false);
        } else {
            removeButton.setVisible(true);
            removeLabel.setVisible(true);
            collectMessagesButton.setVisible(true);
            collectMessagesLabel.setVisible(true);

            if (infCombo.getSelectionIndex() == infCombo.indexOf(Messages.Notification_type_Beep)) {
                removeButton.setEnabled(false);
                removeButton.setSelection(false);
            } else {
                removeButton.setEnabled(true);
            }

            if (infCombo.getSelectionIndex() == infCombo.indexOf(Messages.Notification_type_Dialog)) {
                collectMessagesButton.setEnabled(true);
            } else {
                collectMessagesButton.setEnabled(false);
            }

            prefGroup.setVisible(true);
            if ((inqCombo.getSelectionIndex() == inqCombo.indexOf(Messages.Notification_type_Email))
                || (infCombo.getSelectionIndex() == infCombo.indexOf(Messages.Notification_type_Email))) {
                emailGroup.setVisible(true);
            } else {
                emailGroup.setVisible(false);
            }
        }

        if (smtpCredentialsButton.getSelection()) {
            smtpUserText.setEnabled(true);
            smtpPasswordText.setEnabled(true);
        } else {
            smtpUserText.setEnabled(false);
            smtpPasswordText.setEnabled(false);
        }
    }

    private void testEmail() {

        MessageQueueMailMessenger messenger = new MessageQueueMailMessenger();
        String[] recipients = new String[] { emailText.getText() };
        messenger.setRecipients(recipients);
        messenger.setMailFrom(fromText.getText());
        messenger.setPort(portText.getText());
        messenger.setHost(hostText.getText());

        try {
            if (smtpCredentialsButton.getSelection()) {
                messenger.sendMail(Messages.ISeries_Message_Monitor_Test, Messages.Notification_test_message, smtpUserText.getText(),
                    smtpPasswordText.getText());
            } else {
                messenger.sendMail(Messages.ISeries_Message_Monitor_Test, Messages.Notification_test_message);
            }

            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.ISeries_Message_Monitor_Test,
                Messages.Notification_test_message_sent_to + " " + emailText.getText()); //$NON-NLS-1$
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = e.toString();
            }
            MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Notification_test_message_failed, errorMessage);
        }
    }
}
