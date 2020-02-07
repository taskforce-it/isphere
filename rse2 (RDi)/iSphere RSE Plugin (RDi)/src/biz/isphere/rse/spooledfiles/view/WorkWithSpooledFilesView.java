/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.events.ISystemResourceChangeEvent;
import org.eclipse.rse.core.events.ISystemResourceChangeEvents;
import org.eclipse.rse.core.events.ISystemResourceChangeListener;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterReference;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.core.spooledfiles.view.rse.WorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;

public class WorkWithSpooledFilesView extends AbstractWorkWithSpooledFilesView implements ISystemResourceChangeListener {

    @Override
    public void dispose() {

        ISystemRegistry registry = RSECorePlugin.getTheSystemRegistry();
        registry.removeSystemResourceChangeListener(this);

        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        ISystemRegistry registry = RSECorePlugin.getTheSystemRegistry();
        registry.addSystemResourceChangeListener(this);
    }

    /*
     * AbstractWorkWithSpooledFilesView methods
     */

    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
    }

    /*
     * ISystemRemoteChangeListener methods
     */

    public void systemResourceChanged(ISystemResourceChangeEvent event) {

        int eventType = event.getType();

        if (eventType == ISystemResourceChangeEvents.EVENT_REFRESH_REMOTE) {
            if (event.getSource() instanceof ISystemFilterReference) {
                ISystemFilterReference filterReference = (ISystemFilterReference)event.getSource();
                if (filterReference.getSubSystem() instanceof SpooledFileSubSystem) {
                    refreshData();
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                if (event.getSource() instanceof ISystemFilter) {

                    ISubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    ISystemFilter systemFilter = (ISystemFilter)event.getSource();

                    setInputData(subSystem, systemFilter);
                }
            }
        }
    }

    private void setInputData(ISubSystem subSystem, ISystemFilter systemFilter) {

        String connectionName = getConnectionName(subSystem);
        String filterPoolName = systemFilter.getParentFilterPool().getName();
        String filterName = systemFilter.getName();

        if (isSameFilter(connectionName, filterPoolName, filterName)) {

            WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(connectionName, filterPoolName, filterName);
            inputData.setFilterStrings(systemFilter.getFilterStrings());

            setInputData(inputData);
        }
    }

    private String getConnectionName(ISubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getHost());
    }
}
