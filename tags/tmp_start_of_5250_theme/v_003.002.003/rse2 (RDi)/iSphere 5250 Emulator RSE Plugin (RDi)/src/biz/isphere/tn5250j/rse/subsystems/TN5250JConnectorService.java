/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.AbstractConnectorService;

public class TN5250JConnectorService extends AbstractConnectorService {

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

    @Override
    public boolean supportsRemoteServerLaunching() {
        return false;
    }

    @Override
    public boolean supportsServerLaunchProperties() {
        return false;
    }

    public void acquireCredentials(boolean arg0) throws OperationCanceledException {
    }

    public void clearCredentials() {
    }

    public void clearPassword(boolean arg0, boolean arg1) {
    }

    public String getUserId() {
        return null;
    }

    public boolean hasPassword(boolean arg0) {
        return false;
    }

    public boolean inheritsCredentials() {
        return false;
    }

    public boolean isSuppressed() {
        return false;
    }

    public void removePassword() {
    }

    public void removeUserId() {
    }

    public boolean requiresPassword() {
        return false;
    }

    public boolean requiresUserId() {
        return false;
    }

    public void savePassword() {
    }

    public void saveUserId() {
    }

    public void setPassword(String arg0, String arg1, boolean arg2, boolean arg3) {
    }

    public void setSuppressed(boolean arg0) {
    }

    public void setUserId(String arg0) {
    }

    public boolean sharesCredentials() {
        return false;
    }

    public boolean supportsPassword() {
        return false;
    }

    public boolean supportsUserId() {
        return false;
    }

}
