/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
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
import biz.isphere.rse.spooledfiles.SpooledFileSubSystemFactory;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.core.ui.messages.SystemMessageDialog;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.filters.SystemFilterReference;
import com.ibm.etools.systems.filters.SystemFilterStringReference;
import com.ibm.etools.systems.subsystems.SubSystem;

public class MessageFileSearchAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    private ISeriesConnection _connection;
    private ISeriesObjectFilterString _objectFilterString;
    private boolean _multipleConnection;
    protected ArrayList _selectedElements;
    private MessageFileSearchDelegate _delegate;

    private HashMap<String, SearchElement> _searchElements;

    public MessageFileSearchAction() {
        super(Messages.iSphere_Message_File_Search, "", null);
        _selectedElements = new ArrayList();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_MESSAGE_FILE_SEARCH));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        _connection = null;
        _objectFilterString = null;
        _multipleConnection = false;
        _selectedElements = new ArrayList();

        ArrayList<Object> _selectedElements = new ArrayList<Object>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {

            Object _object = iterSelection.next();

            if ((_object instanceof DataElement)) {

                /*
                 * Started for an object, such as a source file or source member
                 */

                DataElement element = (DataElement)_object;

                if (ISeriesDataElementUtil.getDescriptorTypeObject(element).isLibrary()
                    || ISeriesDataElementUtil.getDescriptorTypeObject(element).isMessageFile()) {

                    _selectedElements.add(element);

                    checkIfMultipleConnections(ISeriesConnection.getConnection(ISeriesDataElementUtil.getConnection(element).getAliasName()));

                }

            } else if ((_object instanceof SystemFilterReference)) {

                /*
                 * Started for a filter node
                 */

                SystemFilterReference element = (SystemFilterReference)_object;
                if (!SpooledFileSubSystemFactory.TYPE.equals(element.getReferencedFilter().getType())) {
                    _selectedElements.add(element);

                    checkIfMultipleConnections(ISeriesConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                        .getSystemConnection().getAliasName()));
                }
            } else if ((_object instanceof SystemFilterStringReference)) {

                /*
                 * Started from ???
                 */

                SystemFilterStringReference element = (SystemFilterStringReference)_object;

                _selectedElements.add(element);

                checkIfMultipleConnections(ISeriesConnection.getConnection(((SubSystem)element.getFilterPoolReferenceManager().getProvider())
                    .getSystemConnection().getAliasName()));

            }

        }

        if (_selectedElements.isEmpty()) {
            return false;
        }

        this._selectedElements = _selectedElements;
        return true;

    }

    @Override
    public void run() {

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

            if ((_object instanceof DataElement)) {

                DataElement element = (DataElement)_object;

                if (ISeriesDataElementUtil.getDescriptorTypeObject(element).isLibrary()) {
                    _continue = addElementsFromLibrary(element);
                } else if (ISeriesDataElementUtil.getDescriptorTypeObject(element).isMessageFile()) {
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

            } else if ((_object instanceof SystemFilterStringReference)) {

                SystemFilterStringReference filterStringReference = (SystemFilterStringReference)_object;
                String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                if (!addElementsFromFilterString(_filterStrings)) {
                    break;
                }

            }

        }

        AS400 as400 = null;
        Connection jdbcConnection = null;
        try {
            as400 = _connection.getAS400ToolboxObject(shell);
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(_connection.getConnectionName());
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not get JDBC connection ***", e); //$NON-NLS-1$
        }

        String connectionName = _connection.getConnectionName();
        if (as400 != null && jdbcConnection != null) {

            if (ISphereHelper.checkISphereLibrary(shell, connectionName)) {

                SearchDialog dialog = new SearchDialog(shell, _searchElements, true);
                if (dialog.open() == Dialog.OK) {

                    SearchPostRun postRun = new SearchPostRun();
                    postRun.setConnection(_connection);
                    postRun.setConnectionName(connectionName);
                    postRun.setSearchString(dialog.getString());
                    postRun.setSearchElements(_searchElements);
                    postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

                    new SearchExec().execute(as400, connectionName, jdbcConnection, dialog.getSearchOptions(), new ArrayList<SearchElement>(
                        _searchElements.values()), postRun);

                }

            }

        }

    }

    private MessageFileSearchDelegate getMessageFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new MessageFileSearchDelegate(getShell(), _connection);
        }

        return _delegate;
    }

    private void checkIfMultipleConnections(ISeriesConnection connection) {
        if (!_multipleConnection) {
            if (this._connection == null) {
                this._connection = connection;
            } else if (connection != this._connection) {
                _multipleConnection = true;
            }
        }
    }

    private void addElement(DataElement element) {

        String key = ISeriesDataElementHelpers.getLibrary(element) + "-" + ISeriesDataElementHelpers.getName(element);

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(ISeriesDataElementHelpers.getLibrary(element));
            _searchElement.setMessageFile(ISeriesDataElementHelpers.getName(element));
            _searchElement.setDescription(ISeriesDataElementHelpers.getDescription(element));
            _searchElements.put(key, _searchElement);

        }

    }

    private boolean addElementsFromLibrary(DataElement element) {

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*");
            _objectFilterString.setObjectType(ISeries.MSGF);
            String attributes = "*MSGF:*";
            _objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
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
            SystemMessageDialog.displayExceptionMessage(getShell(), e);
            return false;
        }
    }

}