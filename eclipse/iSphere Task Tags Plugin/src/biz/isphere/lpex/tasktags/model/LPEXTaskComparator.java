package biz.isphere.lpex.tasktags.model;

import java.util.Comparator;

import org.eclipse.swt.widgets.TableItem;

public class LPEXTaskComparator implements Comparator<TableItem> {

    public int compare(TableItem b1, TableItem b2) {
        if (b1 == null && b2 == null) {
            return 0;
        } else if (b1 == null) {
            return 1;
        } else if (b2 == null) {
            return -1;
        } else {
            if (b1.getText() == null && b2.getText() == null) {
                return 0;
            }
            if (b1.getText() == null) {
                return 1;
            }
            if (b2.getText() == null) {
                return -1;
            }
        }
        return b1.getText().compareTo(b2.getText());
    }
}
