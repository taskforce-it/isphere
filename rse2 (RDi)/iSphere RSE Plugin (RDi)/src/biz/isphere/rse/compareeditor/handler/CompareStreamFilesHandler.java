/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.compareeditor.CompareEditorConfiguration;
import biz.isphere.core.compareeditor.CompareStreamFileAction;
import biz.isphere.core.compareeditor.SourceMemberCompareEditorConfiguration;
import biz.isphere.core.internal.StreamFile;
import biz.isphere.core.internal.handler.AbstractCommandHandler;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.RSECompareStreamFileDialog;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.IFSRemoteFileHelper;
import biz.isphere.rse.internal.RSEStreamFile;

public class CompareStreamFilesHandler extends AbstractCommandHandler {

    public static final String ID = "biz.isphere.core.command.StreamFileCompare.open";

    public Object execute(ExecutionEvent event) throws ExecutionException {

        RSEStreamFile[] selectedStreamFiles = new RSEStreamFile[0];

        handleSourceCompareInternally(getShell(), selectedStreamFiles, true);

        return null;
    }

    public void handleReadOnlySourceCompare(StreamFile[] selectedStreamFiles) {
        handleSourceCompareInternally(getShell(), selectedStreamFiles, false);
    }

    public void handleSourceCompare(StreamFile[] selectedStreamFiles) {
        handleSourceCompareInternally(getShell(), selectedStreamFiles, true);
    }

    private void handleSourceCompareInternally(Shell shell, StreamFile[] selectedStreamFiles, boolean selectEditable) {

        RSECompareStreamFileDialog dialog;
        if (selectedStreamFiles.length > 2) {
            dialog = new RSECompareStreamFileDialog(shell, selectEditable, selectedStreamFiles);
        } else if (selectedStreamFiles.length == 2) {
            dialog = new RSECompareStreamFileDialog(shell, selectEditable, selectedStreamFiles[0], selectedStreamFiles[1]);
        } else if (selectedStreamFiles.length == 1) {
            dialog = new RSECompareStreamFileDialog(shell, selectEditable, selectedStreamFiles[0]);
        } else {
            dialog = new RSECompareStreamFileDialog(shell, selectEditable);
        }

        if (dialog.open() == Dialog.OK) {

            boolean editable = dialog.isEditable();
            boolean ignoreCase = dialog.isIgnoreCase();
            boolean threeWay = dialog.isThreeWay();

            RSEStreamFile rseAncestorStreamFile = null;
            if (threeWay) {
                rseAncestorStreamFile = dialog.getAncestorRSEStreamFile();
            }

            CompareEditorConfiguration cc = new SourceMemberCompareEditorConfiguration();
            cc.setLeftEditable(editable);
            cc.setRightEditable(false);
            cc.setIgnoreCase(ignoreCase);
            cc.setThreeWay(threeWay);

            if (selectedStreamFiles.length > 2) {
                String rightConnection = dialog.getRightConnectionName();
                String rightDirectory = dialog.getRightDirectory();
                for (StreamFile rseSelectedStreamFile : selectedStreamFiles) {
                    String rightStreamFile = rseSelectedStreamFile.getStreamFile();
                    RSEStreamFile rseRightStreamFile = getStreamFile(shell, rightConnection, rightDirectory, rightStreamFile);
                    if (rseRightStreamFile == null || !rseRightStreamFile.exists()) {
                        String message = biz.isphere.core.Messages.bind(Messages.Stream_file_B_not_found_in_directory_A,
                            new Object[] { rightDirectory, rightStreamFile });
                        MessageDialog.openError(shell, biz.isphere.core.Messages.Error, message);

                    } else {
                        CompareStreamFileAction action = new CompareStreamFileAction(cc, rseAncestorStreamFile, rseSelectedStreamFile,
                            rseRightStreamFile, null);
                        action.run();
                    }
                }
            } else {
                StreamFile rseLeftStreamFile = dialog.getLeftRSEStreamFile();
                StreamFile rseRightStreamFile = dialog.getRightRSEStreamFile();
                CompareStreamFileAction action = new CompareStreamFileAction(cc, rseAncestorStreamFile, rseLeftStreamFile, rseRightStreamFile, null);
                action.run();
            }
        }
    }

    private RSEStreamFile getStreamFile(Shell shell, String connectionName, String directory, String streamFile) {
        try {
            IBMiConnection connection = ConnectionManager.getIBMiConnection(connectionName);
            IFSRemoteFile remoteStreamFile = IFSRemoteFileHelper.getRemoteStreamFile(connection, directory, streamFile);
            return new RSEStreamFile(remoteStreamFile);
        } catch (Exception e) {
            MessageDialog.openError(shell, biz.isphere.core.Messages.Error, e.getMessage());
            return null;
        }
    }
}
