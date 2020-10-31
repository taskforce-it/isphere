/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberDialog;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceFile;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class CopyMembersToAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;
    private CopyMemberService jobDescription;

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                Object selectedObject = iterator.next();

                if (selectedObject instanceof QSYSRemoteSourceMember) {
                    QSYSRemoteSourceMember object = (QSYSRemoteSourceMember)selectedObject;
                    if (!addElement(object)) {
                        return;
                    }
                } else if (selectedObject instanceof QSYSRemoteSourceFile) {
                    QSYSRemoteSourceFile object = (QSYSRemoteSourceFile)selectedObject;
                    String connectionName = object.getRemoteObjectContext().getObjectSubsystem().getHost().getAliasName();
                    if (!addElementsFromSourceFile(connectionName, object.getLibrary(), object.getName())) {
                        return;
                    }
                } else if ((selectedObject instanceof SystemFilterReference)) {
                    SystemFilterReference filterReference = (SystemFilterReference)selectedObject;
                    String[] filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                    String connectionName = ((SubSystem)filterReference.getFilterPoolReferenceManager().getProvider()).getHost().getAliasName();
                    if (!addElementsFromFilterString(connectionName, filterStrings)) {
                        return;
                    }
                }
            }

            if (jobDescription.getItems().length > 0) {
                CopyMemberDialog dialog = new CopyMemberDialog(shell);
                dialog.setContent(jobDescription);
                dialog.open();
            }

            jobDescription = null;
        }
    }

    private boolean addElement(QSYSRemoteSourceMember object) {

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
            jobDescription.addItem(object.getFile(), object.getLibrary(), object.getName());
        }

        return true;
    }

    private boolean addElementsFromFilterString(String connectionName, String[] filterStrings) {

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
                } else {
                    for (int idx2 = 0; idx2 < children.length; idx2++) {
                        IQSYSResource element = (IQSYSResource)children[idx2];
                        if (ResourceTypeUtil.isSourceFile(element)) {
                            // not yet used.
                            if (!addElementsFromSourceFile(connectionName, element.getLibrary(), element.getName())) {
                                return false;
                            }
                        } else if (ResourceTypeUtil.isMember(element)) {
                            QSYSRemoteSourceMember object = (QSYSRemoteSourceMember)element;
                            if (!addElement(object)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean addElementsFromSourceFile(String connectionName, String library, String sourceFile) {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*");
        _memberFilterString.setMemberType("*");

        String[] _filterStrings = new String[1];
        _filterStrings[0] = _memberFilterString.toString();
        return addElementsFromFilterString(connectionName, _filterStrings);
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
