/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.compare.contentmergeviewer.SourceMemberTextMergeViewer;

public class SourceMemberMergeViewerCreator implements IViewerCreator {

    public Viewer createViewer(Composite parent, CompareConfiguration configuration) {
        return new SourceMemberTextMergeViewer(parent, configuration);
    }

}
