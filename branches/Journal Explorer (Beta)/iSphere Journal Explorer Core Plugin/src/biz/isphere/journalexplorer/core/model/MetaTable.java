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

import java.util.LinkedList;
import java.util.List;

import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;

/**
 * This class represents the metatada of a table. It contains the name and
 * library of the table and a list of its fields. Also it contains the name and
 * library of the table used to retrieve its structure. Most of the time the
 * attributes name and library will be equal to definitionName and
 * definitionLibrary, but this allows to override the table and library from
 * used as reference to retrieve the metadata. This can be useful if the
 * programmer wants to parse a table row with a different structure Specifying a
 * different definitionName and definitionLibrary than name and library, can
 * generate unexpected results, use with caution
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

    private boolean isJournalOutputFile;

    public MetaTable(String name, String library) {

        this.columns = new LinkedList<MetaColumn>();
        this.name = this.definitionName = name.trim();
        this.library = this.definitionLibrary = library.trim();
        this.loaded = false;
        this.parsingOffset = 0;
    }

    public boolean isJournalOutputFile() {
        return isJournalOutputFile;
    }

    public void setJournalOutputFile(boolean isHidden) {
        this.isJournalOutputFile = isHidden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library.trim();
    }

    public LinkedList<MetaColumn> getColumns() {
        return columns;
    }

    public void setColumns(LinkedList<MetaColumn> columns) {
        this.columns = columns;
    }

    public void setDefinitionLibrary(String definitionLibrary) {
        this.definitionLibrary = definitionLibrary.trim();
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName.trim();
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

    public boolean hasColumn(String columnName) {

        List<MetaColumn> metaColumns = getColumns();
        for (MetaColumn metaColumn : metaColumns) {
            if (metaColumn.getName().equals(columnName.trim())) {
                return true;
            }
        }

        return false;
    }

    public String getQualifiedName() {
        return QualifiedName.getName(getLibrary(), getName());
    }

    public int getOutfileType() {

        if (hasColumn("JOPGMLIB")) { // Added with *TYPE5
            return JournalOutputType.TYPE5;
        } else if (hasColumn("JOJID")) { // Added with *TYPE4
            return JournalOutputType.TYPE5;
        } else if (hasColumn("JOTSTP")) { // Added with *TYPE3
            return JournalOutputType.TYPE3;
        } else if (hasColumn("JOUSPF")) { // Added with *TYPE2
            return JournalOutputType.TYPE2;
        } else {
            return JournalOutputType.TYPE1;
        }
    }
}
