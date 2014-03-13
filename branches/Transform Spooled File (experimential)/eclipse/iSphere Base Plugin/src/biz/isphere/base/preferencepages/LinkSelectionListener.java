package biz.isphere.base.preferencepages;

import java.net.URL;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;

public class LinkSelectionListener extends SelectionAdapter {
    public void widgetSelected(SelectionEvent event) {
        try {
            // Open default external browser
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL("http://" + event.text));
        } catch (Exception e) {
            // ignore all errors
        }
    }
}
