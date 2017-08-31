/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import biz.isphere.journalexplorer.core.api.retrievefielddescription.IQDBRTVFD;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaTable;

public class MetaTableDAO extends DAOBase {

    public MetaTableDAO(String connectionName) throws Exception {
        super(connectionName);
    }

    public void retrieveColumnsMetaData(MetaTable metaTable) throws Exception {

        ResultSet resultSet = null;
        PreparedStatement sqlStatement = null;

        try {

            IQDBRTVFD iqdbrtvfd = new IQDBRTVFD(getSystem(), getConnectionName());
            iqdbrtvfd.setFile(metaTable.getDefinitionName(), metaTable.getDefinitionLibrary());
            MetaColumn[] metaColumns = iqdbrtvfd.retrieveFieldDescriptions();

            for (MetaColumn metaColumn : metaColumns) {
                metaTable.addColumn(metaColumn);
            }

        } finally {

            super.destroy(resultSet);
            super.destroy(sqlStatement);
        }
    }
}
