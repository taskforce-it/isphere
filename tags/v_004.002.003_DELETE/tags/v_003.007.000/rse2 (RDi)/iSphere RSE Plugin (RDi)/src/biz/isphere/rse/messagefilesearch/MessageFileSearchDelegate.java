/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.messagefilesearch.AbstractMessageFileSearchDelegate;

import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMessageFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class MessageFileSearchDelegate extends AbstractMessageFileSearchDelegate {

    private IBMiConnection connection;

    public MessageFileSearchDelegate(Shell shell, IBMiConnection connection) {
        super(shell);

        this.connection = connection;
    }

    protected Object[] resolveFilterString(String filterString) throws Exception {

        QSYSObjectSubSystem objectSubSystem = connection.getQSYSObjectSubSystem();
        return objectSubSystem.resolveFilterString(filterString, null);
    }

    protected void displaySystemErrorMessage(Object message) {
        SystemMessageDialog.displayErrorMessage(getShell(), ((SystemMessageObject)message).getMessage());
    }

    protected boolean isSystemMessageObject(Object object) {
        return (object instanceof SystemMessageObject);
    }

    protected boolean isLibrary(Object object) {
        return ResourceTypeUtil.isLibrary(object);
    }

    protected boolean isMessageFile(Object object) {
        return ResourceTypeUtil.isMessageFile(object);
    }

    protected String getResourceLibrary(Object resource) {
        return ((IQSYSResource)resource).getLibrary();
    }

    protected String getResourceName(Object resource) {
        return ((IQSYSResource)resource).getName();
    }

    protected String getResourceDescription(Object resource) {
        return ((IQSYSMessageFile)resource).getDescription();
    }
}
