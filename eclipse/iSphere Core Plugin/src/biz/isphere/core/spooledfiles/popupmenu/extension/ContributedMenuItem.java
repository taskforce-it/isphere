/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.popupmenu.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributionItem;

public class ContributedMenuItem {

    private static final String CONTRIBUTION_ITEM = "CONTRIBUTION_ITEM";

    private ISpooledFilePopupMenuContributionItem contributionItem;
    private MenuItem menuItem;
    private SelectionListener selectionListener;

    public static ISpooledFilePopupMenuContributionItem getContributionItem(MenuItem menuItem) {
        return (ISpooledFilePopupMenuContributionItem)menuItem.getData(CONTRIBUTION_ITEM);
    }

    public ContributedMenuItem(Menu parent, ISpooledFilePopupMenuContributionItem contributionItem) {
        this.contributionItem = contributionItem;

        this.menuItem = new MenuItem(parent, SWT.PUSH);
        this.menuItem.setText(contributionItem.getText());
        this.menuItem.setToolTipText(contributionItem.getTooltipText());
        this.menuItem.setImage(contributionItem.getImage());
        this.menuItem.setData(CONTRIBUTION_ITEM, contributionItem);
    }

    public void setSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
        menuItem.addSelectionListener(listener);
    }

    public void setSelection(SpooledFile[] spooledFiles) {
        contributionItem.setSelection(spooledFiles);
        menuItem.setEnabled(contributionItem.isEnabled());
    }

    public void execute() {
        contributionItem.execute();
    }

    public boolean isDisposed() {
        return menuItem.isDisposed();
    }

    public void dispose() {
        menuItem.removeSelectionListener(selectionListener);
        menuItem.dispose();
    }
}
