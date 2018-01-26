/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.rse.core.RSECorePlugin;

import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.lpex.menu.AbstractLpexMenuExtension;
import biz.isphere.core.lpex.menu.LpexMenuExtensionPlugin;
import biz.isphere.core.lpex.menu.model.UserAction;
import biz.isphere.core.lpex.menu.model.UserKeyAction;
import biz.isphere.lpex.comments.lpex.action.CommentAction;
import biz.isphere.lpex.comments.lpex.action.IndentAction;
import biz.isphere.lpex.comments.lpex.action.ToggleCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnIndentAction;
import biz.isphere.lpex.comments.preferences.Preferences;

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
    private static final String MARK_ID = "biz.iSphere.LPEX"; //$NON-NLS-1$

    public MenuExtension() {
        super(BOTTOM);
    }

    @Override
    public void initializeLpexEditor(LpexMenuExtensionPlugin plugin) {

        removeOldPopupMenu();
        super.initializeLpexEditor(plugin);
    }

    @Override
    protected UserAction[] getUserActions() {

        List<UserAction> actions = new LinkedList<UserAction>();

        if (isCommentsEnabled()) {
            checkAndAddUserAction(actions, CommentAction.ID, CommentAction.class.getName());
            checkAndAddUserAction(actions, UnCommentAction.ID, UnCommentAction.class.getName());
            checkAndAddUserAction(actions, ToggleCommentAction.ID, ToggleCommentAction.class.getName());
        }

        if (isIndentingEnabled()) {
            checkAndAddUserAction(actions, IndentAction.ID, IndentAction.class.getName());
            checkAndAddUserAction(actions, UnIndentAction.ID, UnIndentAction.class.getName());
        }

        return actions.toArray(new UserAction[actions.size()]);
    }

    @Override
    protected String getMenuName() {
        return MENU_NAME;
    }

    @Override
    protected String getMarkId() {
        return MARK_ID;
    }

    @Override
    protected UserKeyAction[] getUserKeyActions() {

        List<UserKeyAction> actions = new LinkedList<UserKeyAction>();

        if (isCommentsEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.ADD), CommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.SUBSTRACT), UnCommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.MULTIPLY), ToggleCommentAction.ID);
        }

        if (isIndentingEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.TAB), IndentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.TAB), UnIndentAction.ID);
        }

        return actions.toArray(new UserKeyAction[actions.size()]);
    }

    @Override
    protected List<String> getMenuActions() {

        List<String> menuActions = new ArrayList<String>();

        if (isCommentsEnabled()) {
            menuActions.add(CommentAction.getLPEXMenuAction());
            menuActions.add(UnCommentAction.getLPEXMenuAction());
            menuActions.add(ToggleCommentAction.getLPEXMenuAction());
        }

        if (isCommentsEnabled() && isIndentingEnabled()) {
            menuActions.add(null); // Add separator
        }

        if (isIndentingEnabled()) {
            menuActions.add(IndentAction.getLPEXMenuAction());
            menuActions.add(UnIndentAction.getLPEXMenuAction());
        }

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

        if (isCommentsEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.ADD), CommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.SUBSTRACT), UnCommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.MULTIPLY), ToggleCommentAction.ID);
        }

        if (isIndentingEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.TAB), IndentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.TAB), UnIndentAction.ID);
        }

        StringBuilder buffer = new StringBuilder();
        for (UserKeyAction action : actions) {
            appendActionToBuffer(buffer, action);
        }

        return buffer.toString();

    }

    private static boolean isCommentsEnabled() {
        return Preferences.getInstance().isCommentsEnabled();
    }

    private static boolean isIndentingEnabled() {
        return Preferences.getInstance().isIndentionEnabled();
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

        String popupMenu = getCurrentLpexPopupMenu();

        // MARK-Source.Start / MARK-Source.End
        popupMenu = removeMenuItems(popupMenu, "MARK-Source.Start", "MARK-Source.End"); //$NON-NLS-1$ //$NON-NLS-2$

        // MARK-Quelle.Start / MARK-Quelle.End
        popupMenu = removeMenuItems(popupMenu, "MARK-Quelle.Start", "MARK-Quelle.End"); //$NON-NLS-1$ //$NON-NLS-2$

        doSetLpexViewPopup(popupMenu);
    }

    @Override
    protected void waitForRseSubsystem() {
        try {
            RSECorePlugin.waitForInitCompletion();
        } catch (InterruptedException e) {
            MessageDialogAsync.displayError("Lpex Editor: RSE plug-in has not been correctly initialized.");
        }
    }
}
