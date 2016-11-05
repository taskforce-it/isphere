/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.part.PluginTransferData;

import biz.isphere.jobloganalyzer.jobs.IDropFileListener;
import biz.isphere.jobloganalyzer.jobs.ILocalFileReceiver;
import biz.isphere.jobloganalyzer.jobs.LoadLocalSpooledFileJob;

public class DropFileListener extends DropTargetAdapter implements ILocalFileReceiver {

    private IDropFileListener target;
    private DropTargetEvent event;

    public DropFileListener(IDropFileListener iDropFileListener) {
        this.target = iDropFileListener;
    }

    public void dragEnter(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;
    }

    public void dragOver(DropTargetEvent event) {
    }

    public void dragLeave(DropTargetEvent event) {
    }

    public void dropAccept(DropTargetEvent event) {
    }

    public void drop(DropTargetEvent event) {

        if ((event.data instanceof PluginTransferData)) {
            PluginTransferData transferData = (PluginTransferData)event.data;

            byte[] result = transferData.getData();

            String str = null;
            try {
                str = new String(result, "UTF-8");
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
                str = new String(result);
            }

            this.event = event;

            // Split plug-in transfer data into objects
            String[] droppedLocalFilesData = str.split("\\|");

            DroppedLocalFile[] droppedFiles = new DroppedLocalFile[droppedLocalFilesData.length];
            for (int i = 0; i < droppedLocalFilesData.length; i++) {
                droppedFiles[i] = new DroppedLocalFile(droppedLocalFilesData[i]);
            }

            loadFilesAsync(droppedFiles, this, "Parsing Job Log");
        }
    }

    public void setRemoteObjects(DroppedLocalFile[] pathName) {
        target.dropJobLog(pathName[0], event.item);
    }

    protected void loadFilesAsync(DroppedLocalFile[] droppedFiles, ILocalFileReceiver receiver, String jobName) {
        LoadLocalSpooledFileJob job = new LoadLocalSpooledFileJob(jobName, droppedFiles, this);
        job.schedule();
    }
}
