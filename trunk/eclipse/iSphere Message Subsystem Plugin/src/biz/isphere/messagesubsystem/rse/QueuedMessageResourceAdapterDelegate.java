/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.QueuedMessageHelper;

import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageResourceAdapterDelegate {

    private static final String QUEUED_MESSAGE = "Queued message"; //$NON-NLS-1$

    private static final String KEY_REPLY_STS = "replySts"; //$NON-NLS-1$
    private static final String KEY_PGM = "pgm"; //$NON-NLS-1$
    private static final String KEY_JOBNBR = "jobnbr"; //$NON-NLS-1$
    private static final String KEY_JOB = "job"; //$NON-NLS-1$
    private static final String KEY_DATE = "date"; //$NON-NLS-1$
    private static final String KEY_TYPE = "type"; //$NON-NLS-1$
    private static final String KEY_SEV = "sev"; //$NON-NLS-1$
    private static final String KEY_MSGID = "msgid"; //$NON-NLS-1$
    private static final String KEY_FROM = "from"; //$NON-NLS-1$

    private static final String QUEUED_MESSAGE_RESOURCE = "Queued message resource"; //$NON-NLS-1$

    public String getType() {
        return QUEUED_MESSAGE_RESOURCE;
    }

    public Object getParent() {
        return null;
    }

    public boolean hasChildren() {
        return false;
    }

    public boolean showDelete() {
        return true;
    }

    public boolean canDelete() {
        return true;
    }

    public boolean doDelete(Shell shell, IQueuedMessageSubsystem messageSubSystem, QueuedMessage queuedMessage) {

        if (messageSubSystem.isMonitored(queuedMessage.getQueue())) {
            messageSubSystem.removedFromMonitoredMessageQueue(queuedMessage);
            return true;
        }

        try {
            queuedMessage.getQueue().remove(queuedMessage.getKey());
        } catch (Exception e) {
            final String errorMessage;
            if (e.getMessage() == null) {
                errorMessage = e.toString();
            } else {
                errorMessage = e.getMessage();
            }

            shell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Delete_Message_Error, errorMessage);
                }
            });

            return false;
        }

        return true;
    }

    public Object[] getChildren() {
        return new Object[0];
    }

    public IPropertyDescriptor[] internalGetPropertyDescriptors() {

        PropertyDescriptor[] ourPDs = new PropertyDescriptor[9];
        ourPDs[0] = new PropertyDescriptor(KEY_FROM, Messages.From);
        ourPDs[0].setDescription(Messages.From);
        ourPDs[1] = new PropertyDescriptor(KEY_MSGID, Messages.Message_ID);
        ourPDs[1].setDescription(Messages.Message_ID);
        ourPDs[2] = new PropertyDescriptor(KEY_SEV, Messages.Severity);
        ourPDs[2].setDescription(Messages.Severity);
        ourPDs[3] = new PropertyDescriptor(KEY_TYPE, Messages.Message_type);
        ourPDs[3].setDescription(Messages.Message_type);
        ourPDs[4] = new PropertyDescriptor(KEY_DATE, Messages.Date_sent);
        ourPDs[4].setDescription(Messages.Date_sent);
        ourPDs[5] = new PropertyDescriptor(KEY_JOB, Messages.From_job);
        ourPDs[5].setDescription(Messages.From_job);
        ourPDs[6] = new PropertyDescriptor(KEY_JOBNBR, Messages.From_job_number);
        ourPDs[6].setDescription(Messages.From_job_number);
        ourPDs[7] = new PropertyDescriptor(KEY_PGM, Messages.From_program);
        ourPDs[7].setDescription(Messages.From_program);
        ourPDs[8] = new PropertyDescriptor(KEY_REPLY_STS, Messages.Reply_status);
        ourPDs[8].setDescription(Messages.Reply_status);

        return ourPDs;
    }

    public Object internalGetPropertyValue(IQueuedMessageResource queuedMessageResource, Object propKey) {

        try {

            QueuedMessage queuedMessage = queuedMessageResource.getQueuedMessage();

            if (propKey.equals(KEY_FROM)) {
                return queuedMessage.getUser();
            }

            if (propKey.equals(KEY_MSGID)) {
                return queuedMessage.getID();
            }

            if (propKey.equals(KEY_SEV)) {
                return new Integer(queuedMessage.getSeverity()).toString();
            }

            if (propKey.equals(KEY_TYPE)) {
                return QueuedMessageHelper.getMessageTypeAsText(queuedMessage.getType());
            }

            if (propKey.equals(KEY_DATE)) {
                return queuedMessage.getDate().getTime();
            }

            if (propKey.equals(KEY_JOB)) {
                return queuedMessage.getFromJobName();
            }

            if (propKey.equals(KEY_JOBNBR)) {
                return queuedMessage.getFromJobNumber();
            }

            if (propKey.equals(KEY_PGM)) {
                return queuedMessage.getFromProgram();
            }

            if (propKey.equals(KEY_REPLY_STS)) {
                return queuedMessage.getReplyStatus();
            }

        } catch (Exception e) {
        }

        return null;
    }

    public boolean handleDoubleClick(Object object) {
        if (object instanceof IQueuedMessageResource) {
            IQueuedMessageResource queuedMessageResource = (IQueuedMessageResource)object;
            QueuedMessage queuedMessage = queuedMessageResource.getQueuedMessage();
            QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getCurrent().getActiveShell(), new ReceivedMessage(queuedMessage));
            dialog.open();
        }
        return false;
    }

    public String getText(Object element) {
        return ((IQueuedMessageResource)element).getQueuedMessage().getText();
    }

    public String getAbsoluteName(Object object) {
        IQueuedMessageResource queuedMessageResource = (IQueuedMessageResource)object;
        return QUEUED_MESSAGE + queuedMessageResource.getQueuedMessage().getKey();
    }

    public String getAbsoluteParentName() {
        return "root"; //$NON-NLS-1$
    }

    public String getRemoteTypeCategory() {
        return "queued messages"; //$NON-NLS-1$ 
    }

    public String getRemoteType() {
        return "queued message"; //$NON-NLS-1$
    }

    public String getRemoteSubType() {
        return null;
    }

    public boolean refreshRemoteObject(Object oldElement, Object newElement) {

        IQueuedMessageResource oldQueuedMessage = (IQueuedMessageResource)oldElement;
        IQueuedMessageResource newQueuedMessage = (IQueuedMessageResource)newElement;
        newQueuedMessage.setQueuedMessage(oldQueuedMessage.getQueuedMessage());

        return false;
    }

    public Object getRemoteParent() {
        return null;
    }

    public String[] getRemoteParentNamesInUse() {
        return null;
    }
}
