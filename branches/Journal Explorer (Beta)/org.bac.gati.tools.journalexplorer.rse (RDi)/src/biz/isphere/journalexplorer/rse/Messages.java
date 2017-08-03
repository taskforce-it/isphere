package biz.isphere.journalexplorer.rse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.journalexplorer.rse.messages"; //$NON-NLS-1$

    public static String DAOBase_ConnectionNotStablished;
    public static String DAOBase_InvalidConnectionObject;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
