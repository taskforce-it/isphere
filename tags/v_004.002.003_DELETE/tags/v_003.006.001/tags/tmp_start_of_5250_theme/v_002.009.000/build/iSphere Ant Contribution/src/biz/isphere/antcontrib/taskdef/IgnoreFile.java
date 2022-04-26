package biz.isphere.antcontrib.taskdef;

import biz.isphere.antcontrib.sf.SFException;
import biz.isphere.antcontrib.utils.StringUtil;

public class IgnoreFile {

    private Rmdir rmDir;

    private String pattern;
    private boolean ignoreCase;

    public IgnoreFile(Rmdir rmDir) {
        super();

        this.rmDir = rmDir;
        
        // required attributes
        this.pattern = null;
        
        // optional attributes
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

    public boolean matches(String filename) throws SFException {
        
        if (pattern == null) {
            throw new SFException("Attribute 'pattern' not set.");
        }
        
        return StringUtil.matchWildcard(pattern, filename, ignoreCase);
    }
}
