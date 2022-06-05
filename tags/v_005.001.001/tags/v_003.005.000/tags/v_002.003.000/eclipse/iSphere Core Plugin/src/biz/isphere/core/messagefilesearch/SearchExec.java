/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptions;

import com.ibm.as400.access.AS400;

public class SearchExec {

    public static final String CASE_MATCH = "*MATCH";
    public static final String CASE_IGNORE = "*IGNORE";
    public static final String INCLUDE_FIRST_LEVEL_TEXT = "INCLUDE_FIRST_LEVEL_TEXT";
    public static final String INCLUDE_SECOND_LEVEL_TEXT = "INCLUDE_SECOND_LEVEL_TEXT";

    private class Search extends Job {

        private AS400 _as400;
        private String _host;
        private Connection _jdbcConnection;
        private SearchOptions _searchOptions;
        private ArrayList<SearchElement> _searchElements;
        private ISearchPostRun _searchPostRun;
        private int _handle;
        private int _counter;
        private String iSphereLibrary;

        public Search(AS400 _as400, String _host, Connection _jdbcConnection, SearchOptions _searchOptions, ArrayList<SearchElement> _searchElements,
            ISearchPostRun _searchPostRun) {

            super("iSphere Message File Search");

            this._as400 = _as400;
            this._host = _host;
            this._jdbcConnection = _jdbcConnection;
            this._searchOptions = _searchOptions;
            this._searchElements = _searchElements;
            this._searchPostRun = _searchPostRun;

            iSphereLibrary = ISpherePlugin.getISphereLibrary();

        }

        @Override
        public IStatus run(IProgressMonitor monitor) {

            IStatus _status = Status.OK_STATUS;

            String currentLibrary = null;
            try {
                currentLibrary = ISphereHelper.getCurrentLibrary(_as400);
            } catch (Exception e) {
            }

            if (currentLibrary != null) {

                boolean ok = false;
                try {
                    ok = ISphereHelper.setCurrentLibrary(_as400, iSphereLibrary);
                } catch (Exception e1) {
                }

                if (ok) {

                    _handle = new XFNDSTR_getHandle().run(_as400);

                    if (_handle > 0) {

                        SearchElement.setSearchElements(_jdbcConnection, _handle, _searchElements);

                        int _numberOfSearchElements = new XFNDSTR_getNumberOfSearchElements().run(_as400, _handle);

                        monitor.beginTask("Searching", _numberOfSearchElements);

                        new DoSearch(_as400, _handle, _searchOptions).start();

                        int _lastCounter = 0;

                        getStatus();

                        boolean _cancel = false;

                        while (_counter != -1) {

                            monitor.worked(_counter - _lastCounter);

                            _lastCounter = _counter;

                            if (monitor.isCanceled()) {
                                _cancel = true;
                                cancelJob();
                                _status = Status.CANCEL_STATUS;
                                break;
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            getStatus();

                        }

                        monitor.worked(_numberOfSearchElements - _lastCounter);

                        monitor.done();

                        if (!_cancel) {
                            _searchResults = SearchResult.getSearchResults(_jdbcConnection, _handle, _as400, _host);
                        }

                        new XFNDSTR_clear().run(_as400, _handle);

                    }

                    try {
                        ISphereHelper.setCurrentLibrary(_as400, currentLibrary);
                    } catch (Exception e) {
                    }

                }

            }

            if (_searchPostRun != null) {
                _searchPostRun.run(_searchResults);
            }

            return _status;

        }

        private void getStatus() {

            String _separator;
            try {
                _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            } catch (SQLException e) {
                _separator = ".";
                e.printStackTrace();
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
                }
            } catch (SQLException e) {
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                }
            }
            if (preparedStatementSelect != null) {
                try {
                    preparedStatementSelect.close();
                } catch (SQLException e1) {
                }
            }

        }

        private void cancelJob() {

            String _separator;
            try {
                _separator = _jdbcConnection.getMetaData().getCatalogSeparator();
            } catch (SQLException e) {
                _separator = ".";
                e.printStackTrace();
            }

            PreparedStatement preparedStatementUpdate = null;
            try {
                preparedStatementUpdate = _jdbcConnection.prepareStatement("UPDATE " + iSphereLibrary + _separator
                    + "XFNDSTRS SET XSCNL = '*YES' WHERE XSHDL = ?");
                preparedStatementUpdate.setInt(1, _handle);
                preparedStatementUpdate.executeUpdate();
            } catch (SQLException e) {
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

    public SearchResult[] execute(AS400 _as400, String _host, Connection _jdbcConnection, String _string, int _fromColumn, int _toColumn,
        String _case, ArrayList<SearchElement> _searchElements) {

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.addSearchArgument(new SearchArgument(_string, _fromColumn, _toColumn, _case));

        Search search = new Search(_as400, _host, _jdbcConnection, searchOptions, _searchElements, null);
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

    public void execute(AS400 _as400, String _host, Connection _jdbcConnection, String _string, int _fromColumn, int _toColumn, String _case,
        ArrayList<SearchElement> _searchElements, ISearchPostRun _searchPostRun) {

        SearchOptions searchOptions = new SearchOptions(true, true);
        searchOptions.addSearchArgument(new SearchArgument(_string, _fromColumn, _toColumn, _case));

        Search search = new Search(_as400, _host, _jdbcConnection, searchOptions, _searchElements, _searchPostRun);
        search.setUser(true);
        search.schedule();

    }

    public void execute(AS400 _as400, String _host, Connection _jdbcConnection, SearchOptions searchOptions,
        ArrayList<SearchElement> _searchElements, ISearchPostRun _searchPostRun) {

        Search search = new Search(_as400, _host, _jdbcConnection, searchOptions, _searchElements, _searchPostRun);
        search.setUser(true);
        search.schedule();

    }

}
