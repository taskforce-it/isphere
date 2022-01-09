/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import biz.isphere.core.helpbuilder.exception.JobCanceledException;
import biz.isphere.core.helpbuilder.model.Project;
import biz.isphere.core.helpbuilder.utils.FileUtil;
import biz.isphere.core.helpbuilder.utils.LogUtil;

public final class Configuration {

    public static final String REGEX_BACK_SLASH = "\\\\";
    public static final String REGEX_PIPE = "\\|";
    public static final String FORWARD_SLASH = "/";
    public static final String NEW_LINE = "\\\n";

    /**
     * The instance of this Singleton class.
     */
    private static Configuration instance;

    private static final String MAIN_CONFIG_FILE = "build.properties";
    private static final String PROJECT_CONFIG_FILE = "helpbuilder.properties";
    private static final String BUILD_PROJECT_NAME = "build.project.name";
    private static final String OUTPUT_FILE = "build.output.file";
    private static final String BUILD_OUTPUT_DIRECTORY = "build.output.dir";
    private static final String PROJECTS = "build.help.projects";

    private String mainConfigResource;
    private String projectConfigResource;
    private Properties properties;

    private File workspace;
    private Project[] projects;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Configuration() {
        setConfigurationFile(MAIN_CONFIG_FILE, PROJECT_CONFIG_FILE);
        workspace = null;
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

    public void setConfigurationFile(String mainConfig, String projectConfig) {
        mainConfigResource = mainConfig;
        projectConfigResource = projectConfig;
        properties = null;
    }

    public Project[] getProjects() throws JobCanceledException {

        if (projects == null) {
            String[] configString = getStringArray(PROJECTS);
            projects = new Project[configString.length];
            for (int i = 0; i < projects.length; i++) {
                projects[i] = new Project(configString[i]);
            }
        }

        return projects;
    }

    public File getWorkspace() throws JobCanceledException {
        if (workspace == null) {
            workspace = new File(new File(getResourcePath(mainConfigResource)).getParentFile().getParentFile().getParent());
        }
        return workspace;
    }

    public String getOutputDirectory() throws JobCanceledException {

        String fileName = getWorkspace() + File.separator + getString(BUILD_PROJECT_NAME) + File.separator + getString(BUILD_OUTPUT_DIRECTORY);

        try {
            return FileUtil.resolvePath(fileName);
        } catch (IOException e) {
            throw new JobCanceledException(e.getLocalizedMessage(), e);
        }
    }

    public String getOutputFile() throws JobCanceledException {

        String fileName = getWorkspace() + File.separator + getString(BUILD_PROJECT_NAME) + File.separator + getString(OUTPUT_FILE);

        try {
            return FileUtil.resolvePath(fileName);
        } catch (IOException e) {
            throw new JobCanceledException(e.getLocalizedMessage(), e);
        }
    }

    private String getResourcePath(String configResource) throws JobCanceledException {
        
        File file = new File(configResource);
        URI configurationURI = null;
        
        if (file.exists()) {
            configurationURI = file.toURI();
        } else {
            if (getClass().getResource(FORWARD_SLASH + configResource) != null) {
                try {
                    configurationURI = getClass().getResource(FORWARD_SLASH + configResource).toURI();
                } catch (URISyntaxException e) {
                    // ignore exception
                }
            } else {
                throw new JobCanceledException("Configuration file not found: " + configResource);
            }
        }
        
        if (configurationURI == null) {
            String message = "Configuration file not found: " + configResource;
            LogUtil.error(message);
            throw new JobCanceledException(message);
        }
        
        return configurationURI.getPath();
    }

    private String getString(String key) throws JobCanceledException {
        return getProperties().getProperty(key);
    }

    private String[] getStringArray(String key) throws JobCanceledException {
        
        String value = getProperties().getProperty(key);
        if (value.trim().length() == 0) {
            return new String[] {};
        }
        
        return value.split("\\s*,\\s*");
    }

    private Properties getProperties() throws JobCanceledException {
        
        if (properties == null) {
            properties = loadProperties(getResourcePath(mainConfigResource), getResourcePath(projectConfigResource));
        }
        
        return properties;
    }

    private Properties loadProperties(String mainPath, String projectConfigPath) throws JobCanceledException {
        
        Properties nlsProps = new Properties();
        InputStream in = null;
        
        try {
            
            in = new FileInputStream(mainPath);
            nlsProps.load(in);
            in.close();
            
        } catch (Exception e) {
            LogUtil.error("Failed to load properties from file: " + getResourcePath(mainConfigResource));
            throw new JobCanceledException(e.getLocalizedMessage());
        }
        
        try {
        
            Properties tmpNlsProps = new Properties();
            
            in = new FileInputStream(projectConfigPath);
            tmpNlsProps.load(in);
            in.close();
            
            Set<Map.Entry<Object, Object>> properties = tmpNlsProps.entrySet();
            for (Map.Entry<Object, Object> entry : properties) {
                nlsProps.put(entry.getKey(), entry.getValue());
            }
            
        } catch (Exception e) {
            LogUtil.error("Failed to load properties from file: " + getResourcePath(projectConfigPath));
            throw new JobCanceledException(e.getLocalizedMessage());
        }
        
        return nlsProps;
    }

}