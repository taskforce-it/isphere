/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.compareeditor.CompareStreamFileDialog;
import biz.isphere.core.internal.StreamFile;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.RSEStreamFile;

import com.ibm.etools.iseries.rse.ui.widgets.IBMiConnectionCombo;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileServiceSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSECompareStreamFileDialog extends CompareStreamFileDialog {

    private Group ancestorGroup;
    private RSEStreamFile rseLeftStreamFile;
    private IBMiConnectionCombo rightConnectionCombo;
    private StreamFilePrompt rightStreamFilePrompt;
    private IBMiConnection rightConnection;
    private String rightDirectory;
    private String rightStreamFile;
    private IBMiConnectionCombo ancestorConnectionCombo;
    private StreamFilePrompt ancestorStreamFilePrompt;
    private IBMiConnection ancestorConnection;
    private String ancestorDirectory;
    private String ancestorStreamFile;

    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile leftStreamFile, RSEStreamFile rightStreamFile,
        RSEStreamFile ancestorStreamFile) {
        super(parentShell, selectEditable, leftStreamFile, rightStreamFile, ancestorStreamFile);
        this.rseLeftStreamFile = leftStreamFile;
        initializeRightStreamFile(rightStreamFile);
    }

    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile[] selectedStreamFiles) {
        super(parentShell, selectEditable, selectedStreamFiles);
        this.rseLeftStreamFile = selectedStreamFiles[0];
        initializeRightStreamFile(selectedStreamFiles[0]);
    }

    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile leftStreamFile, RSEStreamFile rightStreamFile) {
        super(parentShell, selectEditable, leftStreamFile, rightStreamFile);
        this.rseLeftStreamFile = leftStreamFile;
        initializeRightStreamFile(rightStreamFile);
    }

    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile leftStreamFile) {
        super(parentShell, selectEditable, leftStreamFile);
        this.rseLeftStreamFile = leftStreamFile;
    }

    private void initializeRightStreamFile(RSEStreamFile rightStreamFile) {
        this.rightConnection = rightStreamFile.getRSEConnection();
        this.rightDirectory = rightStreamFile.getDirectory();
        this.rightStreamFile = rightStreamFile.getStreamFile();
    }

    @Override
    public void createRightArea(Composite parent) {

        Group rightGroup = new Group(parent, SWT.NONE);
        rightGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightGroup.setLayout(rightLayout);
        rightGroup.setLayoutData(getGridData());

        rightConnectionCombo = new IBMiConnectionCombo(rightGroup, rseLeftStreamFile.getRSEConnection(), false);
        rightConnectionCombo.setLayoutData(getGridData());
        rightConnectionCombo.getCombo().setLayoutData(getGridData());

        rightConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                rightStreamFilePrompt.setConnection(rightConnectionCombo.getHost());
            }
        });

        rightStreamFilePrompt = new StreamFilePrompt(rightGroup, SWT.NONE);
        rightStreamFilePrompt.setConnection(rightConnectionCombo.getHost());
        rightStreamFilePrompt.setDirectoryName(rseLeftStreamFile.getDirectory());

        if (hasMultipleRightStreamFiles()) {
            rightStreamFilePrompt.setStreamFileName(SPECIAL_MEMBER_NAME_LEFT);
        } else {
            rightStreamFilePrompt.setStreamFileName(rseLeftStreamFile.getStreamFile());
        }

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        rightStreamFilePrompt.getStreamFileWidget().addModifyListener(modifyListener);
        rightStreamFilePrompt.getDirectoryWidget().addModifyListener(modifyListener);
        rightStreamFilePrompt.getDirectoryWidget().setFocus();

        setRightStreamFilePromptEnablement(!hasMultipleRightStreamFiles());
    }

    @Override
    public void createAncestorArea(Composite parent) {

        ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout();
        ancestorLayout.numColumns = 1;
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        ancestorConnectionCombo = new IBMiConnectionCombo(ancestorGroup, rseLeftStreamFile.getRSEConnection(), false);
        ancestorConnectionCombo.setLayoutData(getGridData());
        ancestorConnectionCombo.getCombo().setLayoutData(getGridData());

        ancestorConnectionCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getOkButton().setEnabled(canFinish());
                ancestorStreamFilePrompt.setConnection(ancestorConnectionCombo.getHost());
            }
        });

        ancestorStreamFilePrompt = new StreamFilePrompt(ancestorGroup, SWT.NONE);
        ancestorStreamFilePrompt.setConnection(ancestorConnectionCombo.getHost());
        ancestorStreamFilePrompt.setDirectoryName(rseLeftStreamFile.getDirectory());
        ancestorStreamFilePrompt.setStreamFileName(rseLeftStreamFile.getStreamFile());

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getOkButton().setEnabled(canFinish());
            }
        };

        ancestorStreamFilePrompt.getStreamFileWidget().addModifyListener(modifyListener);
        ancestorStreamFilePrompt.getDirectoryWidget().addModifyListener(modifyListener);

    }

    @Override
    protected void setAncestorVisible(boolean visible) {
        ancestorGroup.setVisible(visible);
        if (visible) {
            ancestorStreamFilePrompt.getDirectoryWidget().setFocus();
        } else {
            rightStreamFilePrompt.getDirectoryWidget().setFocus();
        }
    }

    @Override
    protected void okPressed() {

        if (hasMultipleRightStreamFiles()) {

            rightConnection = ConnectionManager.getIBMiConnection(rightConnectionCombo.getHost());
            rightDirectory = getRightDirectoryName();
            rightStreamFile = null;

            // TODO : Check directory for validity : RIGHT
            /*
             * IQSYSDirectory _directory = null; try { _directory =
             * rightConnection.getDirectory(rightDirectory, null); } catch
             * (Exception e) { } if (_directory == null) { String message =
             * biz.isphere.core.Messages.bind(Messages.Directory_A_not_found,
             * new Object[] { rightDirectory });
             * MessageDialog.openError(getShell(),
             * biz.isphere.core.Messages.Error, message);
             * rightStreamFilePrompt.getDirectoryCombo().setFocus(); return; }
             */

        } else if (!hasRightStreamFile()) {

            rightConnection = ConnectionManager.getIBMiConnection(rightConnectionCombo.getHost());
            rightDirectory = getRightDirectoryName();
            rightStreamFile = getRightStreamFileName();

            // TODO : Check stream file for validity : RIGHT

            /*
             * RSEStreamFile _rightStreamFile = getRightRSEStreamFile(); if
             * (_rightStreamFile == null) {
             * rightStreamFilePrompt.getStreamFileCombo().setFocus(); return; }
             * else if (!_rightStreamFile.exists()) { String message =
             * biz.isphere.core.Messages.bind(biz.isphere.core.Messages.
             * StreamFile_2_in_directory_0_not_found, new Object[] {
             * rightDirectory, rightStreamFile });
             * MessageDialog.openError(getShell(),
             * biz.isphere.core.Messages.Error, message);
             * rightStreamFilePrompt.getStreamFileCombo().setFocus(); return; }
             */
            if (isThreeWay()) {

                ancestorConnection = ConnectionManager.getIBMiConnection(ancestorConnectionCombo.getHost());
                ancestorDirectory = getAncestorDirectoryName();
                ancestorStreamFile = getAncestorStreamFileName();

                // TODO : Check stream file for validity : ANCESTOR

                /*
                 * RSEStreamFile _ancestorStreamFile =
                 * getAncestorRSEStreamFile(); if (_ancestorStreamFile == null)
                 * { ancestorStreamFilePrompt.getStreamFileCombo().setFocus();
                 * return; } else if (!_ancestorStreamFile.exists()) { String
                 * message =
                 * biz.isphere.core.Messages.bind(biz.isphere.core.Messages
                 * .StreamFile_2_in_directory_0_not_found, new Object[] {
                 * ancestorDirectory, ancestorStreamFile });
                 * MessageDialog.openError(getShell(),
                 * biz.isphere.core.Messages.Error, message);
                 * ancestorStreamFilePrompt.getStreamFileCombo().setFocus();
                 * return; }
                 */
            }

        }

        // Close dialog
        super.okPressed();
    }

    @Override
    public boolean canFinish() {
        if (isThreeWay()) {
            if (getRightStreamFileName() == null || getRightStreamFileName().length() == 0 || getRightDirectoryName() == null
                || getRightDirectoryName().length() == 0 || getAncestorStreamFileName() == null || getAncestorStreamFileName().length() == 0
                || getAncestorDirectoryName() == null || getAncestorDirectoryName().length() == 0) {
                return false;
            }
            if (getRightStreamFileName().equalsIgnoreCase(getAncestorStreamFileName())
                && getRightDirectoryName().equalsIgnoreCase(getAncestorDirectoryName())
                && rightConnectionCombo.getHost().getHostName().equals(ancestorConnectionCombo.getHost().getHostName())) {
                return false;
            }
            if (getRightDirectoryName().equalsIgnoreCase(rseLeftStreamFile.getDirectory())
                && getRightStreamFileName().equalsIgnoreCase(rseLeftStreamFile.getStreamFile())
                && rightConnectionCombo.getHost().getHostName().equals(rseLeftStreamFile.getRSEConnection().getHostName())) {
                return false;
            }
            if (getAncestorDirectoryName().equalsIgnoreCase(rseLeftStreamFile.getDirectory())
                && getAncestorStreamFileName().equalsIgnoreCase(rseLeftStreamFile.getStreamFile())
                && ancestorConnectionCombo.getHost().getHostName().equals(rseLeftStreamFile.getRSEConnection().getHostName())) {
                return false;
            }
        } else {
            String rightStreamFile = getRightStreamFileName();
            if (rightStreamFile == null || rightStreamFile.length() == 0) {
                return false;
            }
            if (rightStreamFile.equalsIgnoreCase(rseLeftStreamFile.getStreamFile())
                && getRightDirectoryName().equalsIgnoreCase(rseLeftStreamFile.getDirectory())
                && rightConnectionCombo.getHost().getHostName().equalsIgnoreCase(rseLeftStreamFile.getRSEConnection().getHostName())) {
                return false;
            }
        }
        return true;
    }

    private void setRightStreamFilePromptEnablement(boolean enabled) {
        rightStreamFilePrompt.getStreamFileWidget().setEnabled(enabled);
    }

    private String getRightDirectoryName() {
        if (rightStreamFilePrompt.getDirectoryName() == null) {
            return null;
        }
        return rightStreamFilePrompt.getDirectoryName().trim();
    }

    private String getRightStreamFileName() {
        if (rightStreamFilePrompt.getStreamFileName() == null) {
            return null;
        }
        String streamFileName = rightStreamFilePrompt.getStreamFileName().trim();
        if (SPECIAL_MEMBER_NAME_LEFT.equalsIgnoreCase(streamFileName)) {
            streamFileName = rseLeftStreamFile.getStreamFile();
        }
        return streamFileName;
    }

    private String getAncestorDirectoryName() {
        if (ancestorStreamFilePrompt.getDirectoryName() == null) {
            return null;
        }
        return ancestorStreamFilePrompt.getDirectoryName().trim();
    }

    private String getAncestorStreamFileName() {
        if (ancestorStreamFilePrompt.getStreamFileName() == null) {
            return null;
        }
        return ancestorStreamFilePrompt.getStreamFileName().trim();
    }

    public RSEStreamFile getLeftRSEStreamFile() {
        return rseLeftStreamFile;
    }

    public RSEStreamFile getRightRSEStreamFile() {
        try {
            IFSFileServiceSubSystem fileServiceSubSystem = getFileServiceSubSystem(rightConnection);
            if (fileServiceSubSystem != null) {
                IRemoteFile remoteFile = getRemoteFile(fileServiceSubSystem, rightDirectory, rightStreamFile);
                if (remoteFile != null) {
                    return new RSEStreamFile(remoteFile);
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, e.getMessage());
        }
        return null;
    }

    public IBMiConnection getRightConnection() {
        return rightConnection;
    }

    public String getRightDirectory() {
        return rightDirectory;
    }

    public String getRightStreamFile() {
        return rightStreamFile;
    }

    public IBMiConnection getAncestorConnection() {
        return ancestorConnection;
    }

    public String getAncestorDirectory() {
        return ancestorDirectory;
    }

    public String getAncestorStreamFile() {
        return ancestorStreamFile;
    }

    public RSEStreamFile getAncestorRSEStreamFile() {
        try {
            IFSFileServiceSubSystem fileServiceSubSystem = getFileServiceSubSystem(ancestorConnection);
            if (fileServiceSubSystem != null) {
                IRemoteFile remoteFile = getRemoteFile(fileServiceSubSystem, ancestorDirectory, ancestorStreamFile);
                if (remoteFile != null) {
                    return new RSEStreamFile(remoteFile);
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, e.getMessage());
        }
        return null;
    }

    @Override
    protected void switchLeftAndRightStreamFile(StreamFile leftStreamFile, StreamFile rightStreamFile) {
        super.switchLeftAndRightStreamFile(leftStreamFile, rightStreamFile);
        initializeRightStreamFile((RSEStreamFile)leftStreamFile);
        this.rseLeftStreamFile = (RSEStreamFile)rightStreamFile;
    }

    public IFSFileServiceSubSystem getFileServiceSubSystem(IBMiConnection ibmiConnection) {
        IFSFileServiceSubSystem fileServiceSubSystem = null;
        ISubSystem[] sses = RSECorePlugin.getTheSystemRegistry().getSubSystems(ibmiConnection.getHost());
        for (int i = 0; i < sses.length; i++) {
            if ((sses[i] instanceof IFSFileServiceSubSystem)) {
                fileServiceSubSystem = (IFSFileServiceSubSystem)sses[i];
                break;
            }
        }
        return fileServiceSubSystem;
    }

    public IRemoteFile getRemoteFile(IFSFileServiceSubSystem fileServiceSubSystem, String directory, String streamFile) throws Exception {
        return fileServiceSubSystem.getRemoteFileObject(directory + "/" + streamFile, new NullProgressMonitor());
    }

}
