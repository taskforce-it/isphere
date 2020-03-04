package org.bac.gati.tools.journalexplorer.model;

import java.util.HashMap;

import org.bac.gati.tools.journalexplorer.internals.QualifiedName;
import org.bac.gati.tools.journalexplorer.model.dao.MetaTableDAO;

public class MetaDataCache {

	public static final MetaDataCache INSTANCE = new MetaDataCache();
	
	private HashMap<String, MetaTable> cache;
	
	public MetaDataCache() {
		this.cache = new HashMap<String, MetaTable>();
	}
	
	public void saveMetaData(MetaTable metaTable) {
		this.cache.put(QualifiedName.getName(metaTable.getLibrary(), metaTable.getName()), metaTable);
	}
	
	public MetaTable retrieveMetaData(Journal journal) throws Exception {
		
		MetaTable metatable;
		String key = QualifiedName.getName(journal.getObjectLibrary(), journal.getObjectName());
		
		metatable = this.cache.get(key);
		
		if (metatable == null) {
			metatable = new MetaTable(journal.getObjectName(), journal.getObjectLibrary());
			this.saveMetaData(metatable);
			this.loadMetadata(metatable, journal);
			
		} else if (!metatable.isLoaded()) {
			metatable.clearColumns();
			this.loadMetadata(metatable, journal);
		} 
		
		return metatable;
	}
	
	private void loadMetadata(MetaTable metaTable, Journal journal) throws Exception {
		
		MetaTableDAO metaTableDAO = new MetaTableDAO(journal.connection);
		
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
