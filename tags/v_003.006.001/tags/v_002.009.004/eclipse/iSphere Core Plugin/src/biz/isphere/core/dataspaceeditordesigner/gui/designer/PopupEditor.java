/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.designer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.dataspaceeditordesigner.listener.NewWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DBoolean;
import biz.isphere.core.dataspaceeditordesigner.model.DDecimal;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DInteger;
import biz.isphere.core.dataspaceeditordesigner.model.DLongInteger;
import biz.isphere.core.dataspaceeditordesigner.model.DShortInteger;
import biz.isphere.core.dataspaceeditordesigner.model.DText;
import biz.isphere.core.dataspaceeditordesigner.model.DTinyInteger;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class PopupEditor extends MenuAdapter {

    private IDialogEditor editor;
    private DEditor dialog;

    private MenuItem itemNew;

    public PopupEditor(IDialogEditor editor, DEditor dialog) {
        this.editor = editor;
        this.dialog = dialog;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems(event);
    }

    public void destroyMenuItems() {
        if (!((itemNew == null) || (itemNew.isDisposed()))) {
            itemNew.dispose();
        }
    }

    public void createMenuItems(MenuEvent event) {

        Menu parent = (Menu)event.getSource();

        MenuItem itemNew = new MenuItem(parent, SWT.CASCADE);
        itemNew.setText(Messages.New);
        itemNew.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_NEW));

        createItemNew(itemNew);

        this.itemNew = itemNew;
    }

    private void createItemNew(MenuItem parent) {
        Menu menuNew = new Menu(parent);
        parent.setMenu(menuNew);

        createItemNewWidget(menuNew, "Boolean", DBoolean.class);
        createItemNewWidget(menuNew, "Decimal", DDecimal.class);
        createItemNewWidget(menuNew, "Text", DText.class);
        createItemNewWidget(menuNew, "Integer", DInteger.class);
        createItemNewWidget(menuNew, "Long Integer", DLongInteger.class);
        createItemNewWidget(menuNew, "Short Integer", DShortInteger.class);
        createItemNewWidget(menuNew, "Tiny Integer", DTinyInteger.class);
    }

    private void createItemNewWidget(Menu menuNew, String label, Class<? extends AbstractDWidget> widgetClass) {
        MenuItem menuItem = new MenuItem(menuNew, SWT.PUSH);
        menuItem.setText(label);
        menuItem.setData(DE.KEY_DWIDGET_CLASS, widgetClass);
        menuItem.addSelectionListener(new NewWidgetListener(this.editor, this.dialog));
    }
}
