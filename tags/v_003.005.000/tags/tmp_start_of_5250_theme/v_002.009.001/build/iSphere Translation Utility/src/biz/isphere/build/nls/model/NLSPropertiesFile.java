/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;

import biz.isphere.build.nls.configuration.Configuration;
import biz.isphere.build.nls.exception.JobCanceledException;
import biz.isphere.build.nls.utils.FileUtil;
import biz.isphere.build.nls.utils.LogUtil;
import biz.isphere.build.nls.utils.StringUtil;

/**
 * This class is equivalent to a language properties file such as
 * "plugin_de.properties".
 * 
 * @author Thomas Raddatz
 */
public class NLSPropertiesFile {

    private static final String FILE_EXTENSION_SEPARATOR = "\\.";

    private static final String LANGUAGE_ID_DELIMITER = "_";

    private static final String PROTECTED_LANGUAGE_FLAG = "*";

    private String fProjectPath;
    private File fFile;
    private PropertiesConfiguration fProperties;
    private NLSLanguage fLanguage;

    /**
     * Constructor, used when exporting NLS entries.
     * 
     * @param projectPath Path to the project that contains the properties files
     *        that need to be exported.
     * @param file The properties file with the language strings.
     */
    public NLSPropertiesFile(String projectPath, File file) {
        fProjectPath = projectPath;
        fFile = file;
        fProperties = null;
        fLanguage = null;
    }

    /**
     * Constructor, used when importing NLS entries.
     * 
     * @param projectPath Path to the project that contains the properties files
     *        that need to be exported.
     * @param relativePath Relative path from the project root to the properties
     *        file that contains the NLS strings.
     * @param languageKey Language key as used as the title of the language
     *        column in the Excel sheet.
     * @throws JobCanceledException
     */
    public NLSPropertiesFile(String projectPath, String relativePath, String languageKey) throws JobCanceledException {
        fProjectPath = projectPath;
        fFile = new File(projectPath + relativePath + getLanguageSuffix(removeProtectedLanguageFlag(languageKey)) + ".properties");
        fProperties = null;
        fLanguage = null;
    }

    public String getKey() throws JobCanceledException {
        if (Configuration.getInstance().isDefaultLanguage(getLanguage().getLanguageID())) {
            return PROTECTED_LANGUAGE_FLAG + getLanguage().getLanguageID();
        }
        return getLanguage().getLanguageID();
    }

    public String getResourceNameWithoutLanguageID() {
        return FileUtil.fixRelativePath(getRelativeFile().getParent()) + getFileNameWithoutExtension().split(LANGUAGE_ID_DELIMITER)[0];
    }

    public NLSLanguage getLanguage() throws JobCanceledException {
        if (fLanguage == null) {
            String languageID;
            String[] parts = getFileNameWithoutExtension().split(LANGUAGE_ID_DELIMITER);
            if (parts.length <= 1) {
                languageID = Configuration.getInstance().getDefaultLanguageID();
            } else {
                languageID = parts[parts.length - 1];
            }
            fLanguage = new NLSLanguage(languageID);
        }
        return fLanguage;
    }

    public String getText(String key) {
        try {
            if (getProperties().containsKey(key)) {
                return getProperties().getString(key);
            }
            return "";
        } catch (JobCanceledException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Set<String> getKeys() {
        try {
            Set<String> keys = new HashSet<String>();
            for (Iterator<String> iterator = getProperties().getKeys(); iterator.hasNext();) {
                String key = iterator.next();
                keys.add(key);
            }
            return keys;
        } catch (JobCanceledException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setProperty(String key, String text) throws JobCanceledException {
        PropertiesConfiguration properties = getProperties();
        properties.setProperty(key, StringUtil.trimR(text));
    }

    public void updateProperties(String projectName) throws JobCanceledException {
        PropertiesConfiguration properties = getProperties();

        try {
            FileOutputStream out = new FileOutputStream(fFile);
            properties.save(out);
            out.close();
            LogUtil.print("Updated properties file: " + fFile.getName());
        } catch (Exception e) {
            LogUtil.error("Failed to save properties to: " + fFile);
        }

    }

    private PropertiesConfiguration getProperties() throws JobCanceledException {
        if (fProperties == null) {
            fProperties = new PropertiesConfiguration();
            try {
                fProperties.load(new FileInputStream(fFile));
            } catch (Exception e) {
                LogUtil.error("Failed to load properties from file: " + fFile.getAbsolutePath());
                throw new JobCanceledException(e.getLocalizedMessage());
            }
        }
        return fProperties;
    }

    private String getFileNameWithoutExtension() {
        return getRelativeFile().getName().split(FILE_EXTENSION_SEPARATOR)[0];
    }

    private File getRelativeFile() {
        return new File(FileUtil.fixRelativeFile(fFile.getPath().substring(fProjectPath.length())));
    }

    private String getLanguageSuffix(String languageID) throws JobCanceledException {
        if (Configuration.getInstance().isDefaultLanguage(languageID)) {
            return "";
        }
        return LANGUAGE_ID_DELIMITER + languageID;
    }

    private String removeProtectedLanguageFlag(String languageKey) {
        if (languageKey.startsWith(PROTECTED_LANGUAGE_FLAG)) {
            return languageKey.substring(1);
        }
        return languageKey;
    }

    @Override
    public String toString() {
        return fFile.toString();
    }

}
