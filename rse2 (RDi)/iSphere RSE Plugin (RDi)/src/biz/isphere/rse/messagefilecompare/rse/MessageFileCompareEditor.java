/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilecompare.rse;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;

import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefilecompare.rse.AbstractMessageFileCompareEditor;
import biz.isphere.core.messagefilecompare.rse.AbstractTableLabelProvider;
import biz.isphere.core.swt.widgets.objectselector.ISelectRemoteQSYSObjectDialog;
import biz.isphere.core.swt.widgets.objectselector.ISelectedObject;
import biz.isphere.core.swt.widgets.objectselector.SelectRemoteQSYSObjectDialog;

public class MessageFileCompareEditor extends AbstractMessageFileCompareEditor {

    public MessageFileCompareEditor() {
        super();
    }

    @Override
    protected RemoteObject performSelectRemoteObject(String connectionName, String libraryName, String messageFileName) {

        ISelectRemoteQSYSObjectDialog dialog = SelectRemoteQSYSObjectDialog.createSelectMessageFileDialog(getShell(), connectionName);
        dialog.setLibraryName(libraryName);
        dialog.setObjectName(messageFileName);
        if (dialog.open() == SelectRemoteQSYSObjectDialog.CANCEL) {
            return null;
        }

        ISelectedObject selectedObject = dialog.getObject();

        String connection = selectedObject.getConnectionName();
        String name = selectedObject.getName();
        String library = selectedObject.getLibrary();
        String objectType = selectedObject.getObjectType();
        String description = selectedObject.getDescription();

        return new RemoteObject(connection, name, library, objectType, description);
    }

    @Override
    protected LabelProvider getTableLabelProvider(TableViewer tableViewer, int columnIndex) {
        return new TableLabelProvider(tableViewer, columnIndex);
    }

    /**
     * Class the provides the content for the cells of the table.
     */
    private class TableLabelProvider extends AbstractTableLabelProvider {

        public TableLabelProvider(TableViewer tableViewer, int columnIndex) {
            super(tableViewer, columnIndex);
        }
    }
}
