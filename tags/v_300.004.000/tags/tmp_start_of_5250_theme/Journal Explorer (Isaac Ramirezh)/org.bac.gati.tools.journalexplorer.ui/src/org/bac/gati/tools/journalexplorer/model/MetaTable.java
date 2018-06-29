package org.bac.gati.tools.journalexplorer.model;

import java.util.LinkedList;

/**
 * This class represents the metatada of a table. It contains
 * the name and library of the table and a list of its fields.
 * Also it contains the name and library of the table used to retrieve 
 * its structure. Most of the time the attributes name and library 
 * will be equal to definitionName and definitionLibrary, but this 
 * allows to override the table and library from used as reference 
 * to retrieve the metadata. This can be useful if the programmer
 * wants to parse a table row with a different structure
 * 
 * Specifying a different definitionName and definitionLibrary than
 * name and library, can generate unexpected results, use with 
 * caution
 *    
 * @author Isaac Ramirez Herrera
 */
public class MetaTable {
	
	private String name;
	
	private String library;
	
	private String definitionName;
	
	private String definitionLibrary;
	
	private LinkedList<MetaColumn> columns;
	
	private boolean loaded;
	
	private int parsingOffset;
	
	public MetaTable(String name, String library) {
		
		this.columns = new LinkedList<MetaColumn>();
		this.name	 = this.definitionName = name;
		this.library = this.definitionLibrary = library;
		this.loaded  = false;
		this.parsingOffset = 0;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public LinkedList<MetaColumn> getColumns() {
		return columns;
	}

	public void setColumns(LinkedList<MetaColumn> columns) {
		this.columns = columns;
	}
	
	public void setDefinitionLibrary(String definitionLibrary) {
		this.definitionLibrary = definitionLibrary;
	}
	
	public void setDefinitionName(String definitionName) {
		this.definitionName = definitionName;
	}
	
	public String getDefinitionLibrary() {
		return definitionLibrary;
	}
	
	public String getDefinitionName() {
		return definitionName;
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void clearColumns() {
		this.columns.clear();
	}
	
	public int getParsingOffset() {
		return parsingOffset;
	}
	
	public void setParsingOffset(int parsingOffset) {
		this.parsingOffset = parsingOffset;
	}
}
