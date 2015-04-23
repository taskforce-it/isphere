/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilecompare.rse;

import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefilecompare.rse.AbstractMessageFileCompareEditor;
import biz.isphere.rse.internal.RSESelectObjectDialog;

import com.ibm.etools.iseries.core.api.ISeriesConnection;

public class MessageFileCompareEditor extends AbstractMessageFileCompareEditor {

    public MessageFileCompareEditor() {
        super();
    }

    @Override
    protected RemoteObject performSelectRemoteObject(String connectionName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
        RSESelectObjectDialog dialog = RSESelectObjectDialog.createSelectMessageFileDialog(getShell(), connection);
        if (dialog.open() == RSESelectObjectDialog.CANCEL) {
            return null;
        }

        return dialog.getRemoteObject();
    }

}
