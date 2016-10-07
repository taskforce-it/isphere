/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ExpandAllListener implements SelectionListener {

    private TreeViewer viewer;

    public ExpandAllListener(TreeViewer viewer) {
        this.viewer = viewer;
    }

    public void widgetSelected(SelectionEvent paramSelectionEvent) {
        viewer.expandAll();
    }

    public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
    }
}
