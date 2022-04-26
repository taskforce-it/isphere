/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

import com.ibm.as400.access.AS400;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SearchResult {

    private String connectionName;
    private String hostName;
    private String library;
    private String messageFile;
    private String description;
    private SearchResultMessageId[] messageIds;

    @XStreamOmitField
    private AS400 as400;

    public SearchResult() {
        connectionName = "";
        hostName = "";
        library = "";
        messageFile = "";
        description = "";
        messageIds = null;

        as400 = null;
    }

    public AS400 getAS400() {

        if (as400 == null) {
            as400 = IBMiHostContributionsHandler.getSystem(connectionName);
        }

        return as400;
    }

    private void setAS400(AS400 as400) {
        this.as400 = as400;
    }

    /**
     * This method returns the name of the host, where the search has been
     * executed.
     * <p>
     * This method is used by CMOne and must not be used by the iSphere plug-in.
     * 
     * @return host name
     */
    @CMOne(info = "This method is used by CMOne and must not be used by the iSphere plug-in.")
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the host name, where the search has been executed.
     * <p>
     * This method is for compatibility to CMOne.
     * 
     * @param hostName
     */
    @CMOne(info = "This method is for compatibility to CMOne.")
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getMessageFile() {
        return messageFile;
    }

    public void setMessageFile(String messageFile) {
        this.messageFile = messageFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchResultMessageId[] getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(SearchResultMessageId[] messageIds) {
        this.messageIds = messageIds;
    }

    public static SearchResult[] getSearchResults(String iSphereLibrary, Connection jdbcConnection, int handle, AS400 as400, String connectionName,
        String hostName) {

        // String _separator;
        // try {
        // _separator = jdbcConnection.getMetaData().getCatalogSeparator();
        // } catch (SQLException e) {
        // _separator = ".";
        // e.printStackTrace();
        // }

        SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

        ArrayList<SearchResult> arrayListSearchResults = new ArrayList<SearchResult>();

        PreparedStatement preparedStatementSelect = null;
        ResultSet resultSet = null;

        try {

            preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + sqlHelper.getObjectName(iSphereLibrary, "XFNDSTRO")
                + " WHERE XOHDL = ? ORDER BY XOHDL, XOLIB, XOMSGF", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatementSelect.setString(1, Integer.toString(handle));
            resultSet = preparedStatementSelect.executeQuery();

            String _library = "";
            String _messageFile = "";

            String library;
            String messageFile;

            SearchResult _searchResult = null;
            ArrayList<SearchResultMessageId> alMessageIds = null;

            while (resultSet.next()) {

                library = resultSet.getString("XOLIB").trim();
                messageFile = resultSet.getString("XOMSGF").trim();

                if (!_library.equals(library) || !_messageFile.equals(messageFile)) {

                    if (_searchResult != null) {

                        SearchResultMessageId[] _messageIds = new SearchResultMessageId[alMessageIds.size()];
                        alMessageIds.toArray(_messageIds);

                        _searchResult.setMessageIds(_messageIds);

                        arrayListSearchResults.add(_searchResult);

                    }

                    _library = library;
                    _messageFile = messageFile;

                    _searchResult = new SearchResult();
                    _searchResult.setConnectionName(connectionName);
                    _searchResult.setHostName(hostName); // CMOne compatibility
                    _searchResult.setLibrary(library);
                    _searchResult.setMessageFile(messageFile);

                    _searchResult.setAS400(as400);

                    alMessageIds = new ArrayList<SearchResultMessageId>();

                }

                SearchResultMessageId messageId = new SearchResultMessageId();
                messageId.setMessageId(resultSet.getString("XOMSGID"));
                messageId.setMessage(resultSet.getString("XOMSG"));
                alMessageIds.add(messageId);

            }

            if (_searchResult != null) {

                SearchResultMessageId[] _messageIds = new SearchResultMessageId[alMessageIds.size()];
                alMessageIds.toArray(_messageIds);

                _searchResult.setMessageIds(_messageIds);

                arrayListSearchResults.add(_searchResult);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (preparedStatementSelect != null) {
            try {
                preparedStatementSelect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        SearchResult[] _searchResults = new SearchResult[arrayListSearchResults.size()];
        arrayListSearchResults.toArray(_searchResults);
        return _searchResults;

    }

}
