package org.eclipse.equinox.security.internal.storage;

public class SecureValue {

    private Object value;
    private boolean isEncrypted;

    public SecureValue(Object value, boolean isEncrypted) {
        this.value = value;
        this.isEncrypted = isEncrypted;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public Object getValue() {
        return value;
    }

}
