/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.viewmanager;

import java.util.Map;
import java.util.Set;

import org.eclipse.ui.PartInitException;

/**
 * This interface defined the methods of the view manager.
 * 
 * @author Thomas Raddatz
 */
public interface IViewManager {

    public static final String DATA_SPACE_MONITOR_VIEWS = "DataSpaceMonitorViews";
    public static final String DATA_QUEUE_MONITOR_VIEWS = "DataQueueMonitorViews";
    public static final String TN5250J_SESSION_VIEWS = "TN5250JSessionsViews";
    public static final String SPOOLED_FILES_VIEWS = "SpooledFilesViews";

    public void add(IPinnableView view);

    public void remove(IPinnableView view);

    public boolean isPinned(IPinnableView view);

    public boolean isLoadingView();

    public boolean isInitialized(int timeout);

    public IPinnableView getView(String viewId, String contentId) throws PartInitException;

    public IPinnableView getView(String viewId, String contentId, boolean considerContentId) throws PartInitException;

    public Map<String, String> getPinProperties(IPinnableView view, Set<String> pinKeys);

    public void dispose();
}
