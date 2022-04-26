package biz.isphere.standalone.exceptions;

import org.eclipse.osgi.util.NLS;

public class ConnectionNotFoundException extends Exception {

    private static final long serialVersionUID = -5780369289806283112L;

    private Object connectionName;

    public ConnectionNotFoundException(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public String getMessage() {
        return NLS.bind("Exception does not exist: ", connectionName);
    }
}
