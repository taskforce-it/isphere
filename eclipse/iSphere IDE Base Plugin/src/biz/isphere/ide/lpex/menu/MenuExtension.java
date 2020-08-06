/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide.lpex.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import biz.isphere.ide.lpex.actions.CompareEditorLpexAction;
import biz.isphere.ide.lpex.menu.model.UserAction;
import biz.isphere.ide.lpex.menu.model.UserKeyAction;

import com.ibm.lpex.alef.LpexPlugin;

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
    private static final String MENU_NAME = LpexPlugin.getResourceLpexString(LpexMenu.SOURCE);
    private static final String MARK_ID = "biz.iSphere.Core"; //$NON-NLS-1$

    public MenuExtension() {
        super(BOTTOM);
    }

    @Override
    public void initializeLpexEditor(LpexMenuExtensionPlugin plugin) {
        super.initializeLpexEditor(plugin);
    }

    protected UserAction[] getUserActions() {

        List<UserAction> actions = new LinkedList<UserAction>();
        actions.add(new UserAction(CompareEditorLpexAction.ID, CompareEditorLpexAction.class.getName()));
        // actions.add(new UserAction(RemoveHeaderAction.ID,
        // RemoveHeaderAction.class.getName()));
        // actions.add(new UserAction(AddPreCompileCommandAction.ID,
        // AddPreCompileCommandAction.class.getName()));
        // actions.add(new UserAction(AddPostCompileCommandAction.ID,
        // AddPostCompileCommandAction.class.getName()));
        // actions.add(new UserAction(EditCommandAction.ID,
        // EditCommandAction.class.getName()));

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

        // UserKeyAction[] actions =
        // parseUserKeyActions(Preferences.getInstance().getUserKeyActions());
        UserKeyAction[] actions = new UserKeyAction[0];

        return actions;
    }

    protected List<String> getMenuActions() {

        List<String> menuActions = new ArrayList<String>();

        menuActions.add(CompareEditorLpexAction.getLPEXMenuAction());
        // menuActions.add(RemoveHeaderAction.getLPEXMenuAction());
        // menuActions.add(null);
        // menuActions.add(AddPreCompileCommandAction.getLPEXMenuAction());
        // menuActions.add(AddPostCompileCommandAction.getLPEXMenuAction());
        // menuActions.add(EditCommandAction.getLPEXMenuAction());

        return menuActions;
    }

    @Override
    protected int findStartOfLpexSubMenu(String menu) {

        int i = menu.indexOf(LpexMenu.SOURCE);
        if (i >= 0) {
            i = i + LpexMenu.SOURCE.length();
        }

        return i;
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

        // Preferences.getInstance().setUserKeyActions(buffer.toString());
    }
}
