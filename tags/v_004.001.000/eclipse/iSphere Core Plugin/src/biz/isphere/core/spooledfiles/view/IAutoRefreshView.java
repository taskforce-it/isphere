/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.internal.IJobFinishedListener;

public interface IAutoRefreshView extends IJobFinishedListener {

    public void setRefreshInterval(int seconds);

    public void refreshData();

    public Shell getShell();
}
