/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

public class TN5250JSubSystemConfiguration extends SubSystemConfiguration {

    public TN5250JSubSystemConfiguration() {
        super();
    }

    @Override
    public ISubSystem createSubSystemInternal(IHost host) {
        return new TN5250JSubSystem(host, getConnectorService(host));
    }

    @Override
    public IConnectorService getConnectorService(IHost host) {
        return TN5250JConnectorServiceManager.getInstance().getConnectorService(host, ITN5250JSubSystem.class);
    }

    public boolean supportsUserId() {
        return false;
    }

    @Override
    public boolean supportsServerLaunchProperties(IHost host) {
        return false;
    }

    @Override
    public boolean supportsFilters() {
        return false;
    }

}
