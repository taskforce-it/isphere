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
package biz.isphere.messagesubsystem.rse.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import biz.isphere.messagesubsystem.rse.Messages;

public class MonitoringPropertiesPage extends PropertyPage {

    public final static String ID = "biz.isphere.messagesubsystem.internal.MonitoringPropertiesPage";

    private QueuedMessageSubSystem queuedMessageSubSystem;
    private MonitoringAttributes monitoringAttributes;

    private Button monitorButton;
    private Button removeButton;
    private Label removeLabel;
    private Composite prefGroup;
    private Combo inqCombo;
    private Combo infCombo;
    private Group emailGroup;
    private Text emailText;
    private Text fromText;
    private Text portText;
    private Text hostText;
    private Button testButton;

    public MonitoringPropertiesPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {

        queuedMessageSubSystem = (QueuedMessageSubSystem)getElement();
        monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);

        Composite propsGroup = new Composite(parent, SWT.NONE);
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

        loadSettings();

        setControlVisibility();

        return propsGroup;
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

        Label dummy = new Label(emailGroup, SWT.NONE); // dummy
        dummy.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER, GridData.HORIZONTAL_ALIGN_CENTER, false, false));

        testButton = WidgetFactory.createPushButton(emailGroup);
        testButton.setText(Messages.Email_send_test_message);
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

        saveSettings();

        queuedMessageSubSystem.restartMessageMonitoring();

        return super.performOk();
    }

    private void loadSettings() {

        monitorButton.setSelection(monitoringAttributes.isMonitoringEnabled());
        removeButton.setSelection(monitoringAttributes.removeInformationalMessages());
        inqCombo.select(inqCombo.indexOf(monitoringAttributes.getInqueryMessageNotificationTypeForGUI()));
        infCombo.select(inqCombo.indexOf(monitoringAttributes.getInformationalMessageNotificationTypeForGUI()));
        emailText.setText(monitoringAttributes.getEmail());
        fromText.setText(monitoringAttributes.getFrom());
        hostText.setText(monitoringAttributes.getHost());
        portText.setText(monitoringAttributes.getPort());
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
    }

    private void setControlVisibility() {

        if (!monitorButton.getSelection()) {
            removeButton.setVisible(false);
            removeLabel.setVisible(false);
            prefGroup.setVisible(false);
            emailGroup.setVisible(false);
        } else {
            removeButton.setVisible(true);
            removeLabel.setVisible(true);

            if (infCombo.getSelectionIndex() == infCombo.indexOf(Messages.Notification_type_Beep)) {
                removeButton.setEnabled(false);
                removeButton.setSelection(false);
            } else {
                removeButton.setEnabled(true);
            }

            prefGroup.setVisible(true);
            if ((inqCombo.getSelectionIndex() == inqCombo.indexOf(Messages.Notification_type_Email))
                || (infCombo.getSelectionIndex() == infCombo.indexOf(Messages.Notification_type_Email))) {
                emailGroup.setVisible(true);
            } else {
                emailGroup.setVisible(false);
            }
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
            messenger.sendMail(Messages.ISeries_Message_Monitor_Test, Messages.Notification_test_message);
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
