/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.moduleviewer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.api.debugger.moduleviews.DebuggerView;
import biz.isphere.core.internal.api.debugger.moduleviews.IQSDREGDV;
import biz.isphere.core.internal.api.debugger.viewtext.IQSDRTVVT;
import biz.isphere.core.internal.api.debugger.viewtext.IQSDRTVVTResult;

import com.ibm.as400.access.AS400;

public class ModuleViewStorage extends PlatformObject implements IStorage {

    private static final int RECEIVE_BUFFER_LENGTH = 32767;
    private static final int LINE_LENGTH = 240;

    private AS400 system;
    private String iSphereLibrary;
    private DebuggerView debuggerView;

    private String[] lines;

    public ModuleViewStorage(AS400 system, String iSphereLibrary, DebuggerView debuggerView) {

        this.system = system;
        this.iSphereLibrary = iSphereLibrary;
        this.debuggerView = debuggerView;

        this.lines = null;
    }

    public String getSystemName() {
        return system.getSystemName();
    }

    public String getFullQualifiedName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getSystemName());
        buffer.append(":");
        buffer.append(debuggerView.getLibrary());
        buffer.append("/");
        buffer.append(debuggerView.getObject());
        buffer.append(".");
        buffer.append(debuggerView.getModule());
        buffer.append("[");
        buffer.append(debuggerView.getNumber());
        buffer.append("]");

        return buffer.toString();
    }

    public int getViewNumber() {
        return debuggerView.getNumber();
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(debuggerView.getLibrary());
        buffer.append("/");
        buffer.append(debuggerView.getObject());
        buffer.append(".");
        buffer.append(debuggerView.getModule());

        return buffer.toString();
    }

    public String getDescription() {
        return debuggerView.getDescription();
    }

    public InputStream getContents() throws CoreException {

        try {

            if (lines == null) {
                lines = performLoadViewText(debuggerView);
            }

            StringBuilder buffer = new StringBuilder();

            for (String line : lines) {
                if (buffer.length() > 0) {
                    buffer.append("\n");
                }
                buffer.append(line);
            }

            return new ByteArrayInputStream(buffer.toString().getBytes());

        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, ISpherePlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), e));
        }
    }

    public IPath getFullPath() {
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

    private String[] performLoadViewText(DebuggerView debuggerView) throws Exception {

        String currentLibrary = null;
        try {
            if (!system.isConnected()) {
                system.connectService(AS400.COMMAND);
                System.out.println("Connected system: " + system.hashCode());
            }
            currentLibrary = ISphereHelper.getCurrentLibrary(system);
        } catch (Exception e) {
            throwException("Could not retrieve current library", e); //$NON-NLS-1$
        }

        if (currentLibrary != null) {

            try {

                boolean ok = false;
                try {
                    ok = ISphereHelper.setCurrentLibrary(system, iSphereLibrary);
                } catch (Exception e) {
                    throwException("Could not set current library to: " + iSphereLibrary, e); //$NON-NLS-1$
                }

                if (ok) {
                    return doLoadViewText(debuggerView);
                }

            } finally {
                try {
                    ISphereHelper.setCurrentLibrary(system, currentLibrary);
                } catch (Exception e) {
                    throwException("Could not restore current library to: " + currentLibrary, e); //$NON-NLS-1$
                }
            }
        }

        String message = Messages.bind(Messages.Could_not_retrieve_any_text_lines_from_view, debuggerView.getDescription());
        throwException(message);

        return null;
    }

    private String[] doLoadViewText(DebuggerView debuggerView) throws Exception {

        boolean isDebuggerStarted = false;

        isDebuggerStarted = startDebugger(debuggerView.getObject(), debuggerView.getLibrary(), debuggerView.getObjectType());

        try {

            List<String> lines = retrieveDebugViewText(debuggerView);

            return lines.toArray(new String[lines.size()]);

        } finally {
            if (isDebuggerStarted) {
                endDebugger();
                isDebuggerStarted = false;
            }
        }
    }

    private List<String> retrieveDebugViewText(DebuggerView debuggerView) throws Exception {

        List<String> lines = new LinkedList<String>();

        // Register debugger view
        IQSDREGDV iqsdregdv = new IQSDREGDV(system, iSphereLibrary);
        if (!iqsdregdv.execute(debuggerView)) {
            throwException("Could not register debug view: " + iqsdregdv.getErrorMessage()); //$NON-NLS-1$
        }

        // Retrieve debugger view text
        IQSDRTVVT iqsdrtvvt = new IQSDRTVVT(system, iSphereLibrary);
        IQSDRTVVTResult iqsdrtvvtResult = null;
        int startLine = 1;

        do {

            iqsdrtvvtResult = new IQSDRTVVTResult(system, new byte[RECEIVE_BUFFER_LENGTH], IQSDRTVVT.SDVT0100);
            if (!iqsdrtvvt.execute(iqsdrtvvtResult, debuggerView.getId(), startLine, IQSDRTVVT.ALL_LINES, LINE_LENGTH)) {
                throwException("Could not retrieve view text: " + iqsdrtvvt.getErrorMessage());
            }

            lines.addAll(iqsdrtvvtResult.getLines());

            startLine = iqsdrtvvtResult.getLastLine() + 1;

        } while (iqsdrtvvtResult != null && iqsdrtvvtResult.getLastLine() < debuggerView.getLinesCount());

        return lines;
    }

    private boolean startDebugger(String program, String library, String objectType) throws Exception {

        boolean isDebuggerStarted = false;

        String message;
        String strdbgCommand = "QSYS/STRDBG {2}({1}/{0}) UPDPROD(*NO) DSPMODSRC(*NO) SRCDBGPGM(*LIBL/DBGSSNHDLR)"; //$NON-NLS-1$

        if (ISeries.PGM.equals(objectType)) {
            strdbgCommand = NLS.bind(strdbgCommand, new Object[] { program, library, "PGM" }); //$NON-NLS-1$
        } else if (ISeries.SRVPGM.equals(objectType)) {
            strdbgCommand = NLS.bind(strdbgCommand, new Object[] { program, library, "SRVPGM" }); //$NON-NLS-1$
        } else {
            throw new IllegalArgumentException("Illegal argument of parameter 'objectType': " + objectType); //$NON-NLS-1$
        }

        message = ISphereHelper.executeCommand(system, strdbgCommand);

        if (!StringHelper.isNullOrEmpty(message)) {
            throw new Exception(message);
        } else {
            isDebuggerStarted = true;
        }

        return isDebuggerStarted;
    }

    private void endDebugger() throws Exception {

        String message = ISphereHelper.executeCommand(system, "QSYS/ENDDBG"); //$NON-NLS-1$
        if (!StringHelper.isNullOrEmpty(message)) {
            throw new Exception(message);
        }
    }

    private void throwException(String message) throws Exception {
        throw new Exception(message);
        // throw new CoreException(new Status(IStatus.ERROR,
        // ISpherePlugin.PLUGIN_ID, message));
    }

    private void throwException(String message, Throwable exception) throws Exception {
        throw new Exception(message, exception);
        // throw new CoreException(new Status(IStatus.ERROR,
        // ISpherePlugin.PLUGIN_ID, message, exception));
    }
}
