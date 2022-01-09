/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.as400.access.AS400;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.SqlHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.search.SearchOptions;

public class SearchExec {

    private class Search extends Job {

        private AS400 _as400;
        private Connection _jdbcConnection;
        private SearchOptions _searchOptions;
        private ArrayList<SearchElement> _searchElements;
        private ISearchPostRun _searchPostRun;
        private int _handle;
        private int _counter;
        private String iSphereLibrary;

        @CMOne(info = "This constructor is used by CMOne")
        public Search(AS400 _as400, Connection _jdbcConnection, SearchOptions _searchOptions, ArrayList<SearchElement> _searchElements,
            ISearchPostRun _searchPostRun) {

            super(Messages.iSphere_Source_File_Search);

            this._as400 = _as400;
            this._jdbcConnection = _jdbcConnection;
            this._searchOptions = _searchOptions;
            this._searchElements = _searchElements;
            this._searchPostRun = _searchPostRun;

            iSphereLibrary = ISpherePlugin.getISphereLibrary(); // CHECKED

        }

        public Search(String connectionName, Connection _jdbcConnection, SearchOptions _searchOptions, ArrayList<SearchElement> _searchElements,
            ISearchPostRun _searchPostRun) {

            super(Messages.iSphere_Source_File_Search);

            this._as400 = IBMiHostContributionsHandler.getSystem(connectionName);
            this._jdbcConnection = _jdbcConnection;
            this._searchOptions = _searchOptions;
            this._searchElements = _searchElements;
            this._searchPostRun = _searchPostRun;

            iSphereLibrary = ISpherePlugin.getISphereLibrary(connectionName);

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
                        // FNDSTRS.
                        _handle = new FNDSTR_getHandle().run(_as400);

                        if (_handle > 0) {

                            // Append search elements to FNDSTRI.
                            SearchElement.setSearchElements(iSphereLibrary, _jdbcConnection, _handle, _searchElements);

                            // Expand generic search elements in FNDSTRI.
                            new FNDSTR_resolveGenericSearchElements().run(_as400, _handle);

                            int _numberOfSearchElements = new FNDSTR_getNumberOfSearchElements().run(_as400, _handle);

                            monitor.beginTask(Messages.Searching, _numberOfSearchElements); //$NON-NLS-1$

                            // Start the search job on the host.
                            new DoSearch(_as400, _handle, _searchOptions, monitor).start();

                            int _lastCounter = 0;

                            // Wait for the end of the search job.
                            // Read FNDSTRS to update '_counter'
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
                                _searchResults = getSearchResults(iSphereLibrary, _jdbcConnection, _handle);
                            }

                            new FNDSTR_clear().run(_as400, _handle);

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

        private SearchResult[] getSearchResults(String iSphereLibrary, Connection jdbcConnection, int handle) {

            // String _separator;
            // try {
            // _separator = jdbcConnection.getMetaData().getCatalogSeparator();
            // } catch (SQLException e) {
            //                _separator = "."; //$NON-NLS-1$
            // ISpherePlugin.logError("*** Source file search (1): Could not get JDBC meta data. Using '.' as SQL separator ***",
            // e);
            // }

            SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

            ArrayList<SearchResult> arrayListSearchResults = new ArrayList<SearchResult>();

            PreparedStatement preparedStatementSelect = null;
            ResultSet resultSet = null;

            try {

                preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + sqlHelper.getObjectName(iSphereLibrary, "FNDSTRO")
                    + " WHERE XOHDL = ? ORDER BY XOHDL, XOLIB, XOFILE, XOMBR, XOFLCD", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                preparedStatementSelect.setString(1, Integer.toString(handle));
                resultSet = preparedStatementSelect.executeQuery();

                String _library = ""; //$NON-NLS-1$
                String _file = ""; //$NON-NLS-1$
                String _member = ""; //$NON-NLS-1$

                String library;
                String file;
                String member;
                String srcType;
                Timestamp lastChangedDate;

                SearchResult _searchResult = null;
                ArrayList<SearchResultStatement> alStatements = null;

                while (resultSet.next()) {

                    library = resultSet.getString("XOLIB").trim(); //$NON-NLS-1$
                    file = resultSet.getString("XOFILE").trim(); //$NON-NLS-1$
                    member = resultSet.getString("XOMBR").trim(); //$NON-NLS-1$
                    srcType = resultSet.getString("XOTYPE").trim(); //$NON-NLS-1$
                    lastChangedDate = resultSet.getTimestamp("XOFLCD"); //$NON-NLS-1$

                    if (!_library.equals(library) || !_file.equals(file) || !_member.equals(member)) {

                        if (_searchResult != null) {

                            SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
                            alStatements.toArray(_statements);

                            _searchResult.setStatements(_statements);

                            arrayListSearchResults.add(_searchResult);

                        }

                        _library = library;
                        _file = file;
                        _member = member;

                        _searchResult = new SearchResult();
                        _searchResult.setLibrary(library);
                        _searchResult.setFile(file);
                        _searchResult.setMember(member);
                        _searchResult.setSrcType(srcType);
                        _searchResult.setLastChangedDate(lastChangedDate);

                        alStatements = new ArrayList<SearchResultStatement>();

                    }

                    SearchResultStatement statement = new SearchResultStatement();
                    statement.setStatement(resultSet.getInt("XOSTMT")); //$NON-NLS-1$
                    statement.setLine(StringHelper.trimR(resultSet.getString("XOLINE"))); //$NON-NLS-1$
                    alStatements.add(statement);

                }

                if (_searchResult != null) {

                    SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
                    alStatements.toArray(_statements);

                    _searchResult.setStatements(_statements);

                    arrayListSearchResults.add(_searchResult);

                }

            } catch (SQLException e) {
                ISpherePlugin.logError("*** Could not load source file search result ***", e);
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    ISpherePlugin.logError("*** Could not close source file search result set ***", e);
                }
            }

            if (preparedStatementSelect != null) {
                try {
                    preparedStatementSelect.close();
                } catch (SQLException e) {
                    ISpherePlugin.logError("*** Could not close prepared statement of source file search ***", e);
                }
            }

            SearchResult[] _searchResults = new SearchResult[arrayListSearchResults.size()];
            arrayListSearchResults.toArray(_searchResults);
            return _searchResults;

        }

