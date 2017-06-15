package biz.isphere.lpex.comments.lpex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.lpex.action.CommentAction;
import biz.isphere.lpex.comments.lpex.action.ToggleCommentAction;
import biz.isphere.lpex.comments.lpex.action.UnCommentAction;

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
        checkAndAddUserAction(actions, CommentAction.ID, CommentAction.class.getName());
        checkAndAddUserAction(actions, UnCommentAction.ID, UnCommentAction.class.getName());
        checkAndAddUserAction(actions, ToggleCommentAction.ID, ToggleCommentAction.class.getName());

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
        checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.ADD), CommentAction.ID);
        checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.MULTIPLY), ToggleCommentAction.ID);
        checkAndAddUserKeyAction(actions, createShortcut(LpexKey.CTRL, LpexKey.SHIFT, LpexKey.SUBSTRACT), UnCommentAction.ID);

        return actions;
    }

    @Override
    protected ArrayList<String> getMenuActions() {

        ArrayList<String> menuActions = new ArrayList<String>();

        menuActions.add(CommentAction.getLPEXMenuAction());
        menuActions.add(UnCommentAction.getLPEXMenuAction());
        menuActions.add(ToggleCommentAction.getLPEXMenuAction());
        // menuActions.add(null); // Add seperator

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
}
