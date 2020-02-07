/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.ibm.helper.ISeriesRSEHelper;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesInputData;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPoolReference;
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

        if (eventType == ISystemResourceChangeEvents.EVENT_REFRESH || eventType == ISystemResourceChangeEvents.EVENT_RENAME) {
            if (event.getSource() instanceof SystemFilterReference) {
                SystemFilterReference filterReference = (SystemFilterReference)event.getSource();
                SubSystem subSystem = getSubSystem(filterReference);
                if (subSystem instanceof SpooledFileSubSystem) {
                    SystemFilter systemFilter = filterReference.getReferencedFilter();
                    WorkWithSpooledFilesInputData inputData = (WorkWithSpooledFilesInputData)getInputData();
                    if (inputData.referencesFilter(subSystem, systemFilter)) {
                        refreshData();
                    }
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                if (event.getSource() instanceof SystemFilter) {
                    SubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    SystemFilter systemFilter = (SystemFilter)event.getSource();
                    WorkWithSpooledFilesInputData inputData = (WorkWithSpooledFilesInputData)getInputData();
                    if (inputData.referencesFilter(subSystem, systemFilter)) {
                        setInputData(inputData);
                    }
                }
            }
        }
    }

    private SubSystem getSubSystem(SystemFilterReference filterReference) {
        return (SubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    protected SystemFilter findFilter(String connectionName, String filterPoolName, String filterName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
        SubSystem subSystem = ISeriesRSEHelper.getSubSystemByClass(connection, SpooledFileSubSystem.ID);
        SystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (SystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            if (filterPoolName != null && filterPoolName.equals(systemFilterPoolReference.getReferencedFilterPool().getName())) {
                EList eList = systemFilterPoolReference.getReferencedFilterPool().getFilters();
                for (int i = 0; i < eList.size(); i++) {
                    SystemFilter filter = (SystemFilter)eList.get(i);
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

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        SubSystem subSystem = ISeriesRSEHelper.getSubSystemByClass(connection, SpooledFileSubSystem.ID);
        if (subSystem == null) {
            return null;
        }

        SystemFilter systemFilter = findFilter(connectionName, filterPoolName, filterName);
        if (systemFilter == null) {
            return null;
        }

        WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(subSystem, systemFilter);

        return inputData;
    }
}
