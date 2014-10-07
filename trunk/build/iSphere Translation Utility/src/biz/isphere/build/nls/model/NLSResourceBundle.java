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
 * is the host of all language properties files that belong to the resource.
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

    public String getID() {
        return fRelativePath;
    }

    public void add(NLSPropertiesFile nlsFile) throws JobCanceledException {
        fNLSFiles.put(nlsFile.getKey(), nlsFile);
    }

    public Set<String> getLanguageKeys() {
        return new TreeSet<String>(fNLSFiles.keySet());
    }

    public String[] getKeys() {
        Set<String> keys = new HashSet<String>();
        for (NLSPropertiesFile nlsFile : fNLSFiles.values()) {
            keys.addAll(nlsFile.getKeys());
        }
        return keys.toArray(new String[keys.size()]);
    }

    public NLSTextEntry[] getValues(String key) throws JobCanceledException {
        List<NLSTextEntry> values = new ArrayList<NLSTextEntry>();
        for (String languageKey : getLanguageKeys()) {
            NLSPropertiesFile nlsFile = getNLSFile(languageKey);
            String value = nlsFile.getText(key);
            values.add(new NLSTextEntry(value, nlsFile.getLanguage().isProtected()));
        }
        return values.toArray(new NLSTextEntry[values.size()]);
    }

    public NLSPropertiesFile getNLSFile(String languageKey) {
        return fNLSFiles.get(languageKey);
    }

    public void updateFiles(String projectName) throws JobCanceledException {
        for (NLSPropertiesFile nlsFile : fNLSFiles.values()) {
            if (!nlsFile.getLanguage().isProtected() && isSelectedForImport(nlsFile.getLanguage(), Configuration.getInstance().getImportLanguageIDs())) {
                nlsFile.updateProperties(projectName);
            }
        }
    }

    private boolean isSelectedForImport(NLSLanguage language, String[] importLanguageIDs) {
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
