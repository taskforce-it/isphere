package biz.isphere.strpreprc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.strpreprc.action.EditHeaderAction;
import biz.isphere.strpreprc.action.RemoveHeaderAction;

import com.ibm.lpex.core.LpexView;

public class ISphereStrPrePrcSupportPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.strpreprc"; //$NON-NLS-1$

    private static ISphereStrPrePrcSupportPlugin plugin;

    private static final String BEGIN_SUB_MENU = "beginSubmenu"; //$NON-NLS-1$
    private static final String END_SUB_MENU = "endSubmenu"; //$NON-NLS-1$
    private static final String SEPARATOR = "separator"; //$NON-NLS-1$
    private static final String DOUBLE_QUOTES = "\""; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$

    public ISphereStrPrePrcSupportPlugin() {
        super();
        plugin = this;
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

        initializeLpexEditor();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    protected void initializeLpexEditor() {
        LpexView.doGlobalCommand("set default.updateProfile.userActions "
            + getLPEXEditorUserActions(LpexView.globalQuery("current.updateProfile.userActions")));
        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions "
            + getLPEXEditorUserKeyActions(LpexView.globalQuery("current.updateProfile.userKeyActions")));
        LpexView.doGlobalCommand("set default.popup " + getLPEXEditorPopupMenu(LpexView.globalQuery("current.popup")));
    }

    public String getLPEXEditorUserActions(String existingActions) {

        ArrayList<String> actions = new ArrayList<String>();

        actions.add(EditHeaderAction.class.getName() + " " + EditHeaderAction.class.getName());
        actions.add(RemoveHeaderAction.class.getName() + " " + RemoveHeaderAction.class.getName());

        StringBuilder newUserActions = new StringBuilder();

        if ((existingActions == null) || (existingActions.equalsIgnoreCase("null"))) {
            for (String action : actions) {
                newUserActions.append(action + " ");
            }
        }

        else {
            newUserActions.append(existingActions + " ");
            for (String action : actions) {
                if (existingActions.indexOf(action) < 0) {
                    newUserActions.append(action + " ");
                }
            }
        }

        return newUserActions.toString();
    }

    public static ISphereStrPrePrcSupportPlugin getDefault() {
        return plugin;
    }

    private String getLPEXEditorUserKeyActions(String keyActions) {

        StringBuilder newKeyActions = new StringBuilder();
        if (keyActions == null) {
            newKeyActions.append("");
        } else {
            newKeyActions.append(keyActions);
        }

        // appendKeyAction(newKeyActions, "c-1" + SPACE +
        // AddHeaderAction.class.getName());
        // appendKeyAction(newKeyActions, "c-2" + SPACE +
        // ChangeHeaderAction.class.getName());

        return newKeyActions.toString();
    }

    private StringBuilder appendKeyAction(StringBuilder newKeyActions, String keyAction) {

        if (newKeyActions.indexOf(keyAction) < 0) {
            if (newKeyActions.length() != 0) {
                newKeyActions.append(" ");
            }
            newKeyActions.append(keyAction);
        }

        return newKeyActions;
    }

    private String getLPEXEditorPopupMenu(String popupMenu) {

        ArrayList<String> menuActions = new ArrayList<String>();

        menuActions.add(EditHeaderAction.getLPEXMenuAction());
        menuActions.add(RemoveHeaderAction.getLPEXMenuAction());

        popupMenu = removeSubMenu("STRPREPRC", popupMenu);
        String newMenu = createSubMenu("STRPREPRC", menuActions);

        if (popupMenu != null && popupMenu.contains(newMenu)) {
            return popupMenu;
        }

        if (popupMenu != null) {
            return newMenu + SPACE + popupMenu;
        }

        return newMenu;
    }

    private String removeSubMenu(String subMenu, String menu) {

        int start = menu.indexOf(createStartMenuTag(subMenu));
        if (start < 0) {
            return menu;
        }

        String endSubMenu = createEndMenuTag();
        int end = menu.indexOf(endSubMenu, start);
        if (end < 0) {
            return menu;
        }

        StringBuilder newMenu = new StringBuilder();
        newMenu.append(menu.substring(0, start));
        newMenu.append(menu.substring(end + endSubMenu.length()));

        return newMenu.toString();
    }

    private String createSubMenu(String menu, List<String> menuActions) {

        String startMenu = createStartMenuTag(menu);

        StringBuilder newMenu = new StringBuilder();
        newMenu.append(startMenu);
        newMenu.append(SPACE);
        for (String action : menuActions) {
            newMenu.append(action + SPACE);
        }

        newMenu.append(createEndMenuTag());

        return newMenu.toString();
    }

    private String createStartMenuTag(String subMenu) {
        return BEGIN_SUB_MENU + SPACE + DOUBLE_QUOTES + subMenu + DOUBLE_QUOTES;
    }

    private String createEndMenuTag() {
        return END_SUB_MENU + SPACE + SEPARATOR + SPACE;
    }
}
