/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
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

    private static final String COMBO_DIRECTORY = "DIRECTORY"; //$NON-NLS-1$
    private static final String COMBO_STREAM_FILE = "STREAM_FILE"; //$NON-NLS-1$

    private String historyKey;

    private IHost host;
    private HistoryCombo cboDirectory;
    private Button btnSelectDirectory;
    private HistoryCombo cboStreamFile;
    private Button btnSelectStreamFile;

    private DialogSettingsManager dialogSettingsmanager;

    public StreamFilePrompt(Composite parent, int style) {
        this(parent, null, style);
    }

    public StreamFilePrompt(Composite parent, String historyKey, int style) {
        super(parent, style);

        setHistoryKey(historyKey);

        createContent(this);
    }

    @Override
    public boolean setFocus() {
        return getDirectoryWidget().setFocus();
    }

    public void setHistoryKey(String historyKey) {
        this.historyKey = historyKey;
    }

    private void createContent(Composite parent) {

        setLayout(new GridLayout(3, false));

        Label lblDirectory = new Label(parent, SWT.NONE);
        lblDirectory.setText(Messages.Label_Directory_colon);

        cboDirectory = WidgetFactory.createHistoryCombo(parent);
        cboDirectory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnSelectDirectory = WidgetFactory.createPushButton(parent, Messages.Label_Browse);
        btnSelectDirectory.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                SystemRemoteFolderDialog dialog = new SystemRemoteFolderDialog(getShell(), Messages.Title_Browse_For_Directory);

                dialog.setDefaultSystemConnection(host, true);
                dialog.setShowNewConnectionPrompt(false);
                dialog.setMultipleSelectionMode(false);

                IFSRemoteFile directory = IFSRemoteFileHelper.getRemoteDirectory(host, cboDirectory.getText());
                if (directory != null) {
                    dialog.setPreSelection(directory);
                }

                if (dialog.open() == Dialog.OK) {
                    Object selectedDirectory = dialog.getSelectedObject();
                    if (selectedDirectory instanceof IFSRemoteFile) {
                        directory = (IFSRemoteFile)selectedDirectory;
                        if (directory.isDirectory()) {
                            cboDirectory.setText(directory.getAbsolutePath());
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
                SystemRemoteFileDialog dialog = new SystemRemoteFileDialog(getShell(), Messages.Title_Browse_For_Stream_File);

                dialog.setDefaultSystemConnection(host, true);
                dialog.setShowNewConnectionPrompt(false);
                dialog.setMultipleSelectionMode(false);

                IFSRemoteFile streamFile = IFSRemoteFileHelper.getRemoteStreamFile(host, cboDirectory.getText(), cboStreamFile.getText());
                if (streamFile != null) {
                    dialog.setPreSelection(streamFile);
                }

                if (dialog.open() == Dialog.OK) {
                    Object selectedStreamFile = dialog.getSelectedObject();
                    if (selectedStreamFile instanceof IFSRemoteFile) {
                        streamFile = (IFSRemoteFile)selectedStreamFile;
                        if (!streamFile.isDirectory()) {
                            cboDirectory.setText(streamFile.getParentPath());
                            cboStreamFile.setText(streamFile.getName());
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
        return cboDirectory;
    }

    public Button getSelectDirectoryWidget() {
        return btnSelectDirectory;
    }

    public String getDirectoryName() {
        return cboDirectory.getText();
    }

    public void setDirectoryName(String directory) {
        cboDirectory.setText(directory);
    }

    public HistoryCombo getStreamFileWidget() {
        return cboStreamFile;
    }

    public Button getSelectStreamFileWidget() {
        return btnSelectStreamFile;
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
            cboDirectory.load(dialogSettingsmanager, createKey(historyKey, COMBO_DIRECTORY));
            cboStreamFile.load(dialogSettingsmanager, createKey(historyKey, COMBO_STREAM_FILE));
        }
    }

    public void updateHistory() {

        if (!StringHelper.isNullOrEmpty(historyKey)) {
            cboDirectory.updateHistory(cboDirectory.getText());
            cboStreamFile.updateHistory(cboStreamFile.getText());
        }
    }

    public void storeHistory() {

        if (!StringHelper.isNullOrEmpty(historyKey)) {
            cboDirectory.store();
            cboStreamFile.store();
        }
    }

    private String createKey(String baseKey, String comboKey) {
        return baseKey + "_" + comboKey;
    }
}
