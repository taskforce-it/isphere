package biz.isphere.jobtraceexplorer.core.externalapi;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
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

        JobTraceExplorerView.openStreamFileJobTrace(ensureShell(shell), jobTrace);
    }

    /**
     * Opens the job trace explorer for a given job trace session.
     * <p>
     * This method is intended to be <b>exclusively</b> used <b>by iSphere</b>.
     * 
     * @param shell - the parent shell.
     * @param jobTraceSession - job trace session selection data
     * @throws Exception
     */
    public static void openJobTraceExplorer(Shell shell, JobTraceSession jobTraceSession) throws Exception {

        JobTraceExplorerView.openRemoteSessionJobTrace(ensureShell(shell), jobTraceSession);
    }

    /**
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param libraryName - name of the library where the trace data is stored
     * @param sessionID - id of the job trace session as specified at the
     *        <code>ENDTRC</code> command.
     * @param isIBMDataExcluded - specifies whether IBM specific data is
     *        excluded when loading the trace data.
     * @throws Exception
     */
    public static void openJobTraceExplorer(Shell shell, String connectionName, String libraryName, String sessionID, boolean isIBMDataExcluded)
        throws Exception {

        JobTraceSession jobTraceSession = new JobTraceSession(connectionName, libraryName, sessionID);
        jobTraceSession.setExcludeIBMData(isIBMDataExcluded);

        JobTraceExplorerView.openRemoteSessionJobTrace(ensureShell(shell), jobTraceSession);
    }
}
