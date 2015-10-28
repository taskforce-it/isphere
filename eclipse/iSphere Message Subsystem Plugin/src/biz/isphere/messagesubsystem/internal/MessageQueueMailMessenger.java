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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import biz.isphere.core.internal.BasicMessageFormatter;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

public class MessageQueueMailMessenger {

    private static final String MAIL_DEBUG = "mail.debug"; //$NON-NLS-1$
    private static final String MAIL_SMTP_PORT = "mail.smtp.port"; //$NON-NLS-1$
    private static final String MAIL_SMTP_HOST = "mail.smtp.host"; //$NON-NLS-1$
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth"; //$NON-NLS-1$

    private static final String NEW_LINE = "\n"; //$NON-NLS-1$

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain"; //$NON-NLS-1$

    private BasicMessageFormatter messageFormatter;

    private String[] recipients;
    private boolean debug = false;
    private Properties properties = new Properties();
    private String mailFrom;

    public MessageQueueMailMessenger() {
        super();

        messageFormatter = new BasicMessageFormatter();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug)
            properties.setProperty(MAIL_DEBUG, "true"); //$NON-NLS-1$ //$NON-NLS-2$
        else
            properties.setProperty(MAIL_DEBUG, "false"); //$NON-NLS-1$ //$NON-NLS-2$
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

        session.setDebug(debug);
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

        StringBuffer body = new StringBuffer();
        if (message.getUser() != null) {
            body.append(Messages.From_colon + message.getUser() + NEW_LINE);
        }

        if (message.getID() != null) {
            body.append(Messages.Message_ID_colon + message.getID() + NEW_LINE);
        }

        body.append(Messages.Severity_colon + message.getSeverity() + NEW_LINE);
        body.append(Messages.Message_type_colon);
        body.append(message.getMessageType());
        body.append(NEW_LINE);

        if (message.getDate() != null) {
            body.append(NEW_LINE + Messages.Date_sent_colon + message.getDate().getTime() + NEW_LINE);
        }

        if (message.getFromJobName() != null) {
            body.append(NEW_LINE + Messages.From_job_colon + message.getFromJobName() + NEW_LINE);
        }

        if (message.getFromJobNumber() != null) {
            body.append(Messages.From_job_number_colon + message.getFromJobNumber() + NEW_LINE);
        }

        if (message.getFromProgram() != null) {
            body.append(Messages.From_program_colon + message.getFromProgram() + NEW_LINE);
        }

        body.append(NEW_LINE);
        body.append(messageFormatter.format(message.getText(), message.getHelpFormatted()));

        return body.toString();
    }

    private Session getSession() {

        properties.put(MAIL_SMTP_AUTH, false);
        return Session.getInstance(properties);
    }

    private Session getSession(final String user, final String password) {

        properties.put(MAIL_SMTP_AUTH, true);
        return Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }
}
