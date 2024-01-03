/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.compareeditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileServiceSubSystem;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.compareeditor.CompareStreamFileDialog;
import biz.isphere.core.compareeditor.LoadPreviousStreamFileValue;
import biz.isphere.core.internal.StreamFile;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.IFSRemoteFileHelper;
import biz.isphere.rse.internal.RSEStreamFile;

public class RSECompareStreamFileDialog extends CompareStreamFileDialog {

    private static final String LEFT_HISTORY_KEY = "leftStreamFileHistory"; //$NON-NLS-1$
    private static final String RIGHT_HISTORY_KEY = "rightStreamFileHistory"; //$NON-NLS-1$
    private static final String ANCESTOR_HISTORY_KEY = "ancestorStreamFileHistory"; //$NON-NLS-1$

    private ConnectionCombo leftConnectionCombo;
    private StreamFilePrompt leftStreamFilePrompt;
    private IBMiConnection leftConnection;
    private String leftDirectory;
    private String leftStreamFile;

    private ConnectionCombo rightConnectionCombo;
    private StreamFilePrompt rightStreamFilePrompt;
    private IBMiConnection rightConnection;
    private String rightDirectory;
    private String rightStreamFile;

    private Group leftGroup;
    private Group ancestorGroup;
    private ConnectionCombo ancestorConnectionCombo;
    private StreamFilePrompt ancestorStreamFilePrompt;
    private IBMiConnection ancestorConnection;
    private String ancestorDirectory;
    private String ancestorStreamFile;

