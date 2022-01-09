/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.dataspacemonitor.rse.DataSpaceMonitorView;

import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public abstract class AbstractMonitorDataSpaceAction implements IObjectActionDelegate {

    private IStructuredSelection structuredSelection;
    private String objectType;

    public AbstractMonitorDataSpaceAction(String objectType) {
        this.objectType = objectType;
    }

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {
            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (matchesType(object, objectType)) {
                    IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();
                        if (page != null) {
                            QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;
                            openMonitorForObject(qsysRemoteObject, page);
                        }
                    }
                }
            }
        }
    }

    protected void openMonitorForObject(QSYSRemoteObject qsysRemoteObject, IWorkbenchPage page) {
        try {

            String connectionName = IBMiConnection.getConnection(getHost(qsysRemoteObject)).getConnectionName();
            String name = qsysRemoteObject.getName();
            String library = qsysRemoteObject.getLibrary();
            String objectType = qsysRemoteObject.getType();
            String description = qsysRemoteObject.getDescription();
            RemoteObject remoteObject = new RemoteObject(connectionName, name, library, objectType, description);

            String contentId = remoteObject.getAbsoluteName();
            IViewManager viewManager = ISphereRSEPlugin.getDefault().getViewManager(IViewManager.DATA_SPACE_MONITOR_VIEWS);
            IPinableView view = (IPinableView)viewManager.getView(DataSpaceMonitorView.ID, contentId);
            if (view instanceof IDialogView && !contentId.equals(view.getContentId())) {
                ((IDialogView)view).setData(new RemoteObject[] { remoteObject });
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // nothing to do here
    }

    private boolean matchesType(Object object, String objectType) {
        if ((object instanceof IQSYSResource)) {
            IQSYSResource element = (IQSYSResource)object;
            if (ResourceTypeUtil.isObject(element) && element instanceof QSYSRemoteObject) {
                QSYSRemoteObject qsysObject = (QSYSRemoteObject)element;
                if (objectType.equals(qsysObject.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private IHost getHost(QSYSRemoteObject object) {
        IHost host = object.getRemoteObjectContext().getObjectSubsystem().getHost();
        return host;
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

}
