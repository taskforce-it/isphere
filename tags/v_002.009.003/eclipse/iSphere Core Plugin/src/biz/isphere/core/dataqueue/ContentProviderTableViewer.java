/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.dataqueue.retrieve.message.RDQM0200;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;

public class ContentProviderTableViewer implements IStructuredContentProvider {

    private RDQM0200 rdqm0200;

    public ContentProviderTableViewer(RDQM0200 rdqm0200) {
        this.rdqm0200 = null;
    }

    public Object[] getElements(Object inputElement) {

        if (rdqm0200 != null) {
            return rdqm0200.getMessages();
        }

        return new RDQM0200MessageEntry[0];
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.rdqm0200 = (RDQM0200)newInput;
    }
}
