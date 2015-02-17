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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200;

public class ContentProviderTableViewer implements IStructuredContentProvider {

    private RDQM0200 rdqm0200;

    public ContentProviderTableViewer(RDQM0200 rdqm0200) {
        this.rdqm0200 = null;
    }

    public Object[] getElements(Object inputElement) {

        Object[] elements = new Object[] {};

        if (rdqm0200 != null) {
            try {
                return rdqm0200.getMessages();
            } catch (Throwable e) {
                ISpherePlugin.logError("Failed to get data queue messages.", e); //$NON-NLS-1$
            }
        }

        return elements;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.rdqm0200 = (RDQM0200)newInput;
    }
}
