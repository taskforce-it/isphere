/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspacemonitor.rse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDropDataSpaceListener;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.dataspacemonitor.rse.AbstractDataSpaceMonitorView;
import biz.isphere.core.dataspacemonitor.rse.WatchItemManager;
import biz.isphere.core.internal.IControlDecoration;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.dataspace.rse.WrappedDataSpace;
import biz.isphere.rse.dataspaceeditordesigner.rse.DropDataSpaceListener;
import biz.isphere.rse.internal.RSEControlDecoration;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;

public class DataSpaceMonitorView extends AbstractDataSpaceMonitorView {

    @Override
    protected AbstractDropDataSpaceListener createDropListener(IDialogView editor) {
        return new DropDataSpaceListener(editor);
    }

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {

        ISeriesObject object = ISeriesConnection.getConnection(remoteObject.getConnectionName()).getISeriesObject(null,
            remoteObject.getLibrary(), remoteObject.getName(), remoteObject.getObjectType());
        if (object == null) {
            return null;
        }

        return new WrappedDataSpace(remoteObject);
    }

    protected void createControlPopupMenu(WatchItemManager watchManager, Composite dialogEditor, Control control) {
        Menu controlMenu = new Menu(dialogEditor);
        control.setMenu(controlMenu);
        controlMenu.addMenuListener(new PopupWidget(watchManager, getDecorator(control)));
    }

    protected void createControlDecorator(Control control) {
        IControlDecoration decorator = new RSEControlDecoration(control, SWT.LEFT);
        decorator.setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_WATCHING).createImage());
        decorator.hide();
        decorator.setMarginWidth(5);
        setDecorator(control, decorator);
    }

    @Override
    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.DATA_SPACE_MONITOR_VIEWS);
    }

    private void setDecorator(Control control, IControlDecoration decorator) {
        control.setData(DE.KEY_CONTROL_DECORATOR, decorator);
    }

    private IControlDecoration getDecorator(Control control) {
        Object data = control.getData(DE.KEY_CONTROL_DECORATOR);
        if (data instanceof IControlDecoration) {
            return (IControlDecoration)data;
        }
        return null;
    }
}
