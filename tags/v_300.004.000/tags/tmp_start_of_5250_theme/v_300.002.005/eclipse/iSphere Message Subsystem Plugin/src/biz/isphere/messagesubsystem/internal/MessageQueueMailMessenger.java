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
package biz.isphere.messagesubsystem.internal;

import java.text.DateFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.BasicMessageFormatter;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

public class MessageQueueMailMessenger {

    private static final String MAIL_DEBUG = "mail.debug"; //$NON-NLS-1$
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol"; //$NON-NLS-1$
    private static final String MAIL_SMTP_PORT = "mail.smtp.port"; //$NON-NLS-1$
    private static final String MAIL_SMTP_HOST = "mail.smtp.host"; //$NON-NLS-1$
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth"; //$NON-NLS-1$
    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain"; //$NON-NLS-1$

    private static final String PROTOCOL_SMTP = "smtp"; //$NON-NLS-1$
    private static final String PROPERTY_TRUE = "true"; //$NON-NLS-1$
    private static final String PROPERTY_FALSE = "false"; //$NON-NLS-1$

    private static final String NEW_LINE = "\n"; //$NON-NLS-1$

    private BasicMessageFormatter messageFormatter;
    private DateFormat dateFormat;

    private String[] recipients;
    private boolean debug = false;
    private Properties properties;
    private String mailFrom;

    public MessageQueueMailMessenger() {
        super();

        messageFormatter = new BasicMessageFormatter();
        dateFormat = DateFormat.getDateTimeInstance();

        properties = new Properties();
        properties.put(MAIL_TRANSPORT_PROTOCOL, PROTOCOL_SMTP);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug)
            properties.setProperty(MAIL_DEBUG, PROPERTY_TRUE);
        else
            properties.setProperty(MAIL_DEBUG, PROPERTY_FALSE);
    }

    public void setHost(String host) {
        properties.setProperty(MAIL_SMTP_HOST, host);
    }

    public void setPort(String port) {
        properties.setProperty(MAIL_SMTP_PORT, port);
    }

    public void setRecipients(String[] strings) {
        recipients = strings;
    }

    public void setMailFrom(String string) {
        mailFrom = string;
    }

    public void sendMail(String subject, String message) throws Exception {
        sendMail(getSession(), subject, message);
    }

    public void sendMail(String subject, String message, String user, String password) throws Exception {
        sendMail(getSession(user, password), subject, message);
    }

    public void sendMail(ReceivedMessage message) throws Exception {
        sendMail(getSession(), message.getText(), getMessageBody(message));
    }

    public void sendMail(ReceivedMessage message, String user, String password) throws Exception {
        sendMail(getSession(user, password), message.getText(), getMessageBody(message));
    }

    private void sendMail(Session session, String subject, String message) throws Exception {

        if ((recipients == null) || (recipients.length == 0)) {
            return;
        }

        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(recipients[0], mailFrom);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);
        msg.setSubject(subject);
        msg.setContent(message, MIME_TYPE_TEXT_PLAIN);
        Transport.send(msg);
    }

    private String getMessageBody(ReceivedMessage message) {

        int tabPos = getTabPosition(Messages.From_colon, Messages.Message_ID_colon, Messages.Severity_colon, Messages.Message_type_colon,
            Messages.Date_sent_colon);

        StringBuffer body = new StringBuffer();
        appendMessageLine(body, tabPos, Messages.From_colon, message.getUser());
        appendMessageLine(body, tabPos, Messages.Message_ID_colon, message.getID());
        appendMessageLine(body, tabPos, Messages.Severity_colon, Integer.valueOf(message.getSeverity()).toString());
        appendMessageLine(body, tabPos, Messages.Message_type_colon, message.getMessageType());
        appendMessageLine(body, tabPos, Messages.Date_sent_colon, dateFormat.format(message.getDate().getTime()));

        body.append(NEW_LINE);

        tabPos = getTabPosition(Messages.From_job_colon, Messages.From_job_number_colon, Messages.From_program_colon);
        appendMessageLine(body, tabPos, Messages.From_job_colon, message.getFromJobName());
        appendMessageLine(body, tabPos, Messages.From_job_number_colon, message.getFromJobNumber());
        appendMessageLine(body, tabPos, Messages.From_program_colon, message.getFromProgram());

        body.append(NEW_LINE);
        body.append(messageFormatter.format(message.getText(), message.getHelpFormatted()));

        return body.toString();
    }

    private void appendMessageLine(StringBuffer body, int tabPos, String label, String text) {

        body.append(StringHelper.getFixLength(label, tabPos));
        if (!StringHelper.isNullOrEmpty(text)) {
            body.append(text);
        }
        body.append(NEW_LINE);

    }

    private int getTabPosition(String... text) {

        int tabPos = 0;

        for (int i = 0; i < text.length; i++) {
            if (text[i].length() > tabPos) {
                tabPos = text[i].length();
            }
        }

        tabPos++;

        return tabPos;
    }

    private Session getSession() {

        properties.put(MAIL_SMTP_AUTH, PROPERTY_FALSE);
        Session session = Session.getInstance(properties);
        configureSession(session);

        return session;
    }

    private Session getSession(final String user, final String password) {

        properties.put(MAIL_SMTP_AUTH, PROPERTY_TRUE);
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        configureSession(session);

        return session;
    }

    private void configureSession(Session session) {
        session.setDebug(debug);
    }
}
