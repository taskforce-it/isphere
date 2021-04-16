/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.IMessageFileCompareEditorConfiguration;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefileeditor.MessageDescription;

public class MessageFileCompareEditorInput implements IEditorInput {

    private RemoteObject leftMessageFile;
    private RemoteObject rightMessageFile;
    private Image titleImage;

    private IMessageFileCompareEditorConfiguration configuration;
    private MessageDescription[] leftMessageDescriptions;
    private MessageDescription[] rightMessageDescriptions;

    public MessageFileCompareEditorInput(RemoteObject leftMessageFile, RemoteObject rightMessageFile,
        IMessageFileCompareEditorConfiguration configuration) {

        this.configuration = configuration;
        this.leftMessageFile = leftMessageFile;
        this.rightMessageFile = rightMessageFile;
        this.titleImage = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COMPARE_MESSAGE_FILES);
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    public IMessageFileCompareEditorConfiguration getConfiguration() {
        return configuration;
    }

    public RemoteObject getLeftMessageFile() {
        return leftMessageFile;
    }

    public void setLeftMessageFile(RemoteObject messageFile) {
        this.leftMessageFile = messageFile;
    }

    public RemoteObject getRightMessageFile() {
        return rightMessageFile;
    }

    public void setRightMessageFile(RemoteObject messageFile) {
        this.rightMessageFile = messageFile;
    }

    public String getLeftMessageFileName() {
        if (leftMessageFile == null) {
            return ""; //$NON-NLS-1$
        }
        return leftMessageFile.getAbsoluteName();
    }

    public String getRightMessageFileName() {
        if (rightMessageFile == null) {
            return ""; //$NON-NLS-1$
        }
        return rightMessageFile.getAbsoluteName();
    }

    public MessageDescription[] getLeftMessageDescriptions() {
        return this.leftMessageDescriptions;
    }

    public void setLeftMessageDescriptions(MessageDescription[] leftMessageDescriptions) {
        this.leftMessageDescriptions = leftMessageDescriptions;
    }

    public MessageDescription[] getRightMessageDescriptions() {
        return this.rightMessageDescriptions;
    }

    public void setRightMessageDescriptions(MessageDescription[] rightMessageDescriptions) {
        this.rightMessageDescriptions = rightMessageDescriptions;
    }

    public String getName() {

        if (StringHelper.isNullOrEmpty(getLeftMessageFileName())) {
            return getRightMessageFileName();
        } else if (StringHelper.isNullOrEmpty(getRightMessageFileName())) {
            return getLeftMessageFileName();
        } else {
            return getLeftMessageFileName() + " - " + getRightMessageFileName(); //$NON-NLS-1$
        }
    }

    public String getToolTipText() {
        return getName();
    }

    public Image getTitleImage() {
        return titleImage;
    }

    public MessageFileCompareEditorInput clearAll() {

        leftMessageDescriptions = new MessageDescription[0];
        rightMessageDescriptions = new MessageDescription[0];

        return this;
    }
}
