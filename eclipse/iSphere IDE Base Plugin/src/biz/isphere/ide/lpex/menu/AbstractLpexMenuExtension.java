/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide.lpex.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.ide.lpex.menu.model.AbstractLpexAction;
import biz.isphere.ide.lpex.menu.model.UserAction;
import biz.isphere.ide.lpex.menu.model.UserKeyAction;

import com.ibm.lpex.alef.LpexPlugin;
import com.ibm.lpex.core.LpexView;

// MARK-Source.Start
// MARK-Source.End
//
// beginSubmenu "STRPREPRC"
// endSubmenu
//
// beginSubmenu "STRPREPRC"
// endSubmenu
//
// MARK-biz.Source.Start
// MARK-biz.Source.End
//
// MARK-biz.Quelle.Start
// MARK-biz.Quelle.End
//
// MARK-biz.iSphere.LPEX.Start
// MARK-biz.iSphere.LPEX.End
//
// MARK-biz.iSphere.STRPREPRC.Start
// MARK-biz.iSphere.STRPREPRC.End

public abstract class AbstractLpexMenuExtension implements ILpexMenuExtension {

    protected static final String BEGIN_SUB_MENU = "beginSubmenu"; //$NON-NLS-1$
    protected static final String END_SUB_MENU = "endSubmenu"; //$NON-NLS-1$
    protected static final String SEPARATOR = "separator"; //$NON-NLS-1$
    protected static final String DOUBLE_QUOTES = "\""; //$NON-NLS-1$
    protected static final String ACTION_DELIMITER = " "; //$NON-NLS-1$

    protected static final int TOP = 1;
    protected static final int BOTTOM = 2;

    private int position; // Menu position
    IPropertyChangeListener listener;

    public AbstractLpexMenuExtension(int position) {
        this.position = position;
    }

    protected abstract String getMenuName();

    protected abstract String getMarkId();

    protected abstract IPropertyChangeListener getPreferencesChangeListener();

    public void initializeLpexEditor(LpexMenuExtensionPlugin plugin) {

        // Just in case RDi crashed before.
        uninstall();

        plugin.setLpexMenuExtension(this);

        doSetLpexViewUserActions(getLPEXEditorUserActions(getCurrentLpexUserActions()));
        doSetLpexViewUserKeyActions(getLPEXEditorUserKeyActions(getCurrentLpexUserKeyActions()));
        doSetLpexViewPopup(getLPEXEditorPopupMenu(getCurrentLpexPopupMenu()));

        listener = getPreferencesChangeListener();
        if (listener != null) {
            LpexPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
        }
    }

    public void uninstall() {

        if (listener != null) {
            LpexPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(listener);
            listener = null;
        }

        removeUserActions();
        removeUserKeyActions();
        removePopupMenu();
    }

    private void doSetLpexViewUserActions(String userActions) {
        LpexView.doGlobalCommand("set default.updateProfile.userActions " + userActions); //$NON-NLS-1$
    }

    private void doSetLpexViewUserKeyActions(String userKeyActions) {
        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions " + userKeyActions); //$NON-NLS-1$
    }

    protected void doSetLpexViewPopup(String popup) {
        LpexView.doGlobalCommand("set default.popup " + popup); //$NON-NLS-1$
    }

    private UserAction[] getCurrentLpexUserActions() {
        return parseUserActions(LpexView.globalQuery("current.updateProfile.userActions")); //$NON-NLS-1$
    }

    private UserKeyAction[] getCurrentLpexUserKeyActions() {
        return parseUserKeyActions(LpexView.globalQuery("current.updateProfile.userKeyActions")); //$NON-NLS-1$
    }

    protected String getCurrentLpexPopupMenu() {
        return LpexView.globalQuery("current.popup"); //$NON-NLS-1$
    }

    protected UserAction[] parseUserActions(String actions) {

        List<UserAction> actionsList = new LinkedList<UserAction>();

        if (actions != null) {
            String[] parts = actions.split(ACTION_DELIMITER);
            int i = 0;
            while (i < parts.length - 1) {
                actionsList.add(new UserAction(parts[i], parts[i + 1]));
                i = i + 2;
            }
        }

        return actionsList.toArray(new UserAction[actionsList.size()]);
    }

