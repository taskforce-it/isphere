package biz.isphere.adapter;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.adapter.messages"; //$NON-NLS-1$

    public static String XFileDialog_OverwriteDialog_question;

    public static String XFileDialog_OverwriteDialog_headline;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
