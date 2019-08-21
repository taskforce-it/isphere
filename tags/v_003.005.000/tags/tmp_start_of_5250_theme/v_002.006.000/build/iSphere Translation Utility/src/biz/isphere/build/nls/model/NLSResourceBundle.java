/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import biz.isphere.build.nls.configuration.Configuration;
import biz.isphere.build.nls.exception.JobCanceledException;

/**
 * This class represents a language resource such as "plugin_*.properties". It
 * is the host of all language properties files that belong to the NLS resource.
 * 
 * @author Thomas Raddatz
 */
public class NLSResourceBundle {

    private String fRelativePath;
    private Map<String, NLSPropertiesFile> fNLSFiles;

    public NLSResourceBundle(String relativePath) {
        fRelativePath = relativePath;
        fNLSFiles = new HashMap<String, NLSPropertiesFile>();
    }

    /**
     * Returns the relative path of the resource bundle starting at the
     * project's root path.
     * 
     * @return Relative path of the resource bundle.
     */
    public String getRelativePath() {
        return fRelativePath;
    }

    /**
     * Adds a properties file this resource bundle.
     * 
     * @param nlsFile - Properties file that is added to the resource bundle.
     * @throws JobCanceledException
     */
    public void add(NLSPropertiesFile nlsFile) throws JobCanceledException {
        fNLSFiles.put(nlsFile.getKey(), nlsFile);
    }

    /**
     * Used by the exporter to generate the language headline.
     * 
     * @return list of language IDs.
     * @throws JobCanceledException
     */
    public Set<String> getLanguageKeys() throws JobCanceledException {
        Set<String> languageKeys = new TreeSet<String>();
        for (NLSPropertiesFile nlsFile : fNLSFiles.values()) {
            if (nlsFile.getLanguage().isProtected() || isSelectedForExport(nlsFile.getLanguage(), Configuration.getInstance().getExportLanguageIDs())) {
                languageKeys.add(nlsFile.getKey());
            }
        }
        return languageKeys;
    }

    /**
     * Used by the exporter to get the keys of the available NLS files.
     * 
     * @return The list of keys of the translated strings.
     */
    public String[] getKeys() {
        Set<String> keys = new HashSet<String>();
        for (NLSPropertiesFile nlsFile : fNLSFiles.values()) {
            keys.addAll(nlsFile.getKeys());
        }
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * Used by the exporter to generate the data rows.
     * 
     * @param key - Key of the translated string
     * @return Strings (one per language) that are associated to the key.
     * @throws JobCanceledException
     */
    public NLSTextEntry[] getValues(String key) throws JobCanceledException {
        List<NLSTextEntry> values = new ArrayList<NLSTextEntry>();
        for (String languageKey : getLanguageKeys()) {
            NLSPropertiesFile nlsFile = getNLSFile(languageKey);
            String value = nlsFile.getText(key);
            values.add(new NLSTextEntry(value, nlsFile.getLanguage()));
        }
        return values.toArray(new NLSTextEntry[values.size()]);
    }

    /**
     * Used by the exporter and the importer to get the properties file that is
     * associated to a given language key.
     * 
     * @param languageKey - Key, the properties file is associated with.
     * @return The associated properties file.
     */
    public NLSPropertiesFile getNLSFile(String languageKey) {
        return fNLSFiles.get(languageKey);
    }

    /**
     * Updates the properties files of this NLS resource bundle.
     * 
     * @param projectName - Name of the project, the resource bundle belongs to.
     * @throws JobCanceledException
     */
    public void updateFiles(String projectName) throws JobCanceledException {
        for (NLSPropertiesFile nlsFile : fNLSFiles.values()) {
            if (!nlsFile.getLanguage().isProtected()
                && isSelectedForImport(nlsFile.getLanguage(), Configuration.getInstance().getImportLanguageIDs())) {
                nlsFile.updateProperties(projectName);
            }
        }
    }

    /**
     * Returns <code>true</code> if the given language is selected for export,
     * else <code>false</code>.
     * 
     * @param language - Language that is checked for export.
     * @param exportLanguageIDs - List of language IDs that are to be exported.
     * @return Boolean value indicating whether or not the language must be
     *         exported.
     */
    private boolean isSelectedForExport(NLSLanguage language, String[] exportLanguageIDs) {
        if (exportLanguageIDs.length == 1 && "*".equals(exportLanguageIDs[0])) {
            return true;
        }
        for (String languageID : exportLanguageIDs) {
            if (languageID.equalsIgnoreCase(language.getLanguageID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the given language is selected for import,
     * else <code>false</code>.
     * 
     * @param language - Language that is checked for import.
     * @param exportLanguageIDs - List of language IDs that are to be imported.
     * @return Boolean value indicating whether or not the language must be
     *         imported.
     */
    public boolean isSelectedForImport(NLSLanguage language, String[] importLanguageIDs) {
        if (importLanguageIDs.length == 1 && "*".equals(importLanguageIDs[0])) {
            return true;
        }
        for (String languageID : importLanguageIDs) {
            if (languageID.equalsIgnoreCase(language.getLanguageID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fRelativePath);
        sb.append("(");
        for (String languageKey : fNLSFiles.keySet()) {
            sb.append(languageKey);
            sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
