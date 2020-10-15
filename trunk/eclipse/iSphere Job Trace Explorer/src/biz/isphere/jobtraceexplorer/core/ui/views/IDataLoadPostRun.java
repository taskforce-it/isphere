/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.views;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.jobtraceexplorer.core.ui.widgets.AbstractJobTraceEntriesViewerTab;

public interface IDataLoadPostRun {

    public void finishDataLoading(AbstractJobTraceEntriesViewerTab tabItem, boolean isFilter);

    public void handleDataLoadException(AbstractJobTraceEntriesViewerTab tabItem, Throwable e);

    public Shell getShell();

}
