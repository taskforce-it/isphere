package biz.isphere.journalexplorer.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JournalExplorerPlugin extends AbstractUIPlugin {

    private static final String ICON_PATH = "icons/";
    public static final String IMAGE_COMPARE = "compare.png";
    public static final String IMAGE_DETAILS = "details.png";
    public static final String IMAGE_HIGHLIGHT = "highlight.png";
    public static final String IMAGE_HORIZONTAL_RESULTS_VIEW = "horizontal_results_view.gif";
    public static final String IMAGE_JOURNAL_EXPLORER = "journal_explorer.png";
    public static final String IMAGE_READ_WRITE_OBJ = "readwrite_obj.gif";
    public static final String IMAGE_REFRESH = "refresh.gif";
    public static final String IMAGE_SEGMENT_EDIT = "segment_edit.png";
    public static final String IMAGE_TABLE_BOTTOM_LEFT_CORNER_NEW_GREEN = "table_bottom_left_corner_new_green.png";
    public static final String IMAGE_WARNING_OV = "warning_ov.gif";

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.journalexplorer.core"; //$NON-NLS-1$

    // The shared instance
    private static JournalExplorerPlugin plugin;

    /**
     * The constructor
     */
    public JournalExplorerPlugin() {
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
    public static JournalExplorerPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return ResourceManager.getPluginImageDescriptor(PLUGIN_ID, ICON_PATH + path);
    }

    public static Image getImage(String path) {
        return ResourceManager.getPluginImage(PLUGIN_ID, ICON_PATH + path);
    }
}
