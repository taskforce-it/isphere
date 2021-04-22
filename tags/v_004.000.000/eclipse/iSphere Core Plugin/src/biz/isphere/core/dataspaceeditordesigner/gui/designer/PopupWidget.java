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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.dataspaceeditordesigner.listener.ChangeWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.listener.DeleteWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.listener.MoveDownWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.listener.MoveUpWidgetListener;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DComment;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class PopupWidget extends MenuAdapter {

    private IDialogEditor editor;
    private DEditor dEditor;
    private ControlPayload payload;

    private MenuItem changeWidgetMenuItem;
    private MenuItem deleteWidgetMenuItem;
    private MenuItem moveUpWidgetMenuItem;
    private MenuItem moveDownWidgetMenuItem;

    public PopupWidget(IDialogEditor editor, DEditor dialog, ControlPayload payload) {
        this.editor = editor;
        this.dEditor = dialog;
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

        if (!((moveUpWidgetMenuItem == null) || (moveUpWidgetMenuItem.isDisposed()))) {
            moveUpWidgetMenuItem.dispose();
        }

        if (!((moveDownWidgetMenuItem == null) || (moveDownWidgetMenuItem.isDisposed()))) {
            moveDownWidgetMenuItem.dispose();
        }
    }

    public void createMenuItems(MenuEvent event) {

        Menu menuParent = (Menu)event.getSource();
        AbstractDWidget widget = payload.getWidget();

        String text = widget.getLabel().trim();
        if (DComment.class.equals(widget.getClass())) {
            if (StringHelper.isNullOrEmpty(text)) {
                text = Messages.Data_type_Comment;
            } else if (DComment.SEPARATOR.equals(text) || DComment.NONE.equals(text)) {
                text = Messages.Data_type_Comment + " - " + text;
            }
        }

        changeWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        changeWidgetMenuItem.setText(Messages.Change + ": " + text);
        changeWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
        changeWidgetMenuItem.addSelectionListener(new ChangeWidgetListener(editor));
        changeWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);

        deleteWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        deleteWidgetMenuItem.setText(Messages.Delete + ": " + text);
        deleteWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
        deleteWidgetMenuItem.addSelectionListener(new DeleteWidgetListener(editor, dEditor));
        deleteWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);

        moveUpWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        moveUpWidgetMenuItem.setText(Messages.Move_up + ": " + text);
        moveUpWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_UP));
        moveUpWidgetMenuItem.addSelectionListener(new MoveUpWidgetListener(editor, dEditor));
        moveUpWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);
        if (widget.isFirst()) {
            moveUpWidgetMenuItem.setEnabled(false);
        }

        moveDownWidgetMenuItem = new MenuItem(menuParent, SWT.NONE);
        moveDownWidgetMenuItem.setText(Messages.Move_down + ": " + text);
        moveDownWidgetMenuItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DOWN));
        moveDownWidgetMenuItem.addSelectionListener(new MoveDownWidgetListener(editor, dEditor));
        moveDownWidgetMenuItem.setData(DE.KEY_DATA_SPACE_PAYLOAD, payload);
        if (widget.isLast()) {
            moveDownWidgetMenuItem.setEnabled(false);
        }
    }
}
