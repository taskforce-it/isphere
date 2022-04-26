/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.SubSystem;

import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;

public class TN5250JSubSystem extends SubSystem {

    private RSESession[] rseSessions = null;

    public TN5250JSubSystem(IHost host, IConnectorService connectorService) {
        super(host, connectorService);
    }

    @Override
    public void initializeSubSystem(IProgressMonitor monitor) {
    }

    @Override
    public void uninitializeSubSystem(IProgressMonitor monitor) {
    }

    @Override
    public Object getObjectWithAbsoluteName(String key) {
        if (key.startsWith("Session_")) {
            String sessionName = key.substring(8);
            RSESession[] rseSessions = getRSESessions();
            for (int idx = 0; idx < rseSessions.length; idx++)
                if (rseSessions[idx].getName().equals(sessionName)) return rseSessions[idx];
        }
        return null;
    }

    @Override
    public boolean hasChildren() {
        if (getRSESessions().length == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object[] getChildren() {
        return getRSESessions();
    }

    public RSESession[] getRSESessions() {

        ArrayList<RSESession> arrayListRSESessions = new ArrayList<RSESession>();

        String directory = TN5250JRSEPlugin.getRSESessionDirectory(getSystemProfileName() + "-" + getHostAliasName());
        File directoryTN5250J = new File(directory);
        if (!directoryTN5250J.exists()) {
            directoryTN5250J.mkdir();
        }

        String stringSessions[] = new File(directory).list();
        for (int idx = 0; idx < stringSessions.length; idx++) {
            RSESession rseSession = RSESession.load(this, stringSessions[idx]);
            if (rseSession != null) {
                arrayListRSESessions.add(rseSession);
            }
        }

        rseSessions = new RSESession[arrayListRSESessions.size()];
        arrayListRSESessions.toArray(rseSessions);

        return rseSessions;
    }

}
