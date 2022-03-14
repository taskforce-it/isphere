/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.files.ui.dialogs.SystemRemoteFileDialog;
import org.eclipse.rse.files.ui.dialogs.SystemRemoteFolderDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.IFSRemoteFileHelper;

// TODO : All methods are currently empty and have to filled with life.

public class StreamFilePrompt extends Composite {

    private static final String COMBO_FOLDER = "FOLDER"; //$NON-NLS-1$
    private static final String COMBO_STREAM_FILE = "STREAM_FILE"; //$NON-NLS-1$

    private String historyKey;

    private IHost host;
    private HistoryCombo cboFolder;
    private Button btnSelectDirectory;
    private HistoryCombo cboStreamFile;
    private Button btnSelectStreamFile;

    private DialogSettingsManager dialogSettingsmanager;

    public StreamFilePrompt(Composite parent, String historyKey, int style) {
        super(parent, style);

        this.historyKey = historyKey;

        createContent(this);
    }

    private void createContent(Composite parent) {

        setLayout(new GridLayout(3, false));

        Label lblDirectory = new Label(parent, SWT.NONE);
        lblDirectory.setText(Messages.Label_Folder_colon);

        cboFolder = WidgetFactory.createHistoryCombo(parent);
        cboFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectDirectory = WidgetFactory.createPushButton(parent, Messages.Label_Browse);
        btnSelectDirectory.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                SystemRemoteFolderDialog dialog = new SystemRemoteFolderDialog(getShell(), Messages.Title_Browse_For_Folder, host);

                dialog.setDefaultSystemConnection(host, true);
                dialog.setShowNewConnectionPrompt(false);
                dialog.setMultipleSelectionMode(false);

                IFSRemoteFile folder = IFSRemoteFileHelper.getRemoteFolder(host, cboFolder.getText());
                if (folder != null) {
                    dialog.setPreSelection(folder);
                }

                if (dialog.open() == Dialog.OK) {
                    Object selectedFolder = dialog.getSelectedObject();
                    if (selectedFolder instanceof IFSRemoteFile) {
                        folder = (IFSRemoteFile)selectedFolder;
                        if (folder.isDirectory()) {
                            cboFolder.setText(folder.getAbsolutePath());
                        }
                    }
                }
            }
        });

        Label lblStreamFile = new Label(parent, SWT.NONE);
        lblStreamFile.setText(Messages.Label_Stream_file_colon);

        cboStreamFile = WidgetFactory.createHistoryCombo(parent);
        cboStreamFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectStreamFile = WidgetFactory.createPushButton(parent, Messages.Label_Browse);
        btnSelectStreamFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                SystemRemoteFileDialog dialog = new SystemRemoteFileDialog(getShell(), Messages.Title_Browse_For_File, host);

                dialog.setDefaultSystemConnection(host, true);
                dialog.setShowNewConnectionPrompt(false);
                dialog.setMultipleSelectionMode(false);

                IFSRemoteFile file = IFSRemoteFileHelper.getRemoteFile(host, cboFolder.getText(), cboStreamFile.getText());
                if (file != null) {
                    dialog.setPreSelection(file);
                }

                if (dialog.open() == Dialog.OK) {
                    Object selectedFile = dialog.getSelectedObject();
                    if (selectedFile instanceof IFSRemoteFile) {
                        file = (IFSRemoteFile)selectedFile;
                        if (!file.isDirectory()) {
                            cboFolder.setText(file.getParentPath());
                            cboStreamFile.setText(file.getName());
                        }
                    }
                }
            }
        });
    }

    public void setConnection(IHost host) {
        this.host = host;
    }

    public HistoryCombo getDirectoryWidget() {
        return cboFolder;
    }

    public String getDirectoryName() {
        return cboFolder.getText();
    }

    public void setDirectoryName(String directory) {
        cboFolder.setText(directory);
    }

    public HistoryCombo getStreamFileWidget() {
        return cboStreamFile;
    }

    public String getStreamFileName() {
        return cboStreamFile.getText();
    }

    public void setStreamFileName(String streamFile) {
        cboStreamFile.setText(streamFile);
    }

    public void loadHistory(DialogSettingsManager dialogSettingsManager) {

        this.dialogSettingsmanager = dialogSettingsManager;

        if (!StringHelper.isNullOrEmpty(historyKey)) {
            cboFolder.load(dialogSettingsmanager, createKey(historyKey, COMBO_FOLDER));
            cboStreamFile.load(dialogSettingsmanager, createKey(historyKey, COMBO_STREAM_FILE));
        }
    }

    public void updateHistory() {

        if (!StringHelper.isNullOrEmpty(historyKey)) {
            cboFolder.updateHistory(cboFolder.getText());
            cboStreamFile.updateHistory(cboStreamFile.getText());
        }
    }

    public void storeHistory() {

        if (!StringHelper.isNullOrEmpty(historyKey)) {
            cboFolder.store();
            cboStreamFile.store();
        }
    }

    private String createKey(String baseKey, String comboKey) {
        return baseKey + "_" + comboKey;
    }
}
