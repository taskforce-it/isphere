package biz.isphere.joblogexplorer.preferences;

public enum SeverityColor {
    SEVERITY_00 ("SEVERITY_00"),
    SEVERITY_10 ("SEVERITY_10"),
    SEVERITY_20 ("SEVERITY_20"),
    SEVERITY_30 ("SEVERITY_30"),
    SEVERITY_40 ("SEVERITY_40");
    ;

    private String keyValue;

    private SeverityColor(String keyValue) {
        this.keyValue = keyValue;
    }

    public String key() {
        return keyValue;
    }
}
