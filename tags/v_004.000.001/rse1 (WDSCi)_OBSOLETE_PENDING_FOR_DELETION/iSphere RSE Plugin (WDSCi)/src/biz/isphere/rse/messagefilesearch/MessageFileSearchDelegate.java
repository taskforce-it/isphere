/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.messagefilesearch.AbstractMessageFileSearchDelegate;
import biz.isphere.rse.ibm.helper.ISeriesDataElementHelper;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.as400filesubsys.FileSubSystem;
import com.ibm.etools.systems.core.ui.messages.SystemMessageDialog;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.model.impl.SystemMessageObject;

public class MessageFileSearchDelegate extends AbstractMessageFileSearchDelegate {

    private ISeriesConnection connection;

    public MessageFileSearchDelegate(Shell shell, ISeriesConnection connection) {
        super(shell);

        this.connection = connection;
    }

    protected Object[] resolveFilterString(String filterString) throws Exception {

        FileSubSystem objectSubSystem = connection.getISeriesFileSubSystem();
        return objectSubSystem.resolveFilterString(filterString, null);
    }

    protected void displaySystemErrorMessage(Object message) {
        SystemMessageDialog.displayErrorMessage(getShell(), ((SystemMessageObject)message).getMessage());
    }

    protected boolean isSystemMessageObject(Object object) {
        return (object instanceof SystemMessageObject);
    }

    protected boolean isLibrary(Object object) {
        return ISeriesDataElementHelper.isLibrary(object);
    }

    protected boolean isMessageFile(Object object) {
        return ISeriesDataElementHelper.isMessageFile(object);
    }

    protected String getResourceLibrary(Object resource) {
        return ISeriesDataElementHelper.getLibrary((DataElement)resource);
    }

    protected String getResourceName(Object resource) {
        return ISeriesDataElementHelper.getName((DataElement)resource);
    }

    protected String getResourceDescription(Object resource) {
        return ISeriesDataElementHelper.getDescription((DataElement)resource);
    }

}
