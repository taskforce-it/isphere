package biz.isphere.rcp;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ISphereRCPExampleApplication extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.rcp"; //$NON-NLS-1$

    // The shared instance
    private static ISphereRCPExampleApplication plugin;

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    public void start(BundleContext bundleContext) throws Exception {
        ISphereRCPExampleApplication.context = bundleContext;
        plugin = this;
    }

    public void stop(BundleContext bundleContext) throws Exception {
        ISphereRCPExampleApplication.context = null;
        plugin = null;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereRCPExampleApplication getDefault() {
        return plugin;
    }

}
