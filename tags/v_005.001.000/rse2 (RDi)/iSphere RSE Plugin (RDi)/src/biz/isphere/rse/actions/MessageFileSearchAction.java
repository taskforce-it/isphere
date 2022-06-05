/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import org.eclipse.ui.PlatformUI;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMessageFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.IRemoteObjectContextProvider;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.messagefilesearch.SearchDialog;
import biz.isphere.core.messagefilesearch.SearchElement;
import biz.isphere.core.messagefilesearch.SearchExec;
import biz.isphere.core.messagefilesearch.SearchPostRun;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.messagefilesearch.MessageFileSearchDelegate;

/**
 * Action that launches the iSphere Message File Search from the Remote Systems
 * view.
 */
public class MessageFileSearchAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell _shell;
    private IBMiConnection _connection;
    private boolean _multipleConnection;
    private List<Object> _selectedElements;
    private MessageFileSearchDelegate _delegate;

    public MessageFileSearchAction() {
        this._selectedElements = new LinkedList<Object>();
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

        HashMap<String, SearchElement> _searchElements = new HashMap<String, SearchElement>();

        boolean _continue = true;

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof IQSYSResource)) {

                IQSYSResource element = (IQSYSResource)_object;

                if (ResourceTypeUtil.isLibrary(element)) {
                    _continue = addElementsFromLibrary(_searchElements, element);
                } else if ((ResourceTypeUtil.isMessageFile(element))) {
                    addElement(_searchElements, element);
                }
                if (!_continue) {
                    break;
                }

            } else if ((_object instanceof SystemFilterReference)) {

                SystemFilterReference filterReference = (SystemFilterReference)_object;
                String[] _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                if (!addElementsFromFilterString(_searchElements, _filterStrings)) {
                    break;
                }

            } else if ((_object instanceof ISystemFilterStringReference)) {

                ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                if (!addElementsFromFilterString(_searchElements, _filterStrings)) {
                    break;
                }

            }

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

                    SearchPostRun postRun = new SearchPostRun();
                    postRun.setConnection(_connection);
                    postRun.setConnectionName(qualifiedConnectionName);
                    postRun.setSearchString(dialog.getCombinedSearchString());
                    postRun.setSearchElements(_searchElements);
                    postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

                    new SearchExec().execute(as400, qualifiedConnectionName, jdbcConnection, dialog.getSearchOptions(), dialog.getSelectedElements(),
                        postRun);

                }

            }

        }
    }

    private void addElement(HashMap<String, SearchElement> _searchElements, IQSYSResource element) {

        String library = element.getLibrary();
        String file = ((IQSYSMessageFile)element).getName();

        String key = library + "-" + file;

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(element.getLibrary());
            _searchElement.setMessageFile(element.getName());
            _searchElement.setDescription(element.getDescription());
            _searchElements.put(key, _searchElement);

        }

    }

    private boolean addElementsFromLibrary(HashMap<String, SearchElement> _searchElements, IQSYSResource element) {

        ISeriesObjectFilterString _objectFilterString = new ISeriesObjectFilterString();
        _objectFilterString.setObject("*");
        _objectFilterString.setObjectType(ISeries.MSGF);
        _objectFilterString.setLibrary(element.getName());

        return addElementsFromFilterString(_searchElements, _objectFilterString.toString());
    }

    private boolean addElementsFromFilterString(HashMap<String, SearchElement> _searchElements, String... filterStrings) {

        try {
            return getMessageFileSearchDelegate().addElementsFromFilterString(_searchElements, filterStrings);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(_shell, e);
            return false;
        }
    }

    private MessageFileSearchDelegate getMessageFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new MessageFileSearchDelegate(_shell, _connection);
        }

        return _delegate;
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
                 * Started for an object, such as a message file or library.
                 */

                IQSYSResource element = (IQSYSResource)object;

                if (ResourceTypeUtil.isLibrary(element) || ResourceTypeUtil.isMessageFile(element)) {

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
                 * Started from ??? See:
                 * biz.isphere.rse.actions.SourceFileSearchAction
                 */

                // TODO: remove obsolete code (also plugin.xml)

                // ISystemFilterStringReference element =
                // (ISystemFilterStringReference)object;
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

    }

}
