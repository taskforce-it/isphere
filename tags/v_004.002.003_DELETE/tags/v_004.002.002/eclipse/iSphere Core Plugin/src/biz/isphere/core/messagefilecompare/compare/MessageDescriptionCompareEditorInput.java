/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.messagefileeditor.MessageDescription;

public class MessageDescriptionCompareEditorInput extends CompareEditorInput {

    private MessageDescription leftMessageDescription;
    private MessageDescription rightMessageDescription;

    public MessageDescriptionCompareEditorInput(CompareConfiguration configuration, MessageDescription leftMessageDescription,
        MessageDescription rightMessageDescription) {
        super(configuration);

        this.leftMessageDescription = leftMessageDescription;
        this.rightMessageDescription = rightMessageDescription;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        MessageDescriptionCompareDifferencer differencer = new MessageDescriptionCompareDifferencer(getConfiguration());

        MessageDescriptionCompareNode leftNode = new MessageDescriptionCompareNode(leftMessageDescription);
        MessageDescriptionCompareNode rightNode = new MessageDescriptionCompareNode(rightMessageDescription);
        Object fRoot = differencer.findDifferences(false, monitor, null, null, leftNode, rightNode);

        return fRoot;
    }

    public CompareConfiguration getConfiguration() {
        return (CompareConfiguration)super.getCompareConfiguration();
    }

}
