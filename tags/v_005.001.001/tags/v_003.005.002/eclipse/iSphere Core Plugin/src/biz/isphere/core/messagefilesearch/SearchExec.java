/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.search.SearchOptions;

import com.ibm.as400.access.AS400;

public class SearchExec {

    private class Search extends Job {

        private AS400 _as400;
        private String _hostName;
        private String _connectionName;
        private Connection _jdbcConnection;
        private SearchOptions _searchOptions;
        private ArrayList<SearchElement> _searchElements;
        private ISearchPostRun _searchPostRun;
        private int _handle;
        private int _counter;
        private String iSphereLibrary;

        public Search(AS400 _as400, String _connectionName, Connection _jdbcConnection, SearchOptions _searchOptions,
            ArrayList<SearchElement> _searchElements, ISearchPostRun _searchPostRun) {

            this(_as400, _connectionName, null, _jdbcConnection, _searchOptions, _searchElements, _searchPostRun);
        }

        @CMOne(info = "This constructor is used by CMOne")
        public Search(AS400 _as400, String _hostName, Connection _jdbcConnection, SearchOptions _searchOptions,
            ArrayList<SearchElement> _searchElements) {

            this(_as400, null, _hostName, _jdbcConnection, _searchOptions, _searchElements, null);
        }

        private Search(AS400 _as400, String _connectionName, String _hostName, Connection _jdbcConnection, SearchOptions _searchOptions,
            ArrayList<SearchElement> _searchElements, ISearchPostRun _searchPostRun) {

            super("iSphere Message File Search");

            this._as400 = _as400;
            this._hostName = _hostName;
            this._connectionName = _connectionName;
            this._jdbcConnection = _jdbcConnection;
            this._searchOptions = _searchOptions;
            this._searchElements = _searchElements;
            this._searchPostRun = _searchPostRun;

            iSphereLibrary = ISpherePlugin.getISphereLibrary(this._connectionName);

        }

        @Override
        public IStatus run(IProgressMonitor monitor) {

            IStatus _status = Status.OK_STATUS;

            String currentLibrary = null;
            try {
                currentLibrary = ISphereHelper.getCurrentLibrary(_as400);
            } catch (Exception e) {
                ISpherePlugin.logError("*** Could not retrieve current library ***", e);
            }

            if (currentLibrary != null) {

                boolean ok = false;
                try {
                    ok = ISphereHelper.setCurrentLibrary(_as400, iSphereLibrary);
                } catch (Exception e1) {
                    ISpherePlugin.logError("*** Could not set current library to: " + iSphereLibrary + " ***", e1);
                }

                if (ok) {

                    try {

                        // Get search handle and create status record in
                        // XFNDSTRS.
                        _handle = new XFNDSTR_getHandle().run(_as400);

                        if (_handle > 0) {

                            // Append search elements to XFNDSTRI.
                            SearchElement.setSearchElements(iSphereLibrary, _jdbcConnection, _handle, _searchElements);

                            int _numberOfSearchElements = new XFNDSTR_getNumberOfSearchElements().run(_as400, _handle);

                            monitor.beginTask("Searching", _numberOfSearchElements);

                            // Start the search job on the host.
                            new DoSearch(_as400, _handle, _searchOptions).start();

                            int _lastCounter = 0;

                            // Wait for the end of the search job.
                            // Read XFNDSTRS to update '_counter'
                            getStatus(monitor);

                            while (_counter != -1) {

                                monitor.worked(_counter - _lastCounter);

                                _lastCounter = _counter;

                                if (monitor.isCanceled()) {
                                    cancelJob();
                                    _status = Status.CANCEL_STATUS;
                                    break;
                                }

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                }

                                getStatus(monitor);

                            }

                            monitor.worked(_numberOfSearchElements - _lastCounter);

                            monitor.done();

                            if (!monitor.isCanceled()) {
                                _searchResults = SearchResult.getSearchResults(iSphereLibrary, _jdbcConnection, _handle, _as400, _connectionName,
                                    _hostName);
                            }

                            new XFNDSTR_clear().run(_as400, _handle);

                        }

                    } finally {

                        try {
                            ISphereHelper.setCurrentLibrary(_as400, currentLibrary);
                        } catch (Exception e) {
                            ISpherePlugin.logError("*** Could not restore current library to: " + currentLibrary + " ***", e);
                        }
                    }

                }

            }

