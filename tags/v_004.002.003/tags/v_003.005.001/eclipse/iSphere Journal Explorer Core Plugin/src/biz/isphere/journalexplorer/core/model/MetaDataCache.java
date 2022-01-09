/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.Collection;
import java.util.HashMap;

import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.MetaTableDAO;

public final class MetaDataCache {

    /**
     * The instance of this Singleton class.
     */
    private static MetaDataCache instance;

    private HashMap<String, MetaTable> cache;

    private MetaDataCache() {
        this.cache = new HashMap<String, MetaTable>();
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static MetaDataCache getInstance() {
        if (instance == null) {
            instance = new MetaDataCache();
        }
        return instance;
    }

    public void prepareMetaData(JournalEntry journalEntry) throws Exception {

        String key = produceKey(journalEntry);
        if (!this.cache.containsKey(key)) {
            saveMetaData(produceMetaTable(journalEntry));
        }
    }

    public MetaTable retrieveMetaData(OutputFile file) throws Exception {
        return loadMetadata(file.getConnectionName(), file.getOutFileLibrary(), file.getOutFileName());
    }

    public MetaTable retrieveMetaData(String connectionName, String library, String file) throws Exception {
        return loadMetadata(connectionName, library, file);
    }

    public MetaTable retrieveMetaData(JournalEntry journalEntry) throws Exception {
        return loadMetadata(journalEntry.getConnectionName(), journalEntry.getObjectLibrary(), journalEntry.getObjectName());
    }

    private MetaTable loadMetadata(String connectionName, String objectLibrary, String objectName) throws Exception {

        String key = produceKey(objectLibrary, objectName);
        MetaTable metatable = this.cache.get(key);

        if (metatable == null) {
            metatable = produceMetaTable(objectLibrary, objectName);
            this.saveMetaData(metatable);
            this.loadMetadata(metatable, connectionName);
        } else if (!metatable.isLoaded()) {
            metatable.clearColumns();
            this.loadMetadata(metatable, connectionName);
        }

        return metatable;
    }

    private MetaTable produceMetaTable(JournalEntry journalEntry) {
        return produceMetaTable(journalEntry.getObjectLibrary(), journalEntry.getObjectName());
    }

    private MetaTable produceMetaTable(String objectLibrary, String objectName) {
        return new MetaTable(objectName, objectLibrary);
    }

    private String produceKey(JournalEntry journalEntry) {
        return QualifiedName.getName(journalEntry.getObjectLibrary(), journalEntry.getObjectName());
    }

    private String produceKey(String objectLibrary, String objectName) {
        return QualifiedName.getName(objectLibrary, objectName);
    }

    private void loadMetadata(MetaTable metaTable, String connectionName) throws Exception {

        MetaTableDAO metaTableDAO = new MetaTableDAO(connectionName);

        try {

            metaTableDAO.retrieveColumnsMetaData(metaTable);
            if (metaTable.hasColumns()) {
                metaTable.setLoaded(true);
            } else {
                metaTable.setLoaded(false);
            }

        } catch (Exception exception) {
            metaTable.setLoaded(false);
            throw exception;
        }
    }

    private void saveMetaData(MetaTable metaTable) {
        this.cache.put(QualifiedName.getName(metaTable.getLibrary(), metaTable.getName()), metaTable);
    }

    public void removeMetaData(MetaTable metaTable) {
        this.cache.remove(QualifiedName.getName(metaTable.getLibrary(), metaTable.getName()));
    }

    public Collection<MetaTable> getCachedParsers() {
        return this.cache.values();
    }
}
