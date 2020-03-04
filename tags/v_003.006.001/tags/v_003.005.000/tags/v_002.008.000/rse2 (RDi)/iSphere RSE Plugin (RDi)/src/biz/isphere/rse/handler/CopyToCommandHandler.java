/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import biz.isphere.core.sourcemembercopy.rse.CopyMemberDialog;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class CopyToCommandHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.rse.handler.CopyToCommandHandler";

    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (!(selection instanceof TreeSelection)) {
            return null;
        }

        CopyMemberService jobDescription = null;

        TreeSelection selectedMembers = (TreeSelection)selection;
        for (Iterator<?> iterator = selectedMembers.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            if (item instanceof QSYSRemoteSourceMember) {
                QSYSRemoteSourceMember sourceMember = (QSYSRemoteSourceMember)item;
                String connectionName = sourceMember.getConnection().getConnectionName();
                if (jobDescription == null) {
                    jobDescription = new CopyMemberService(connectionName);
                } else {
                    if (!jobDescription.getFromConnectionName().equals(connectionName)) {
                        MessageDialog.openError(getShell(event), Messages.E_R_R_O_R, Messages.Cannot_copy_source_members_from_different_connections);
                        return null;
                    }
                }
                jobDescription.addItem(sourceMember.getFile(), sourceMember.getLibrary(), sourceMember.getName());
            }
        }

        if (jobDescription.getItems().length > 0) {
            CopyMemberDialog dialog = new CopyMemberDialog(getShell(event));
            dialog.setContent(jobDescription);
            dialog.open();
        }

        return null;
    }

    private Shell getShell(ExecutionEvent event) {
        return HandlerUtil.getActivePart(event).getSite().getShell();
    }
}
