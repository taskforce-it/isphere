/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.WorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystemFactory;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;

import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.filters.SystemFilterReference;
import com.ibm.etools.systems.subsystems.SubSystem;

public class WorkWithSpooledFilesAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    private IStructuredSelection structuredSelection;

    private ArrayList<Object> _selectedElements;

    public WorkWithSpooledFilesAction() {
        super("Work With Spooled Files", "", null); //$NON-NLS-1$
        setContextMenuGroup("additions"); //$NON-NLS-1$
        allowOnMultipleSelection(true);
        setHelp(""); //$NON-NLS-1$
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SOURCE_FILE_SEARCH));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this); //$NON-NLS-1$
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        ArrayList<Object> _selectedElements = new ArrayList<Object>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {

            Object _object = iterSelection.next();

            if ((_object instanceof SystemFilterReference)) {

                /*
                 * Started for a filter node
                 */

                SystemFilterReference element = (SystemFilterReference)_object;
                if (SpooledFileSubSystemFactory.TYPE.equals(element.getReferencedFilter().getType())) {
                    SubSystem subSystem = getSubSystem(element);
                    if (subSystem != null && !subSystem.isOffline()) {
                        _selectedElements.add(element);
                    }
                }
            }
        }

        if (_selectedElements.isEmpty()) {
            return false;
        }

        this._selectedElements = _selectedElements;
        return true;

    }

    public void run() {

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof SystemFilterReference)) {
                IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                if (window != null) {
                    IWorkbenchPage page = window.getActivePage();
                    if (page != null) {
                        openWorkWithSpooledFilesView((SystemFilterReference)_object, page);
                    }

                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(SystemFilterReference filterReference, IWorkbenchPage page) {

        try {

            SubSystem subSystem = getSubSystem(filterReference);
            String connectionName = getConnectionName(subSystem);
            String filterPoolName = filterReference.getReferencedFilter().getParentFilterPool().getName();
            String filterName = filterReference.getReferencedFilter().getName();

            WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(connectionName, filterPoolName, filterName);
            inputData.setFilterStrings(filterReference.getReferencedFilter().getFilterStrings());

            String contentId = inputData.getContentId();
            IViewManager viewManager = ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
            IPinnableView view = (IPinnableView)viewManager.getView(WorkWithSpooledFilesView.ID, contentId);

            if (view instanceof WorkWithSpooledFilesView) {
                WorkWithSpooledFilesView wrkSplfView = (WorkWithSpooledFilesView)view;
                wrkSplfView.setInputData(inputData);
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    private SubSystem getSubSystem(SystemFilterReference filterReference) {
        return (SubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

    public String getConnectionName(SubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getSystemConnection());
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
}
