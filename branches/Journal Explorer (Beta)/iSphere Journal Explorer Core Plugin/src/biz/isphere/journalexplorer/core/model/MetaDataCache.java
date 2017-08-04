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

package biz.isphere.journalexplorer.core.model;

import java.util.HashMap;

import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.MetaTableDAO;

public class MetaDataCache {

    public static final MetaDataCache INSTANCE = new MetaDataCache();

    private HashMap<String, MetaTable> cache;

    public MetaDataCache() {
        this.cache = new HashMap<String, MetaTable>();
    }

    public void saveMetaData(MetaTable metaTable) {
        this.cache.put(QualifiedName.getName(metaTable.getLibrary(), metaTable.getName()), metaTable);
    }

    public MetaTable retrieveMetaData(File file) throws Exception {
        return loadMetadata(file.getConnectionName(), file.getOutFileLibrary(), file.getOutFileName());
    }

    public MetaTable retrieveMetaData(JournalEntry journal) throws Exception {
        return loadMetadata(journal.getConnectionName(), journal.getObjectLibrary(), journal.getObjectName());
    }

    private MetaTable loadMetadata(String connectionName, String objectLibrary, String objectName) throws Exception {

        String key = QualifiedName.getName(objectLibrary, objectName);
        MetaTable metatable = this.cache.get(key);

        if (metatable == null) {
            metatable = new MetaTable(objectName, objectLibrary);
            this.saveMetaData(metatable);
            this.loadMetadata(metatable, connectionName);

        } else if (!metatable.isLoaded()) {
            metatable.clearColumns();
            this.loadMetadata(metatable, connectionName);
        }

        return metatable;
    }

    private void loadMetadata(MetaTable metaTable, String connectionName) throws Exception {

        MetaTableDAO metaTableDAO = new MetaTableDAO(connectionName);

        try {
            metaTableDAO.retrieveColumnsMetaData(metaTable);
            metaTable.setLoaded(true);

        } catch (Exception exception) {
            metaTable.setLoaded(false);
            throw exception;
        }
    }

    public Object getCachedParsers() {
        return this.cache.values();
    }
}
