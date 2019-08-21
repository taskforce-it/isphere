/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractSystem;

public class TN5250JSystem extends AbstractSystem {

    private boolean connected = false;

    public TN5250JSystem(SubSystem subsystem) {
        super(subsystem);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void connect(IProgressMonitor monitor) throws Exception {
        connected = true;
    }

    @Override
    public void disconnect() throws Exception {
        connected = false;
    }

}
