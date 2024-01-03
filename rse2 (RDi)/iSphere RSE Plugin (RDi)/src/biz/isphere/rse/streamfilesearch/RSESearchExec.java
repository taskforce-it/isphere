/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.streamfilesearch;

import java.sql.Connection;
import java.util.ArrayList;

import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.streamfilesearch.SearchElement;
import biz.isphere.core.streamfilesearch.SearchExec;
import biz.isphere.core.streamfilesearch.SearchPostRun;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSESearchExec extends SearchExec {

    private IWorkbenchWindow _workbenchWindow;
    private IBMiConnection _connection;
    private Connection _jdbcConnection;
    private String _qualifiedConnectionName;

    public RSESearchExec(IWorkbenchWindow workbenchWindow, IBMiConnection connection) {

        this._workbenchWindow = workbenchWindow;
        this._connection = connection;
        this._qualifiedConnectionName = ConnectionManager.getConnectionName(this._connection);
        this._jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(this._qualifiedConnectionName);
    }

    public void execute(ArrayList<SearchElement> filteredElements, SearchOptions searchOptions) {
        SearchPostRun postRun = createPostRun(_workbenchWindow, _connection, filteredElements, searchOptions);
        execute(filteredElements, searchOptions, postRun);
    }

    private void execute(ArrayList<SearchElement> filteredElements, SearchOptions searchOptions, SearchPostRun postRun) {
        execute(_qualifiedConnectionName, _jdbcConnection, searchOptions, filteredElements, postRun);
    }

    private SearchPostRun createPostRun(IWorkbenchWindow workbenchWindow, IBMiConnection connection, ArrayList<SearchElement> searchElements,
        SearchOptions searchOptions) {

        SearchPostRun postRun = new SearchPostRun();
        postRun.setConnection(connection);
        postRun.setConnectionName(ConnectionManager.getConnectionName(connection));
        postRun.setSearchString(searchOptions.getCombinedSearchString());
        postRun.setSearchElements(new StreamFileSearchDelegate(workbenchWindow.getShell(), connection).createHashMap(searchElements));
        postRun.setWorkbenchWindow(workbenchWindow);

        return postRun;
    }
    
}
