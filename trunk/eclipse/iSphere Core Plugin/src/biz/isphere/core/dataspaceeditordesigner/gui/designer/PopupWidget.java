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
import biz.isphere.core.dataspaceeditordesigner.listener.ChangeWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.listener.DeleteWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class PopupWidget extends MenuAdapter {

    private IDialogEditor editor;
    private DEditor dialog;
    private ControlPayload payload;

    private MenuItem changeWidgetMenuItem;
    private MenuItem deleteWidgetMenuItem;

    public PopupWidget(IDialogEditor editor, DEditor dialog, ControlPayload payload) {
        this.editor = editor;
        this.dialog = dialog;
        this.payload = payload;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems(event);
    }

    public void destroyMenuItems() {

        if (!((changeWidgetMenuItem == null) || (changeWidgetMenuItem.isDisposed()))) {
            changeWidgetMenuItem.dispose();
        }

        if (!((deleteWidgetMenuItem == null) || (deleteWidgetMenuItem.isDisposed()))) {
            deleteWidgetMenuItem.dispose();
        }
    }

    public void createMenuItems(MenuEvent event) {

        Menu menuParent = (Menu)event.getSource();

        changeWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        changeWidgetMenuItem.setText(Messages.Change + ": " + payload.getWidget().getLabel());
        changeWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
        changeWidgetMenuItem.addSelectionListener(new ChangeWidgetListener(editor, dialog));
        changeWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);

        deleteWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        deleteWidgetMenuItem.setText(Messages.Delete + ": " + payload.getWidget().getLabel());
        deleteWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
        deleteWidgetMenuItem.addSelectionListener(new DeleteWidgetListener(editor, dialog));
        deleteWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);
    }
}
