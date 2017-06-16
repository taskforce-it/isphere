package biz.isphere.antcontrib.configuration;

public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 2440520784825745346L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable e) {
        super(message, e);
    }
}
