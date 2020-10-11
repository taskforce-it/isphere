package biz.isphere.jobtraceexplorer.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereJobTraceExplorerCorePlugin extends AbstractUIPlugin {

    private static final String ICON_PATH = "icons/";
    public static final String IMAGE_REFRESH = "refresh.gif";
    public static final String IMAGE_OPEN_JOB_TRACE_SESSION = "open_jobtrace_session.png";
    public static final String IMAGE_JUMP_PROC_ENTER = "jump_proc_enter.png";
    public static final String IMAGE_JUMP_PROC_EXIT = "jump_proc_exit.png";

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.jobtraceexplorer.core"; //$NON-NLS-1$

    // The shared instance
    private static ISphereJobTraceExplorerCorePlugin plugin;

    /**
     * The constructor
     */
    public ISphereJobTraceExplorerCorePlugin() {
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
    public static ISphereJobTraceExplorerCorePlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
        reg.put(IMAGE_OPEN_JOB_TRACE_SESSION, getImageDescriptor(IMAGE_OPEN_JOB_TRACE_SESSION));
        reg.put(IMAGE_JUMP_PROC_ENTER, getImageDescriptor(IMAGE_JUMP_PROC_ENTER));
        reg.put(IMAGE_JUMP_PROC_EXIT, getImageDescriptor(IMAGE_JUMP_PROC_EXIT));
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

    public Image getImage(String path) {
        return ResourceManager.getPluginImage(PLUGIN_ID, ICON_PATH + path);
    }

    /**
     * Convenience method to log error messages to the application log.
     * 
     * @param message Message
     * @param e The exception that has produced the error
     */
    public static void logError(String message, Throwable e) {
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
    }
}
