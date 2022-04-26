/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import biz.isphere.core.internal.AbstractMessageFormatter;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteMessageDescription;

public class MessageFormatter extends AbstractMessageFormatter {

    public String format(MessageDescription aMessageDescription) {
        String messageHelp = aMessageDescription.getHelpText();
        if (MessageDescription.VALUE_NONE.equals(messageHelp)) {
            messageHelp = "";
        }
        return format(aMessageDescription.getMessage(), messageHelp);
    }

    public String format(QSYSRemoteMessageDescription aMessageDescription) {
        return format(aMessageDescription.getText(), aMessageDescription.getHelp());
    }
}
