package biz.isphere.base.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class NumericOnlyVerifyListener implements VerifyListener {

    public void verifyText(VerifyEvent event) {
        switch (event.keyCode) {
        case SWT.BS: // Backspace
        case SWT.DEL: // Delete
        case SWT.HOME: // Home
        case SWT.END: // End
        case SWT.ARROW_LEFT: // Left arrow
        case SWT.ARROW_RIGHT: // Right arrow
            return;
        }

        if (event.keyCode != 0 && !Character.isDigit(event.character)) {
            event.doit = false; // disallow the action
        }
    }
}
