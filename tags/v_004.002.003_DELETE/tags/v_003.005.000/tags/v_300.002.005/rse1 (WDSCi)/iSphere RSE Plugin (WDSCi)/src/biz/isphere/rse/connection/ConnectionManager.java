/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.connection.rse.ConnectionProperties;
import biz.isphere.core.preferences.Preferences;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.model.ISystemModelChangeEvent;
import com.ibm.etools.systems.model.ISystemModelChangeEvents;
import com.ibm.etools.systems.model.ISystemModelChangeListener;
import com.ibm.etools.systems.model.SystemConnection;

public class ConnectionManager implements ISystemModelChangeListener {

    private static final String PATH = "properties.path";

    private Map<String, ConnectionProperties> connectionList;

    /**
     * The instance of this Singleton class.
     */
    private static ConnectionManager instance;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private ConnectionManager() {

        this.connectionList = new HashMap<String, ConnectionProperties>();
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void systemModelResourceChanged(ISystemModelChangeEvent event) {

        if (event.getResource() instanceof SystemConnection) {
            SystemConnection host = (SystemConnection)event.getResource();
            if ("iSeries".equals(host.getSystemType())) {
                if (event.getEventType() == ISystemModelChangeEvents.SYSTEM_RESOURCE_ADDED) {
                    createProperties(host);
                } else if (event.getEventType() == ISystemModelChangeEvents.SYSTEM_RESOURCE_REMOVED) {
                    deleteProperties(host);
                } else if (event.getEventType() == ISystemModelChangeEvents.SYSTEM_RESOURCE_RENAMED) {
                    renameProperties(host, event.getOldName());
                }
            }
        }
        return;
    }

    public ConnectionProperties getConnectionProperties(String connectionName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);

        if (connection == null) {
            return null;
        }

        try {
            return getOrCreateProperties(connection.getSystemConnection());
        } catch (Exception e) {
            ISpherePlugin.logError("Could not get connection properties of connection: " + connectionName, null); //$NON-NLS-1$
            return null;
        }
    }

    public void saveConnectionProperties(String connectionName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);

        if (connection == null) {
            return;
        }

