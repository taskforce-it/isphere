/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.helpers.TimeTaken;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.MetaTableDAO;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;

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

    public MetaTable retrieveMetaData(OutputFile outputFile) throws Exception {
        return loadMetadata(outputFile.getConnectionName(), outputFile.getLibraryName(), outputFile.getFileName(), ISeries.FILE);
    }

    public MetaTable retrieveMetaData(String connectionName, String library, String file, String objectType) throws Exception {
        return loadMetadata(connectionName, library, file, objectType);
    }

    public MetaTable retrieveMetaData(JournalEntry journalEntry) throws Exception {
        return loadMetadata(journalEntry.getConnectionName(), journalEntry.getObjectLibrary(), journalEntry.getObjectName(),
            journalEntry.getObjectType());
    }

    private synchronized MetaTable loadMetadata(String connectionName, String objectLibrary, String objectName, String objectType) throws Exception {

        String key = produceKey(connectionName, objectLibrary, objectName, objectType);
        MetaTable metatable = this.cache.get(key);

        if (metatable == null) {
            metatable = produceMetaTable(connectionName, objectLibrary, objectName, objectType);
            this.saveMetaData(metatable);
            this.loadMetadata(metatable, getMetaTableDAO(connectionName));
        } else if (!metatable.isLoaded()) {
            metatable.clearColumns();
            this.loadMetadata(metatable, getMetaTableDAO(connectionName));
        }

        return metatable;
    }

    private MetaTableDAO getMetaTableDAO(String connectionName) throws Exception {
        return new MetaTableDAO(connectionName);
    }

    private MetaTable produceMetaTable(JournalEntry journalEntry) {
        return produceMetaTable(journalEntry.getConnectionName(), journalEntry.getObjectLibrary(), journalEntry.getObjectName(),
            journalEntry.getObjectType());
    }

    private MetaTable produceMetaTable(String connectionName, String objectLibrary, String objectName, String objectType) {
        return new MetaTable(connectionName, objectName, objectLibrary, objectType);
    }

    private String produceKey(MetaTable metaTable) {
        return produceKey(metaTable.getConnectionName(), metaTable.getLibrary(), metaTable.getName(), metaTable.getObjectType());
    }

    private String produceKey(JournalEntry journalEntry) {
        return produceKey(journalEntry.getConnectionName(), journalEntry.getObjectLibrary(), journalEntry.getObjectName(),
            journalEntry.getObjectType());
    }

    private String produceKey(String connectionName, String objectLibrary, String objectName, String objectType) {
        objectType = convertToWellKnownType(objectType);
        return new QualifiedName(connectionName, objectLibrary, objectName).getQualifiedName() + " [" + objectType + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String convertToWellKnownType(String objectType) {

        String wellKnownType;
        if ("*QDDS".equals(objectType)) { //$NON-NLS-1$
            wellKnownType = ISeries.FILE;
        } else {
            wellKnownType = objectType;
        }

        return wellKnownType;
    }

    private void loadMetadata(MetaTable metaTable, MetaTableDAO metaTableDAO) throws Exception {

        try {

            TimeTaken timeTaken = TimeTaken.start("Loading meta table " + metaTable.getName()); // //$NON-NLS-1$

            metaTableDAO.retrieveColumnsMetaData(metaTable);
            if (metaTable.hasColumns()) {
                metaTable.setLoaded(true);

            } else {
                metaTable.setLoaded(false);
            }

            timeTaken.stop();

        } catch (Exception exception) {
            metaTable.setLoaded(false);
            throw exception;
        }
    }

    private void saveMetaData(MetaTable metaTable) {
        this.cache.put(produceKey(metaTable), metaTable);
    }

    public void removeMetaData(MetaTable metaTable) {
        this.cache.remove(produceKey(metaTable));
    }

    public Collection<MetaTable> getCachedParsers() {
        return this.cache.values();
    }

    public void preloadTables(JournaledFile[] files) {

        try {

            TimeTaken timeTaken = TimeTaken.start("Pre-loading meta data"); // //$NON-NLS-1$

            for (JournaledFile file : files) {
                String connectionName = file.getConnectionName();
                String fileName = file.getFileName();
                String libraryName = file.getLibraryName();
                retrieveMetaData(connectionName, libraryName, fileName, ISeries.FILE);
            }

            timeTaken.stop();

        } catch (Exception e) {
            MessageDialogAsync.displayError(Messages.Status_Loading_meta_data, ExceptionHelper.getLocalizedMessage(e));
        }

    }
}
