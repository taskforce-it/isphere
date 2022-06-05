/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;

import biz.isphere.rexec.internal.RexecServer;
import biz.isphere.rexec.preferences.Preferences;

public class StartUp implements IStartup, IPropertyChangeListener {

    private RexecServer rexecServer;

    public void earlyStartup() {
        startRexecServer();
    }

    private void startRexecServer() {

        iSphereRexecPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        if (Preferences.getInstance().isServerEnabled()) {
            startServer();
        }
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (Preferences.SERVER_ENABLED.equals(event.getProperty())) {
            boolean isEnabled = Preferences.getInstance().isServerEnabled();
            if (!isEnabled) {
                shutdownServer();
            } else {
                startServer();
            }
        } else if (Preferences.SERVER_LISTENER_PORT.equals(event.getProperty())) {
            shutdownServer();
            startServer();
        }
    }

    private void shutdownServer() {
        if (rexecServer != null) {
            rexecServer.shutdown();
            rexecServer = null;
        }
    }

    private void startServer() {
        rexecServer = new RexecServer();
        rexecServer.start();
    }
}
