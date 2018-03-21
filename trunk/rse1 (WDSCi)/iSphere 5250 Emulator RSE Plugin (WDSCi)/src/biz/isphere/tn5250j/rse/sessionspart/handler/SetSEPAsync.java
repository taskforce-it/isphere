/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.systems.as400.debug.launchconfig.IDEALPlugin;
import com.ibm.etools.systems.as400.debug.sep.ServiceEntryPointActionDelegate;

public class SetSEPAsync extends AbstractAsyncHandler {

    private String library;
    private String program;
    private String type;

    public SetSEPAsync(Shell shell, SessionsInfo sessionsInfo, String library, String sourceFile, String member) {

        super(shell, sessionsInfo);

        this.library = library;
        this.program = sourceFile;
        this.type = member;
    }

    public void runInternally() {

        try {

            ISeriesObject[] objects = getConnection().listObjects(getShell(), library, program, new String[] { type });
            if (objects != null && objects.length > 0) {
                IAction action = new SetSEPAction();
                IStructuredSelection selection = new StructuredSelection(objects);
                ServiceEntryPointActionDelegate delegate = new ServiceEntryPointActionDelegate();
                delegate.selectionChanged(action, selection);
                delegate.run(action);
            }
        } catch (Throwable e) {
        }
    }

    private class SetSEPAction extends Action {
        @Override
        public String getText() {
            return IDEALPlugin.getPluginMessagesResourceBundle().getString("Set_SEP_NoPrompt_MenuItem");
        }
    };
}
