package biz.isphere.core.lpex.menu.model;

public class UserKeyAction extends AbstractLpexAction<UserKeyAction> implements Comparable<UserKeyAction> {

    private String keyStrokes;

    public UserKeyAction(String name, String actionId) {
        super(actionId);
        this.keyStrokes = name;
    }

    public String getKeyStrokes() {
        return keyStrokes;
    }

    @Override
    public String toString() {
        return keyStrokes + ACTION_DELIMITER + getActionId();
    }

    public int compareTo(UserKeyAction action) {

        if (action == null || action.getKeyStrokes() == null) {
            return 1;
        }

        return getKeyStrokes().compareTo(action.getKeyStrokes());
    }
}
