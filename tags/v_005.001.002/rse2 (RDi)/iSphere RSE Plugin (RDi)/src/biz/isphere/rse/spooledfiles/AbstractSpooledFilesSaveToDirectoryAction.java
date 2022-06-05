/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.core.spooledfiles.SpooledFileFilter;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.handler.SaveSpooledFilesToDirectoryHandler;

public abstract class AbstractSpooledFilesSaveToDirectoryAction implements IObjectActionDelegate {

    private String format;
    private Shell shell;
    private IStructuredSelection structuredSelection;

    public AbstractSpooledFilesSaveToDirectoryAction(String format) {
        super();

        this.format = format;
    }

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {
            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();
                SystemFilterReference filterReference = (SystemFilterReference)object;
                if ((object instanceof SystemFilterReference) && (filterReference.getSubSystem() instanceof SpooledFileSubSystem)) {
                    SpooledFile[] spooledFiles = loadSpooledFiles(filterReference);
                    new SaveSpooledFilesToDirectoryHandler(getShell(), format).exportSpooledFiles(spooledFiles);
                }
            }
        }
    }

    protected SpooledFile[] loadSpooledFiles(SystemFilterReference filterReference) {

        String connectionName = getConnectionName(filterReference.getSubSystem());
        Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);

        Set<SpooledFile> spooledFilesSet = new HashSet<SpooledFile>();
        Vector<SpooledFile> spooledFilesList = new Vector<SpooledFile>();

        String[] filterStrings = filterReference.getReferencedFilter().getFilterStrings();

        for (String filterString : filterStrings) {
            SpooledFileFilter spooledFileFilter = new SpooledFileFilter(filterString);
            SpooledFile[] spooledFiles = SpooledFileFactory.getSpooledFiles(connectionName, jdbcConnection, spooledFileFilter);
            for (SpooledFile spooledFile : spooledFiles) {
                if (!spooledFilesSet.contains(spooledFile)) {
                    spooledFilesSet.add(spooledFile);
                    spooledFilesList.add(spooledFile);
                }
            }
        }

        SpooledFile[] spooledFiles = spooledFilesList.toArray(new SpooledFile[spooledFilesList.size()]);

        return spooledFiles;
    }

    public String getConnectionName(ISubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getHost());
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    private Shell getShell() {

        if (shell != null) {
            shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }

        return shell;
    }

}
