package biz.isphere.rcp;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;

public class ISphereRCPExampleApplication extends AbstractUIPlugin {

    private static final String ICON_PATH = "icons/"; //$NON-NLS-1$
    public static final String IMAGE_OPEN_JOURNAL_OUTFILE = "open_journal_outfile.png"; //$NON-NLS-1$

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

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public ImageDescriptor getImageDescriptor(String path) {
        return ResourceManager.getPluginImageDescriptor(PLUGIN_ID, ICON_PATH + path);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_OPEN_JOURNAL_OUTFILE, getImageDescriptor(IMAGE_OPEN_JOURNAL_OUTFILE));
    }

}
