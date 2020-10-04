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
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesFilterInputData;

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

        if (eventType == ISystemResourceChangeEvents.EVENT_RENAME) {
            if (event.getSource() instanceof ISystemFilterReference) {
                // Filter renamed.
                ISystemFilterReference filterReference = (ISystemFilterReference)event.getSource();
                if (getSubSystem(filterReference) instanceof SpooledFileSubSystem) {
                    ISystemFilter filter = filterReference.getReferencedFilter();
                    ISubSystem subSystem = getSubSystem(filterReference);
                    doEvent(eventType, subSystem, filter);
                }
            } else if (event.getSource() instanceof IHost) {
                // Connection renamed.
                IHost host = (IHost)event.getSource();
                IBMiConnection connection = IBMiConnection.getConnection(host.getAliasName());
                ISubSystem subSystem = connection.getSubSystemByClass(SpooledFileSubSystem.ID);
                ISystemFilterReference[] filterReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterReferences(subSystem);
                for (ISystemFilterReference reference : filterReferences) {
                    ISystemFilter filter = reference.getReferencedFilter();
                    doEvent(eventType, subSystem, filter);
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            // Filter strings changed.
            if (event.getSource() instanceof ISystemFilter) {
                if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                    ISystemFilter filter = (ISystemFilter)event.getSource();
                    ISubSystem subSystem = (SpooledFileSubSystem)event.getGrandParent();
                    doEvent(eventType, subSystem, filter);
                }
            }
        }
    }

    private void doEvent(int eventType, ISubSystem subSystem, ISystemFilter filter) {

        WorkWithSpooledFilesFilterInputData inputData = (WorkWithSpooledFilesFilterInputData)getInputData();
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

    private ISubSystem getSubSystem(ISystemFilterReference filterReference) {
        return (ISubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    private ISystemFilterReference findFilterReference(String connectionName, String filterPoolName, String filterName) {

        if (connectionName == null || filterPoolName == null || filterName == null) {
            return null;
        }

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        ISubSystem subSystem = connection.getSubSystemByClass(SpooledFileSubSystem.ID);
        ISystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (ISystemFilterPoolReference filterPoolReference : filterPoolReferences) {
            if (filterPoolName.equals(filterPoolReference.getName())) {
                ISystemFilterReference[] filterReferences = filterPoolReference.getSystemFilterReferences(subSystem);
                for (ISystemFilterReference filterReference : filterReferences) {
                    ISystemFilter filter = filterReference.getReferencedFilter();
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

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        ISystemFilterReference filterReference = findFilterReference(connectionName, filterPoolName, filterName);
        if (filterReference == null) {
            return null;
        }

        WorkWithSpooledFilesFilterInputData inputData = new WorkWithSpooledFilesFilterInputData(filterReference);

        return inputData;
    }
}
