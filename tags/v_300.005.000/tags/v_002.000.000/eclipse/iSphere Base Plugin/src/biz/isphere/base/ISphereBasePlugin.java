package biz.isphere.base;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import biz.isphere.base.versioncheck.PluginCheck;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereBasePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.base"; //$NON-NLS-1$

    // Contributor logos
    public static final String IMAGE_ISPHERE = "isphere.gif";

    public static final String IMAGE_TASKFORCE = "TaskForce.bmp";

    public static final String IMAGE_TOOLS400 = "Tools400.bmp";

    // The shared instance
    private static ISphereBasePlugin plugin;
    
    private static URL installURL;

    /**
     * The constructor
     */
    public ISphereBasePlugin() {
        
        
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        installURL = context.getBundle().getEntry("/");
        
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereBasePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF".
     * 
     * @return Version of the plugin.
     */
    public String getVersion() {
        String version = (String)getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        if (version == null) {
            version = "0.0.0";
        }
        return version;
    }
    
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_TASKFORCE, getImageDescriptor(IMAGE_TASKFORCE));
        reg.put(IMAGE_TOOLS400, getImageDescriptor(IMAGE_TOOLS400));
        reg.put(IMAGE_ISPHERE, getImageDescriptor(IMAGE_ISPHERE));
    }
    
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/"; 
        try {
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

}
