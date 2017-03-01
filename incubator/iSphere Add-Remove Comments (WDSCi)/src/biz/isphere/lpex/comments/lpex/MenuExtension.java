package biz.isphere.lpex.comments.lpex;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.lpex.comments.ISphereAddRemoveCommentsPlugin;
import biz.isphere.lpex.comments.lpex.action.CommentAction;
import biz.isphere.lpex.comments.lpex.action.ToggleCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnCommentAction;

import com.ibm.lpex.core.LpexView;

/**
 * This class extends the popup menue of the Lpex editor. It adds the following
 * options:
 * <ul>
 * <li>Edit STRPREPRC header</li>
 * <li>Remove STRPREPRC header</li>
 * </ul>
 */
public class MenuExtension {

    private static final String MENU_NAME = "iSphere"; //$NON-NLS-1$
    private static final String BEGIN_SUB_MENU = "beginSubmenu"; //$NON-NLS-1$
    private static final String END_SUB_MENU = "endSubmenu"; //$NON-NLS-1$
    private static final String SEPARATOR = "separator"; //$NON-NLS-1$
    private static final String DOUBLE_QUOTES = "\""; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String IBM_SOURCE_SUB_MENU = "popup.sourceMenu"; //$NON-NLS-1$
    private static final String MARK_ISPHERE_SOURCE_END = "MARK-iSphere.Source.End"; //$NON-NLS-1$
    private static final String MARK_ISPHERE_SOURCE_START = "MARK-iSphere.Source.Start"; //$NON-NLS-1$

