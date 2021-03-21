package biz.isphere.standalone.exceptions;

import org.eclipse.osgi.util.NLS;

public class ConnectionExistException extends Exception {

    private static final long serialVersionUID = -5780369289806283112L;

    private Object connectionName;

    public ConnectionExistException(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public String getMessage() {
        return NLS.bind("Exception exist: ", connectionName);
    }
}
