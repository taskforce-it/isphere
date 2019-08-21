/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse.internal;

import biz.isphere.messagesubsystem.rse.MessageHandler;
import biz.isphere.messagesubsystem.rse.MonitoredMessageQueue;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.etools.systems.subsystems.CommunicationsEvent;
import com.ibm.etools.systems.subsystems.ICommunicationsListener;

public class CommunicationsListener implements ICommunicationsListener {

    private QueuedMessageSubSystem queuedMessageSubSystem;
    private MonitoredMessageQueue monitoredMessageQueue;
    private MonitoringAttributes monitoringAttributes;
    private boolean isStarting;

    public CommunicationsListener(QueuedMessageSubSystem queuedMessageSubSystem) {
        super();

        this.queuedMessageSubSystem = queuedMessageSubSystem;
        this.monitoringAttributes = new MonitoringAttributes(this.queuedMessageSubSystem);
        this.isStarting = false;
    }

    public void communicationsStateChange(CommunicationsEvent ce) {

        if (ce.getState() == CommunicationsEvent.AFTER_CONNECT) {
            if (monitoringAttributes.isMonitoringEnabled()) {
                startMonitoring();
            }
        }

        if (ce.getState() == CommunicationsEvent.BEFORE_DISCONNECT) {
            stopMonitoring();
        }
    }

    public boolean isPassiveCommunicationsListener() {
        return true;
    }

    public void startMonitoring() {

        if (isStarting) {
            return;
        }

        try {

            isStarting = true;

            if (monitoredMessageQueue == null) {
                QueuedMessageFilter filter = new QueuedMessageFilter();
                filter.setMessageQueue("*CURRENT"); //$NON-NLS-1$
                AS400 as400 = new AS400(queuedMessageSubSystem.getToolboxAS400Object());
                monitoredMessageQueue = new MonitoredMessageQueue(as400, filter.getPath(), filter, new MessageHandler(queuedMessageSubSystem),
                    monitoringAttributes);
            }

            monitoredMessageQueue.startMonitoring(MessageQueue.OLD, MessageQueue.ANY);

        } finally {
            isStarting = false;
        }
    }

    public void stopMonitoring() {

        if (monitoredMessageQueue != null) {
            monitoredMessageQueue.stopMonitoring();
        }
    }

}