        try {
            ConnectionProperties connectionProperties = getOrCreateProperties(connection.getSystemConnection());
            saveProperties(connection.getSystemConnection(), connectionProperties.getProperties());
            commitProfile(connectionProperties);
        } catch (Exception e) {
            ISpherePlugin.logError("Could not save connection properties of connection: " + connectionName, null); //$NON-NLS-1$
            return;
        }

    }

    private void createProperties(SystemConnection host) {

        if (host == null) {
            ISpherePlugin.logError("Host is null. Can not create connection properties.", null); //$NON-NLS-1$
            return;
        }

        try {

            ConnectionProperties connectionProperties = getOrCreateProperties(host);
            updatePropertiesConnection(host, connectionProperties.getProperties());
            commitProfile(connectionProperties);

        } catch (Exception e) {
            ISpherePlugin.logError("Could not save properties. Can not create connection properties.", e); //$NON-NLS-1$
        }

    }

    private void deleteProperties(SystemConnection host) {

        if (host == null) {
            ISpherePlugin.logError("Host is null. Can not delete connection properties.", null); //$NON-NLS-1$
            return;
        }

        ConnectionProperties properties = connectionList.get(getConnectionName(host));
        if (properties == null) {
            return;
        }

        deleteProfile(properties);
        connectionList.remove(getConnectionName(host));
    }

    private void renameProperties(SystemConnection newHost, String oldHostName) {

        if (newHost == null) {
            ISpherePlugin.logError("Host is null. Can not rename connection properties.", null); //$NON-NLS-1$
            return;
        }

        try {

            ConnectionProperties connectionProperties = connectionList.get(oldHostName);
            if (connectionProperties == null) {
                connectionProperties = getOrCreateProperties(newHost);
            }

            Properties propertiesList = connectionProperties.getProperties();
            if (propertiesList == null) {
                ISpherePlugin.logError("Properties not found. Can not rename connection properties of old connection: " + oldHostName, null); //$NON-NLS-1$
                return;
            }

            updatePropertiesConnection(newHost, propertiesList);
            saveProperties(newHost, propertiesList);

            commitProfile(connectionProperties);

        } catch (Exception e) {
            ISpherePlugin.logError("Properties not saved. Can not rename connection properties of old connection: " + oldHostName, null); //$NON-NLS-1$
        }
    }

    public static String getConnectionName(SystemConnection host) {
        return host.getAliasName();
    }

    private ConnectionProperties getOrCreateProperties(SystemConnection host) throws FileNotFoundException, IOException {

        String connectionName = getConnectionName(host);

        if (connectionList.containsKey(connectionName)) {
            ConnectionProperties connectionProperties = connectionList.get(connectionName);
            return connectionProperties;
        }

        ConnectionProperties connectionProperties = new ConnectionProperties(loadProperties(host));
        connectionList.put(getConnectionName(host), connectionProperties);

        return connectionProperties;
    }

    /**
     * Saves the 'iSphere' connection properties.
     * 
     * @param host - connection whose properties are copied
     * @param propertiesList - properties list the already contains the
     *        properties
     */
    private void saveProperties(SystemConnection host, Properties propertySet) {
        // There is nothing to do here for WDSCi
    }

    private void savePropertyValue(Properties propertiesList, String key, String value) {
        propertiesList.put(key, value);
    }

    /**
     * Loads the 'iSphere' connection properties from a file.
     * 
     * @param host - connection whose properties are loaded
     * @return connection properties
     */
    private Properties loadProperties(SystemConnection host) throws FileNotFoundException, IOException {

        Properties propertySet = ensurePropertySet(host);

        updatePropertiesConnection(host, propertySet);

        loadPropertyValue(propertySet, ConnectionProperties.USE_CONNECTION_SPECIFIC_SETTINGS, Boolean.toString(false));
        loadPropertyValue(propertySet, ConnectionProperties.ISPHERE_FTP_PORT_NUMBER, Integer.toString(Preferences.getInstance().getFtpPortNumber()));
        loadPropertyValue(propertySet, ConnectionProperties.ISPHERE_LIBRARY_NAME, Preferences.getInstance().getISphereLibrary()); // CHECKED

        // Transient connection properties coming from the RSE connection
        propertySet.put(ConnectionProperties.ISPHERE_FTP_HOST_NAME, host.getHostName());

        return propertySet;
    }

    private void loadPropertyValue(Properties propertySet, String key, String defaultValue) {

        if (!propertySet.containsKey(key)) {
            propertySet.setProperty(key, defaultValue);
        }
    }

    private void updatePropertiesConnection(SystemConnection host, Properties propertiesList) {

        savePropertyValue(propertiesList, PATH, getFilePath(host).getAbsolutePath());
        savePropertyValue(propertiesList, ConnectionProperties.CONNECTION_NAME, getConnectionName(host));
    }

    private Properties ensurePropertySet(SystemConnection host) throws FileNotFoundException, IOException {

        Properties properties = new Properties();

        File file = getFilePath(host);
        if (file.exists()) {
            properties.load(new FileInputStream(file));
        }

        return properties;
    }

    private File getFilePath(SystemConnection host) {

        File directory = getDirectoryPath(host);
        if (directory == null) {
            return null;
        }

        File file = new File(directory, "iSphere-Connection.properties"); //$NON-NLS-1$

        return file;
    }

    private File getDirectoryPath(SystemConnection host) {

        if (host.eResource() == null) {
            return null;
        }

        File directory = new File(host.eResource().getURI().toFileString()).getParentFile();
        if (!directory.exists()) {
            ISpherePlugin.logError("Connection properties file not found: " + directory.getPath(), null);
            return null;
        }

        return directory;
    }

    private void commitProfile(ConnectionProperties profile) throws FileNotFoundException, IOException {

        Properties properties = profile.getProperties();
        File file = new File(properties.getProperty(PATH));
        properties.store(new FileOutputStream(file), "iSphere connection properties");
    }

    private void deleteProfile(ConnectionProperties profile) {

        Properties properties = profile.getProperties();
        File file = new File(properties.getProperty(PATH));
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }
}
