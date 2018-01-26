/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import biz.isphere.core.lpex.menu.AbstractLpexMenuExtension;
import biz.isphere.core.lpex.menu.LpexMenuExtensionPlugin;
import biz.isphere.core.lpex.menu.model.UserAction;
import biz.isphere.core.lpex.menu.model.UserKeyAction;
import biz.isphere.strpreprc.lpex.action.AddPostCompileCommandAction;
import biz.isphere.strpreprc.lpex.action.AddPreCompileCommandAction;
import biz.isphere.strpreprc.lpex.action.EditCommandAction;
import biz.isphere.strpreprc.lpex.action.EditHeaderAction;
import biz.isphere.strpreprc.lpex.action.RemoveHeaderAction;
import biz.isphere.strpreprc.preferences.Preferences;

/**
 * This class extends the popup menue of the Lpex editor. It adds the following
 * options:
 * <ul>
 * <li>Edit STRPREPRC header</li>
 * <li>Remove STRPREPRC header</li>
 * </ul>
 */
public class MenuExtension extends AbstractLpexMenuExtension implements IPropertyChangeListener {

    private static final String PROPERTY_LPEX_USER_KEY_ACTIONS = "default.updateProfile.userKeyActions"; //$NON-NLS-1$
    private static final String MENU_NAME = "STRPREPRC"; //$NON-NLS-1$
    private static final String MARK_ID = "biz.iSphere.STRPREPRC"; //$NON-NLS-1$

    public MenuExtension() {
        super(TOP);
    }

    @Override
    public void initializeLpexEditor(LpexMenuExtensionPlugin plugin) {

        removeOldPopupMenu();
        super.initializeLpexEditor(plugin);
    }

    protected UserAction[] getUserActions() {

        List<UserAction> actions = new LinkedList<UserAction>();
        actions.add(new UserAction(EditHeaderAction.ID, EditHeaderAction.class.getName()));
        actions.add(new UserAction(RemoveHeaderAction.ID, RemoveHeaderAction.class.getName()));
        actions.add(new UserAction(AddPreCompileCommandAction.ID, AddPreCompileCommandAction.class.getName()));
        actions.add(new UserAction(AddPostCompileCommandAction.ID, AddPostCompileCommandAction.class.getName()));
        actions.add(new UserAction(EditCommandAction.ID, EditCommandAction.class.getName()));

        return actions.toArray(new UserAction[actions.size()]);
    }

    @Override
    protected String getMenuName() {
        return MENU_NAME;
    }

    protected String getMarkId() {
        return MARK_ID;
    }

    protected UserKeyAction[] getUserKeyActions() {

        UserKeyAction[] actions = parseUserKeyActions(Preferences.getInstance().getUserKeyActions());

        return actions;
    }

    protected List<String> getMenuActions() {

        List<String> menuActions = new ArrayList<String>();

        menuActions.add(EditHeaderAction.getLPEXMenuAction());
        menuActions.add(RemoveHeaderAction.getLPEXMenuAction());
        menuActions.add(null);
        menuActions.add(AddPreCompileCommandAction.getLPEXMenuAction());
        menuActions.add(AddPostCompileCommandAction.getLPEXMenuAction());
        menuActions.add(EditCommandAction.getLPEXMenuAction());

        return menuActions;
    }

    protected int findStartOfLpexSubMenu(String menu) {

        return -1;
    }

    @Override
    protected IPropertyChangeListener getPreferencesChangeListener() {
        return this;
    }

    public static String getInitialUserKeyActions() {

        List<UserKeyAction> actions = new LinkedList<UserKeyAction>();
        //        actions.add(new UserKeyAction("c-s-2", EditHeaderAction.ID)); //$NON-NLS-1$
        //        actions.add(new UserKeyAction("c-s-4", RemoveHeaderAction.ID)); //$NON-NLS-1$

        StringBuilder buffer = new StringBuilder();
        for (UserKeyAction action : actions) {
            appendActionToBuffer(buffer, action);
        }

        return buffer.toString();

    }

    public void propertyChange(PropertyChangeEvent event) {

        if (!PROPERTY_LPEX_USER_KEY_ACTIONS.equals(event.getProperty())) {
            return;
        }

        UserKeyAction[] newUserKeyActions = parseUserKeyActions((String)event.getNewValue());

        UserAction[] userActionsList = getUserActions();
        Set<String> knownActionClasses = new HashSet<String>();
        for (UserAction action : userActionsList) {
            knownActionClasses.add(action.getActionId());
        }

        StringBuilder buffer = new StringBuilder();
        for (UserKeyAction action : newUserKeyActions) {
            if (knownActionClasses.contains(action.getActionId())) {
                appendActionToBuffer(buffer, action);
            }
        }

        Preferences.getInstance().setUserKeyActions(buffer.toString());
    }

    /*
     * TODO: remove start of start of 2019
     */
    private void removeOldPopupMenu() {

        final String SPACE = " "; //$NON-NLS-1$
        final String subMenu = "STRPREPRC"; //$NON-NLS-1$

        String popupMenu = getCurrentLpexPopupMenu();

        // beginSubmenu "STRPREPRC"
        String startMenu = BEGIN_SUB_MENU + SPACE + DOUBLE_QUOTES + subMenu + DOUBLE_QUOTES;

        // endSubmenu separator
        String endMenu = END_SUB_MENU + SPACE + SEPARATOR + SPACE;

        popupMenu = removeMenuItems(popupMenu, startMenu, endMenu);

        doSetLpexViewPopup(popupMenu);
    }
}
