/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.isphere.lpex.comments.ISphereAddRemoveCommentsPlugin;

import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexMenuExtension implements ILpexMenuExtension {

    private static final String BEGIN_SUB_MENU = "beginSubmenu"; //$NON-NLS-1$
    private static final String END_SUB_MENU = "endSubmenu"; //$NON-NLS-1$
    private static final String SEPARATOR = "separator"; //$NON-NLS-1$
    private static final String DOUBLE_QUOTES = "\""; //$NON-NLS-1$
    private static final String SPACE_CHAR = " "; //$NON-NLS-1$
    private static final String NOTHING = ""; //$NON-NLS-1$

    private Map<String, String> userActions;
    private Map<String, String> userKeyActions;

    protected abstract String getMenuName();

    protected abstract String getMarkStart();

    protected abstract String getMarkEnd();

    public void initializeLpexEditor() {

        ISphereAddRemoveCommentsPlugin.getDefault().setLpexMenuExtension(this);

        userActions = new HashMap<String, String>();
        userKeyActions = new HashMap<String, String>();

        LpexView.doGlobalCommand("set default.updateProfile.userActions " //$NON-NLS-1$
            + getLPEXEditorUserActions(LpexView.globalQuery("current.updateProfile.userActions"))); //$NON-NLS-1$
        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions " //$NON-NLS-1$
            + getLPEXEditorUserKeyActions(LpexView.globalQuery("current.updateProfile.userKeyActions"))); //$NON-NLS-1$
        LpexView.doGlobalCommand("set default.popup " + getLPEXEditorPopupMenu(LpexView.globalQuery("current.popup"))); //$NON-NLS-1$ //$NON-NLS-2$
        // LpexView.doGlobalCommand("set default.popup install");
    }

    public void uninstall() {

        removeUserActions();
        removeUserKeyActions();
        removePopupMenu();
    }

    private void removeUserActions() {

        String existingUserActions = LpexView.globalQuery("current.updateProfile.userActions"); //$NON-NLS-1$
        String newUserActions = removeActions(existingUserActions, userActions);

        LpexView.doGlobalCommand("set default.updateProfile.userActions " + newUserActions); //$NON-NLS-1$
    }

    private void removeUserKeyActions() {

        String existingUserKeyActions = LpexView.globalQuery("current.updateProfile.userKeyActions"); //$NON-NLS-1$
        String newUserKeyActions = removeActions(existingUserKeyActions, userKeyActions);

        LpexView.doGlobalCommand("set default.updateProfile.userKeyActions " + newUserKeyActions); //$NON-NLS-1$
    }

    private String removeActions(String existingActions, Map<String, String> actions) {

        StringBuilder buffer = new StringBuilder(existingActions);

        int start;
        for (String action : actions.values()) {
            if ((start = buffer.indexOf(action)) >= 0) {
                int end = start + action.length();
                buffer.replace(start, end, NOTHING);
            }
        }

        return buffer.toString().trim();
    }

    private void removePopupMenu() {

        String popupMenu = LpexView.globalQuery("current.popup"); //$NON-NLS-1$
        popupMenu = removeMenuItems(popupMenu, getMarkStart(), getMarkEnd());

        LpexView.doGlobalCommand("set default.popup " + popupMenu.trim()); //$NON-NLS-1$
    }

    private String getLPEXEditorUserActions(String existingUserActions) {

        Map<String, String> actions = getUserActions();

        String newUserActions = addActions(existingUserActions, actions, userActions);

        return newUserActions;
    }

    protected abstract Map<String, String> getUserActions();

    protected void checkAndAddUserAction(Map<String, String> actions, String actionId, String className) {

        String existingActions = LpexView.globalQuery("current.updateProfile.userActions"); //$NON-NLS-1$

        String userAction = actionId + SPACE_CHAR + className;

        if (existingActions.indexOf(userAction) < 0) {
            actions.put(actionId, userAction);
        }
    }

    private String getLPEXEditorUserKeyActions(String existingUserKeyActions) {

        Map<String, String> actions = getUserKeyActions();

        String newUserKeyActions = addActions(existingUserKeyActions, actions, userKeyActions);

        return newUserKeyActions;
    }

    private String addActions(String existingActions, Map<String, String> actions, Map<String, String> addedActions) {

        StringBuilder newUserKeyActions = new StringBuilder();

        if (!isNullOrEmpty(existingActions)) {
            newUserKeyActions.append(existingActions + SPACE_CHAR);
        }

        for (String shortcut : actions.keySet()) {
            String action = actions.get(shortcut);
            newUserKeyActions.append(action + SPACE_CHAR);
            addedActions.put(shortcut, action);
        }

        return newUserKeyActions.toString();
    }

    private boolean isNullOrEmpty(String actions) {
        return actions == null || "null".equalsIgnoreCase(actions) || actions.trim().length() == 0; //$NON-NLS-1$
    }

    protected abstract Map<String, String> getUserKeyActions();

    protected String createShortcut(String... modifiers) {

        StringBuilder buffer = new StringBuilder();
        for (String modifier : modifiers) {
            if (buffer.length() > 0) {
                buffer.append("-"); //$NON-NLS-1$
            }
            buffer.append(modifier);
        }

        return buffer.toString();
    }

    protected void checkAndAddUserKeyAction(Map<String, String> actions, String shortcut, String actionId) {

        String existingActions = LpexView.globalQuery("current.updateProfile.userKeyActions"); //$NON-NLS-1$

        String userKeyAction = shortcut + SPACE_CHAR + actionId;

        if (existingActions.indexOf(userKeyAction) < 0) {
            actions.put(shortcut, userKeyAction);
        }
    }

    private String getLPEXEditorPopupMenu(String popupMenu) {

        ArrayList<String> menuActions = getMenuActions();

        String newPopupMenu = addMenuItems(popupMenu, menuActions);

        return newPopupMenu;
    }

    private String addMenuItems(String popupMenu, ArrayList<String> menuActions) {

        StringBuilder newMenu = new StringBuilder(createMenuItem(getMarkStart()));

        int sourceMenuLocation = findStartOfLpexSubMenu(popupMenu);
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
            StringBuilder newPopupMenu = new StringBuilder(popupMenu);
            if (sourceMenuLocation >= 0) {
                newPopupMenu.insert(sourceMenuLocation, SPACE_CHAR);
                newPopupMenu.insert(sourceMenuLocation + SPACE_CHAR.length(), newMenu);
            } else {
                newPopupMenu.append(SPACE_CHAR);
                newPopupMenu.append(newMenu);
            }
            return newPopupMenu.toString();
        }
        return newMenu.toString();
    }

    protected abstract ArrayList<String> getMenuActions();

    protected abstract int findStartOfLpexSubMenu(String menu);

    private String removeMenuItems(String menu, String startMark, String endMark) {

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
        return BEGIN_SUB_MENU + SPACE_CHAR + DOUBLE_QUOTES + subMenu + DOUBLE_QUOTES + SPACE_CHAR;
    }

    private String createMenuItem(String action) {
        return action + SPACE_CHAR;
    }

    private String createEndMenuTag() {
        return END_SUB_MENU + SPACE_CHAR;
    }
}
