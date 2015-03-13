package biz.isphere.antcontrib.taskdef;

import biz.isphere.antcontrib.utils.StringUtil;

public class IgnoreFile {

    private Rmdir rmDir;

    private String pattern;
    private boolean ignoreCase;

    public IgnoreFile(Rmdir rmDir) {
        super();

        this.rmDir = rmDir;
        this.ignoreCase = true;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean matches(String filename) {
        return StringUtil.matchWildcard(pattern, filename, ignoreCase);
    }
}
