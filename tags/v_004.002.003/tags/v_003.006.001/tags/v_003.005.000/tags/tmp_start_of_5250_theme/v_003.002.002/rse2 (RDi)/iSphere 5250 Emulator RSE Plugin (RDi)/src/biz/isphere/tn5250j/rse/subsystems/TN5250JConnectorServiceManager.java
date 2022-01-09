/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.AbstractConnectorServiceManager;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;

public class TN5250JConnectorServiceManager extends AbstractConnectorServiceManager {

    private static TN5250JConnectorServiceManager inst;

    public TN5250JConnectorServiceManager() {
        super();
    }

    public static TN5250JConnectorServiceManager getInstance() {
        if (inst == null) inst = new TN5250JConnectorServiceManager();
        return inst;
    }

    @Override
    public IConnectorService createConnectorService(IHost host) {
        return new TN5250JConnectorService(host);
    }

    @Override
    public boolean sharesSystem(ISubSystem otherSubSystem) {
        return (otherSubSystem instanceof ITN5250JSubSystem);
    }

    @Override
    public Class<ITN5250JSubSystem> getSubSystemCommonInterface(ISubSystem subsystem) {
        return ITN5250JSubSystem.class;
    }

}
