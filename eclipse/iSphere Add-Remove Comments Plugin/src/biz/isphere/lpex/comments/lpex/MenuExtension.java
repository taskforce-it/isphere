package biz.isphere.lpex.comments.lpex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.lpex.action.CommentAction;
import biz.isphere.lpex.comments.lpex.action.IndentAction;
import biz.isphere.lpex.comments.lpex.action.ToggleCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnIndentAction;
import biz.isphere.lpex.comments.preferences.Preferences;

/**
 * This class extends the popup menue of the Lpex editor. It adds the following
 * options:
 * <ul>
 * <li>Edit STRPREPRC header</li>
 * <li>Remove STRPREPRC header</li>
 * </ul>
 */
public class MenuExtension extends AbstractLpexMenuExtension {

    private static final String MENU_NAME = Messages.Menu_Source;
    private static final String MARK_END = "MARK-" + MENU_NAME + ".End"; //$NON-NLS-1$ //$NON-NLS-2$
    private static final String MARK_START = "MARK-" + MENU_NAME + ".Start"; //$NON-NLS-1$ //$NON-NLS-2$

    @Override
    protected Map<String, String> getUserActions() {

        Map<String, String> actions = new HashMap<String, String>();

        if (isCommentsEnabled()) {
            checkAndAddUserAction(actions, CommentAction.ID, CommentAction.class.getName());
            checkAndAddUserAction(actions, UnCommentAction.ID, UnCommentAction.class.getName());
            checkAndAddUserAction(actions, ToggleCommentAction.ID, ToggleCommentAction.class.getName());
        }

        if (isIndentingEnabled()) {
            checkAndAddUserAction(actions, IndentAction.ID, IndentAction.class.getName());
            checkAndAddUserAction(actions, UnIndentAction.ID, UnIndentAction.class.getName());
        }

        return actions;
    }

    @Override
    protected String getMenuName() {
        return MENU_NAME;
    }

    @Override
    protected String getMarkStart() {
        return MARK_START;
    }

    @Override
    protected String getMarkEnd() {
        return MARK_END;
    }

    @Override
    protected Map<String, String> getUserKeyActions() {

        Map<String, String> actions = new HashMap<String, String>();

        if (isCommentsEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.ADD), CommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.SUBSTRACT), UnCommentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.MULTIPLY), ToggleCommentAction.ID);
        }

        if (isIndentingEnabled()) {
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.TAB), IndentAction.ID);
            checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.TAB), UnIndentAction.ID);
        }

        return actions;
    }

    @Override
    protected ArrayList<String> getMenuActions() {

        ArrayList<String> menuActions = new ArrayList<String>();

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

    private boolean isCommentsEnabled() {
        return Preferences.getInstance().isCommentsEnabled();
    }

    private boolean isIndentingEnabled() {
        return Preferences.getInstance().isIndentionEnabled();
    }
}
