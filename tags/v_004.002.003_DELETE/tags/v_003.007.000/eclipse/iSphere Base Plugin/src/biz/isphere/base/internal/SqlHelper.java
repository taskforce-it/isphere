/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.sql.Connection;
import java.sql.SQLException;

public final class SqlHelper {

    private static final String DEFAULT_CATALOG_SEPARATOR = "."; //$NON-NLS-1$
    private static final String DEFAULT_NAME_QUOTES = "\""; //$NON-NLS-1$

    private Connection jdbcConnection;
    private String catalogSeparator;
    private String nameQuotes;

    public SqlHelper(Connection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
        this.catalogSeparator = getCatalogSeparator();
        this.nameQuotes = getIdentifierQuoteString();
    }

    public String getObjectName(String library, String object) {
        StringBuilder qualifiedName = new StringBuilder();
        appendQuotedName(qualifiedName, library);
        qualifiedName.append(catalogSeparator);
        appendQuotedName(qualifiedName, object);
        return qualifiedName.toString();
    }

    private String appendQuotedName(StringBuilder buffer, String name) {
        buffer.append(nameQuotes);
        buffer.append(name);
        buffer.append(nameQuotes);
        return buffer.toString();
    }

    private String getCatalogSeparator() {
        try {
            return jdbcConnection.getMetaData().getCatalogSeparator();
        } catch (SQLException e) {
            return DEFAULT_CATALOG_SEPARATOR;
        }
    }

    private String getIdentifierQuoteString() {
        try {
            return jdbcConnection.getMetaData().getIdentifierQuoteString();
        } catch (SQLException e) {
            return DEFAULT_NAME_QUOTES;
        }
    }
}
