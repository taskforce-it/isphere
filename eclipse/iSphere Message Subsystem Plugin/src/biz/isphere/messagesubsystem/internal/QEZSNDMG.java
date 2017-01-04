/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.internal;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;
import biz.isphere.messagesubsystem.rse.SendMessageOptions;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;

public class QEZSNDMG {

    public static final String TYPE_INQUERY = "*INQ";
    public static final String TYPE_INFO = "*INFO";
    public static final String DELIVERY_NORMAL = "*NORMAL";
    public static final String DELIVERY_BREAK = "*BREAK";
    public static final String RECIPIENT_ALL = "*ALL";
    public static final String RECIPIENT_ALLACT = "*ALLACT";
    public static final String RECIPIENT_SYSOPR = "*SYSOPR";
    public static final String RECIPIENT_TYPE_USER = "*USR";
    public static final String RECIPIENT_TYPE_DSP = "*DSP";

    private PcmlProgramCallDocument pcml;

    public void sendMessages(AS400 system, SendMessageOptions options) {
        run(system, options);
    }

    private void run(AS400 system, SendMessageOptions options) {

        try {

            String[] recipients = options.getRecipients();

            pcml = new PcmlProgramCallDocument(system, "biz.isphere.messagesubsystem.internal.QEZSNDMG", getClass().getClassLoader());
            pcml.setValue("QEZSNDMG.msgType", options.getMessageType()); //$NON-NLS-1$
            pcml.setValue("QEZSNDMG.deliveryMode", options.getDeliveryMode()); //$NON-NLS-1$
            pcml.setValue("QEZSNDMG.msgLen", new Integer(options.getMessageText().length())); //$NON-NLS-1$
            pcml.setValue("QEZSNDMG.msgText", options.getMessageText()); //$NON-NLS-1$
            pcml.setValue("QEZSNDMG.userNum", new Integer(recipients.length)); //$NON-NLS-1$
            pcml.setValue("QEZSNDMG.nameType", options.getRecipientType()); //$NON-NLS-1$

            for (int i = 0; i < recipients.length; i++) {
                pcml.setValue("QEZSNDMG.userList", new int[] { i }, recipients[i]); //$NON-NLS-1$
            }

            if (options.isInquiryMessage()) {
                pcml.setQualifiedObjectName("QEZSNDMG.queueName", options.getReplyMessageQueueLibrary(), options.getReplyMessageQueueName());
            }

            boolean rc = pcml.callProgram("QEZSNDMG"); //$NON-NLS-1$

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("QEZSNDMG"); //$NON-NLS-1$
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                }
                ISpherePlugin.logError("*** Call to QEZSNDMG failed. See previous messages ***", null); //$NON-NLS-1$

            }

        } catch (PcmlException e) {
            ISpherePlugin.logError("Failed calling the QEZSNDMG API.", e);
        }
    }

    public static void main(String[] args) {

        String hostname = System.getProperty("isphere.junit.as400"); //$NON-NLS-1$
        String user = System.getProperty("isphere.junit.username"); //$NON-NLS-1$
        String password = System.getProperty("isphere.junit.password"); //$NON-NLS-1$

        AS400 system = new AS400(hostname, user, password);

        SendMessageOptions options = new SendMessageOptions();

        QEZSNDMG main = new QEZSNDMG();
        main.run(system, options);

    }
}
