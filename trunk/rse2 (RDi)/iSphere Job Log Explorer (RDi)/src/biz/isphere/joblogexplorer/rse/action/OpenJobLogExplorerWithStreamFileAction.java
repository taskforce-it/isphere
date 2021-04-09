/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.joblogexplorer.action.rse.AbstractOpenJobLogExplorerAction;
import biz.isphere.joblogexplorer.externalapi.Access;
import biz.isphere.joblogexplorer.rse.Messages;

public class OpenJobLogExplorerWithStreamFileAction extends AbstractOpenJobLogExplorerAction {

    public static final String ID = "biz.isphere.joblogexplorer.rse.action.OpenJobLogExplorerWithStreamFileAction"; //$NON-NLS-1$

    @Override
    protected void execute(Object object) {

        if (object instanceof IRemoteFile) {
            IRemoteFile remoteFile = (IRemoteFile)object;

            try {

                Access.loadJobLogExplorer(shell, new File(remoteFile.getAbsolutePath()));

            } catch (Exception e) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

        }
    }

}
