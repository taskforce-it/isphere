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
import biz.isphere.core.dataspaceeditordesigner.listener.AddReferencedObjectListener;
import biz.isphere.core.dataspaceeditordesigner.listener.ChangeDataSpaceEditorListener;
import biz.isphere.core.dataspaceeditordesigner.listener.ChangeDataSpaceEditorPropertiesListener;
import biz.isphere.core.dataspaceeditordesigner.listener.DeleteDataSpaceEditorListener;
import biz.isphere.core.dataspaceeditordesigner.listener.NewDataSpaceEditorListener;
import biz.isphere.core.dataspaceeditordesigner.listener.RemoveReferencedObjectListener;
import biz.isphere.core.dataspaceeditordesigner.listener.RenameDataSpaceEditorListener;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DReferencedObject;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;
import biz.isphere.core.internal.ISeries;

public class PopupTreeViewer extends MenuAdapter {

    private IDialogEditor editor;

    public PopupTreeViewer(IDialogEditor editor) {
        this.editor = editor;
    }

    @Override
    public void menuShown(MenuEvent event) {

        Menu menuParent = (Menu)event.getSource();

        destroyMenuItems(menuParent);
        createMenuItems(menuParent);
    }

    public void destroyMenuItems(Menu menu) {

        for (MenuItem item : menu.getItems()) {
            item.dispose();
        }
    }

    public void createMenuItems(Menu menu) {

        DEditor[] dataSpaceEditors = editor.getSelectedDataSpaceEditors();
        DReferencedObject[] referencedObjects = editor.getSelectedReferencedObjects();

        MenuItem itemNew = new MenuItem(menu, SWT.NONE);
        itemNew.setText(Messages.New_Editor);
        itemNew.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_NEW));
        itemNew.addSelectionListener(new NewDataSpaceEditorListener(menu.getShell(), editor));

        if (dataSpaceEditors.length == 1) {
            MenuItem itemChange = new MenuItem(menu, SWT.NONE);
            itemChange.setText(Messages.Change_Editor);
            itemChange.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
            itemChange.addSelectionListener(new ChangeDataSpaceEditorListener(editor));

            MenuItem itemRename = new MenuItem(menu, SWT.NONE);
            itemRename.setText(Messages.Rename_Editor);
            itemRename.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_RENAME));
            itemRename.addSelectionListener(new RenameDataSpaceEditorListener(menu.getShell(), editor));
        }

        if (dataSpaceEditors.length > 0) {
            MenuItem itemDelete = new MenuItem(menu, SWT.NONE);
            itemDelete.setText(Messages.Delete_Editor);
            itemDelete.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
            itemDelete.addSelectionListener(new DeleteDataSpaceEditorListener(menu.getShell(), editor));
        }

        if (dataSpaceEditors.length == 1) {
            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem itemAddRefObj = new MenuItem(menu, SWT.NONE);
            itemAddRefObj.setText(Messages.Assign_data_area);
            itemAddRefObj.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ADD_DATA_AREA));
            itemAddRefObj.addSelectionListener(new AddReferencedObjectListener(menu.getShell(), editor, ISeries.DTAARA));

            itemAddRefObj = new MenuItem(menu, SWT.NONE);
            itemAddRefObj.setText(Messages.Assign_user_space);
            itemAddRefObj.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ADD_USER_SPACE));
            itemAddRefObj.addSelectionListener(new AddReferencedObjectListener(menu.getShell(), editor, ISeries.USRSPC));
        }

        if (referencedObjects.length > 0) {
            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem itemRemoveRefObj = new MenuItem(menu, SWT.NONE);
            itemRemoveRefObj.setText(Messages.Remove_referenced_objects);
            itemRemoveRefObj.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REMOVE_DATA_SPACE));
            itemRemoveRefObj.addSelectionListener(new RemoveReferencedObjectListener(menu.getShell(), editor));
        }

        if (dataSpaceEditors.length == 1) {
            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem itemChangeProperties = new MenuItem(menu, SWT.NONE);
            itemChangeProperties.setText(Messages.Properties);
            itemChangeProperties.addSelectionListener(new ChangeDataSpaceEditorPropertiesListener(menu.getShell(), editor));
        }
    }
}
