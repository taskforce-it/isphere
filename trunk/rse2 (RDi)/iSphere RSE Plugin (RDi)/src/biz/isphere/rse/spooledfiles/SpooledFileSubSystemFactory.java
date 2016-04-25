/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.SpooledFileFilter;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class SpooledFileSubSystemFactory extends SubSystemConfiguration {

    @Override
    public ISubSystem createSubSystemInternal(IHost host) {
        return new SpooledFileSubSystem(host, getConnectorService(host));
    }

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

    @Override
    public String getTranslatedFilterTypeProperty(ISystemFilter selectedFilter) {
        return Messages.Spooled_File_Filter;
    }

    @Override
    protected ISystemFilterPool createDefaultFilterPool(ISystemFilterPoolManager mgr) {
        ISystemFilterPool defaultPool = super.createDefaultFilterPool(mgr);
        Vector<String> strings = new Vector<String>();
        SpooledFileFilter splfFilter = new SpooledFileFilter();
        splfFilter.setUser("*CURRENT");
        strings.add(splfFilter.getFilterString());
        try {
            ISystemFilter filter = mgr.createSystemFilter(defaultPool, Messages.My_spooled_files, strings);
            filter.setType("spooled file");
        } catch (Exception localException) {
        }
        return defaultPool;
    }

    @Override
    public boolean showGenericShowInTableOnFilter() {
        return true;
    }

}
