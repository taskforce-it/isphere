package biz.isphere.core.messagefilecompare;

/**
 * Class that provides the selection settings for the table filter.
 */
public class TableFilterData {

    private boolean copyRight;
    private boolean copyLeft;
    private boolean noCopy;
    private boolean equal;
    private boolean singles;
    private boolean duplicates;

    public boolean isCopyRight() {
        return copyRight;
    }

    public void setCopyRight(boolean copyRight) {
        this.copyRight = copyRight;
    }

    public boolean isCopyLeft() {
        return copyLeft;
    }

    public void setCopyLeft(boolean copyLeft) {
        this.copyLeft = copyLeft;
    }

    public boolean isCopyNotEqual() {
        return noCopy;
    }

    public void setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean eEqual) {
        this.equal = eEqual;
    }

    public boolean isSingles() {
        return singles;
    }

    public void setSingles(boolean singles) {
        this.singles = singles;
    }

    public boolean isDuplicates() {
        return duplicates;
    }

    public void setDuplicates(boolean duplicates) {
        this.duplicates = duplicates;
    }
}
