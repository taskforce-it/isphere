package biz.isphere.core.dataspaceeditordesigner.model;

@SuppressWarnings("serial")
public class DComment extends AbstractDWidget {

    public static final String SEPARATOR = "*SEPARATOR";
    public static final String NONE = "*NONE";
    
    DComment(String label) {
        super(label, -1, -1);
    }

}
