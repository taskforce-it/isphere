/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.popupmenus;

import java.util.Iterator;

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
import biz.isphere.jobtraceexplorer.core.ui.actions.ExcludeProcAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.HighlightAttributeAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.HighlightProcAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcEntryAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.JumpToProcExitAction;

public class JobTraceEntryMenuAdapter extends MenuAdapter {

    private Menu parentMenu;
    private TableViewer tableViewer;

    private Shell shell;

    private MenuItem menuItemHighlightRowAttribute;
    private MenuItem menuItemHighlightProc;
    private MenuItem menuItemExcludeProc;
    private MenuItem menuItemProcedureSeparator;
    private MenuItem menuItemJumpToProcEnter;
    private MenuItem menuItemJumpToProcExit;

    private boolean needProcedureSeparator;

    public JobTraceEntryMenuAdapter(Menu parentMenu, TableViewer tableViewer) {
        this.parentMenu = parentMenu;
        this.tableViewer = tableViewer;

        this.shell = tableViewer.getControl().getShell();

        this.needProcedureSeparator = false;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {

        dispose(menuItemHighlightRowAttribute);
        dispose(menuItemHighlightProc);
        dispose(menuItemExcludeProc);
        dispose(menuItemProcedureSeparator);
        dispose(menuItemJumpToProcEnter);
        dispose(menuItemJumpToProcExit);

        this.needProcedureSeparator = false;
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

    private boolean isMenuItem(MenuItem menuItem) {
        return !isDisposed(menuItem);
    }

    private boolean isDisposed(MenuItem menuItem) {

        if (menuItem == null || menuItem.isDisposed()) {
            return true;
        }

        return false;
    }

    private void dispose(MenuItem menuItem) {

        if (!isDisposed(menuItem)) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        StructuredSelection selection = getSelection();
        Iterator<JobTraceEntry> iterator = selection.iterator();

        boolean isProcEntry = true;
        boolean isProcExit = true;
        boolean isProcEntryOrExit = true;

        while (iterator.hasNext() && (isProcEntry || isProcExit)) {
            JobTraceEntry jobTraceEntry = (JobTraceEntry)iterator.next();
            if (!jobTraceEntry.isProcEntry()) {
                isProcEntry = false;
            }
            if (!jobTraceEntry.isProcExit()) {
                isProcExit = false;
            }
            if (!(jobTraceEntry.isProcEntry() || jobTraceEntry.isProcExit())) {
                isProcEntryOrExit = false;
            }
        }

        if (selectedItemsCount() == 1) {
            if (isProcEntryOrExit) {
                menuItemHighlightRowAttribute = createMenuItem(new HighlightAttributeAction(shell, tableViewer));
                needProcedureSeparator = true;
            }
        }

        if (selectedItemsCount() >= 1) {
            if (isProcEntryOrExit) {
                menuItemHighlightProc = createMenuItem(new HighlightProcAction(shell, tableViewer));
                menuItemExcludeProc = createMenuItem(new ExcludeProcAction(shell, tableViewer, getSelectedItem().isExcluded()));
                needProcedureSeparator = true;
            }
        }

        if (selectedItemsCount() == 1) {
            if (isProcExit) {
                addProcedureSeparator();
                menuItemJumpToProcEnter = createMenuItem(new JumpToProcEntryAction(shell, tableViewer));
            }

            if (isProcEntry) {
                addProcedureSeparator();
                menuItemJumpToProcExit = createMenuItem(new JumpToProcExitAction(shell, tableViewer));
            }
        }
    }

    private void addProcedureSeparator() {

        if (needProcedureSeparator) {
            if (!isMenuItem(menuItemProcedureSeparator)) {
                menuItemProcedureSeparator = new MenuItem(parentMenu, SWT.SEPARATOR);
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
