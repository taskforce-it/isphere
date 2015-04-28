/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;

import biz.isphere.core.messagefileeditor.MessageDescription;

public class MessageDescriptionCompareDifferencer extends Differencer {

    public MessageDescriptionCompareDifferencer(CompareConfiguration aCompareEditorConfiguration) {
        super();
    }

    @Override
    protected boolean contentsEqual(Object left, Object right) {

        MessageDescription leftMessageDescription = ((MessageDescriptionCompareNode)left).getMessageDescription();
        MessageDescription rightMessageDescription = ((MessageDescriptionCompareNode)right).getMessageDescription();

        if (leftMessageDescription.asComparableText(-1).equals(rightMessageDescription.asComparableText(-1))) {
            return true;
        }

        return false;
    }

    @Override
    protected Object visit(Object data, int result, Object ancestor, Object left, Object right) {
        return new MyDiffNode((IDiffContainer)data, result, (ITypedElement)ancestor, (ITypedElement)left, (ITypedElement)right);
    }

    public static class MyDiffNode extends DiffNode {
        public MyDiffNode(IDiffContainer parent, int kind, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
            super(parent, kind, ancestor, left, right);
        }

        @Override
        public void fireChange() {
            super.fireChange();
        }
    }
}
