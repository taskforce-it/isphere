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
package biz.isphere.messagesubsystem.rse.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.ui.SystemMenuManager;
import org.eclipse.rse.ui.view.AbstractSystemViewAdapter;
import org.eclipse.rse.ui.view.ISystemRemoteElementAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.messagesubsystem.rse.ISphereMessageSubsystemRSEPlugin;
import biz.isphere.messagesubsystem.rse.Messages;
import biz.isphere.messagesubsystem.rse.QueuedMessageDialog;
import biz.isphere.messagesubsystem.rse.QueuedMessageHelper;
import biz.isphere.messagesubsystem.rse.QueuedMessageResourceAdapterDelegate;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageResourceAdapter extends AbstractSystemViewAdapter implements ISystemRemoteElementAdapter {

    private static final String KEY_REPLY_STS = "replySts"; //$NON-NLS-1$
    private static final String KEY_PGM = "pgm"; //$NON-NLS-1$
    private static final String KEY_JOBNBR = "jobnbr"; //$NON-NLS-1$
    private static final String KEY_JOB = "job"; //$NON-NLS-1$
    private static final String KEY_DATE = "date"; //$NON-NLS-1$
    private static final String KEY_TYPE = "type"; //$NON-NLS-1$
    private static final String KEY_SEV = "sev"; //$NON-NLS-1$
    private static final String KEY_MSGID = "msgid"; //$NON-NLS-1$
    private static final String KEY_FROM = "from"; //$NON-NLS-1$

    private static final String QUEUED_MESSAGE = "Queued message"; //$NON-NLS-1$

    private QueuedMessageResourceAdapterDelegate delegate;

    public QueuedMessageResourceAdapter() {
        super();

        delegate = new QueuedMessageResourceAdapterDelegate();
    }

    @Override
    public void addActions(SystemMenuManager menu, IStructuredSelection selection, Shell parent, String menuGroup) {
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        QueuedMessageResource queuedMessageResource = (QueuedMessageResource)object;
        if (queuedMessageResource.getQueuedMessage().getType() == QueuedMessage.INQUIRY) {
            return ISphereMessageSubsystemRSEPlugin.getImageDescriptor(ISphereMessageSubsystemRSEPlugin.IMAGE_INQUIRY);
        } else {
            return ISphereMessageSubsystemRSEPlugin.getImageDescriptor(ISphereMessageSubsystemRSEPlugin.IMAGE_MESSAGE);
        }
    }

    @Override
    public boolean handleDoubleClick(Object object) {
        if (object instanceof QueuedMessageResource) {
            QueuedMessageResource queuedMessageResource = (QueuedMessageResource)object;
            QueuedMessage queuedMessage = queuedMessageResource.getQueuedMessage();
            QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getCurrent().getActiveShell(), new ReceivedMessage(queuedMessage));
            dialog.open();
        }
        return false;
    }

    public String getText(Object element) {
        return ((QueuedMessageResource)element).getQueuedMessage().getText();
    }

    public String getAbsoluteName(Object object) {
        QueuedMessageResource queuedMessageResource = (QueuedMessageResource)object;
        return QUEUED_MESSAGE + queuedMessageResource.getQueuedMessage().getKey();
    }

    @Override
    public String getType(Object element) {
        return delegate.getType();
    }

    @Override
    public Object getParent(Object element) {
        return delegate.getParent();
    }

    @Override
    public boolean hasChildren(IAdaptable element) {
        return delegate.hasChildren();
    }

    @Override
    public boolean showDelete(Object element) {
        return delegate.showDelete();
    }

    @Override
    public boolean canDelete(Object element) {
        return delegate.canDelete();
    }

    @Override
    public boolean doDelete(Shell shell, Object element, IProgressMonitor monitor) {

        QueuedMessageResource queuedMessageResource = (QueuedMessageResource)element;
        QueuedMessage queuedMessage = queuedMessageResource.getQueuedMessage();

        return delegate.doDelete(shell, queuedMessage, monitor);
    }

    @Override
    public Object[] getChildren(IAdaptable paramIAdaptable, IProgressMonitor paramIProgressMonitor) {
        return new Object[0];
    }

    @Override
    protected IPropertyDescriptor[] internalGetPropertyDescriptors() {

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

    @Override
    public Object internalGetPropertyValue(Object propKey) {

        try {

            QueuedMessageResource queuedMessage = (QueuedMessageResource)propertySourceInput;

            if (propKey.equals(KEY_FROM)) {
                return queuedMessage.getQueuedMessage().getUser();
            }

            if (propKey.equals(KEY_MSGID)) {
                return queuedMessage.getQueuedMessage().getID();
            }

            if (propKey.equals(KEY_SEV)) {
                return new Integer(queuedMessage.getQueuedMessage().getSeverity()).toString();
            }

            if (propKey.equals(KEY_TYPE)) {
                return QueuedMessageHelper.getMessageTypeAsText(queuedMessage.getQueuedMessage().getType());
            }

            if (propKey.equals(KEY_DATE)) {
                return queuedMessage.getQueuedMessage().getDate().getTime().toString();
            }

            if (propKey.equals(KEY_JOB)) {
                return queuedMessage.getQueuedMessage().getFromJobName();
            }

            if (propKey.equals(KEY_JOBNBR)) {
                return queuedMessage.getQueuedMessage().getFromJobNumber();
            }

            if (propKey.equals(KEY_PGM)) {
                return queuedMessage.getQueuedMessage().getFromProgram();
            }

            if (propKey.equals(KEY_REPLY_STS)) {
                return queuedMessage.getQueuedMessage().getReplyStatus();
            }

        } catch (Exception e) {
        }

        return null;
    }

    public String getAbsoluteParentName(Object element) {
        return "root"; //$NON-NLS-1$
    }

    public String getSubSystemFactoryId(Object element) {
        return QueuedMessageSubSystemFactory.ID;
    }

    public String getRemoteTypeCategory(Object element) {
        return "queued messages"; //$NON-NLS-1$ 
    }

    public String getRemoteType(Object element) {
        return "queued message"; //$NON-NLS-1$
    }

    public String getRemoteSubType(Object arg0) {
        return null;
    }

    public boolean refreshRemoteObject(Object oldElement, Object newElement) {

        QueuedMessageResource oldQueuedMessage = (QueuedMessageResource)oldElement;
        QueuedMessageResource newQueuedMessage = (QueuedMessageResource)newElement;
        newQueuedMessage.setQueuedMessage(oldQueuedMessage.getQueuedMessage());

        return false;
    }

    public Object getRemoteParent(Object paramObject, IProgressMonitor paramIProgressMonitor) throws Exception {
        return null;
    }

    public String[] getRemoteParentNamesInUse(Object paramObject, IProgressMonitor paramIProgressMonitor) throws Exception {
        return null;
    }

    /*
     * Start of RDi/WDSCi specific methods.
     */

    public String getSubSystemConfigurationId(Object arg0) {
        return null;
    }
}