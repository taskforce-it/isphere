package biz.isphere.base.swt.widgets;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class UpperCaseOnlyVerifier implements VerifyListener {

    public void verifyText(VerifyEvent event) {
        event.text = event.text.toUpperCase();
    }

}