    public void initializeLpexEditor() {

        ISphereAddRemoveCommentsPlugin.getDefault().setLpexMenuExtension(this);

        LpexView.doGlobalCommand("set default.updateProfile.userActions "
            + getLPEXEditorUserActions(LpexView.globalQuery("current.updateProfile.userActions")));
        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions "
            + getLPEXEditorUserKeyActions(LpexView.globalQuery("current.updateProfile.userKeyActions")));
        LpexView.doGlobalCommand("set default.popup " + getLPEXEditorPopupMenu(LpexView.globalQuery("current.popup")));
        // LpexView.doGlobalCommand("set default.popup install");
    }

    public void uninstall() {

        removeUserActions();
        removeUserKeyActions();
        removePopupMenu();
    }

    private void removeUserActions() {

        StringBuilder existingActions = new StringBuilder(LpexView.globalQuery("current.updateProfile.userActions"));
        ArrayList<String> actions = getUserActions();

        int start;
        for (String action : actions) {
            if ((start = existingActions.indexOf(action)) >= 0) {
                int end = start + action.length();
                existingActions.replace(start, end, "");
            }
        }

        LpexView.doGlobalCommand("set default.updateProfile.userActions " + existingActions.toString().trim());
    }

    private void removeUserKeyActions() {

        StringBuilder existingActions = new StringBuilder(LpexView.globalQuery("current.updateProfile.userKeyActions"));
        ArrayList<String> actions = getUserKeyActions();

        int start;
        for (String action : actions) {
            if ((start = existingActions.indexOf(action)) >= 0) {
                int end = start + action.length();
                existingActions.replace(start, end, "");
            }
        }

        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions " + existingActions.toString().trim());
    }

    private void removePopupMenu() {

        String popupMenu = LpexView.globalQuery("current.popup");
        popupMenu = removeSubMenu(MENU_NAME, popupMenu);

        LpexView.doGlobalCommand("set default.popup " + popupMenu.trim());
    }

    private String getLPEXEditorUserActions(String existingActions) {

        ArrayList<String> actions = getUserActions();

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

    private ArrayList<String> getUserActions() {

        ArrayList<String> actions = new ArrayList<String>();
        actions.add(CommentAction.ID + " " + CommentAction.class.getName());
        actions.add(UnCommentAction.ID + " " + UnCommentAction.class.getName());
        actions.add(ToggleCommentAction.ID + " " + ToggleCommentAction.class.getName());
        // actions.add(AddPreCompileCommandAction.ID + " " +
        // AddPreCompileCommandAction.class.getName());
        // actions.add(AddPostCompileCommandAction.ID + " " +
        // AddPostCompileCommandAction.class.getName());
        // actions.add(EditCommandAction.ID + " " +
        // EditCommandAction.class.getName());

        return actions;
    }

    private String getLPEXEditorUserKeyActions(String existingUserKeyActions) {

        ArrayList<String> actions = getUserKeyActions();

        StringBuilder newUserKeyActions = new StringBuilder();

        if ((existingUserKeyActions == null) || (existingUserKeyActions.equalsIgnoreCase("null"))) {
            for (String action : actions) {
                newUserKeyActions.append(action + " ");
            }
        }

        else {
            newUserKeyActions.append(existingUserKeyActions + " ");
            for (String action : actions) {
                if (existingUserKeyActions.indexOf(action) < 0) {
                    newUserKeyActions.append(action + " ");
                }
            }
        }

        return newUserKeyActions.toString();
    }

    private ArrayList<String> getUserKeyActions() {

        ArrayList<String> actions = new ArrayList<String>();
        // actions.add("c-1" + SPACE + EditHeaderAction.ID);
        // actions.add("c-2" + SPACE + RemoveHeaderAction.ID);

        return actions;
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

        menuActions.add(CommentAction.getLPEXMenuAction());
        menuActions.add(UnCommentAction.getLPEXMenuAction());
        menuActions.add(ToggleCommentAction.getLPEXMenuAction());
        // menuActions.add(null);
        // menuActions.add(AddPreCompileCommandAction.getLPEXMenuAction());
        // menuActions.add(AddPostCompileCommandAction.getLPEXMenuAction());
        // menuActions.add(EditCommandAction.getLPEXMenuAction());

        popupMenu = removeSubMenu(MENU_NAME, popupMenu);

        StringBuilder newMenu = new StringBuilder(createMenuItem(MARK_ISPHERE_SOURCE_START));
        int sourceMenuLocation = findStartOfSourceSubMenu(popupMenu);
        if (sourceMenuLocation >= 0) {
            newMenu.append(createMenuItem(SEPARATOR));
            newMenu.append(createMenuItems(menuActions));
        } else {
            newMenu.append(createSubMenu(MENU_NAME, menuActions));
        }
        newMenu.append(createMenuItem(MARK_ISPHERE_SOURCE_END));

        if (popupMenu != null && popupMenu.contains(newMenu)) {
            return popupMenu;
        }

        if (popupMenu != null) {
            StringBuilder newPopupMenu = new StringBuilder(popupMenu);
            if (sourceMenuLocation >= 0) {
                newPopupMenu.insert(sourceMenuLocation, SPACE);
                newPopupMenu.insert(sourceMenuLocation + SPACE.length(), newMenu);
            } else {
                newPopupMenu.append(SPACE);
                newPopupMenu.append(newMenu);
            }
            return newPopupMenu.toString();
        }

        return newMenu.toString();
    }

    private int findStartOfSourceSubMenu(String menu) {

        int i = menu.indexOf(IBM_SOURCE_SUB_MENU);
        if (i >= 0) {
            i = i + IBM_SOURCE_SUB_MENU.length();
        }

        return i;
    }

    private String removeSubMenu(String subMenu, String menu) {

        int start = menu.indexOf(MARK_ISPHERE_SOURCE_START);
        if (start < 0) {
            return menu;
        }

        String endSubMenu = MARK_ISPHERE_SOURCE_END;
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

        StringBuilder newMenu = new StringBuilder();

        // Start submenu
        newMenu.append(createStartMenuTag(menu));

        // Add menu items
        newMenu.append(createMenuItems(menuActions));

        // End submenu
        newMenu.append(createEndMenuTag());

        return newMenu.toString();
    }

    private String createMenuItems(List<String> menuActions) {

        StringBuilder menuItems = new StringBuilder();
        for (String action : menuActions) {
            if (action == null) {
                menuItems.append(createMenuItem(SEPARATOR));
            } else {
                menuItems.append(createMenuItem(action));
            }
        }

        return menuItems.toString();
    }

    private String createStartMenuTag(String subMenu) {
        return BEGIN_SUB_MENU + SPACE + DOUBLE_QUOTES + subMenu + DOUBLE_QUOTES + SPACE;
    }

    private String createMenuItem(String action) {
        return action + SPACE;
    }

    private String createEndMenuTag() {
        return END_SUB_MENU + SPACE;
    }
}
