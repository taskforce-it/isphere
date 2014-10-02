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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
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
    private static final String DEFAULT_LANGUAGE = "defaultLanguage";

    URI fResource;
    File fWorkspace;
    Properties fProperties;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Configuration() throws JobCanceledException {
        setConfigurationFile(CONFIG_FILE);
        fWorkspace = new File(new File(fResource).getParentFile().getParentFile().getParent());
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

    public void setConfigurationFile(String fileName) throws JobCanceledException {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                fResource = file.toURI();
            } else {
                fResource = getClass().getResource("/" + fileName).toURI();
            }
        } catch (URISyntaxException e) {
            LogUtil.error("Configuration file not found: " + fileName);
            throw new JobCanceledException(e.getLocalizedMessage());
        }
        fProperties = loadProperties();
    }

    public File getExcelFile() {
        String file = getString(EXPORT_FILE);
        if (!file.endsWith(".xls")) {
            file = file + ".xls";
        }
        int i = file.lastIndexOf(".");
        if (i != -1) {
            file = file.substring(0, i) + "_" + getDateAsString() + file.substring(i);
        }
        return new File(file);
    }

    public String getWorkspacePath() {
        return FileUtil.fixAbsolutePath(fWorkspace.getPath());
    }

    public String[] getProjects() {
        return getStringArray(PROJECTS);
    }

    public FileSelectionEntry[] getFiles() {
        List<FileSelectionEntry> files = new ArrayList<FileSelectionEntry>();
        String[] entries = getStringArray(FILES);
        for (String entry : entries) {
            files.add(new FileSelectionEntry(entry));
        }
        return files.toArray(new FileSelectionEntry[files.size()]);
    }

    public String getDefaultLanguageID() {
        return getString(DEFAULT_LANGUAGE);
    }

    public boolean isDefaultLanguage(String languageID) {
        return getDefaultLanguageID().equalsIgnoreCase(languageID);
    }

    private String getDateAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(GregorianCalendar.getInstance().getTime());
    }

    private String getResourcePath() {
        return new File(fResource).getAbsolutePath();
    }

    private String getString(String key) {
        return getProperties().getProperty(key);
    }

    private String[] getStringArray(String key) {
        String value = getProperties().getProperty(key);
        return value.split(",");
    }

    private Properties getProperties() {
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