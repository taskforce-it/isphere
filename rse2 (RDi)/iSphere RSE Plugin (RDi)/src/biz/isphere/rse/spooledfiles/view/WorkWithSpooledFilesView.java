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
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.filters.ISystemFilterReference;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesInputData;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

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

        if (eventType == ISystemResourceChangeEvents.EVENT_REFRESH_REMOTE || eventType == ISystemResourceChangeEvents.EVENT_RENAME) {
            if (event.getSource() instanceof ISystemFilterReference) {
                ISystemFilterReference filterReference = (ISystemFilterReference)event.getSource();
                if (filterReference.getSubSystem() instanceof SpooledFileSubSystem) {
                    ISubSystem subSystem = filterReference.getSubSystem();
                    ISystemFilter systemFilter = filterReference.getReferencedFilter();
                    WorkWithSpooledFilesInputData inputData = (WorkWithSpooledFilesInputData)getInputData();
                    if (inputData.referencesFilter(subSystem, systemFilter)) {
                        refreshData();
                    }
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                if (event.getSource() instanceof ISystemFilter) {
                    ISubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    ISystemFilter systemFilter = (ISystemFilter)event.getSource();
                    WorkWithSpooledFilesInputData inputData = (WorkWithSpooledFilesInputData)getInputData();
                    if (inputData.referencesFilter(subSystem, systemFilter)) {
                        setInputData(inputData);
                    }
                }
            }
        }
    }

    private ISystemFilter findFilter(String connectionName, String filterPoolName, String filterName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        ISubSystem subSystem = connection.getSubSystemByClass(SpooledFileSubSystem.ID);
        ISystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (ISystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            if (filterPoolName != null && filterPoolName.equals(systemFilterPoolReference.getReferencedFilterPool().getName())) {
                ISystemFilter[] filters = systemFilterPoolReference.getReferencedFilterPool().getFilters();
                for (ISystemFilter filter : filters) {
                    if (filterName != null && filterName.equals(filter.getName())) {
                        return filter;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected AbstractWorkWithSpooledFilesInputData produceInputData(String connectionName, String filterPoolName, String filterName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        ISubSystem subSystem = connection.getSubSystemByClass(SpooledFileSubSystem.ID);
        if (subSystem == null) {
            return null;
        }

        ISystemFilter systemFilter = findFilter(connectionName, filterPoolName, filterName);
        if (systemFilter == null) {
            return null;
        }

        WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(subSystem, systemFilter);

        return inputData;
    }
}