    protected UserKeyAction[] parseUserKeyActions(String actions) {

        List<UserKeyAction> actionsList = new LinkedList<UserKeyAction>();

        if (actions != null) {
            actions = actions.trim();
            if (actions.startsWith(DOUBLE_QUOTES)) {
                actions = actions.substring(1);
            }

            if (actions.endsWith(DOUBLE_QUOTES)) {
                actions = actions.substring(0, actions.length() - 1);
            }

            String[] parts = actions.split(ACTION_DELIMITER);
            int i = 0;
            while (i < parts.length - 1) {
                actionsList.add(new UserKeyAction(parts[i], parts[i + 1]));
                i = i + 2;
            }
        }

        return actionsList.toArray(new UserKeyAction[actionsList.size()]);
    }

    private void removeUserActions() {

        UserAction[] existingActions = getCurrentLpexUserActions();
        Map<String, UserAction> userActions = new HashMap<String, UserAction>();
        for (UserAction action : existingActions) {
            userActions.put(action.getActionId(), action);
        }

        UserAction[] actions = getUserActions();
        for (UserAction action : actions) {
            if (userActions.containsKey(action.getActionId())) {
                userActions.remove(action.getActionId());
            }
        }

        StringBuilder buffer = new StringBuilder();
        for (UserAction action : userActions.values()) {
            appendActionToBuffer(buffer, action);
        }

        doSetLpexViewUserActions(buffer.toString());
    }

    private void removeUserKeyActions() {

        UserKeyAction[] existingActions = getCurrentLpexUserKeyActions();
        Map<String, UserKeyAction> userActions = new HashMap<String, UserKeyAction>();
        for (UserKeyAction action : existingActions) {
            userActions.put(action.getKeyStrokes(), action);
        }

        UserKeyAction[] actions = getUserKeyActions();
        for (UserKeyAction action : actions) {
            if (userActions.containsKey(action.getKeyStrokes())) {
                userActions.remove(action.getKeyStrokes());
            }
        }

        StringBuilder buffer = new StringBuilder();
        for (UserKeyAction action : userActions.values()) {
            appendActionToBuffer(buffer, action);
        }

        doSetLpexViewUserKeyActions(buffer.toString());
    }

    private void removePopupMenu() {

        String popupMenu = getCurrentLpexPopupMenu();
        if (popupMenu != null) {
            popupMenu = removeMenuItems(popupMenu, getMarkStart(), getMarkEnd());
        }

        doSetLpexViewPopup(popupMenu.trim());
    }

    private String getLPEXEditorUserActions(UserAction[] existingActions) {

        Set<String> actionNames = new HashSet<String>();
        List<UserAction> newUserActions = new LinkedList<UserAction>();

        for (UserAction action : existingActions) {
            newUserActions.add(action);
            actionNames.add(action.getActionId());
        }

        UserAction[] actions = getUserActions();
        for (UserAction action : actions) {
            if (!actionNames.contains(action.getActionId())) {
                newUserActions.add(action);
                actionNames.add(action.getActionId());
            } else {
                // ISphereAddRemoveCommentsPlugin.logError("STRPREPRC plug-in conflict: Lpex user action exists: "
                // + action.getActionId(), null);
            }
        }

        StringBuilder buffer = new StringBuilder();
        for (UserAction action : newUserActions) {
            appendActionToBuffer(buffer, action);
        }

        return buffer.toString();
    }

    protected abstract UserAction[] getUserActions();

    protected void checkAndAddUserAction(List<UserAction> actions, String actionId, String className) {

        actions.add(new UserAction(actionId, className));
    }

    protected static void appendActionToBuffer(StringBuilder buffer, AbstractLpexAction<? extends AbstractLpexAction<?>> action) {

        if (buffer.length() > 0) {
            buffer.append(ACTION_DELIMITER);
        }

        buffer.append(action.toString());
    }

