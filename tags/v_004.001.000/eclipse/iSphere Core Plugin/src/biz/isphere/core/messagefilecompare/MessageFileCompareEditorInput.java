/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((leftMessageFile == null) ? 0 : leftMessageFile.hashCode());
        result = prime * result + ((leftMessageFile.getConnectionName() == null) ? 0 : leftMessageFile.getConnectionName().hashCode());
        result = prime * result + ((leftMessageFile.getLibrary() == null) ? 0 : leftMessageFile.getLibrary().hashCode());
        result = prime * result + ((leftMessageFile.getName() == null) ? 0 : leftMessageFile.getName().hashCode());
        result = prime * result + ((rightMessageFile == null) ? 0 : rightMessageFile.hashCode());
        result = prime * result + ((rightMessageFile.getConnectionName() == null) ? 0 : rightMessageFile.getConnectionName().hashCode());
        result = prime * result + ((rightMessageFile.getLibrary() == null) ? 0 : rightMessageFile.getLibrary().hashCode());
        result = prime * result + ((rightMessageFile.getName() == null) ? 0 : rightMessageFile.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        MessageFileCompareEditorInput other = (MessageFileCompareEditorInput)obj;

        if (leftMessageFile == null) {
            if (other.leftMessageFile != null) return false;
        } else if (other.leftMessageFile != null) {
            if (leftMessageFile.getConnectionName() == null) {
                if (other.leftMessageFile.getConnectionName() != null) return false;
            } else if (!leftMessageFile.getConnectionName().equals(other.leftMessageFile.getConnectionName())) return false;

            if (leftMessageFile.getLibrary() == null) {
                if (other.leftMessageFile.getLibrary() != null) return false;
            } else if (!leftMessageFile.getLibrary().equals(other.leftMessageFile.getLibrary())) return false;

            if (leftMessageFile.getName() == null) {
                if (other.leftMessageFile.getName() != null) return false;
            } else if (!leftMessageFile.getName().equals(other.leftMessageFile.getName())) return false;
        }

        if (rightMessageFile == null) {
            if (other.rightMessageFile != null) return false;
        } else if (other.rightMessageFile != null) {
            if (rightMessageFile.getConnectionName() == null) {
                if (other.rightMessageFile.getConnectionName() != null) return false;
            } else if (!rightMessageFile.getConnectionName().equals(other.rightMessageFile.getConnectionName())) return false;

            if (rightMessageFile.getLibrary() == null) {
                if (other.rightMessageFile.getLibrary() != null) return false;
            } else if (!rightMessageFile.getLibrary().equals(other.rightMessageFile.getLibrary())) return false;

            if (rightMessageFile.getName() == null) {
                if (other.rightMessageFile.getName() != null) return false;
            } else if (!rightMessageFile.getName().equals(other.rightMessageFile.getName())) return false;
        }

        return true;
    }
}
