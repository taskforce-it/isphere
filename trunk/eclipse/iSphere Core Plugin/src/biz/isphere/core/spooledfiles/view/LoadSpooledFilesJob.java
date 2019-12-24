/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.core.spooledfiles.SpooledFileFilter;

public class LoadSpooledFilesJob extends Job {

    private String connectionName;
    private String filterName;
    private String[] filterStrings;
    private int itemIndex;
    private ILoadSpooledFilesPostRun postRun;

    public LoadSpooledFilesJob(String connectionName, String filterName, String[] filterStrings, ILoadSpooledFilesPostRun postRun) {
        super(Messages.Loading_spooled_file);

        this.connectionName = connectionName;
        this.filterName = filterName;
        this.filterStrings = filterStrings;
        this.postRun = postRun;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        Connection jdbcConnection = getToolboxJDBCConnection(connectionName);

        Set<SpooledFile> spooledFilesSet = new HashSet<SpooledFile>();
        Vector<SpooledFile> spooledFilesList = new Vector<SpooledFile>();

        for (String filterString : filterStrings) {
            SpooledFileFilter spooledFileFilter = new SpooledFileFilter(filterString);
            SpooledFile[] spooledFiles = SpooledFileFactory.getSpooledFiles(connectionName, jdbcConnection, spooledFileFilter);
            for (SpooledFile spooledFile : spooledFiles) {
                if (monitor.isCanceled()) {
                    break;
                }
                if (!spooledFilesSet.contains(spooledFile)) {
                    spooledFilesSet.add(spooledFile);
                    spooledFilesList.add(spooledFile);
                }
            }
        }

        SpooledFile[] spooledFiles = spooledFilesList.toArray(new SpooledFile[spooledFilesList.size()]);
        postRun.setLoadSpooledFilesPostRunData(connectionName, filterName, spooledFiles, itemIndex);

        return Status.OK_STATUS;
    }

    private Connection getToolboxJDBCConnection(String connectionName) {

        Connection jdbcConnection = null;

        try {
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
        } catch (Throwable e) {
            ISpherePlugin.logError(NLS.bind("*** Could not get JDBC connection for system {0} ***", connectionName), e); //$NON-NLS-1$
        }
        return jdbcConnection;
    }
}
