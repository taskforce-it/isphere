/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.messagefilesearch.SearchDialog;
import biz.isphere.core.messagefilesearch.SearchElement;
import biz.isphere.core.messagefilesearch.SearchExec;
import biz.isphere.core.messagefilesearch.SearchPostRun;
import biz.isphere.rse.Messages;
import biz.isphere.rse.messagefilesearch.MessageFileSearchDelegate;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMessageFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.IRemoteObjectContextProvider;

public class MessageFileSearchAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell _shell;
    private IBMiConnection _connection;
    private ISeriesObjectFilterString _objectFilterString;
    private boolean _multipleConnection;
    private ArrayList<Object> _selectedElements;
    private MessageFileSearchDelegate _delegate;

    private HashMap<String, SearchElement> _searchElements;

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            _connection = null;
            _objectFilterString = null;
            _multipleConnection = false;
            _selectedElements = new ArrayList<Object>();

            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {

                Object _object = iterator.next();

                if ((_object instanceof IQSYSResource)) {

                    /*
                     * Started for an object, such as a message file or library.
                     */

                    IQSYSResource element = (IQSYSResource)_object;

                    if (ResourceTypeUtil.isLibrary(element) || ResourceTypeUtil.isMessageFile(element)) {

                        _selectedElements.add(element);

                        checkIfMultipleConnections(IBMiConnection.getConnection(((IRemoteObjectContextProvider)element).getRemoteObjectContext()
                            .getObjectSubsystem().getHost()));

                    }

                } else if ((_object instanceof SystemFilterReference)) {

                    /*
                     * Started for a filter node
                     */

                    SystemFilterReference element = (SystemFilterReference)_object;

                    _selectedElements.add(element);

                    checkIfMultipleConnections(IBMiConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                        .getHost()));

                } else if ((_object instanceof ISystemFilterStringReference)) {

                    /*
                     * Started from ???
                     */

                    ISystemFilterStringReference element = (ISystemFilterStringReference)_object;

                    _selectedElements.add(element);

                    checkIfMultipleConnections(IBMiConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                        .getHost()));

                }

            }

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

            _searchElements = new HashMap<String, SearchElement>();

            boolean _continue = true;

            for (int idx = 0; idx < _selectedElements.size(); idx++) {

                Object _object = _selectedElements.get(idx);

                if ((_object instanceof IQSYSResource)) {

                    IQSYSResource element = (IQSYSResource)_object;

                    if (ResourceTypeUtil.isLibrary(element)) {
                        _continue = addElementsFromLibrary(element);
                    } else if ((ResourceTypeUtil.isMessageFile(element))) {
                        addElement(element);
                    }
                    if (!_continue) {
                        break;
                    }

                } else if ((_object instanceof SystemFilterReference)) {

                    SystemFilterReference filterReference = (SystemFilterReference)_object;
                    String[] _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                    if (!addElementsFromFilterString(_filterStrings)) {
                        break;
                    }

                } else if ((_object instanceof ISystemFilterStringReference)) {

                    ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                    String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                    if (!addElementsFromFilterString(_filterStrings)) {
                        break;
                    }

                }

            }

            AS400 as400 = null;
            Connection jdbcConnection = null;
            try {
                as400 = _connection.getAS400ToolboxObject();
                jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(_connection.getConnectionName());
            } catch (Exception e) {
                ISpherePlugin.logError("*** Could not get JDBC connection ***", e);
            }

            if (as400 != null && jdbcConnection != null) {

                String connectionName = _connection.getConnectionName();
                if (ISphereHelper.checkISphereLibrary(_shell, connectionName)) {

                    SearchDialog dialog = new SearchDialog(_shell, _searchElements, true);
                    if (dialog.open() == Dialog.OK) {

                        SearchPostRun postRun = new SearchPostRun();
                        postRun.setConnection(_connection);
                        postRun.setConnectionName(connectionName);
                        postRun.setSearchString(dialog.getString());
                        postRun.setSearchElements(_searchElements);
                        postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

                        new SearchExec().execute(as400, connectionName, jdbcConnection, dialog.getSearchOptions(), dialog.getSelectedElements(),
                            postRun);

                    }

                }

            }

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

    private void addElement(IQSYSResource element) {

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

    private boolean addElementsFromLibrary(IQSYSResource element) {

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*");
            _objectFilterString.setObjectType(ISeries.MSGF);
        }

        _objectFilterString.setLibrary(element.getName());

        return addElementsFromFilterString(_objectFilterString.toString());
    }

    private boolean addElementsFromFilterString(String... filterStrings) {

        try {
            return getMessageFileSearchDelegate().addElementsFromFilterString(_searchElements, filterStrings);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(_shell, e);
            return false;
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }

    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {

        _shell = workbenchPart.getSite().getShell();

    }

}
