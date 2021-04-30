/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.events.ISystemResourceChangeEvent;
import org.eclipse.rse.core.events.ISystemResourceChangeEvents;
import org.eclipse.rse.core.events.ISystemResourceChangeListener;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.filters.ISystemFilterReference;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.references.IRSEBaseReferencingObject;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesFilterInputData;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class WorkWithSpooledFilesView extends AbstractWorkWithSpooledFilesView implements ISystemResourceChangeListener {

    Map<Integer, Date> lastEvent;

    public WorkWithSpooledFilesView() {
        lastEvent = new HashMap<Integer, Date>();
    }

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
                    doEvent(eventType, filterReference);
                }
            } else if (event.getSource() instanceof IHost) {
                // Connection renamed.
                IHost host = (IHost)event.getSource();
                IBMiConnection connection = ConnectionManager.getIBMiConnection(host);
                ISubSystem subSystem = connection.getSubSystemByClass(SpooledFileSubSystem.ID);
                ISystemFilterReference[] filterReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterReferences(subSystem);
                for (ISystemFilterReference reference : filterReferences) {
                    doEvent(eventType, reference);
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE) {
            // Filter strings changed.
            if (event.getSource() instanceof ISystemFilter) {
                if (event.getGrandParent() instanceof SpooledFileSubSystem) {
                    ISystemFilter filter = (ISystemFilter)event.getSource();
                    for (IRSEBaseReferencingObject referencingObject : filter.getReferencingObjects()) {
                        ISystemFilterReference filterReference = (ISystemFilterReference)referencingObject;
                        doEvent(eventType, filterReference);
                    }
                }
            }
        } else if (eventType == ISystemResourceChangeEvents.EVENT_REFRESH_REMOTE) {
            if (event.getSource() instanceof ISystemFilterReference) {
                // Filter refreshed.
                ISystemFilterReference filterReference = (ISystemFilterReference)event.getSource();
                if (getSubSystem(filterReference) instanceof SpooledFileSubSystem) {
                    doEvent(eventType, filterReference);
                }
            }
        }
    }

    private void doEvent(int eventType, ISystemFilterReference filterReference) {

        WorkWithSpooledFilesFilterInputData newInputData = new WorkWithSpooledFilesFilterInputData(filterReference);

        WorkWithSpooledFilesFilterInputData otherInputData = (WorkWithSpooledFilesFilterInputData)getInputData();
        if (otherInputData == null) {
            return;
        }

        if (otherInputData.isSameFilterReference(newInputData)) {

            if (canRefresh(eventType)) {

                switch (eventType) {
                case ISystemResourceChangeEvents.EVENT_RENAME:
                    setInputData(newInputData);
                    break;
                case ISystemResourceChangeEvents.EVENT_CHANGE_FILTER_REFERENCE:
                    setInputData(newInputData);
                    break;
                case ISystemResourceChangeEvents.EVENT_REFRESH_REMOTE:
                    refreshData();
                    break;
                }

                lastEvent.put(eventType, new Date());
            }
        }
    }

    /**
     * Hugly hack to catch duplicate events.
     * 
     * @param eventType - the system resource change event type
     * @return can refresh or not
     */
    private boolean canRefresh(Integer eventType) {

        Date lastEventDate = lastEvent.get(eventType);

        if (lastEventDate == null || isWaitTimeElapsed(lastEventDate)) {
            return true;
        }

        return false;
    }

    private boolean isWaitTimeElapsed(Date lastEventDate) {

        if (lastEventDate == null) {
            return true;
        }

        Date now = new Date();
        long mSecsBetweenEvents = now.getTime() - lastEventDate.getTime();

        if (mSecsBetweenEvents > 500) {
            return true;
        }

        return false;
    }

    private ISubSystem getSubSystem(ISystemFilterReference filterReference) {
        return (ISubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    private ISystemFilterReference findFilterReference(String connectionName, String filterPoolName, String filterName) {

        if (connectionName == null || filterPoolName == null || filterName == null) {
            return null;
        }

        IBMiConnection connection = ConnectionManager.getIBMiConnection(connectionName);
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

        IBMiConnection connection = ConnectionManager.getIBMiConnection(connectionName);
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
