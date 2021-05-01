/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.rse.core.IRSESystemType;
import org.eclipse.rse.core.events.ISystemModelChangeEvent;
import org.eclipse.rse.core.events.ISystemModelChangeEvents;
import org.eclipse.rse.core.events.ISystemModelChangeListener;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.IProperty;
import org.eclipse.rse.core.model.IPropertySet;
import org.eclipse.rse.core.model.ISystemProfile;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.connection.rse.ConnectionProperties;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.rse.ibmi.contributions.extension.point.QualifiedConnectionName;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

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

        if (event.getResource() instanceof IHost) {
            IHost host = (IHost)event.getResource();
            if (IRSESystemType.SYSTEMTYPE_ISERIES_ID.equals(host.getSystemType().getId())) {
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

        IBMiConnection connection = getIBMiConnection(connectionName);

        if (connection == null) {
            return null;
        }

        return getOrCreateProperties(connection.getHost());
    }

    public void saveConnectionProperties(String connectionName) {

        IBMiConnection connection = getIBMiConnection(connectionName);

        if (connection == null) {
            return;
        }

        ConnectionProperties connectionProperties = getOrCreateProperties(connection.getHost());

        saveProperties(connection.getHost(), connectionProperties.getProperties());
        commitProfile(connection.getHost().getSystemProfile());
    }

    private void createProperties(IHost host) {

        if (host == null) {
            ISpherePlugin.logError("Host is null. Can not create connection properties.", null); //$NON-NLS-1$
            return;
        }

        ISystemProfile profile = host.getSystemProfile();
        if (profile == null) {
            ISpherePlugin.logError("System profile not found. Can not create connection properties of connection: " + getConnectionName(host), null); //$NON-NLS-1$
            return;
        }

        ConnectionProperties connectionProperties = getOrCreateProperties(host);
        updatePropertiesConnection(host, connectionProperties.getProperties());
        commitProfile(profile);
    }

    private void deleteProperties(IHost host) {

        if (host == null) {
            ISpherePlugin.logError("Host is null. Can not delete connection properties.", null); //$NON-NLS-1$
            return;
        }

        ConnectionProperties properties = connectionList.get(getConnectionName(host));
        if (properties == null) {
            ISpherePlugin.logError(
                "Connection properties not found. Can not delete connection properties of connection: " + getConnectionName(host), null); //$NON-NLS-1$
            return;
        }

        ISystemProfile profile = host.getSystemProfile();
        if (profile == null) {
            ISpherePlugin.logError("System profile not found. Can not delete connection properties of connection: " + getConnectionName(host), null); //$NON-NLS-1$
            return;
        }

        profile.removePropertySet(getConnectionName(host));
        commitProfile(profile);

        connectionList.remove(getConnectionName(host));
    }

    private void renameProperties(IHost newHost, String oldHostName) {

        if (newHost == null) {
            ISpherePlugin.logError("Host is null. Can not rename connection properties.", null); //$NON-NLS-1$
            return;
        }

        ConnectionProperties connectionProperties = connectionList.get(oldHostName);
        if (connectionProperties == null) {
            ISpherePlugin.logError("Connection properties not found. Can not rename connection properties of old connection: " + oldHostName, null); //$NON-NLS-1$
            return;
        }

        ISystemProfile profile = newHost.getSystemProfile();
        if (profile == null) {
            ISpherePlugin.logError("System profile not found. Can not rename connection properties of old connection: " + oldHostName, null); //$NON-NLS-1$
            return;
        }

        Properties propertiesList = connectionProperties.getProperties();
        if (propertiesList == null) {
            ISpherePlugin.logError("Properties not found. Can not rename connection properties of old connection: " + oldHostName, null); //$NON-NLS-1$
            return;
        }

        updatePropertiesConnection(newHost, propertiesList);
        saveProperties(newHost, propertiesList);

        profile.removePropertySet(oldHostName);
        commitProfile(profile);
    }

    /**
     * Returns the connection name in the form of "[profile]:connection".
     * 
     * @param qsysRemoteObject - A remote QSYS object.
     * @return qualified connection name
     */
    public static String getConnectionName(QSYSRemoteObject qsysRemoteObject) {

        IHost host = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getHost();

        return getConnectionName(host);
    }

    /**
     * Returns the connection name in the form of "[profile]:connection".
     * 
     * @param connection - A remote system connection.
     * @return qualified connection name
     */
    public static String getConnectionName(IBMiConnection connection) {
        return getConnectionName(connection.getHost());
    }

    /**
     * Returns the connection name in the form of "[profile]:connection".
     * 
     * @param host - A remote system host.
     * @return qualified connection name
     */
    public static String getConnectionName(IHost host) {
        QualifiedConnectionName connectionName = new QualifiedConnectionName(host);
        return connectionName.getQualifiedName();
    }

    /**
     * Returns the remote system connection identified by a given qualified
     * connection name, which must be formatted as
     * <code>'profileName:connectionName'</code>.
     * 
     * @param qualifiedConnectionName - A qualified connection name.
     * @return remote system connection
     * @see QualifiedConnectionName
     */
    public static IBMiConnection getIBMiConnection(String qualifiedConnectionName) {
        QualifiedConnectionName connectionName = new QualifiedConnectionName(qualifiedConnectionName);
        return IBMiConnection.getConnection(connectionName.getProfileName(), connectionName.getConnectionName());
    }

    /**
     * Returns the remote system connection identified by given profile and
     * connection names.
     * 
     * @param profileName - A remote system profile name.
     * @param connectionName - A connection name.
     * @return remote system connection
     */
    public static IBMiConnection getIBMiConnection(String profileName, String connectionName) {
        return IBMiConnection.getConnection(profileName, connectionName);
    }

    /**
     * Returns the remote system connection identified by a given remote system
     * host.
     * 
     * @param host - A remote system host.
     * @return remote system connection
     */
    public static IBMiConnection getIBMiConnection(IHost host) {
        return IBMiConnection.getConnection(host);
    }

    private ConnectionProperties getOrCreateProperties(IHost host) {

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
     * Copies the connection properties from a properties list into the
     * 'iSphere' property set of the connection.
     * 
     * @param host - connection whose properties are copied
     * @param propertiesList - properties list the properties are copied to
     */
    private void saveProperties(IHost host, Properties propertiesList) {

        IPropertySet propertySet = ensurePropertySet(host);

        // TODO: store all properties except for PATH and CONNECTION_NAME
        savePropertyValue(propertiesList, ConnectionProperties.USE_CONNECTION_SPECIFIC_SETTINGS, propertySet);
        savePropertyValue(propertiesList, ConnectionProperties.ISPHERE_FTP_PORT_NUMBER, propertySet);
        savePropertyValue(propertiesList, ConnectionProperties.ISPHERE_LIBRARY_NAME, propertySet);
    }

    private void savePropertyValue(Properties propertiesList, String key, IPropertySet propertySet) {
        savePropertyValue(propertiesList, key, propertySet, "");
    }

    private void savePropertyValue(Properties propertiesList, String key, IPropertySet propertySet, String defaultValue) {

        String value = propertiesList.getProperty(key);
        if (value == null) {
            value = defaultValue;
        }

        IProperty property = propertySet.getProperty(key);
        if (property == null) {
            property = propertySet.addProperty(key, value);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Loads the connection properties from the 'iSphere' property set of the
     * connection into a properties list.
     * 
     * @param host - connection whose properties are loaded
     * @return connection properties
     */
    private Properties loadProperties(IHost host) {

        IPropertySet propertySet = ensurePropertySet(host);

        Properties propertiesList = new Properties();

        loadPropertyValue(propertySet, PATH, propertiesList);
        loadPropertyValue(propertySet, ConnectionProperties.CONNECTION_NAME, propertiesList);

        loadPropertyValue(propertySet, ConnectionProperties.USE_CONNECTION_SPECIFIC_SETTINGS, propertiesList, Boolean.toString(false));
        loadPropertyValue(propertySet, ConnectionProperties.ISPHERE_FTP_PORT_NUMBER, propertiesList,
            Integer.toString(Preferences.getInstance().getFtpPortNumber()));
        loadPropertyValue(propertySet, ConnectionProperties.ISPHERE_LIBRARY_NAME, propertiesList, Preferences.getInstance().getISphereLibrary()); // CHECKED

        // Transient connection properties coming from the RSE connection
        propertiesList.put(ConnectionProperties.ISPHERE_FTP_HOST_NAME, host.getHostName());

        return propertiesList;
    }

    private void loadPropertyValue(IPropertySet propertySet, String key, Properties propertiesList) {
        loadPropertyValue(propertySet, key, propertiesList, "");
    }

    private void loadPropertyValue(IPropertySet propertySet, String key, Properties propertiesList, String defaultValue) {

        String value;

        IProperty property = propertySet.getProperty(key);
        if (property != null) {
            value = property.getValue();
        } else {
            property = propertySet.addProperty(key, defaultValue);
            value = property.getValue();
        }

        propertiesList.put(key, value);
    }

    private void updatePropertiesConnection(IHost host, Properties propertiesList) {

        IPropertySet propertySet = ensurePropertySet(host);

        propertiesList.put(PATH, "");
        propertiesList.put(ConnectionProperties.CONNECTION_NAME, getConnectionName(host));

        savePropertyValue(propertiesList, PATH, propertySet);
        savePropertyValue(propertiesList, ConnectionProperties.CONNECTION_NAME, propertySet);
    }

    private IPropertySet ensurePropertySet(IHost host) {

        String connectionName = getConnectionName(host);

        IPropertySet propertySet = host.getSystemProfile().getPropertySet(connectionName);
        if (propertySet == null) {
            propertySet = host.getSystemProfile().createPropertySet(connectionName);
        }

        return propertySet;
    }

    private void commitProfile(ISystemProfile profile) {
        profile.setDirty(true);
        profile.getProfileManager().commitSystemProfile(profile);
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
