/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.IRemoteObjectContextProvider;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcefilesearch.SearchDialog;
import biz.isphere.core.sourcefilesearch.SearchElement;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.sourcefilesearch.RSESearchExec;
import biz.isphere.rse.sourcefilesearch.SourceFileSearchFilterResolver;

/**
 * Action that launches the iSphere Source File Search from the Remote Systems
 * view.
 */
public class SourceFileSearchAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell _shell;
    private IBMiConnection _connection;
    private boolean _multipleConnection;
    private ArrayList<Object> _selectedElements;

    protected IWorkbenchWindow _workbenchWindow;

    public SourceFileSearchAction() {
        this._selectedElements = new ArrayList<Object>();
    }

    public void run(IAction action) {

        try {

            if (_selectedElements.size() > 0) {
                doWork();
            }

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(_shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private void doWork() {

        if (_multipleConnection) {
            MessageBox errorBox = new MessageBox(_shell, SWT.ICON_ERROR);
            errorBox.setText(Messages.E_R_R_O_R);
            errorBox.setMessage(Messages.Resources_with_different_connections_have_been_selected);
            errorBox.open();
            return;
        }

        if (!_connection.isConnected()) {
            try {
                _connection.connect();
            } catch (SystemMessageException e) {
                return;
            }
        }

        Map<String, SearchElement> _searchElements = null;

        try {
            if (Preferences.getInstance().isSourceFileSearchBatchResolveEnabled()) {
                _searchElements = null;
            } else {
                _searchElements = new SourceFileSearchFilterResolver(_shell, _connection).resolveRSEFilter(_selectedElements);
            }
        } catch (InterruptedException e) {
            SystemMessageDialog.displayExceptionMessage(_shell, e);
            return;
        } catch (InvalidFilterException e) {
            MessageDialog.openError(_shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(_shell, e);
            return;
        }

        AS400 as400 = null;
        Connection jdbcConnection = null;
        String qualifiedConnectionName = ConnectionManager.getConnectionName(_connection);

        try {
            as400 = _connection.getAS400ToolboxObject();
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(qualifiedConnectionName);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not get JDBC connection ***", e); //$NON-NLS-1$
        }

        if (as400 != null && jdbcConnection != null) {

            if (ISphereHelper.checkISphereLibrary(_shell, qualifiedConnectionName)) {

                SearchDialog dialog = new SearchDialog(_shell, _searchElements, true);
                if (dialog.open() == Dialog.OK) {

                    RSESearchExec searchExec = new RSESearchExec(_workbenchWindow, _connection);
                    if (_searchElements == null) {
                        searchExec.resolveAndExecute(_selectedElements, dialog.getSearchOptions());
                    } else {
                        searchExec.execute(dialog.getSelectedElements(), dialog.getSearchOptions());
                    }

                }

            }

        }
    }

    private void checkIfMultipleConnections(IBMiConnection connection) {
        if (!_multipleConnection) {
            if (this._connection == null) {
                this._connection = connection;
            } else if (connection != this._connection) {
                _multipleConnection = true;
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        _selectedElements.clear();

        if (selection instanceof IStructuredSelection) {
            getSelectedElemenetsFromSelection((IStructuredSelection)selection);
        }

        if (_selectedElements.size() >= 1) {
            action.setEnabled(true);
        } else {
            action.setEnabled(false);
        }

    }

    private void getSelectedElemenetsFromSelection(IStructuredSelection structuredSelection) {

        _connection = null;
        _multipleConnection = false;

        if (structuredSelection != null) {
            addSelectedElementsFromList(structuredSelection.toList());
        }
    }

    private void addSelectedElementsFromList(List<?> objects) {

        for (Object object : objects) {
            if ((object instanceof IQSYSResource)) {

                /*
                 * Started for an object, such as a source file or source member
                 */

                IQSYSResource element = (IQSYSResource)object;

                if (ResourceTypeUtil.isLibrary(element) || ResourceTypeUtil.isSourceFile(element) || ResourceTypeUtil.isMember(element)) {

                    _selectedElements.add(element);

                    IHost host = ((IRemoteObjectContextProvider)element).getRemoteObjectContext().getObjectSubsystem().getHost();
                    checkIfMultipleConnections(ConnectionManager.getIBMiConnection(host));

                }

            } else if ((object instanceof SystemFilterReference)) {

                /*
                 * Started for a filter node
                 */

                SystemFilterReference element = (SystemFilterReference)object;

                _selectedElements.add(element);

                IHost host = ((SubSystem)element.getFilterPoolReferenceManager().getProvider()).getHost();
                checkIfMultipleConnections(ConnectionManager.getIBMiConnection(host));

            } else if ((object instanceof ISystemFilterStringReference)) {

                /*
                 * Started from ??? Deactivated in plugin.xml with rev. #327 in
                 * 2014.
                 */

                // TODO:remove obsolete code (also plugin.xml)

                // ISystemFilterStringReference element =
                // (ISystemFilterStringReference)_object;
                //
                // _selectedElements.add(element);
                //
                // IHost host =
                // ((SubSystem)element.getFilterPoolReferenceManager().getProvider()).getHost();
                // checkIfMultipleConnections(ConnectionManager.getIBMiConnection(host));

            }
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {

        _shell = workbenchPart.getSite().getShell();
        _workbenchWindow = workbenchPart.getSite().getWorkbenchWindow();

    }

}
