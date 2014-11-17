package biz.isphere.core.internal.exception;

import biz.isphere.core.Messages;

public class ErrorLoggedException extends Exception {

    private static final long serialVersionUID = -4497514234146475576L;

    private static final String localizedText = Messages.Error_logged_exception;

    public ErrorLoggedException(String message) {
        super(message);
    }

    public ErrorLoggedException(String message, Throwable aCause) {
        super(message, aCause);
    }
    
    @Override
    public String getLocalizedMessage() {
        return localizedText;
    }

}
