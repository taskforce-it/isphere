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

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;

public class SearchResult {

    private AS400 as400;
    private String host;
    private String library;
    private String messageFile;
    private String description;
    private SearchResultMessageId[] messageIds;

    public SearchResult() {
        as400 = null;
        host = "";
        library = "";
        messageFile = "";
        description = "";
        messageIds = null;
    }

    public AS400 getAS400() {
        return as400;
    }

    public void setAS400(AS400 as400) {
        this.as400 = as400;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public static SearchResult[] getSearchResults(Connection jdbcConnection, int handle, AS400 as400, String host) {

        String _separator;
        try {
            _separator = jdbcConnection.getMetaData().getCatalogSeparator();
        } catch (SQLException e) {
            _separator = ".";
            e.printStackTrace();
        }

        ArrayList<SearchResult> arrayListSearchResults = new ArrayList<SearchResult>();

        PreparedStatement preparedStatementSelect = null;
        ResultSet resultSet = null;

        try {

            preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + ISpherePlugin.getISphereLibrary() + _separator
                + "XFNDSTRO WHERE XOHDL = ? ORDER BY XOHDL, XOLIB, XOMSGF", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
                    _searchResult.setAS400(as400);
                    _searchResult.setHost(host);
                    _searchResult.setLibrary(library);
                    _searchResult.setMessageFile(messageFile);

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