        private void getStatus(IProgressMonitor monitor) {

            Throwable error = null;

            // String _separator;
            // try {
            // _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            // } catch (SQLException e) {
            //                _separator = "."; //$NON-NLS-1$
            // ISpherePlugin.logError("*** Source file search (2): Could not get JDBC meta data. Using '.' as SQL separator ***",
            // e);
            // }

            SqlHelper sqlHelper = new SqlHelper(_jdbcConnection);

            PreparedStatement preparedStatementSelect = null;
            ResultSet resultSet = null;
            try {
                preparedStatementSelect = _jdbcConnection.prepareStatement("SELECT XSCNT, XSCNL FROM "
                    + sqlHelper.getObjectName(iSphereLibrary, "FNDSTRS") + " WHERE XSHDL = ?");
                preparedStatementSelect.setInt(1, _handle);
                resultSet = preparedStatementSelect.executeQuery();
                if (resultSet.next()) {
                    _counter = resultSet.getInt("XSCNT"); //$NON-NLS-1$
                } else {
                    ISpherePlugin.logError("*** Source file search: Could not read status record (" + _handle + ") from file FNDSTRS ***", error);
                    monitor.setCanceled(true);
                    MessageDialogAsync.displayError(Messages.bind(Messages.Could_not_read_status_from_file_B_A_for_search_job_handle_C, new Object[] {
                        "FNDSTRS", iSphereLibrary, new Integer(_handle) }));
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
                ISpherePlugin.logError("*** Source file search: Unexpected connection error. ***", error);
            }

        }

        private void cancelJob() {

            // String _separator;
            // try {
            // _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            // } catch (SQLException e) {
            //                _separator = "."; //$NON-NLS-1$
            // ISpherePlugin.logError("*** Source file search (3): Could not get JDBC meta data. Using '.' as SQL separator ***",
            // e);
            // }

            SqlHelper sqlHelper = new SqlHelper(_jdbcConnection);

            PreparedStatement preparedStatementUpdate = null;
            try {
                preparedStatementUpdate = _jdbcConnection.prepareStatement("UPDATE " + sqlHelper.getObjectName(iSphereLibrary, "FNDSTRS")
                    + " SET XSCNL = '*YES' WHERE XSHDL = ?");
                preparedStatementUpdate.setInt(1, _handle);
                preparedStatementUpdate.executeUpdate();
            } catch (SQLException e) {
                ISpherePlugin.logError("*** Could not cancel host job of source file search ***", e);
            }
            if (preparedStatementUpdate != null) {
                try {
                    preparedStatementUpdate.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

    private class DoSearch extends Thread {

        private AS400 _as400;
        private int _handle;
        private SearchOptions _searchOptions;
        private IProgressMonitor _monitor;

        public DoSearch(AS400 _as400, int _handle, SearchOptions _searchOptions, IProgressMonitor _monitor) {
            this._as400 = _as400;
            this._handle = _handle;
            this._searchOptions = _searchOptions;
            this._monitor = _monitor;
        }

        @Override
        public void run() {
            if (new FNDSTR_search().run(_as400, _handle, _searchOptions) < 0) {
                _monitor.setCanceled(true);
            }
        }

    }

    private SearchResult[] _searchResults = null;

    @CMOne(info = "This method is used by CMOne")
    public SearchResult[] executeJoin(AS400 _as400, Connection _jdbcConnection, SearchOptions _searchOptions, ArrayList<SearchElement> _searchElements) {

        Search search = new Search(_as400, _jdbcConnection, _searchOptions, _searchElements, null);
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

    public void execute(String connectionName, Connection _jdbcConnection, SearchOptions _searchOptions, ArrayList<SearchElement> _searchElements,
        ISearchPostRun _searchPostRun) {

        Search search = new Search(connectionName, _jdbcConnection, _searchOptions, _searchElements, _searchPostRun);
        search.setUser(true);
        search.schedule();

    }

}