    /**
     * CMOne specific constructor that create a 3-way compare dialog.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftStreamFile - the left selected stream file
     * @param rightStreamFile - the right selected stream file
     * @param ancestorStreamFile - the ancestor selected stream file
     * @param switchStreamFileAllowed
     */
    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile leftStreamFile, RSEStreamFile rightStreamFile,
        RSEStreamFile ancestorStreamFile, boolean switchStreamFileAllowed) {
        super(parentShell, selectEditable, leftStreamFile, rightStreamFile, ancestorStreamFile);
        setHistoryValuesCategoryKey(null);
        initializeLeftStreamFile(leftStreamFile);
        initializeRightStreamFile(rightStreamFile);
        initializeAncestorStreamFile(ancestorStreamFile);
        setSwitchStreamFileAllowed(switchStreamFileAllowed);
    }

    /**
     * CMOne specific constructor that create compare dialog for 2 selected
     * stream files.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftStreamFile - the left selected stream file
     * @param rightStreamFile - the right selected stream file
     * @param switchStreamFileAllowed
     */
    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, RSEStreamFile leftStreamFile, RSEStreamFile rightStreamFile,
        boolean switchStreamFileAllowed) {
        super(parentShell, selectEditable, leftStreamFile, rightStreamFile);
        setHistoryValuesCategoryKey(null);
        initializeLeftStreamFile(leftStreamFile);
        initializeRightStreamFile(rightStreamFile);
        setSwitchStreamFileAllowed(switchStreamFileAllowed);
    }

    /**
     * Creates the compare dialog, for 3 and more selected stream files.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param selectedStreamFiles - the selected stream files that go to the
     *        left side of the compare dialog
     */
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile[] selectedStreamFiles) {
        super(parentShell, selectEditable, selectedStreamFiles);
        setHistoryValuesCategoryKey("multiple");
        initializeLeftStreamFile(selectedStreamFiles[0]);
        initializeRightStreamFile(selectedStreamFiles[0]);
    }

    /**
     * Creates the compare dialog, for 2 selected stream files.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftStreamFile - the left selected stream file
     * @param rightStreamFile - the right selected stream file
     */
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile, StreamFile rightStreamFile) {
        super(parentShell, selectEditable, leftStreamFile, rightStreamFile);
        setHistoryValuesCategoryKey("2");
        initializeLeftStreamFile(leftStreamFile);
        initializeRightStreamFile(rightStreamFile);
        setSwitchStreamFileAllowed(true);
    }

    /**
     * Creates the compare dialog, for 1 selected stream file.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     * @param leftStreamFile - the left selected stream file
     */
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile) {
        super(parentShell, selectEditable, leftStreamFile);
        setHistoryValuesCategoryKey("1");
        initializeLeftStreamFile(leftStreamFile);
    }

    /**
     * Creates the compare dialog, for 0 selected stream files.
     * 
     * @param parentShell - shell the dialog is associated to
     * @param selectEditable - specifies whether or not option "Open for
     *        browse/edit" is displayed
     */
    public RSECompareStreamFileDialog(Shell parentShell, boolean selectEditable) {
        super(parentShell, selectEditable, null, null, null);
        setHistoryValuesCategoryKey("0");
    }

    private void initializeLeftStreamFile(StreamFile leftStreamFile) {
        this.leftConnection = getConnection(leftStreamFile);
        this.leftDirectory = leftStreamFile.getDirectory();
        this.leftStreamFile = leftStreamFile.getStreamFile();
    }

    private void initializeRightStreamFile(StreamFile rightStreamFile) {
        this.rightConnection = getConnection(rightStreamFile);
        this.rightDirectory = rightStreamFile.getDirectory();
        this.rightStreamFile = rightStreamFile.getStreamFile();
    }

    private void initializeAncestorStreamFile(StreamFile ancestorStreamFile) {
        this.ancestorConnection = getConnection(ancestorStreamFile);
        this.ancestorDirectory = ancestorStreamFile.getDirectory();
        this.ancestorStreamFile = ancestorStreamFile.getStreamFile();
    }

    private IBMiConnection getConnection(StreamFile streamFile) {

        if (streamFile instanceof RSEStreamFile) {
            return ((RSEStreamFile)streamFile).getRSEConnection();
        } else {
            return ConnectionManager.getIBMiConnection(streamFile.getConnection());
        }

    }

    @Override
    public Control createDialogArea(Composite parent) {

        Control composite = super.createDialogArea(parent);

        if (hasEditableLeftStreamFile()) {
            leftStreamFilePrompt.loadHistory(getDialogSettingsManager());
        }

        if (hasEditableRightStreamFile()) {
            rightStreamFilePrompt.loadHistory(getDialogSettingsManager());
        }

        if (hasEditableAncestorStreamFile()) {
            ancestorStreamFilePrompt.loadHistory(getDialogSettingsManager());
        }

        return composite;
    }

    @Override
    protected void createEditableLeftArea(Composite parent) {

        leftGroup = createStreamFileGroup(parent, Messages.Left);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setOKButtonEnablement();
                leftStreamFilePrompt.setConnection(getHost(getCurrentLeftConnectionName()));
            }
        };

        leftConnectionCombo = createConnectionCombo(leftGroup, getLeftConnection(), selectionListener);

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setOKButtonEnablement();
            }
        };

        leftStreamFilePrompt = createStreamFilePrompt(leftGroup, modifyListener, LEFT_HISTORY_KEY);
        leftStreamFilePrompt.setConnection(getHost(getCurrentLeftConnectionName()));
        leftStreamFilePrompt.getDirectoryWidget().setFocus();
    }

    @Override
    public void createEditableRightArea(Composite parent) {

        Group rightGroup = createStreamFileGroup(parent, Messages.Right);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setOKButtonEnablement();
                rightStreamFilePrompt.setConnection(getHost(getCurrentRightConnectionName()));
            }
        };

        rightConnectionCombo = createConnectionCombo(rightGroup, getRightConnection(), selectionListener);

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setOKButtonEnablement();
            }
        };

        rightStreamFilePrompt = createStreamFilePrompt(rightGroup, modifyListener, RIGHT_HISTORY_KEY);
        rightStreamFilePrompt.setConnection(getHost(getCurrentRightConnectionName()));
        rightStreamFilePrompt.getDirectoryWidget().setFocus();

        rightStreamFilePrompt.getStreamFileWidget().setEnabled(!hasMultipleRightStreamFiles());
        rightStreamFilePrompt.getSelectStreamFileWidget().setEnabled(!hasMultipleRightStreamFiles());
    }

    @Override
    public void createEditableAncestorArea(Composite parent) {

        ancestorGroup = createStreamFileGroup(parent, Messages.Ancestor);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setOKButtonEnablement();
                ancestorStreamFilePrompt.setConnection(getHost(getCurrentAncestorConnectionName()));
            }
        };

        ancestorConnectionCombo = createConnectionCombo(ancestorGroup, getAncestorConnection(), selectionListener);

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setOKButtonEnablement();
            }
        };

        ancestorStreamFilePrompt = createStreamFilePrompt(ancestorGroup, modifyListener, ANCESTOR_HISTORY_KEY);
        ancestorStreamFilePrompt.setConnection(getHost(getCurrentAncestorConnectionName()));
    }

    protected void setLeftGroupLabel(String label) {
        if (leftGroup == null) {
            super.setLeftGroupLabel(label);
        } else {
            leftGroup.setText(label);
        }
    }

    private Group createStreamFileGroup(Composite parent, String label) {

        Group streamFileGroup = new Group(parent, SWT.NONE);
        streamFileGroup.setText(label);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        streamFileGroup.setLayout(layout);
        streamFileGroup.setLayoutData(getGridData());

        return streamFileGroup;
    }

    private ConnectionCombo createConnectionCombo(Group group, IBMiConnection connection, SelectionListener selectionListener) {

        ConnectionCombo connectionCombo = WidgetFactory.createConnectionCombo(group);
        if (connection != null) {
            connectionCombo.setQualifiedConnectionName(connection.getConnectionName());
        }
        connectionCombo.setLayoutData(getGridData());
        connectionCombo.setLayoutData(getGridData());

        connectionCombo.addSelectionListener(selectionListener);

        return connectionCombo;
    }

    private StreamFilePrompt createStreamFilePrompt(Composite leftGroup, ModifyListener modifyListener, String historyKey) {

        StreamFilePrompt streamFilePrompt = new StreamFilePrompt(leftGroup, SWT.NONE);

        if (canStoreHistory()) {
            streamFilePrompt.setHistoryKey(historyKey);
        }

        streamFilePrompt.getDirectoryWidget().addModifyListener(modifyListener);
        streamFilePrompt.getStreamFileWidget().addModifyListener(modifyListener);

        return streamFilePrompt;
    }

    private void setOKButtonEnablement() {
        if (getOkButton() != null) {
            getOkButton().setEnabled(canFinish());
        }
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
    public void setFocus() {

        if (leftStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftDirectoryName())) {
            leftStreamFilePrompt.getDirectoryWidget().setFocus();
            return;
        }
        if (leftStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentLeftStreamFileName())) {
            leftStreamFilePrompt.getStreamFileWidget().setFocus();
            return;
        }

        if (rightStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentRightDirectoryName())) {
            rightStreamFilePrompt.getDirectoryWidget().setFocus();
            return;
        }
        if (rightStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentRightStreamFileName())) {
            rightStreamFilePrompt.getStreamFileWidget().setFocus();
            return;
        }

        if (ancestorStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorDirectoryName())) {
            ancestorStreamFilePrompt.getDirectoryWidget().setFocus();
            return;
        }
        if (ancestorStreamFilePrompt != null && StringHelper.isNullOrEmpty(getCurrentAncestorStreamFileName())) {
            ancestorStreamFilePrompt.getStreamFileWidget().setFocus();
            return;
        }

    }

    @Override
    protected void okPressed() {

        if (hasEditableLeftStreamFile()) {

            leftConnection = getCurrentLeftConnection();
            leftDirectory = getCurrentLeftDirectoryName();
            leftStreamFile = getCurrentLeftStreamFileName();

            if (!validateStreamFile(leftConnection, leftDirectory, leftStreamFile, leftStreamFilePrompt)) {
                return;
            }

        }

        if (hasEditableRightStreamFile()) {

            rightConnection = getCurrentRightConnection();
            rightDirectory = getCurrentRightDirectoryName();
            rightStreamFile = getCurrentRightStreamFileName();

            if (hasMultipleRightStreamFiles()) {
                rightStreamFile = null;
            } else {
                rightStreamFile = getCurrentRightStreamFileName();
            }

            if (!validateStreamFile(rightConnection, rightDirectory, rightStreamFile, rightStreamFilePrompt)) {
                return;
            }

        }

        if (isThreeWay() && hasEditableAncestorStreamFile()) {

            ancestorConnection = getCurrentAncestorConnection();
            ancestorDirectory = getCurrentAncestorDirectoryName();
            ancestorStreamFile = getCurrentAncestorStreamFileName();

            if (!validateStreamFile(ancestorConnection, ancestorDirectory, ancestorStreamFile, ancestorStreamFilePrompt)) {
                return;
            }

        }

        // Close dialog
        super.okPressed();
    }

    private boolean validateStreamFile(IBMiConnection connection, String directory, String streamFile, StreamFilePrompt streamFilePrompt) {

        if (!checkDirectory(connection, directory)) {
            displayDirectoryNotFoundMessage(directory, streamFilePrompt);
            return false;
        }

        if (streamFile != null && !checkStreamFile(connection, directory, streamFile)) {
            displayStreamFileNotFoundMessage(directory, streamFile, streamFilePrompt);
            return false;
        }

        return true;
    }

    private boolean checkDirectory(IBMiConnection connection, String directory) {

        IFSRemoteFile remoteDirectory = IFSRemoteFileHelper.getRemoteDirectory(connection, directory);
        if (remoteDirectory == null) {
            return false;
        }

        return remoteDirectory.exists();
    }

    private void displayDirectoryNotFoundMessage(String directory, StreamFilePrompt streamFilePrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.Directory_not_found_A, new Object[] { directory });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        streamFilePrompt.getDirectoryWidget().setFocus();
    }

    private boolean checkStreamFile(IBMiConnection connection, String directory, String streamFile) {

        IFSRemoteFile remoteStreamFile = IFSRemoteFileHelper.getRemoteStreamFile(connection, directory, streamFile);
        if (remoteStreamFile == null) {
            return false;
        }

        return remoteStreamFile.exists();
    }

    private void displayStreamFileNotFoundMessage(String directory, String streamFile, StreamFilePrompt streamFilePrompt) {

        String message = biz.isphere.core.Messages.bind(Messages.Stream_file_B_not_found_in_directory_A, new Object[] { directory, streamFile });
        MessageDialog.openError(getShell(), biz.isphere.core.Messages.Error, message);
        streamFilePrompt.getDirectoryWidget().setFocus();
    }

    @Override
    public boolean canFinish() {

        // Check left stream file is specified
        if (StringHelper.isNullOrEmpty(getCurrentLeftConnectionName()) || StringHelper.isNullOrEmpty(getCurrentLeftDirectoryName())
            || StringHelper.isNullOrEmpty(getCurrentLeftStreamFileName())) {
            return false;
        }

        // Check right stream file is specified
        if (StringHelper.isNullOrEmpty(getCurrentRightConnectionName()) || StringHelper.isNullOrEmpty(getCurrentRightDirectoryName())
            || StringHelper.isNullOrEmpty(getCurrentRightStreamFileName())) {
            return false;
        }

        // Check ancestor stream file is specified
        if (isThreeWay()) {
            if (StringHelper.isNullOrEmpty(getCurrentAncestorConnectionName()) || StringHelper.isNullOrEmpty(getCurrentAncestorDirectoryName())
                || StringHelper.isNullOrEmpty(getCurrentAncestorStreamFileName())) {
                return false;
            }
        }

        // Ensure right and left stream files are different
        if (getCurrentRightConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
            && getCurrentRightDirectoryName().equalsIgnoreCase(getCurrentLeftDirectoryName())
            && getCurrentRightStreamFileName().equalsIgnoreCase(getCurrentLeftStreamFileName())) {
            return false;
        }

        if (isThreeWay()) {
            // Ensure ancestor stream file is different from right stream file
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentRightConnectionName())
                && getCurrentAncestorDirectoryName().equalsIgnoreCase(getCurrentRightDirectoryName())
                && getCurrentAncestorStreamFileName().equalsIgnoreCase(getCurrentRightStreamFileName())) {
                return false;
            }
            // Ensure ancestor stream file is different from right stream file
            if (getCurrentAncestorConnectionName().equalsIgnoreCase(getCurrentLeftConnectionName())
                && getCurrentAncestorDirectoryName().equalsIgnoreCase(getCurrentLeftDirectoryName())
                && getCurrentAncestorStreamFileName().equalsIgnoreCase(getCurrentLeftStreamFileName())) {
                return false;
            }
        }

        return true;
    }

    private IHost getHost(String qualifiedConnectionName) {

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName)) {
            return null;
        }

        IBMiConnection ibMiConnection = ConnectionManager.getIBMiConnection(qualifiedConnectionName);
        if (ibMiConnection == null) {
            return null;
        }

        IHost host = ibMiConnection.getHost();
        return host;
    }

    private IBMiConnection getCurrentLeftConnection() {
        if (leftConnectionCombo == null) {
            // return value for read-only left stream file
            return leftConnection;
        }
        return ConnectionManager.getIBMiConnection(getCurrentLeftConnectionName());
    }

    private String getCurrentLeftConnectionName() {
        if (leftConnectionCombo == null) {
            // return value for read-only left stream file
            IBMiConnection connection = getLeftConnection();
            String qualifiedConnectionName = ConnectionManager.getConnectionName(connection);
            return qualifiedConnectionName;
        }
        return leftConnectionCombo.getQualifiedConnectionName();
    }

    private String getCurrentLeftDirectoryName() {
        if (leftStreamFilePrompt == null) {
            // return value for read-only left stream file
            return getLeftDirectory();
        }
        return leftStreamFilePrompt.getDirectoryName();
    }

    private String getCurrentLeftStreamFileName() {
        if (leftStreamFilePrompt == null) {
            // return value for read-only left member
            return getLeftStreamFile();
        }
        return leftStreamFilePrompt.getStreamFileName();
    }

    private IBMiConnection getCurrentRightConnection() {
        return ConnectionManager.getIBMiConnection(getCurrentRightConnectionName());
    }

    private String getCurrentRightConnectionName() {
        String qualifiedConnectionName = rightConnectionCombo.getQualifiedConnectionName();
        return qualifiedConnectionName;
    }

    private String getCurrentRightDirectoryName() {
        return rightStreamFilePrompt.getDirectoryName();
    }

    private String getCurrentRightStreamFileName() {
        return rightStreamFilePrompt.getStreamFileName();
    }

    private IBMiConnection getCurrentAncestorConnection() {
        return ConnectionManager.getIBMiConnection(getCurrentAncestorConnectionName());
    }

    private String getCurrentAncestorConnectionName() {
        String qualifiedConnectionName = ancestorConnectionCombo.getQualifiedConnectionName();
        return qualifiedConnectionName;
    }

    private String getCurrentAncestorDirectoryName() {
        return ancestorStreamFilePrompt.getDirectoryName();
    }

    private String getCurrentAncestorStreamFileName() {
        return ancestorStreamFilePrompt.getStreamFileName();
    }

    public RSEStreamFile getRightRSEStreamFile() {
        return getRSEStreamFile(rightConnection, rightDirectory, rightStreamFile);
    }

    public RSEStreamFile getLeftRSEStreamFile() {
        return getRSEStreamFile(leftConnection, leftDirectory, leftStreamFile);
    }

    public RSEStreamFile getAncestorRSEStreamFile() {
        return getRSEStreamFile(ancestorConnection, ancestorDirectory, ancestorStreamFile);
    }

    private RSEStreamFile getRSEStreamFile(IBMiConnection connection, String directory, String streamFile) {
        try {
            IFSFileServiceSubSystem fileServiceSubSystem = IFSRemoteFileHelper.getIFSFileServiceSubsystem(connection);
            if (fileServiceSubSystem != null) {
                IRemoteFile remoteFile = IFSRemoteFileHelper.getRemoteStreamFile(connection, directory, streamFile);
                if (remoteFile != null) {
                    return new RSEStreamFile(remoteFile); // remoteFile.exists()
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
        initializeLeftStreamFile((RSEStreamFile)rightStreamFile);
    }

    /*
     * TODO: replace with getLeftRSEStreamFile and set to private
     */

    public IBMiConnection getLeftConnection() {
        return leftConnection;
    }

    public String getLeftDirectory() {
        if (leftDirectory == null) {
            return ""; //$NON-NLS-1$

        }
        return leftDirectory;
    }

    public String getLeftStreamFile() {
        if (leftStreamFile == null) {
            return ""; //$NON-NLS-1$
        }
        return leftStreamFile;
    }

    public IBMiConnection getRightConnection() {
        return rightConnection;
    }

    public String getRightConnectionName() {
        return rightConnection.getConnectionName();
    }

    public String getRightDirectory() {
        if (rightDirectory == null) {
            return ""; //$NON-NLS-1$
        }
        return rightDirectory;
    }

    public String getRightStreamFile() {
        if (rightStreamFile == null) {
            return ""; //$NON-NLS-1$
        }
        return rightStreamFile;
    }

    /*
     * TODO: replace with getAncestorRSEStreamFile and delete
     */

    public IBMiConnection getAncestorConnection() {
        return ancestorConnection;
    }

    public String getAncestorDirectory() {
        if (ancestorDirectory == null) {
            return ""; //$NON-NLS-1$
        }
        return ancestorDirectory;
    }

    public String getAncestorStreamFile() {
        if (ancestorStreamFile == null) {
            return ""; //$NON-NLS-1$
        }
        return ancestorStreamFile;
    }

    @Override
    protected void loadScreenValues() {
        super.loadScreenValues();

        if (hasEditableLeftStreamFile()) {
            // Load left stream file, when no file has been selected (iSphere
            // search selected from the main menu)
            if (isLoadingPreviousValuesOfLeftStreamFileEnabled()) {
                LoadPreviousStreamFileValue loadPreviousValue = LoadPreviousStreamFileValue.CONNECTION_DIRECTORY_FILE;
                loadStreamFileValues(PREFIX_LEFT, loadPreviousValue, leftConnectionCombo, leftStreamFilePrompt);
            }
        }

        if (hasEditableRightStreamFile()) {

            boolean hasLoaded = false;

            if (isLoadingPreviousValuesOfRightStreamFileEnabled()) {
                // Load previous member values
                LoadPreviousStreamFileValue loadPreviousValue = getLoadPreviousValuesOfRightStreamFile();
                hasLoaded = loadStreamFileValues(PREFIX_RIGHT, loadPreviousValue, rightConnectionCombo, rightStreamFilePrompt);
            }

            if (!hasLoaded) {
                // Initialize right stream file with left file prompt
                setStreamFileValues(rightConnectionCombo, rightStreamFilePrompt, getCurrentLeftConnectionName(), getCurrentLeftDirectoryName(),
                    getCurrentLeftStreamFileName());
            }

            if (hasMultipleRightStreamFiles()) {
                // Overwrite right stream file name to: *LEFT
                rightStreamFilePrompt.getStreamFileWidget().setText(SPECIAL_STREAM_FILE_NAME_LEFT);
            }
        }

        if (hasEditableAncestorStreamFile()) {

            boolean hasLoaded = false;

            // if (isLoadingPreviousValuesOfAncestorMemberEnabled()) {
            // LoadPreviousValues loadPreviousValue =
            // getLoadPreviousValuesOfAncestorMember();
            // hasLoaded = loadMemberValues(PREFIX_ANCESTOR, loadPreviousValue,
            // ancestorConnectionCombo, ancestorMemberPrompt);
            // }

            if (!hasLoaded) {
                // Initialize ancestor member with left member prompt
                setStreamFileValues(ancestorConnectionCombo, ancestorStreamFilePrompt, getCurrentLeftConnectionName(), getCurrentLeftDirectoryName(),
                    getCurrentLeftStreamFileName());
            }
        }
    }

    private boolean loadStreamFileValues(String prefix, LoadPreviousStreamFileValue loadPreviousValue, ConnectionCombo connectionCombo,
        StreamFilePrompt streamFilePrompt) {

        String connection;
        if (loadPreviousValue.isConnection()) {
            connection = loadValue(getStreamFilePromptDialogSettingsKey(prefix, CONNECTION), getCurrentLeftConnectionName());
        } else {
            connection = getCurrentLeftConnectionName();
        }

        String directory;
        if (loadPreviousValue.isDirectory()) {
            directory = loadValue(getStreamFilePromptDialogSettingsKey(prefix, DIRECTORY), getCurrentLeftDirectoryName());
        } else {
            directory = getCurrentLeftDirectoryName();
        }

        String streamFile;
        if (loadPreviousValue.isFile()) {
            streamFile = loadValue(getStreamFilePromptDialogSettingsKey(prefix, STREAM_FILE), getCurrentLeftStreamFileName());
        } else {
            streamFile = getCurrentLeftStreamFileName();
        }

        return setStreamFileValues(connectionCombo, streamFilePrompt, connection, directory, streamFile);
    }

    private boolean setStreamFileValues(ConnectionCombo connectionCombo, StreamFilePrompt memberPrompt, String qualifiedConnectionName,
        String directory, String streamFile) {

        memberPrompt.setConnection(null);
        memberPrompt.setDirectoryName(""); //$NON-NLS-1$
        memberPrompt.setStreamFileName(""); //$NON-NLS-1$

        if (haveStreamFileValues(qualifiedConnectionName, directory, streamFile)) {
            connectionCombo.setQualifiedConnectionName(qualifiedConnectionName);
            memberPrompt.setDirectoryName(directory);
            memberPrompt.setStreamFileName(streamFile);
            memberPrompt.setConnection(getHost(qualifiedConnectionName));
            return true;
        }

        return false;
    }

    @Override
    protected void storeScreenValues() {
        super.storeScreenValues();

        if (hasEditableLeftStreamFile()) {
            if (isLoadingPreviousValuesOfLeftStreamFileEnabled()) {
                storeStreamFileValues(PREFIX_LEFT, leftConnectionCombo, leftStreamFilePrompt);
            }
            storeHistory(leftStreamFilePrompt);
        }

        if (hasEditableRightStreamFile()) {
            if (hasMultipleRightStreamFiles()) {
                storeStreamFileValues(PREFIX_RIGHT, rightConnectionCombo, rightStreamFilePrompt);
            } else if (isLoadingPreviousValuesOfRightStreamFileEnabled()) {
                storeStreamFileValues(PREFIX_RIGHT, rightConnectionCombo, rightStreamFilePrompt);
            }
            storeHistory(rightStreamFilePrompt);
        }

        if (hasEditableAncestorStreamFile()) {
            if (isLoadingPreviousValuesOfAncestorStreamFileEnabled()) {
                storeStreamFileValues(PREFIX_ANCESTOR, ancestorConnectionCombo, ancestorStreamFilePrompt);
            }
            storeHistory(ancestorStreamFilePrompt);
        }
    }

    private void storeStreamFileValues(String prefix, ConnectionCombo connectionCombo, StreamFilePrompt streamFilePrompt) {

        String qualifiedConnectionName = connectionCombo.getQualifiedConnectionName();
        String directory = streamFilePrompt.getDirectoryName();
        String streamFile = streamFilePrompt.getStreamFileName();

        if (haveStreamFileValues(qualifiedConnectionName, directory, streamFile)) {
            storeValue(getStreamFilePromptDialogSettingsKey(prefix, CONNECTION), qualifiedConnectionName);
            storeValue(getStreamFilePromptDialogSettingsKey(prefix, DIRECTORY), directory);
            storeValue(getStreamFilePromptDialogSettingsKey(prefix, STREAM_FILE), streamFile);
        }
    }

    private void storeHistory(StreamFilePrompt streamFilePrompt) {

        if (!canStoreHistory()) {
            return;
        }

        if (isSpecialStreamFileName(streamFilePrompt.getStreamFileName())) {
            return;
        }

        streamFilePrompt.updateHistory();
        streamFilePrompt.storeHistory();
    }

    private boolean haveStreamFileValues(String qualifiedConnectionName, String directory, String streamFile) {

        if (!StringHelper.isNullOrEmpty(qualifiedConnectionName) && !StringHelper.isNullOrEmpty(directory)
            && !StringHelper.isNullOrEmpty(streamFile)) {
            try {
                if (ConnectionManager.getIBMiConnection(qualifiedConnectionName) != null) {
                    return true;
                }
            } catch (Exception e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }

        return false;
    }

    private boolean hasEditableLeftStreamFile() {
        return leftStreamFilePrompt != null;
    }

    private boolean hasEditableRightStreamFile() {
        return rightStreamFilePrompt != null;
    }

    private boolean hasEditableAncestorStreamFile() {
        return ancestorStreamFilePrompt != null;
    }
}
