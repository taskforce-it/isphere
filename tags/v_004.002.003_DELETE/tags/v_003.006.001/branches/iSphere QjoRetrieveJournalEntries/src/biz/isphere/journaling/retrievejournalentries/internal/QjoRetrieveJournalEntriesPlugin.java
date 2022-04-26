package biz.isphere.journaling.retrievejournalentries.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class QjoRetrieveJournalEntriesPlugin implements BundleActivator {

    private static QjoRetrieveJournalEntriesPlugin plugin;
    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    public static QjoRetrieveJournalEntriesPlugin getDefault() {
        return plugin;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext bundleContext) throws Exception {
        QjoRetrieveJournalEntriesPlugin.context = bundleContext;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        QjoRetrieveJournalEntriesPlugin.context = null;
    }

}
