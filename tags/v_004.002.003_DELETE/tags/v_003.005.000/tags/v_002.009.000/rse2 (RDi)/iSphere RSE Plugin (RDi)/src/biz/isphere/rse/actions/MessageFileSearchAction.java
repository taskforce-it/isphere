/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.messagefilesearch.SearchDialog;
import biz.isphere.core.messagefilesearch.SearchElement;
import biz.isphere.core.messagefilesearch.SearchExec;
import biz.isphere.core.messagefilesearch.SearchPostRun;
import biz.isphere.rse.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.IRemoteObjectContextProvider;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class MessageFileSearchAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;
    private IBMiConnection _connection;
    private boolean _multipleConnection;
    private ArrayList<Object> _selectedElements;
    private HashMap<String, SearchElement> _searchElements;
    private String _filterString;
    private String[] _filterStrings;
    private ISeriesObjectFilterString _objectFilterString;
    private QSYSObjectSubSystem _fileSubSystemImpl;

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            _objectFilterString = null;
            _connection = null;
            _multipleConnection = false;
            _selectedElements = new ArrayList<Object>();

            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {

                Object _object = iterator.next();

                if ((_object instanceof IQSYSResource)) {

                    IQSYSResource element = (IQSYSResource)_object;

                    if (ResourceTypeUtil.isLibrary(element) || ResourceTypeUtil.isMessageFile(element)) {

                        _selectedElements.add(element);

                        checkIfMultipleConnections(IBMiConnection.getConnection(((IRemoteObjectContextProvider)element).getRemoteObjectContext()
                            .getObjectSubsystem().getHost()));

                    }

                } else if ((_object instanceof SystemFilterReference)) {

                    SystemFilterReference element = (SystemFilterReference)_object;

                    _selectedElements.add(element);

                    checkIfMultipleConnections(IBMiConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                        .getHost()));

                } else if ((_object instanceof ISystemFilterStringReference)) {

                    ISystemFilterStringReference element = (ISystemFilterStringReference)_object;

                    _selectedElements.add(element);

                    checkIfMultipleConnections(IBMiConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                        .getHost()));

                }

            }

            if (_multipleConnection) {
                MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
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
                    _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                    if (!addElementsFromFilterString(_filterStrings)) {
                        break;
                    }

                } else if ((_object instanceof ISystemFilterStringReference)) {

                    ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                    _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                    if (!addElementsFromFilterString(_filterStrings)) {
                        break;
                    }

                }

            }

            AS400 as400 = null;
            String connectionName = null;
            Connection jdbcConnection = null;
            try {
                as400 = _connection.getAS400ToolboxObject();
                connectionName = _connection.getConnectionName();
                jdbcConnection = _connection.getJDBCConnection(null, false);
            } catch (SystemMessageException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (as400 != null && connectionName != null && jdbcConnection != null) {

                if (ISphereHelper.checkISphereLibrary(shell, connectionName)) {

                    SearchDialog dialog = new SearchDialog(shell, _searchElements, true);
                    if (dialog.open() == Dialog.OK) {

                        SearchPostRun postRun = new SearchPostRun();
                        postRun.setConnection(_connection);
                        postRun.setConnectionName(_connection.getConnectionName());
                        postRun.setSearchString(dialog.getString());
                        postRun.setSearchElements(_searchElements);
                        postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

                        new SearchExec().execute(as400, connectionName, jdbcConnection, dialog.getSearchOptions(), new ArrayList<SearchElement>(
                            _searchElements.values()), postRun);

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

    private void addElement(IQSYSResource element) {

        String key = element.getLibrary() + "-" + element.getName();

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(element.getLibrary());
            _searchElement.setMessageFile(element.getName());
            _searchElement.setDescription(element.getDescription());
            _searchElements.put(key, _searchElement);

        }

    }

    private boolean addElementsFromLibrary(IQSYSResource element) {

        Vector<IQSYSResource> libElements = new Vector<IQSYSResource>();
        Object[] children = null;

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*");
            _objectFilterString.setObjectType(ISeries.MSGF);
            String attributes = ISeries.MSGF + ":*";
            _objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
        }

        _objectFilterString.setLibrary(element.getName());
        _filterString = _objectFilterString.toString();

        _fileSubSystemImpl = _connection.getQSYSObjectSubSystem();
        try {
            children = _fileSubSystemImpl.resolveFilterString(_filterString, null);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(shell, e);
            return false;
        }

        if ((children == null) || (children.length == 0)) {
            return true;
        }

        Object firstObject = children[0];
        if ((firstObject instanceof SystemMessageObject)) {
            SystemMessageDialog.displayErrorMessage(shell, ((SystemMessageObject)firstObject).getMessage());
            return true;
        }

        for (int idx2 = 0; idx2 < children.length; idx2++) {
            libElements.addElement((IQSYSResource)children[idx2]);
        }

        for (Enumeration<IQSYSResource> enumeration = libElements.elements(); enumeration.hasMoreElements();) {
            element = enumeration.nextElement();
            addElement(element);
        }

        return true;

    }

    private boolean addElementsFromFilterString(String[] filterStrings) {

        boolean _continue = true;
        Object[] children = null;

        for (int idx = 0; idx < filterStrings.length; idx++) {

            _filterString = filterStrings[idx];
            _fileSubSystemImpl = _connection.getQSYSObjectSubSystem();

            try {
                children = _fileSubSystemImpl.resolveFilterString(_filterString, null);
            } catch (InterruptedException localInterruptedException) {
                return false;
            } catch (Exception e) {
                SystemMessageDialog.displayExceptionMessage(shell, e);
                return false;
            }

            if ((children != null) && (children.length != 0)) {

                Object firstObject = children[0];

                if ((firstObject instanceof SystemMessageObject)) {

                    SystemMessageDialog.displayErrorMessage(shell, ((SystemMessageObject)firstObject).getMessage());

                } else {

                    for (int idx2 = 0; idx2 < children.length; idx2++) {

                        IQSYSResource element = (IQSYSResource)children[idx2];

                        if (ResourceTypeUtil.isLibrary(element)) {
                            _continue = addElementsFromLibrary(element);
                        } else if (ResourceTypeUtil.isMessageFile(element)) {
                            addElement(element);
                        }

                        if (!_continue) break;

                    }

                }

            }

        }

        return true;

    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }

    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {

        shell = workbenchPart.getSite().getShell();

    }

}
