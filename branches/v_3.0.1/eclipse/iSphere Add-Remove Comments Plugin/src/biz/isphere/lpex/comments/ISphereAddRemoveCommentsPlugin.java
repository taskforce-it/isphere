package biz.isphere.lpex.comments;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.lpex.comments.lpex.ILpexMenuExtension;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereAddRemoveCommentsPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.lpex.comments";

    // The shared instance
    private static ISphereAddRemoveCommentsPlugin plugin;

    // The Lpex menu extension
    private ILpexMenuExtension menuExtension;

    /**
     * The constructor
     */
    public ISphereAddRemoveCommentsPlugin() {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {

        if (menuExtension != null) {
            menuExtension.uninstall();
        }

        plugin = null;
        super.stop(context);
    }

    public boolean isEnabled() {
        
        if (menuExtension != null) {
            return true;
        }
        
        return false;
    }
    
    public void setLpexMenuExtension(ILpexMenuExtension menuExtension) {
        this.menuExtension = menuExtension;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereAddRemoveCommentsPlugin getDefault() {
        return plugin;
    }

}
