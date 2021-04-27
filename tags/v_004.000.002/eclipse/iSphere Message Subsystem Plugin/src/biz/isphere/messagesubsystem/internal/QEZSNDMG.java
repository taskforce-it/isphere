/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.internal;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;
import biz.isphere.messagesubsystem.rse.SendMessageOptions;

public class QEZSNDMG {

    public static final String TYPE_INQUERY = "*INQ"; //$NON-NLS-1$
    public static final String TYPE_INFORMATIONAL = "*INFO"; //$NON-NLS-1$
    public static final String DELIVERY_NORMAL = "*NORMAL"; //$NON-NLS-1$
    public static final String DELIVERY_BREAK = "*BREAK"; //$NON-NLS-1$
    public static final String RECIPIENT_ALL = "*ALL"; //$NON-NLS-1$
    public static final String RECIPIENT_ALLACT = "*ALLACT"; //$NON-NLS-1$
    public static final String RECIPIENT_SYSOPR = "*SYSOPR"; //$NON-NLS-1$
    public static final String RECIPIENT_TYPE_USER = "*USR"; //$NON-NLS-1$
    public static final String RECIPIENT_TYPE_DISPLAY = "*DSP"; //$NON-NLS-1$

    private PcmlProgramCallDocument pcml;

    public void sendMessages(AS400 system, SendMessageOptions options) {
        run(system, options);
    }

    private void run(AS400 system, SendMessageOptions options) {

        try {

            String[] recipients = options.getRecipients();

            pcml = new PcmlProgramCallDocument(system, "biz.isphere.messagesubsystem.internal.QEZSNDMG", getClass().getClassLoader()); //$NON-NLS-1$
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
                if (options.getReplyMessageQueueName() != null) {
                    pcml.setQualifiedObjectName("QEZSNDMG.queueName", options.getReplyMessageQueueLibrary(), options.getReplyMessageQueueName()); //$NON-NLS-1$
                }
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
            ISpherePlugin.logError("Failed calling the QEZSNDMG API.", e); //$NON-NLS-1$
        }
    }
}
