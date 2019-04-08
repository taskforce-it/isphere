/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import biz.isphere.messagesubsystem.internal.QEZSNDMG;

import com.ibm.as400.access.AS400;

public class SendMessageDelegate {

    public void sendMessage(AS400 system, SendMessageOptions options) {

        QEZSNDMG qezsndmg = new QEZSNDMG();
        qezsndmg.sendMessages(system, options);

    }

}
