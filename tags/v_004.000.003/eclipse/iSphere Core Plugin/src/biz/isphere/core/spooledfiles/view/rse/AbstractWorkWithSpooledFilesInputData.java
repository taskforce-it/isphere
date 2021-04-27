/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.core.spooledfiles.SpooledFileFilter;

public abstract class AbstractWorkWithSpooledFilesInputData {

    private String connectionName;
    private Set<String> filterStrings;

    public AbstractWorkWithSpooledFilesInputData(String connetionName) {
        this.connectionName = connetionName;
        this.filterStrings = new HashSet<String>();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String[] getFilterStrings() {
        return filterStrings.toArray(new String[filterStrings.size()]);
    }

    public void addFilterString(String filterString) {
        filterStrings.add(filterString);
    }

    public boolean isValid() {

        if (StringHelper.isNullOrEmpty(getConnectionName())) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(getFilterName())) {
            return false;
        }

        if (getFilterStrings() == null || getFilterStrings().length == 0) {
            return false;
        }

        for (String filterString : getFilterStrings()) {
            if (StringHelper.isNullOrEmpty(filterString)) {
                return false;
            }
        }

        return true;
    }

    public abstract String getFilterPoolName();

    public abstract String getFilterName();

    public abstract boolean isPersistable();

    public abstract String getContentId();

    public SpooledFile[] load(IProgressMonitor monitor) {

        Connection jdbcConnection = getToolboxJDBCConnection(getConnectionName());

        Set<SpooledFile> spooledFilesSet = new HashSet<SpooledFile>();
        Vector<SpooledFile> spooledFilesList = new Vector<SpooledFile>();

        for (String filterString : getFilterStrings()) {
            SpooledFileFilter spooledFileFilter = new SpooledFileFilter(filterString);
            SpooledFile[] spooledFiles = SpooledFileFactory.getSpooledFiles(getConnectionName(), jdbcConnection, spooledFileFilter);
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

        return spooledFiles;
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
