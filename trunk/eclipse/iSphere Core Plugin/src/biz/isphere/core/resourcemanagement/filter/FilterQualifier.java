package biz.isphere.core.resourcemanagement.filter;

public class FilterQualifier {

    private boolean singleFilterPool;
    
    public FilterQualifier(boolean singleFilterPool) {
        this.singleFilterPool = singleFilterPool;
    }

    public boolean isSingleFilterPool() {
        return singleFilterPool;
    }
    
}
