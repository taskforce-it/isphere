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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.files.IFileService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.streamfilesearch.SearchDialog;
import biz.isphere.core.streamfilesearch.SearchElement;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.IFSRemoteFileHelper;
import biz.isphere.rse.internal.RSEStreamFile;
import biz.isphere.rse.streamfilesearch.RSESearchExec;

public class StreamFileSearchAction implements IObjectActionDelegate {

    private Shell _shell;
    private IWorkbenchWindow _workbenchWindow;
    private RSEStreamFile[] selectedStreamFiles;
    private List<RSEStreamFile> selectedStreamFilesList;
    private IBMiConnection _connection;
    private boolean _multipleConnection;

    public StreamFileSearchAction() {
        selectedStreamFilesList = new LinkedList<RSEStreamFile>();
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        _shell = workbenchPart.getSite().getShell();
        _workbenchWindow = workbenchPart.getSite().getWorkbenchWindow();
    }

    public void run(IAction arg0) {

        try {

            if (selectedStreamFiles.length > 0) {
                doWork();
            }

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(_shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            selectedStreamFiles = getStreamFilesFromSelection((IStructuredSelection)selection);
            if (selectedStreamFiles.length >= 1) {
                action.setEnabled(true);
            } else {
                action.setEnabled(false);
            }
        } else {
            action.setEnabled(false);
        }
    }

    private RSEStreamFile[] getStreamFilesFromSelection(IStructuredSelection structuredSelection) {

        selectedStreamFilesList.clear();

        if (structuredSelection != null) {
            addStreamFilesFromList(structuredSelection.toList());
        }

        return selectedStreamFilesList.toArray(new RSEStreamFile[selectedStreamFilesList.size()]);
    }

    private void addStreamFilesFromList(List<?> objects) {

        try {

            for (Object object : objects) {
                if (object instanceof IFSRemoteFile) {
                    IFSRemoteFile ifsRemoteFile = (IFSRemoteFile)object;
                    if (ifsRemoteFile.isFile()) {
                        selectedStreamFilesList.add(new RSEStreamFile(ifsRemoteFile));
                    } else if (ifsRemoteFile.isDirectory()) {
                        addStreamFilesFromList(IFSRemoteFileHelper.listFiles(ifsRemoteFile, IFileService.FILE_TYPE_FILES_AND_FOLDERS, true));
                    }
                }
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }
    }

    private void doWork() {

        _connection = null;
        _multipleConnection = false;

        HashMap<String, SearchElement> _searchElements = new LinkedHashMap<String, SearchElement>();

        for (int idx = 0; idx < selectedStreamFiles.length; idx++) {

            RSEStreamFile element = selectedStreamFiles[idx];

            String key = element.getDirectory() + "/" + element.getStreamFile();

            if (!_searchElements.containsKey(key)) {

                int _offset = 0;
                int _length = element.getStreamFile().length();
                String _type = "";
                do {
                    int _pos = element.getStreamFile().indexOf(".", _offset);
                    if (_pos == -1) {
                        break;
                    } else if (_pos + 1 == _length) {
                        _type = "";
                        break;
                    } else {
                        _type = element.getStreamFile().substring(_pos + 1);
                        _offset = _pos + 1;
                    }
                } while (true);

                SearchElement _searchElement = new SearchElement();
                _searchElement.setDirectory(element.getDirectory());
                _searchElement.setStreamFile(element.getStreamFile());
                _searchElement.setType(_type);

                _searchElements.put(key, _searchElement);

            }

            checkIfMultipleConnections(element.getRSEConnection());

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
                    searchExec.execute(dialog.getSelectedElements(), dialog.getSearchOptions());

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

}
