/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.part.PluginTransferData;

import biz.isphere.joblogexplorer.jobs.IDropFileListener;

public class DropFileListener extends DropTargetAdapter {

    private IDropFileListener target;

    public DropFileListener(IDropFileListener iDropFileListener) {
        this.target = iDropFileListener;
    }

    @Override
    public void dragEnter(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;
    }

    @Override
    public void dragOver(DropTargetEvent event) {
    }

    @Override
    public void dragLeave(DropTargetEvent event) {
    }

    @Override
    public void dropAccept(DropTargetEvent event) {
    }

    @Override
    public void drop(DropTargetEvent event) {

        if ((event.data instanceof PluginTransferData)) {
            PluginTransferData transferData = (PluginTransferData)event.data;

            byte[] result = transferData.getData();

            String str = null;
            try {
                str = new String(result, "UTF-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
                str = new String(result);
            }

            // Split plug-in transfer data into objects
            String[] droppedLocalFilesData = str.split("\\|"); //$NON-NLS-1$

            DroppedLocalFile[] droppedFiles = new DroppedLocalFile[droppedLocalFilesData.length];
            for (int i = 0; i < droppedLocalFilesData.length; i++) {
                droppedFiles[i] = new DroppedLocalFile(droppedLocalFilesData[i]);
            }

            target.dropJobLog(droppedFiles[0].getPathName(), droppedFiles[0].getPathName(), event.item);
        }
    }

}
