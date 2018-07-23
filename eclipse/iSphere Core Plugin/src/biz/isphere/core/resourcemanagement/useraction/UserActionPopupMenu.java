package biz.isphere.core.resourcemanagement.useraction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

public abstract class UserActionPopupMenu extends MenuAdapter {

    private Menu menu;
    private Table table;

    private MenuItem menuItemShowDifferences;

    public UserActionPopupMenu(Menu menu, Table table) {
        this.menu = menu;
        this.table = table;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(menuItemShowDifferences);
    }

    private void dispose(MenuItem menuItem) {
        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        menuItemShowDifferences = new MenuItem(menu, SWT.NONE);
        menuItemShowDifferences.setText(Messages.Show_differences);
        menuItemShowDifferences.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COMPARE));
        menuItemShowDifferences.setEnabled(isEnabled(menuItemShowDifferences, table));
        menuItemShowDifferences.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performAction(menuItemShowDifferences, table);
            }
        });

    }

    protected abstract boolean isEnabled(MenuItem menuItem, Table table);

    protected abstract void performAction(MenuItem menuItem, Table table);
}
