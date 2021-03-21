package biz.isphere.standalone.connections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.as400.access.AS400;

import biz.isphere.standalone.exceptions.ConnectionExistException;
import biz.isphere.standalone.exceptions.ConnectionNotFoundException;

public final class Connections {

    /**
     * The instance of this Singleton class.
     */
    private static Connections instance;

    /**
     * List of available connections.
     */
    private Map<String, Connection> connections;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Connections() {
        this.connections = new HashMap<String, Connection>();
        loadConnections();
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Connections getInstance() {
        if (instance == null) {
            instance = new Connections();
        }
        return instance;
    }

    public Connection getConnection(String connectionName) {
        return connections.get(connectionName);
    }

    public AS400 getSystem(String connectionName) {

        Connection connection = getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        return connection.getSystem();
    }

    public String[] getConnectionNames() {
        List<String> connectionNames = new LinkedList<String>();
        for (String connectionName : connections.keySet()) {
            connectionNames.add(connectionName);
        }
        return connectionNames.toArray(new String[connectionNames.size()]);
    }

    public synchronized Connection addConnection(Connection connection) throws ConnectionExistException {
        if (connections.containsKey(connection.getConnectionName())) {
            throw new ConnectionExistException(connection.getConnectionName());
        }
        return connections.put(connection.getConnectionName(), connection);
    }

    public synchronized void removeConnection(String connectionName) throws ConnectionExistException, ConnectionNotFoundException {
        if (!connections.containsKey(connectionName)) {
            throw new ConnectionNotFoundException(connectionName);
        }
        connections.remove(connectionName);
    }

    private void loadConnections() {
        Connection connection = new Connection("iSphere");
        connection.setHostName(System.getProperty("HOST"));
        connection.setUserName(System.getProperty("USER"));
        connection.setPassword(System.getProperty("PASSWORD"));
        connections.put(connection.getConnectionName(), connection);
    }
}
