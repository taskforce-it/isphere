package biz.isphere.journalexplorer.core.ui.popupmenus;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.actions.ShowSideBySideAction;

public class JournalEntryMenuAdapter extends MenuAdapter {

    private Tree tree;
    private TableViewer tableViewer;
    private Menu menuTableMembers;
    private Shell shell;
    private MenuItem showSideBySideMenuItem;

    public JournalEntryMenuAdapter(Menu menuTableMembers, Tree tree) {
        this.tree = tree;
        this.tableViewer = null;
        this.shell = tree.getShell();
        this.menuTableMembers = menuTableMembers;
    }

    public JournalEntryMenuAdapter(Menu menuTableMembers, TableViewer tableViewer) {
        this.tree = null;
        this.tableViewer = tableViewer;
        this.shell = tableViewer.getControl().getShell();
        this.menuTableMembers = menuTableMembers;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(showSideBySideMenuItem);
    }

    private int selectedItemsCount() {
        return getSelection().size();
    }

    private StructuredSelection getSelection() {

        if (tree != null) {
            List<JournalProperties> journalProperties = new LinkedList<JournalProperties>();
            for (TreeItem treeItem : tree.getSelection()) {
                Object data = treeItem.getData();
                if (data instanceof JournalProperties) {
                    journalProperties.add((JournalProperties)treeItem.getData());
                }
            }
            return new StructuredSelection(journalProperties.toArray(new JournalProperties[journalProperties.size()]));
        }

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new Object[0]);
    }

    private void dispose(MenuItem menuItem) {

        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        if (selectedItemsCount() == 2) {
            showSideBySideMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final ShowSideBySideAction action = new ShowSideBySideAction(shell);
            showSideBySideMenuItem.setText(action.getText());
            showSideBySideMenuItem.setImage(action.getImage());
            showSideBySideMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ShowSideBySideAction showSideBySideAction = new ShowSideBySideAction(shell);
                    showSideBySideAction.setSelectedItems(getSelection());
                    showSideBySideAction.run();
                }
            });
        }
    }
}
