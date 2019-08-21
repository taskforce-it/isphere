/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;

public class ViewerFilterTableViewer extends ViewerFilter {

    private boolean isMessageTooLongWarning = false;

    public ViewerFilterTableViewer() {
        return;
    }

    @Override
    public boolean select(Viewer arg0, Object parent, Object element) {

        RDQM0200MessageEntry messageDescription = (RDQM0200MessageEntry)element;

        if (messageDescription.isTableViewerDataTruncation()) {
            isMessageTooLongWarning = true;
        }

        return true;
    }

    public boolean isMessageTooLongWarning() {
        return isMessageTooLongWarning;
    }

    public void reset() {
        isMessageTooLongWarning = false;
    }
}
