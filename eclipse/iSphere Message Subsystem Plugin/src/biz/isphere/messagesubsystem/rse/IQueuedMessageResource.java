package biz.isphere.messagesubsystem.rse;

import com.ibm.as400.access.QueuedMessage;

public interface IQueuedMessageResource {

    public QueuedMessage getQueuedMessage();

    public void setQueuedMessage(QueuedMessage queuedMessage);
}
