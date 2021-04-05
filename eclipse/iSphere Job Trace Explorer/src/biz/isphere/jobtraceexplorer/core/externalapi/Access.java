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
import biz.isphere.jobtraceexplorer.core.model.JobTraceExplorerFileInput;
import biz.isphere.jobtraceexplorer.core.model.JobTraceExplorerSessionInput;
import biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView;

/**
 * This class is the public API of the iSphere Job Log Explorer.
 */
public class Access extends AbstractAccess {

    /**
     * Opens the job trace explorer for a given Json file, which has been saved
     * from the iSphere job trace explorer view.
     * 
     * @param shell - the parent shell.
     * @param jobTrace - stream file that contains the job trace data.
     * @throws Exception
     */
    public static void openJobTraceExplorer(Shell shell, File jobTrace) throws Exception {

        JobTraceExplorerFileInput input = new JobTraceExplorerFileInput(jobTrace);

        JobTraceExplorerView.openRemoteSessionJobTrace(ensureShell(shell), input);
    }

    /**
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
    public static void openJobTraceExplorer(Shell shell, String connectionName, String libraryName, String sessionID, boolean isIBMDataExcluded)
        throws Exception {

        JobTraceExplorerSessionInput input = new JobTraceExplorerSessionInput(connectionName, libraryName, sessionID);
        input.setExcludeIBMData(isIBMDataExcluded);

        JobTraceExplorerView.openRemoteSessionJobTrace(ensureShell(shell), input);
    }
}
