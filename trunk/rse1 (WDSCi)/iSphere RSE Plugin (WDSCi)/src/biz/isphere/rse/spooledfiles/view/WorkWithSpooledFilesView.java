/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.core.spooledfiles.view.rse.WorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;

import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterReference;
import com.ibm.etools.systems.model.ISystemResourceChangeEvent;
import com.ibm.etools.systems.model.ISystemResourceChangeEvents;
import com.ibm.etools.systems.model.ISystemResourceChangeListener;
import com.ibm.etools.systems.model.SystemRegistry;
import com.ibm.etools.systems.subsystems.SubSystem;

public class WorkWithSpooledFilesView extends AbstractWorkWithSpooledFilesView implements ISystemResourceChangeListener {

    @Override
    public void dispose() {

        SystemRegistry registry = SystemPlugin.getTheSystemRegistry();
        registry.removeSystemResourceChangeListener(this);

        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        SystemRegistry registry = SystemPlugin.getTheSystemRegistry();
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

        if (eventType == ISystemResourceChangeEvents.EVENT_REFRESH) {
            if (event.getSource() instanceof SystemFilterReference) {
                SystemFilterReference filterReference = (SystemFilterReference)event.getSource();
                if (getSubSystem(filterReference) instanceof SpooledFileSubSystem) {
                    refreshData();
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                if (event.getSource() instanceof SystemFilter) {

                    SubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    SystemFilter systemFilter = (SystemFilter)event.getSource();

                    setInputData(subSystem, systemFilter);
                }
            }
        }
    }

    private void setInputData(SubSystem subSystem, SystemFilter systemFilter) {

        String connectionName = getConnectionName(subSystem);
        String filterPoolName = systemFilter.getParentFilterPool().getName();
        String filterName = systemFilter.getName();

        if (isSameFilter(connectionName, filterPoolName, filterName)) {

            WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(connectionName, filterPoolName, filterName);
            inputData.setFilterStrings(systemFilter.getFilterStrings());

            setInputData(inputData);
        }
    }

    private SubSystem getSubSystem(SystemFilterReference filterReference) {
        return (SubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    public String getConnectionName(SubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getSystemConnection());
    }
}
