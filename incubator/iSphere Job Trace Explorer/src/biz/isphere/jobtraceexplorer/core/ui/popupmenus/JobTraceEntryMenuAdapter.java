/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.popupmenus;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.ui.actions.AbstractJobTraceEntryAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcEnterAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcExitAction;

public class JobTraceEntryMenuAdapter extends MenuAdapter {

    private Menu parentMenu;
    private TableViewer tableViewer;

    private Shell shell;

    private MenuItem menuItemJumpToProcEnter;
    private MenuItem menuItemJumpToProcExit;

    public JobTraceEntryMenuAdapter(Menu parentMenu, TableViewer tableViewer) {
        this.parentMenu = parentMenu;
        this.tableViewer = tableViewer;

        this.shell = tableViewer.getControl().getShell();
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(menuItemJumpToProcEnter);
        dispose(menuItemJumpToProcExit);
    }

    private int selectedItemsCount() {
        return getSelection().size();
    }

    private StructuredSelection getSelection() {

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new Object[0]);
    }

    private JobTraceEntry getSelectedItem() {
        JobTraceEntry jobTraceEntry = (JobTraceEntry)getSelection().getFirstElement();
        return jobTraceEntry;
    }

    private void dispose(MenuItem menuItem) {

        if (menuItem != null && !menuItem.isDisposed()) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        if (selectedItemsCount() == 1) {

            if (getSelectedItem().isProcExit()) {
                menuItemJumpToProcEnter = createMenuItem(new JumpToProcEnterAction(shell, tableViewer));
            }

            if (getSelectedItem().isProcEntry()) {
                menuItemJumpToProcExit = createMenuItem(new JumpToProcExitAction(shell, tableViewer));
            }
        }
    }

    private MenuItem createMenuItem(AbstractJobTraceEntryAction action) {

        MenuItem menuItem = new MenuItem(parentMenu, SWT.NONE);
        menuItem.setText(action.getText());
        menuItem.setImage(action.getImage());
        menuItem.addSelectionListener(new ActionSelectionListener(action));

        return menuItem;
    }
}
