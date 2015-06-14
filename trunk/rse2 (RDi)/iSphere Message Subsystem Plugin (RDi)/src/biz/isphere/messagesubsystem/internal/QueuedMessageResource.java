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

import org.eclipse.rse.core.subsystems.AbstractResource;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageResource extends AbstractResource {
    private QueuedMessage queuedMessage;

    public QueuedMessageResource(ISubSystem subSystem) {
        super(subSystem);
    }

    public QueuedMessageResource() {
        super();
    }

    public QueuedMessage getQueuedMessage() {
        return queuedMessage;
    }

    public void setQueuedMessage(QueuedMessage message) {
        queuedMessage = message;
    }

}
