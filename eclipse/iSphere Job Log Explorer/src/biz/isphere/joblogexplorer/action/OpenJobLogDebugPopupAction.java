/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.joblogexplorer.jobs.rse.LoadRemoteJobLogJob;

import com.ibm.debug.pdt.internal.core.PDTDebugTarget;
import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

@SuppressWarnings("restriction")
public class OpenJobLogDebugPopupAction implements IViewActionDelegate {

    private static final String PATTERN = "\\S*\\s+((\\d{6})/(.{1,10})/(.{1,10}))\\s+.*";

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {
        Object selectedObject = structuredSelection.getFirstElement();
        if (selectedObject instanceof DebuggeeProcess) {
            DebuggeeProcess debuggeeProcess = (DebuggeeProcess)selectedObject;
            String connectionName = getConnectionName(debuggeeProcess);
            String qualifiedJobName = getQualifiedJobName(debuggeeProcess);
            if (!StringHelper.isNullOrEmpty(qualifiedJobName)) {
                LoadRemoteJobLogJob job = new LoadRemoteJobLogJob(connectionName, qualifiedJobName);
                job.run();
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (isIBMiJob(selection)) {
            structuredSelection = ((IStructuredSelection)selection);
            action.setEnabled(true);
        } else {
            structuredSelection = null;
            action.setEnabled(false);
        }
    }

    protected String getQualifiedJobName(IProcess process) {

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(process.getAttribute(null));
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    protected String getConnectionName(DebuggeeProcess debuggeeProcess) {

        if (debuggeeProcess.getDebugTarget() instanceof PDTDebugTarget) {
            PDTDebugTarget debugTarget = (PDTDebugTarget)debuggeeProcess.getDebugTarget();

            String tcpAddr = null;

            try {
                String hostName = debugTarget.getSocket().getInetAddress().getHostName();
                tcpAddr = InetAddress.getByName(hostName).getHostAddress();
            } catch (UnknownHostException e1) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e1));
            }

            if (!StringHelper.isNullOrEmpty(tcpAddr)) {
                String connectionName = IBMiHostContributionsHandler.getConnectionNameByIPAddr(tcpAddr, true);
                return connectionName;
            }
        }

        return null;
    }

    protected boolean isIBMiJob(ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
            Object selectedObject = structuredSelection.getFirstElement();
            if ((selectedObject instanceof IProcess)) {
                IProcess process = (IProcess)selectedObject;
                String patternString = PATTERN;
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(process.getAttribute(null));
                if (matcher.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void init(IViewPart view) {
        this.shell = view.getSite().getShell();
    }
}
