/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

import com.ibm.etools.iseries.core.api.ISeriesConnection;

public abstract class AbstractAsyncHandler implements Runnable {

    private Shell shell;
    private SessionsInfo sessionInfo;
    private String currentLibrary;

    public AbstractAsyncHandler(Shell shell, SessionsInfo sessionInfo) {
        this.shell = shell;
        this.sessionInfo = sessionInfo;
    }

    protected Shell getShell() {
        return shell;
    }

    public void run() {

        try {
            setCurrentLibrary(getCurrentLibrary());
            runInternally();
        } finally {
            restoreCurrentLibrary();
        }
    }

    protected abstract void runInternally();

    protected String getCurrentLibrary() {
        return null;
    }

    protected ISeriesConnection getConnection() {
        ISeriesConnection iSeriesConnection = ISeriesConnection.getConnection(sessionInfo.getRSEProfil(), sessionInfo.getRSEConnection());
        return iSeriesConnection;
    }

    protected void setCurrentLibrary(String library) {

        try {
            currentLibrary = ISphereHelper.getCurrentLibrary(getConnection().getAS400ToolboxObject(null));
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not retrieve current library ***", e);
        }
    }

    protected void restoreCurrentLibrary() {

        if (currentLibrary == null) {
            return;
        }

        try {
            ISphereHelper.setCurrentLibrary(getConnection().getAS400ToolboxObject(null), currentLibrary);
        } catch (Exception e1) {
            ISpherePlugin.logError("*** Could not set current library to: " + currentLibrary + " ***", e1);
        }

        currentLibrary = null;
    }
}