            if (_searchPostRun != null) {
                _searchPostRun.run(_searchResults, _searchOptions);
            }

            return _status;

        }

        private void getStatus(IProgressMonitor monitor) {

            Throwable error = null;

            String _separator;
            try {
                _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            } catch (SQLException e) {
                _separator = "."; //$NON-NLS-1$
                ISpherePlugin.logError("*** Message file search (1): Could not get JDBC meta data. Using '.' as SQL separator ***", e);
            }

            PreparedStatement preparedStatementSelect = null;
            ResultSet resultSet = null;
            try {

                preparedStatementSelect = _jdbcConnection.prepareStatement("SELECT XSCNT, XSCNL FROM " + iSphereLibrary + _separator
                    + "XFNDSTRS WHERE XSHDL = ?");
                preparedStatementSelect.setInt(1, _handle);
                resultSet = preparedStatementSelect.executeQuery();
                if (resultSet.next()) {
                    _counter = resultSet.getInt("XSCNT");
                } else {
                    ISpherePlugin.logError("*** Message file search: Could not read status record (" + _handle + ") from file XFNDSTRS ***", error);
                    monitor.setCanceled(true);
                    MessageDialogAsync.displayError(Messages.bind(Messages.Could_not_read_status_from_file_B_A_for_search_job_handle_C, new Object[] {
                        "XFNDSTRS", iSphereLibrary, new Integer(_handle) }));
                }
            } catch (SQLException e) {
                error = e;
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    error = e1;
                }
            }

            if (preparedStatementSelect != null) {
                try {
                    preparedStatementSelect.close();
                } catch (SQLException e1) {
                    error = e1;
                }
            }

            if (error != null) {
                monitor.setCanceled(true);
                MessageDialogAsync.displayError(ExceptionHelper.getLocalizedMessage(error));
                ISpherePlugin.logError("*** Message file search: Unexpected connection error. ***", error);
            }

        }

        private void cancelJob() {

            String _separator;
            try {
                _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            } catch (SQLException e) {
                _separator = ".";
                ISpherePlugin.logError("*** Message file search (3): Could not get JDBC meta data. Using '.' as SQL separator ***", e);
            }

            PreparedStatement preparedStatementUpdate = null;
            try {
                preparedStatementUpdate = _jdbcConnection.prepareStatement("UPDATE " + iSphereLibrary + _separator
                    + "XFNDSTRS SET XSCNL = '*YES' WHERE XSHDL = ?");
                preparedStatementUpdate.setInt(1, _handle);
                preparedStatementUpdate.executeUpdate();
            } catch (SQLException e) {
                ISpherePlugin.logError("*** Could not cancel host job of message file search ***", e);
            }
            if (preparedStatementUpdate != null) {
                try {
                    preparedStatementUpdate.close();
                } catch (SQLException e1) {
                }
            }

        }

    }

    private class DoSearch extends Thread {

        private AS400 _as400;
        private int _handle;
        private SearchOptions _searchOptions;

        public DoSearch(AS400 _as400, int _handle, SearchOptions _searchOptions) {
            this._as400 = _as400;
            this._handle = _handle;
            this._searchOptions = _searchOptions;
        }

        @Override
        public void run() {
            new XFNDSTR_search().run(_as400, _handle, _searchOptions);
        }

    }

    private SearchResult[] _searchResults;

    @CMOne(info = "This method is used by CMOne")
    public SearchResult[] executeJoin(AS400 _as400, String _hostName, Connection _jdbcConnection, SearchOptions _searchOptions,
        ArrayList<SearchElement> _searchElements) {

        Search search = new Search(_as400, _hostName, _jdbcConnection, _searchOptions, _searchElements);
        search.setUser(true);
        search.schedule();

        try {
            search.join();
        } catch (InterruptedException e) {
        }

        if (_searchResults == null) {
            return new SearchResult[0];
        } else {
            return _searchResults;
        }

    }

    public void execute(AS400 _as400, String _connectionName, Connection _jdbcConnection, SearchOptions searchOptions,
        ArrayList<SearchElement> _searchElements, ISearchPostRun _searchPostRun) {

        Search search = new Search(_as400, _connectionName, _jdbcConnection, searchOptions, _searchElements, _searchPostRun);
        search.setUser(true);
        search.schedule();

    }

}
