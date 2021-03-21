package biz.isphere.standalone.connections;

import com.ibm.as400.access.AS400;

public class Connection {

    private String connectionName;
    private String hostName;
    private String userName;
    private String password;

    private transient AS400 system;

    public Connection(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AS400 getSystem() {
        if (system == null) {
            system = new AS400(hostName, userName, password);
        }
        return system;
    }

    @Override
    public String toString() {
        return connectionName + " (host=" + hostName + ", user=" + userName + ")";
    }
}
