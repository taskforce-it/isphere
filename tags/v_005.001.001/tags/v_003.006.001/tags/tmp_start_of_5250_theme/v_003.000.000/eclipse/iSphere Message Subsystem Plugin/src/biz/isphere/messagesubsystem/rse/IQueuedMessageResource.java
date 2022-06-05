/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import com.ibm.as400.access.QueuedMessage;

public interface IQueuedMessageResource {

    public QueuedMessage getQueuedMessage();

    public void setQueuedMessage(QueuedMessage queuedMessage);

    public String getDefaultReply();

    public boolean isInquiryMessage();

    public String getReplyStatus();

    public boolean isPendingReply();
}