    private String getLPEXEditorUserKeyActions(UserKeyAction[] existingActions) {

        Set<String> actionKeyStrokes = new HashSet<String>();
        List<UserKeyAction> newUserActions = new LinkedList<UserKeyAction>();

        for (UserKeyAction action : existingActions) {
            newUserActions.add(action);
            actionKeyStrokes.add(action.getKeyStrokes());
        }

        UserKeyAction[] actions = getUserKeyActions();
        for (UserKeyAction action : actions) {
            if (!actionKeyStrokes.contains(action.getKeyStrokes())) {
                newUserActions.add(action);
                actionKeyStrokes.add(action.getKeyStrokes());
            } else {
                ISpherePlugin.logError("STRPREPRC plug-in conflict: Lpex user key action exists: " + action.getKeyStrokes(), null); //$NON-NLS-1$
            }
        }

        StringBuilder buffer = new StringBuilder();
        for (UserKeyAction action : newUserActions) {
            appendActionToBuffer(buffer, action);
        }

        return buffer.toString();
    }

    protected abstract UserKeyAction[] getUserKeyActions();

    protected static String createShortcut(String... modifiers) {

        StringBuilder buffer = new StringBuilder();
        for (String modifier : modifiers) {
            if (buffer.length() > 0) {
                buffer.append("-"); //$NON-NLS-1$
            }
            buffer.append(modifier);
        }

        return buffer.toString();
    }

    protected static void checkAndAddUserKeyAction(List<UserKeyAction> actions, String shortcut, String actionId) {

        String existingActions = LpexView.globalQuery("current.updateProfile.userKeyActions"); //$NON-NLS-1$

        String userKeyAction = shortcut + ACTION_DELIMITER + actionId;

        if (existingActions == null || existingActions.indexOf(userKeyAction) < 0) {
            actions.add(new UserKeyAction(shortcut, actionId));
        }
    }

    public static String getInitialUserKeyActions() {
        return ""; //$NON-NLS-1$
    }

    private String getLPEXEditorPopupMenu(String popupMenu) {

        List<String> menuActions = getMenuActions();

        String newPopupMenu = addMenuItems(popupMenu, menuActions);

        return newPopupMenu;
    }

    private String addMenuItems(String popupMenu, List<String> menuActions) {

        StringBuilder newMenu = new StringBuilder(createMenuItem(getMarkStart()));

        int sourceMenuLocation;
        if (popupMenu != null) {
            sourceMenuLocation = findStartOfLpexSubMenu(popupMenu);
        } else {
            sourceMenuLocation = -1;
        }

        if (sourceMenuLocation >= 0) {
            newMenu.append(createMenuItem(SEPARATOR));
            newMenu.append(createMenuItems(menuActions));
        } else {
            newMenu.append(createSubMenu(getMenuName(), menuActions));
        }

        newMenu.append(createMenuItem(getMarkEnd()));

        if (popupMenu != null && popupMenu.contains(newMenu)) {
            return popupMenu;
        }

        if (popupMenu != null) {

            String before;
            String after;
            if (position == TOP) {
                before = ""; //$NON-NLS-1$
                after = ACTION_DELIMITER + popupMenu;
            } else {
                before = popupMenu;
                after = ""; //$NON-NLS-1$
            }

            StringBuilder newPopupMenu = new StringBuilder(before);
            if (sourceMenuLocation >= 0) {
                newPopupMenu.insert(sourceMenuLocation, ACTION_DELIMITER);
                newPopupMenu.insert(sourceMenuLocation + ACTION_DELIMITER.length(), newMenu);
            } else {
                newPopupMenu.append(ACTION_DELIMITER);
                newPopupMenu.append(newMenu);
            }
            newPopupMenu.append(after);
            return newPopupMenu.toString();
        }

        return newMenu.toString();
    }

    private String getMarkStart() {
        return "MARK-" + getMarkId() + ".End"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String getMarkEnd() {
        return "MARK-" + getMarkId() + ".Start"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected abstract List<String> getMenuActions();

    protected abstract int findStartOfLpexSubMenu(String menu);

    protected String removeMenuItems(String menu, String startMark, String endMark) {

        int start = menu.indexOf(startMark);
        if (start < 0) {
            return menu;
        }

        String endSubMenu = endMark;
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
        return BEGIN_SUB_MENU + ACTION_DELIMITER + DOUBLE_QUOTES + subMenu + DOUBLE_QUOTES + ACTION_DELIMITER;
    }

    private String createMenuItem(String action) {
        return action + ACTION_DELIMITER;
    }

    private String createEndMenuTag() {
        return END_SUB_MENU + ACTION_DELIMITER + createMenuItem(SEPARATOR);
    }
}
