/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.api.debugger.moduleviews.DebuggerView;
import biz.isphere.core.internal.api.debugger.moduleviews.IQSDREGDV;
import biz.isphere.core.internal.api.debugger.moduleviews.IQSDRTVMV;
import biz.isphere.core.internal.api.debugger.moduleviews.IQSDRTVMVResult;
import biz.isphere.core.internal.api.debugger.viewtext.IQSDRTVVT;
import biz.isphere.core.internal.api.debugger.viewtext.IQSDRTVVTResult;
import biz.isphere.core.moduleviewer.ModuleViewEditor;
import biz.isphere.core.moduleviewer.ModuleViewEditorInput;

import com.ibm.as400.access.AS400;

public class DisplayDebugModuleViewHandlerDelegate {

    private static final int RECEIVE_BUFFER_LENGTH = 32767;

    private String connectionName;

    private AS400 system;
    private String iSphereLibrary;

    public DisplayDebugModuleViewHandlerDelegate(String connectionName) {
        this.connectionName = connectionName;
    }

    public Object execute(String program, String library, String objectType, String module) throws Exception {

        system = IBMiHostContributionsHandler.getSystem(connectionName);

        iSphereLibrary = ISpherePlugin.getISphereLibrary(connectionName);

        if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), connectionName)) {

            String currentLibrary = null;
            try {
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
                        retrieveViewAndOpenItInEditor(program, library, objectType, module);
                    }

                } finally {
                    try {
                        ISphereHelper.setCurrentLibrary(system, currentLibrary);
                    } catch (Exception e) {
                        throwException("Could not restore current library to: " + currentLibrary, e); //$NON-NLS-1$
                    }
                }
            }
        }

        return null;
    }

    private void retrieveViewAndOpenItInEditor(String program, String library, String objectType, String module) throws Exception {

        boolean isDebuggerStarted = false;

        try {

            isDebuggerStarted = startDebugger(program, library, objectType);

            DebuggerView[] debuggerViews = retrieveDebuggerViews(program, library, objectType, module);
            openModuleViewEditor(debuggerViews);

        } finally {

            if (isDebuggerStarted) {
                endDebugger();
                isDebuggerStarted = false;
            }

        }
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

    private void openModuleViewEditor(DebuggerView[] debuggerViews) throws Exception {

        DebuggerView debuggerView = findDebugView(debuggerViews);
        if (debuggerView != null) {

            ModuleViewEditorInput tEditorInput = new ModuleViewEditorInput(system, iSphereLibrary, debuggerViews, debuggerView.getNumber());

            IWorkbenchPage tPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            ModuleViewEditor tEditor = tEditorInput.findEditor(tPage);
            if (tEditor != null) {
                tEditor.setInput(tEditorInput);
            }

            if (tPage != null) {
                if (tPage.openEditor(tEditorInput, ModuleViewEditor.ID) == null) {
                    tEditorInput.dispose();
                }
            }

        } else {
            throw new Exception(Messages.No_TEXT_or_LISTING_views_available);
        }
    }

    private DebuggerView[] retrieveDebuggerViews(String program, String library, String objectType, String module) throws Exception {

        IQSDRTVMV iqsdrtvmv = new IQSDRTVMV(system, iSphereLibrary);
        IQSDRTVMVResult iqsdrtvmvResult = new IQSDRTVMVResult(system, new byte[RECEIVE_BUFFER_LENGTH], IQSDRTVMV.SDMV0100);
        if (!iqsdrtvmv.execute(iqsdrtvmvResult, program, library, objectType, module)) {
            throwException("Could not retrieve module views: " + iqsdrtvmv.getErrorMessage()); //$NON-NLS-1$
        }

        List<DebuggerView> debuggerViews = iqsdrtvmvResult.getViews();

        List<DebuggerView> filteredDebuggerViews = new LinkedList<DebuggerView>();
        for (DebuggerView debuggerView : debuggerViews) {
            if (debuggerView.isListingView() || debuggerView.isTextView()) {
                filteredDebuggerViews.add(debuggerView);
            }
        }

        return filteredDebuggerViews.toArray(new DebuggerView[filteredDebuggerViews.size()]);
    }

    private DebuggerView findDebugView(DebuggerView[] debuggerViews) {

        DebuggerView firstTextView = null;

        for (DebuggerView debuggerView : debuggerViews) {
            if (debuggerView.isListingView()) {
                return debuggerView;
            } else if (debuggerView.isTextView()) {
                firstTextView = debuggerView;
            }
        }

        if (firstTextView != null) {
            return firstTextView;
        }

        return null;
    }

    private List<String> retrieveDebugViewText(DebuggerView debuggerView, int lineLength) throws Exception {

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
            if (!iqsdrtvvt.execute(iqsdrtvvtResult, debuggerView.getId(), startLine, IQSDRTVVT.ALL_LINES, lineLength)) {
                throwException("Could not retrieve view text: " + iqsdrtvvt.getErrorMessage());
            }

            lines.addAll(iqsdrtvvtResult.getLines());

            startLine = iqsdrtvvtResult.getLastLine() + 1;

        } while (iqsdrtvvtResult != null && iqsdrtvvtResult.getLastLine() < debuggerView.getLinesCount());

        return lines;
    }

    private void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    private void throwException(String message, Throwable exception) throws Exception {
        throw new Exception(message, exception);
    }
}
