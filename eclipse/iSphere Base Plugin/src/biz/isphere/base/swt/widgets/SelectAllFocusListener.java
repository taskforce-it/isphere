package biz.isphere.base.swt.widgets;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

public class SelectAllFocusListener implements FocusListener {

    public void focusGained(FocusEvent event) {
        
        if (event.getSource() instanceof Text) {
            Text text = (Text)event.getSource();
            text.selectAll();
        }
    }

    public void focusLost(FocusEvent event) {
    }

}
