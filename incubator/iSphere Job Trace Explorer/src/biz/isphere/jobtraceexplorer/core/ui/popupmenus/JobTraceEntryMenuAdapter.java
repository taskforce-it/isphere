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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcEnterAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcExitAction;

public class JobTraceEntryMenuAdapter extends MenuAdapter {

    private TableViewer tableViewer;
    private Menu menuTableMembers;
    private Shell shell;
    private MenuItem jumpToProcEnterMenuItem;
    private MenuItem jumpToProcExitMenuItem;

    public JobTraceEntryMenuAdapter(Menu menuTableMembers, TableViewer tableViewer) {
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
        dispose(jumpToProcEnterMenuItem);
        dispose(jumpToProcExitMenuItem);
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

        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        if (selectedItemsCount() == 1) {

            if (getSelectedItem().isProcExit()) {
                jumpToProcEnterMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
                final JumpToProcEnterAction jumpToProcEnterAction = new JumpToProcEnterAction(shell);
                jumpToProcEnterMenuItem.setText(jumpToProcEnterAction.getText());
                jumpToProcEnterMenuItem.setImage(jumpToProcEnterAction.getImage());
                jumpToProcEnterMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        jumpToProcEnterAction.setTableViewer(tableViewer);
                        jumpToProcEnterAction.setSelectedItems(getSelection());
                        jumpToProcEnterAction.run();
                    }
                });
            }

            if (getSelectedItem().isProcEntry()) {
                jumpToProcExitMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
                final JumpToProcExitAction jumpToProcExitAction = new JumpToProcExitAction(shell);
                jumpToProcExitMenuItem.setText(jumpToProcExitAction.getText());
                jumpToProcExitMenuItem.setImage(jumpToProcExitAction.getImage());
                jumpToProcExitMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        jumpToProcExitAction.setTableViewer(tableViewer);
                        jumpToProcExitAction.setSelectedItems(getSelection());
                        jumpToProcExitAction.run();
                    }
                });
            }
        }
    }
}
