/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

public class DataQueueEntryMenuAdapter extends MenuAdapter {

    private Menu menu;
    private Table table;
    private MenuItem menuItemDisplay;

    public DataQueueEntryMenuAdapter(Menu menu, Table table) {
        this.menu = menu;
        this.table = table;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    private void destroyMenuItems() {
        if (!((menuItemDisplay == null) || (menuItemDisplay.isDisposed()))) {
            menuItemDisplay.dispose();
        }
    }

    private void createMenuItems() {
        createMenuItemDisplay();
    }

    public void createMenuItemDisplay() {
        menuItemDisplay = new MenuItem(menu, SWT.NONE);
        menuItemDisplay.setText(Messages.Display);
        menuItemDisplay.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY));
        menuItemDisplay.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MessageDialog.openInformation(shell, "Information", "Not yet implementes."); //$NON-NLS-1$ //$NON-NLS-2$
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }
}
