/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.compare;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.preferences.Preferences;

public class MessageDescriptionCompareNode extends BufferedContent implements ITypedElement, IEditableContent {

    private MessageDescription messageDescription;

    public MessageDescriptionCompareNode(MessageDescription messageDescription) {

        this.messageDescription = messageDescription;

        Assert.isNotNull(messageDescription);
    }

    public MessageDescription getMessageDescription() {
        return messageDescription;
    }
    
    public String getName() {
        return messageDescription.getFullQualifiedName();
    }

    public String getType() {
        return "text";
    }

    public Image getImage() {
        return CompareUI.getImage(messageDescription.getMessageId());
    }

    public boolean isEditable() {
        return false;
    }

    public ITypedElement replace(ITypedElement child, ITypedElement other) {
        return child;
    }

    @Override
    protected InputStream createStream() throws CoreException {
        try {
            int lineWidth = Preferences.getInstance().getMessageFileCompareLineWidth();
            return new BufferedInputStream(new ByteArrayInputStream(messageDescription.asComparableText(lineWidth).getBytes()));
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not create InputStream of message description ***", e);
            return null;
        }
    }

}
