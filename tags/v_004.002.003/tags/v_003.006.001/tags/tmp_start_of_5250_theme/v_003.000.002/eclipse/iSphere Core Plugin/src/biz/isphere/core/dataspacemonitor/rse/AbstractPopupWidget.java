/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.rse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.core.Messages;

public abstract class AbstractPopupWidget extends MenuAdapter {

    private MenuItem menuItem;

    public AbstractPopupWidget() {
        this.menuItem = null;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems(event);
    }

    public void destroyMenuItems() {
        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems(MenuEvent event) {

        Menu menuParent = (Menu)event.getSource();
        if (isVisible()) {
            menuItem = new MenuItem(menuParent, SWT.NONE);
            menuItem.setText(Messages.Stop_watching);
        } else {
            menuItem = new MenuItem(menuParent, SWT.NONE);
            menuItem.setText(Messages.Start_watching);
        }
        menuItem.addSelectionListener(createChangeWatchingListener());
    }

    protected abstract SelectionListener createChangeWatchingListener();

    protected abstract boolean isVisible();
}
