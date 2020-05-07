/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

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
import com.ibm.etools.systems.model.SystemConnection;
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

        if (eventType == ISystemResourceChangeEvents.EVENT_RENAME) {
            if (event.getSource() instanceof SystemFilterReference) {
                // Filter renamed.
                SystemFilterReference filterReference = (SystemFilterReference)event.getSource();
                if (getSubSystem(filterReference) instanceof SpooledFileSubSystem) {
                    SystemFilter filter = filterReference.getReferencedFilter();
                    SubSystem subSystem = getSubSystem(filterReference);
                    doEvent(eventType, subSystem, filter);
                }
            } else if (event.getSource() instanceof SystemConnection) {
                // Connection renamed.
                SystemConnection systemConnection = (SystemConnection)event.getSource();
                ISeriesConnection connection = ISeriesConnection.getConnection(systemConnection.getAliasName());
                SubSystem subSystem = ISeriesRSEHelper.getSubSystemByClass(connection, SpooledFileSubSystem.ID);
                SystemFilterReference[] filterReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterReferences();
                for (SystemFilterReference reference : filterReferences) {
                    SystemFilter filter = reference.getReferencedFilter();
                    doEvent(eventType, subSystem, filter);
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            // Filter strings changed.
            if (event.getSource() instanceof SystemFilter) {
                if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                    SystemFilter filter = (SystemFilter)event.getSource();
                    SubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    doEvent(eventType, subSystem, filter);
                }
            }
        }
    }

    private void doEvent(int eventType, SubSystem subSystem, SystemFilter filter) {

        WorkWithSpooledFilesInputData inputData = (WorkWithSpooledFilesInputData)getInputData();
        if (inputData != null && inputData.referencesFilter(subSystem, filter)) {

            switch (eventType) {
            case ISystemResourceChangeEvents.EVENT_RENAME:
                refreshTitle();
                break;
            case ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE:
                refreshData();
                break;
            }
        }
    }

    private SubSystem getSubSystem(SystemFilterReference filterReference) {
        return (SubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    protected SystemFilterReference findFilterReference(String connectionName, String filterPoolName, String filterName) {

        if (connectionName == null || filterPoolName == null || filterName == null) {
            return null;
        }

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
        SubSystem subSystem = ISeriesRSEHelper.getSubSystemByClass(connection, SpooledFileSubSystem.ID);
        SystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (SystemFilterPoolReference filterPoolReference : filterPoolReferences) {
            if (filterPoolName.equals(filterPoolReference.getName())) {
                SystemFilterReference[] filterReferences = filterPoolReference.getSystemFilterReferences();
                for (SystemFilterReference filterReference : filterReferences) {
                    SystemFilter filter = filterReference.getReferencedFilter();
                    if (filterName.equals(filter.getName())) {
                        return filterReference;
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

        SystemFilterReference filterReference = findFilterReference(connectionName, filterPoolName, filterName);
        if (filterReference == null) {
            return null;
        }

        WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(filterReference);

        return inputData;
    }
}
