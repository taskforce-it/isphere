/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberDialog;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceFile;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class CopyMembersToHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.core.command.SourceMember.copyTo";

    private CopyMemberService jobDescription;

    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            Shell shell = HandlerUtil.getActiveShell(event);
            executeInternally(shell, structuredSelection);
        }

        return null;
    }

    public void execute(Shell shell, IStructuredSelection structuredSelection) {
        executeInternally(shell, structuredSelection);
    }

    private void executeInternally(Shell shell, IStructuredSelection structuredSelection) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            collectSourceMembers(shell, structuredSelection);

            if (jobDescription != null && jobDescription.getItems().length > 0) {
                CopyMemberDialog dialog = new CopyMemberDialog(shell);
                dialog.setContent(jobDescription);
                dialog.open();
            } else {
                MessageDialog.openInformation(shell, Messages.Information, Messages.Selection_does_not_include_any_source_members);
            }

            jobDescription = null;
        }
    }

    private boolean collectSourceMembers(Shell shell, IStructuredSelection structuredSelection) {

        for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
            Object selectedObject = iterator.next();

            if (ResourceTypeUtil.isSrcMember(selectedObject)) {
                QSYSRemoteSourceMember object = (QSYSRemoteSourceMember)selectedObject;
                if (!addElement(shell, object)) {
                    return false;
                }
            } else if (ResourceTypeUtil.isSourceFile(selectedObject)) {
                QSYSRemoteSourceFile object = (QSYSRemoteSourceFile)selectedObject;
                String connectionName = object.getRemoteObjectContext().getObjectSubsystem().getHost().getAliasName();
                if (!addElementsFromSourceFile(shell, connectionName, object.getLibrary(), object.getName())) {
                    return false;
                }
            } else if ((selectedObject instanceof SystemFilterReference)) {
                SystemFilterReference filterReference = (SystemFilterReference)selectedObject;
                String[] filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                String connectionName = ((SubSystem)filterReference.getFilterPoolReferenceManager().getProvider()).getHost().getAliasName();
                if (!addElementsFromFilterString(shell, connectionName, filterStrings)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean addElement(Shell shell, QSYSRemoteSourceMember object) {

        if (object == null) {
            ISpherePlugin.logError("*** CopyToAction.addElement(): 'object' must not be null ***", null);
            return false;
        }

        IBMiConnection IBMiConnection = object.getConnection();
        if (IBMiConnection != null) {
            String connectionName = IBMiConnection.getConnectionName();
            if (jobDescription == null) {
                jobDescription = new CopyMemberService(shell, connectionName);
            } else {
                if (!jobDescription.getFromConnectionName().equals(connectionName)) {
                    MessageDialog.openError(shell, Messages.E_R_R_O_R, Messages.Cannot_copy_source_members_from_different_connections);
                    return false;
                }
            }
            jobDescription.addItem(object.getFile(), object.getLibrary(), object.getName(), object.getType());
        }

        return true;
    }

    private boolean addElementsFromFilterString(Shell shell, String connectionName, String[] filterStrings) {

        Object[] children = null;

        for (int idx = 0; idx < filterStrings.length; idx++) {

            String _filterString = filterStrings[idx];
            IBMiConnection _connection = IBMiConnection.getConnection(connectionName);
            QSYSObjectSubSystem _fileSubSystemImpl = _connection.getQSYSObjectSubSystem();

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
                    return false;
                } else {
                    if (!collectSourceMembers(shell, new StructuredSelection(children))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean addElementsFromSourceFile(Shell shell, String connectionName, String library, String sourceFile) {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*");
        _memberFilterString.setMemberType("*");

        String[] _filterStrings = new String[1];
        _filterStrings[0] = _memberFilterString.toString();
        return addElementsFromFilterString(shell, connectionName, _filterStrings);
    }
}
