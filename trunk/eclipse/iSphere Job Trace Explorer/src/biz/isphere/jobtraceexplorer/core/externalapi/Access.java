/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.externalapi;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.jobtraceexplorer.core.model.JobTraceExplorerJsonFileInput;
import biz.isphere.jobtraceexplorer.core.model.JobTraceExplorerSessionInput;
import biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView;

/**
 * This class is the public API of the iSphere Job Log Explorer.
 */
public class Access extends AbstractAccess {

    /**
     * Loads a given given Json file with job trace entries into the iSphere Job
     * Trace Explorer. The file must have been saved from the iSphere Job Trace
     * Explorer view.
     * 
     * @param shell - the parent shell.
     * @param jobTrace - stream file that contains the job trace data.
     * @throws Exception
     */
    public static void loadJobTraceExplorer(Shell shell, File jobTrace) throws Exception {

        JobTraceExplorerJsonFileInput input = new JobTraceExplorerJsonFileInput(jobTrace);

        JobTraceExplorerView.openJobTrace(ensureShell(shell), input);
    }

    /**
     * Loads a given job trace session into tje iSphere Job Trace Explorer. The
     * session must have been captured with the <code>STRTRC</code> and
     * <code>ENDTRC</code> commands.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param libraryName - name of the library where the trace data is stored
     * @param sessionID - id of the job trace session as specified at the
     *        <code>ENDTRC</code> command.
     * @param isIBMDataExcluded - specifies whether IBM specific data is
     *        excluded when loading the trace data. The SQL WHERE clause for
     *        excluding IBM specific data can be managed on the preferences
     *        page.
     * @throws Exception
     */
    public static void loadJobTraceExplorer(Shell shell, String connectionName, String libraryName, String sessionID, boolean isIBMDataExcluded)
        throws Exception {

        JobTraceExplorerSessionInput input = new JobTraceExplorerSessionInput(connectionName, libraryName, sessionID);
        input.setExcludeIBMData(isIBMDataExcluded);

        JobTraceExplorerView.openJobTrace(ensureShell(shell), input);
    }
}
