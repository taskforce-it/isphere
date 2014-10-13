/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import biz.isphere.build.nls.exception.JobCanceledException;
import biz.isphere.build.nls.model.FileSelectionEntry;
import biz.isphere.build.nls.utils.FileUtil;
import biz.isphere.build.nls.utils.LogUtil;

public final class Configuration {

    /**
     * The instance of this Singleton class.
     */
    private static Configuration instance;

    private static final String CONFIG_FILE = "nls.properties";
    private static final String PROJECTS = "projects";
    private static final String FILES = "files";
    private static final String EXPORT_FILE = "exportFile";
    private static final String IMPORT_FILE = "importFile";
    private static final String DEFAULT_LANGUAGE = "defaultLanguage";
    private static final String EXPORT_LANGUAGE_IDS = "exportLanguageIDs";
    private static final String IMPORT_LANGUAGE_IDS = "importLanguageIDs";

    String fConfigurationResource;
    File fWorkspace;
    Properties fProperties;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Configuration() {
        setConfigurationFile(CONFIG_FILE);
        fWorkspace = null;
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Configuration getInstance() throws JobCanceledException {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void setConfigurationFile(String fileName) {
        fConfigurationResource = fileName;
        fProperties = null;
    }

    public File getExportFile() throws JobCanceledException {
        String file = getString(EXPORT_FILE);
        file = addFileExtension(file);
        int i = file.lastIndexOf(".");
        if (i != -1) {
            file = file.substring(0, i) + "_" + getDateAsString() + file.substring(i);
        }
        return new File(file);
    }

    public File getImportFile() throws JobCanceledException {
        String file = getString(IMPORT_FILE);
        file = addFileExtension(file);
        return new File(file);
    }

    private String addFileExtension(String file) {
        if (!file.endsWith(".xls")) {
            file = file + ".xls";
        }
        return file;
    }

    public String getWorkspacePath() throws JobCanceledException {
        return FileUtil.fixAbsolutePath(getWorkspace().getPath());
    }

    public String[] getProjects() throws JobCanceledException {
        return getStringArray(PROJECTS);
    }

    public FileSelectionEntry[] getFiles() throws JobCanceledException {
        List<FileSelectionEntry> files = new ArrayList<FileSelectionEntry>();
        String[] entries = getStringArray(FILES);
        for (String entry : entries) {
            files.add(new FileSelectionEntry(entry));
        }
        return files.toArray(new FileSelectionEntry[files.size()]);
    }

    public String getDefaultLanguageID() throws JobCanceledException {
        return getString(DEFAULT_LANGUAGE);
    }

    public boolean isDefaultLanguage(String languageID) throws JobCanceledException {
        return getDefaultLanguageID().equalsIgnoreCase(languageID);
    }

    public String[] getExportLanguageIDs() throws JobCanceledException {
        String[] languageIDs = getStringArray(EXPORT_LANGUAGE_IDS);
        if (languageIDs.length == 0) {
            throw new JobCanceledException("No languages specified for export: exportLanguageIDs");
        }
        return languageIDs;
    }

    public String[] getImportLanguageIDs() throws JobCanceledException {
        String[] languageIDs = getStringArray(IMPORT_LANGUAGE_IDS);
        if (languageIDs.length == 0) {
            throw new JobCanceledException("No languages specified for import: importLanguageIDs");
        }
        return languageIDs;
    }

    private File getWorkspace() throws JobCanceledException {
        if (fWorkspace == null) {
            fWorkspace = new File(new File(getResourcePath()).getParentFile().getParentFile().getParent());
        }
        return fWorkspace;
    }

    private String getDateAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(GregorianCalendar.getInstance().getTime());
    }

    private String getResourcePath() throws JobCanceledException {
        try {
            File file = new File(fConfigurationResource);
            URI configurationURI;
            if (file.exists()) {
                configurationURI = file.toURI();
            } else {
                configurationURI = getClass().getResource("/" + fConfigurationResource).toURI();
            }
            return configurationURI.getPath();
        } catch (URISyntaxException e) {
            LogUtil.error("Configuration file not found: " + fConfigurationResource);
            throw new JobCanceledException(e.getLocalizedMessage());
        }
    }

    private String getString(String key) throws JobCanceledException {
        return getProperties().getProperty(key);
    }

    private String[] getStringArray(String key) throws JobCanceledException {
        String value = getProperties().getProperty(key);
        if (value.trim().length() == 0) {
            return new String[]{};
        }
        return value.split("\\s*,\\s*");
    }

    private Properties getProperties() throws JobCanceledException {
        if (fProperties == null) {
            fProperties = loadProperties();
        }
        return fProperties;
    }

    private Properties loadProperties() throws JobCanceledException {
        Properties nlsProps = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(getResourcePath());
            nlsProps.load(in);
            in.close();
        } catch (Exception e) {
            LogUtil.error("Failed to load properties from file: " + getResourcePath());
            throw new JobCanceledException(e.getLocalizedMessage());
        }
        return nlsProps;
    }

}