/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.annotations.CMOne;

@CMOne(info = "Be careful, when changing this class! Also test CMOne stream file search.")
public class SearchElement implements Comparable<SearchElement> {

    private String directory;
    private String streamFile;
    private String type;
    private Object data;

    public SearchElement() {
        directory = "";
        streamFile = "";
        type = "";
        data = null;
    }

    @Deprecated
    @CMOne(info = "Deprecated but required for compiling CMOne.")
    public void setLastChangedDate(Date lastChangedDate) {
        // throw new
        // IllegalAccessError("Don't call setLastChangedDate()! This method has
        // become obsolete with rev. 6056.");
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getStreamFile() {
        return streamFile;
    }

    public void setStreamFile(String streamFile) {
        this.streamFile = streamFile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static void setSearchElements(String iSphereLibrary, Connection jdbcConnection, int handle, ArrayList<SearchElement> _searchElements) {

        // String _separator;
        // try {
        // _separator = jdbcConnection.getMetaData().getCatalogSeparator();
        // } catch (SQLException e) {
        // _separator = ".";
        // ISpherePlugin.logError("*** Stream file search, setSearchElements():
        // Could not get JDBC meta data. Using '.' as SQL separator ***",
        // e);
        // }

        SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

        if (_searchElements.size() > 0) {

            int _start;
            int _end;
            int _elements = 100;

            _start = 1;

            do {

                _end = _start + _elements - 1;

                if (_end > _searchElements.size()) {
                    _end = _searchElements.size();
                }

                StringBuffer sqlInsert = new StringBuffer();
                sqlInsert.append("INSERT INTO " + sqlHelper.getObjectName(iSphereLibrary, "ZFNDSTRI") + " (XIHDL, XIDIR, XISTMF) VALUES");
                boolean first = true;

                for (int idx = _start - 1; idx <= _end - 1; idx++) {

                    if (first) {
                        first = false;
                        sqlInsert.append(" ");
                    } else {
                        sqlInsert.append(", ");
                    }

                    //@formatter:off
                    sqlInsert.append("(");
                    sqlInsert.append(
                        "'" + Integer.toString(handle) + "', " + 
                        "'" + _searchElements.get(idx).getDirectory() + "', " + 
                        "'" + _searchElements.get(idx).getStreamFile() + "'");
                    sqlInsert.append(")");
                    //@formatter:on
                }

                String _sqlInsert = sqlInsert.toString();

                Statement statementInsert = null;

                try {
                    statementInsert = jdbcConnection.createStatement();
                    statementInsert.executeUpdate(_sqlInsert);
                } catch (SQLException e) {
                    ISpherePlugin.logError("*** Stream file search, setSearchElements(): Could not insert search elements into ZFNDSTRI ***", e);
                }

                if (statementInsert != null) {
                    try {
                        statementInsert.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                _start = _start + _elements;

            } while (_end < _searchElements.size());

        }

    }

    @Override
    public String toString() {
        return String.format("%s/%s", getDirectory(), getStreamFile());
    }

    public int compareTo(SearchElement other) {
        if (other == null) {
            return 1;
        } else {
            int rc = getDirectory().compareTo(other.getDirectory());
            if (rc != 0) {
                return rc;
            } else {
                rc = getStreamFile().compareTo(other.getStreamFile());
                if (rc != 0) {
                    return rc;
                } else {
                    return getType().compareTo(other.getType());
                }
            }
        }
    }

}
