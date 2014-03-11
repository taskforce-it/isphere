package biz.isphere.adapter;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereAdapterPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.adapter"; //$NON-NLS-1$

    // The shared instance
    private static ISphereAdapterPlugin plugin;

    /**
     * The constructor
     */
    public ISphereAdapterPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereAdapterPlugin getDefault() {
        return plugin;
    }

    /**
     * Return the preferences store of this plugin.
     * 
     * @return Preferences store of the plugin.
     */
    public static IPreferenceStore getPreferencesStore() {
        return getDefault().getPreferenceStore();
    }

    /**
     * Convenience method to log error messages to the application log.
     * 
     * @param message Message
     * @param e The exception that has produced the error
     */
    public static void logError(String message, Exception e) {
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
    }

}
