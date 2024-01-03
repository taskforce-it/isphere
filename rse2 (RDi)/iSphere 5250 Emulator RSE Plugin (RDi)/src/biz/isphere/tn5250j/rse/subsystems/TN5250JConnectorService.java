/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.BasicConnectorService;

public class TN5250JConnectorService extends BasicConnectorService  {

    private boolean connected = false;

    public TN5250JConnectorService(IHost host) {
        super("connectorservice.devr.name", "connectorservice.devr.desc", host, 0);
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    protected void internalConnect(IProgressMonitor monitor) throws Exception {
        connected = true;
    }

    @Override
    public void internalDisconnect(IProgressMonitor monitor) throws Exception {
        connected = false;
    }

}
