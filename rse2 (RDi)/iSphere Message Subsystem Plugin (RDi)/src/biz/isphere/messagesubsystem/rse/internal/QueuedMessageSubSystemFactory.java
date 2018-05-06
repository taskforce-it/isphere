/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse.internal;

import java.util.Vector;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.messagesubsystem.rse.Messages;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class QueuedMessageSubSystemFactory extends SubSystemConfiguration {

    public static final String ID = "biz.isphere.messagesubsystem.internal.QueuedMessageSubSystemFactory"; //$NON-NLS-1$

    public QueuedMessageSubSystemFactory() {
        super();
    }

    public int getCurrentlySelectedSystemCcsid() {
        return IBMiHostContributionsHandler.getSystemCcsid(currentlySelectedConnection.getAliasName());
    }

    @Override
    public ISubSystem createSubSystemInternal(IHost host) {
        QueuedMessageSubSystem subSystem = new QueuedMessageSubSystem(host, getConnectorService(host));
        return subSystem;
    }

    @Override
    protected void removeSubSystem(ISubSystem subSystem) {
        getSubSystems(false);
        super.removeSubSystem(subSystem);
    }

    @Override
    public String getTranslatedFilterTypeProperty(ISystemFilter selectedFilter) {
        return Messages.Message_Filter;
    }

    @Override
    protected ISystemFilterPool createDefaultFilterPool(ISystemFilterPoolManager mgr) {
        ISystemFilterPool defaultPool = super.createDefaultFilterPool(mgr);
        Vector<String> strings = new Vector<String>();
        QueuedMessageFilter messageFilter = QueuedMessageFilter.getDefaultFilter();
        strings.add(messageFilter.getFilterString());
        try {
            ISystemFilter filter = mgr.createSystemFilter(defaultPool, Messages.My_Messages, strings);
            filter.setType(Messages.Message_Filter);
        } catch (Exception exc) {
        }
        return defaultPool;
    }

    @Override
    public boolean supportsNestedFilters() {
        return false;
    }

    /*
     * Start of RDi/WDSCi specific methods.
     */

    @Override
    public IConnectorService getConnectorService(IHost host) {

        ISubSystem[] subSystems = host.getSubSystems();
        for (int i = 0; i < subSystems.length; i++) {
            ISubSystem subSystem = subSystems[i];
            if ((subSystem instanceof QSYSObjectSubSystem)) {
                return subSystem.getConnectorService();
            }
        }

        return null;
    }
}