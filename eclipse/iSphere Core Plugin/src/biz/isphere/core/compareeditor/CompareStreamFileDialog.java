/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.point.BasicQualifiedConnectionName;
import biz.isphere.core.internal.StreamFile;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class CompareStreamFileDialog extends XDialog {

    private static final String BASE_SETTINGS_KEY = "biz.isphere.rse.comparestreamfileeditor.ui."; //$NON-NLS-1$

    protected static final String PREFIX_LEFT = "LEFT";
    protected static final String PREFIX_RIGHT = "RIGHT";
    protected static final String PREFIX_ANCESTOR = "ANCESTOR";
    protected static final String CONNECTION = "CONNECTION";
    protected static final String DIRECTORY = "DIRECTORY";
    protected static final String STREAM_FILE = "STREAM_FILE";

    protected static final String SPECIAL_STREAM_FILE_NAME_LEFT = "*LEFT"; //$NON-NLS-1$
    protected static final String SPECIAL_STREAM_FILE_NAME_SELECTED = "*SELECTED"; //$NON-NLS-1$

    private static final String EDITABLE_PROPERTY = "EDITABLE_PROPERTY"; //$NON-NLS-1$
    private static final String IGNORE_CASE_PROPERTY = "IGNORE_CASE_PROPERTY"; //$NON-NLS-1$

    private boolean selectEditable;
    private StreamFile leftStreamFile;
    private StreamFile rightStreamFile;
    private StreamFile ancestorStreamFile;
    private Button editButton;
    private Button browseButton;
    private Button dontIgnoreCaseButton;
    private Button ignoreCaseButton;
    private Button twoWayButton;
    private Button threeWayButton;
    private boolean editable;
    private boolean ignoreCase;
    private boolean threeWay;
    private Button okButton;
    private boolean hasLeftStreamFile;
    private boolean hasRightStreamFile;
    private boolean hasMultipleRightStreamFiles;
    private boolean hasAncestorStreamFile;
    private boolean switchStreamFileAllowed = true;
    private Image switchImage;
    private Group leftGroup;
    private Text leftConnectionText;
    private Text leftDirectoryText;
    private Text leftStreamFileText;
    private Text rightConnectionText;
    private Text rightDirectoryText;
    private Text rightStreamFileText;

    /*
     * Controls whether or not to store/load stream file values. For now stream
     * files are stored and loaded when the editor has been opened from the
     * iSphere main menu, when no initial stream files are selected.
     */
    private String historyValuesCategoryKey;

    public CompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftStreamFile, null, null);
        hasMultipleRightStreamFiles = false;
    }

    public CompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile, StreamFile rightStreamFile) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftStreamFile, rightStreamFile, null);
        hasMultipleRightStreamFiles = false;
    }

    public CompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile[] rightStreamFiles) {
        super(parentShell);
        initialize(parentShell, selectEditable, rightStreamFiles[0], rightStreamFiles[0], null);
        hasMultipleRightStreamFiles = true;
    }

    public CompareStreamFileDialog(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile, StreamFile rightStreamFile,
        StreamFile ancestorStreamFile) {
        super(parentShell);
        initialize(parentShell, selectEditable, leftStreamFile, rightStreamFile, ancestorStreamFile);
        hasMultipleRightStreamFiles = false;
    }

    protected void setHistoryValuesCategoryKey(String historyValuesCategoryKey) {
        this.historyValuesCategoryKey = historyValuesCategoryKey;
    }

    private void initialize(Shell parentShell, boolean selectEditable, StreamFile leftStreamFile, StreamFile rightStreamFile,
        StreamFile ancestorStreamFile) {
        this.selectEditable = selectEditable;
        this.leftStreamFile = leftStreamFile;
        this.rightStreamFile = rightStreamFile;
        this.ancestorStreamFile = ancestorStreamFile;

        if (this.leftStreamFile == null) {
            hasLeftStreamFile = false;
        } else {
            hasLeftStreamFile = true;
        }

        if (this.rightStreamFile == null) {
            hasRightStreamFile = false;
        } else {
            hasRightStreamFile = true;
        }

        if (this.ancestorStreamFile == null) {
            hasAncestorStreamFile = false;
            threeWay = false;
        } else {
            hasAncestorStreamFile = true;
            threeWay = true;
        }
    }

    protected boolean isLoadingPreviousValuesOfLeftStreamFileEnabled() {

        if (isLoadedFromMainMenu()) {
            return true;
        }

        return false;
    }

    protected boolean isLoadingPreviousValuesOfRightStreamFileEnabled() {

        if (isLoadedFromMainMenu()) {
            return true;
        }

        boolean isLoadingPreviousValuesEnabled = Preferences.getInstance().isSourceStreamFileCompareLoadingPreviousValuesOfRightStreamFileEnabled();

        return isLoadingPreviousValuesEnabled;
    }

    public LoadPreviousStreamFileValue getLoadPreviousValuesOfRightStreamFile() {
        return Preferences.getInstance().getSourceStreamFileCompareLoadingPreviousValuesOfRightStreamFile();
    }

    protected boolean isLoadingPreviousValuesOfAncestorStreamFileEnabled() {

        if (isLoadedFromMainMenu()) {
            return true;
        }

        boolean isLoadingPreviousValuesEnabled = Preferences.getInstance()
            .isSourceStreamFileCompareLoadingPreviousValuesOfAncestorStreamFileEnabled();

        return isLoadingPreviousValuesEnabled;
    }

    public LoadPreviousStreamFileValue getLoadPreviousValuesOfAncestorStreamFile() {
        return Preferences.getInstance().getSourceStreamFileCompareLoadingPreviousValuesOfAncestorStreamFile();
    }

    private boolean isLoadedFromMainMenu() {

        /*
         * Load previous values when called from the main menu.
         */
        if (!hasLeftStreamFile() && !hasRightStreamFile() && !hasAncestorStreamFile()) {
            return true;
        }

        return false;
    }

    protected boolean canStoreHistory() {

        if (!StringHelper.isNullOrEmpty(historyValuesCategoryKey)) {
            return true;
        }

        return false;
    }

    protected boolean isSpecialStreamFileName(String streamFileName) {

        if (SPECIAL_STREAM_FILE_NAME_LEFT.equals(streamFileName) || SPECIAL_STREAM_FILE_NAME_SELECTED.equals(streamFileName)) {
            return true;
        }

        return false;
    }

    protected String getStreamFilePromptDialogSettingsKey(String streamFilePromptType, String objectType) {

        if (!canStoreHistory()) {
            return null;
        }

        return historyValuesCategoryKey + "_" + streamFilePromptType + "_" + objectType;
    }

    protected String getStreamFilePromptHistoryKey(String streamFilePromptType, String objectType) {

        if (!canStoreHistory()) {
            return null;
        }

        return BASE_SETTINGS_KEY + historyValuesCategoryKey + "." + streamFilePromptType + "." + objectType;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);

        loadScreenValues();
        setFocus();

        return control;
    }

    @Override
    public Control createDialogArea(Composite parent) {
        Composite rtnGroup = (Composite)super.createDialogArea(parent);
        parent.getShell().setText(Messages.Compare_Stream_Files);

        GridLayout rtnLayout = new GridLayout();
        rtnLayout.numColumns = 1;
        rtnGroup.setLayout(rtnLayout);
        rtnGroup.setLayoutData(getGridData());

        Group modeGroup = new Group(rtnGroup, SWT.NONE);
        modeGroup.setText(Messages.Mode);
        GridLayout modeLayout = new GridLayout();
        modeLayout.numColumns = 1;
        modeGroup.setLayout(modeLayout);
        modeGroup.setLayoutData(getGridData());

        /*
         * ---------------- Create options area ----------------
         */
        if (selectEditable) {

            Composite editableGroup = new Composite(modeGroup, SWT.NONE);
            GridLayout editableLayout = new GridLayout(2, true);
            editableGroup.setLayout(editableLayout);
            editableGroup.setLayoutData(getGridData());

            browseButton = WidgetFactory.createRadioButton(editableGroup);
            browseButton.setText(Messages.Open_for_browse);
            browseButton.setLayoutData(getGridData());

            editButton = WidgetFactory.createRadioButton(editableGroup);
            editButton.setText(Messages.Open_for_edit);
            editButton.setLayoutData(getGridData());
            editButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    if (editButton.getSelection()) {
                        dontIgnoreCaseButton.setSelection(true);
                        ignoreCaseButton.setSelection(false);
                    }
                    setLeftGroupLabel(getLeftGroupLabel());
                }
            });

        }

        Composite ignoreCaseGroup = new Composite(modeGroup, SWT.NONE);
        GridLayout ignoreCaseLayout = new GridLayout(2, true);
        ignoreCaseGroup.setLayout(ignoreCaseLayout);
        ignoreCaseGroup.setLayoutData(getGridData());

        dontIgnoreCaseButton = WidgetFactory.createRadioButton(ignoreCaseGroup);
        dontIgnoreCaseButton.setText(Messages.Don_t_ignore_case);
        dontIgnoreCaseButton.setLayoutData(getGridData());

        ignoreCaseButton = WidgetFactory.createRadioButton(ignoreCaseGroup);
        ignoreCaseButton.setText(Messages.Ignore_case);
        ignoreCaseButton.setLayoutData(getGridData());
        ignoreCaseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (selectEditable) {
                    if (ignoreCaseButton.getSelection()) {
                        browseButton.setSelection(true);
                        editButton.setSelection(false);
                        setLeftGroupLabel(getLeftGroupLabel());
                    }
                }
            }
        });

        if (!hasRightStreamFile()) {

            Composite threeWayGroup = new Composite(modeGroup, SWT.NONE);
            GridLayout threeWayLayout = new GridLayout(2, true);
            threeWayGroup.setLayout(threeWayLayout);
            threeWayGroup.setLayoutData(getGridData());

            twoWayButton = WidgetFactory.createRadioButton(threeWayGroup);
            twoWayButton.setText(Messages.Two_way_compare);
            twoWayButton.setLayoutData(getGridData());
            if (!threeWay) {
                twoWayButton.setSelection(true);
            }
            twoWayButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    threeWay = false;
                    setAncestorVisible(false);
                    okButton.setEnabled(canFinish());
                }
            });

            threeWayButton = WidgetFactory.createRadioButton(threeWayGroup);
            threeWayButton.setText(Messages.Three_way_compare);
            threeWayButton.setLayoutData(getGridData());
            if (threeWay) {
                threeWayButton.setSelection(true);
            }
            threeWayButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    threeWay = true;
                    setAncestorVisible(true);
                    okButton.setEnabled(canFinish());
                }
            });

        }

        /*
         * ---------------- Create left area ----------------
         */
        if (hasLeftStreamFile()) {
            createReadOnlyLeftArea(rtnGroup);
        } else {
            createEditableLeftArea(rtnGroup);
        }

        /*
         * -------- Create right and ancestor areas --------
         */
        if (hasAncestorStreamFile()) {
            createReadOnlyRightArea(rtnGroup);
            createReadOnlyAncestorArea(rtnGroup);
        } else if (hasRightStreamFile()) {
            if (!hasMultipleRightStreamFiles()) {
                if (switchStreamFileAllowed) {
                    createSwitchStreamFileButton(rtnGroup);
                }
                createReadOnlyRightArea(rtnGroup);
            } else {
                createEditableRightArea(rtnGroup);
            }
        } else {
            createEditableRightArea(rtnGroup);
            createEditableAncestorArea(rtnGroup);
            if (isThreeWay()) {
                setAncestorVisible(true);
            } else {
                setAncestorVisible(false);
            }
        }

        return rtnGroup;
    }

    protected GridData getGridData() {
        return new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
    }

    private Image getSwitchImage() {
        if (switchImage == null) {
            switchImage = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SWITCH_MEMBER);
        }
        return switchImage;
    }

    protected void switchLeftAndRightStreamFile(StreamFile leftStreamFile, StreamFile rightStreamFile) {
        StreamFile tempStreamFile = leftStreamFile;
        this.leftStreamFile = rightStreamFile;

        leftConnectionText.setText(getUIConnectionName(this.leftStreamFile));
        leftDirectoryText.setText(this.leftStreamFile.getDirectory());
        leftStreamFileText.setText(this.leftStreamFile.getStreamFile());

        this.rightStreamFile = tempStreamFile;

        rightConnectionText.setText(getUIConnectionName(this.rightStreamFile));
        rightDirectoryText.setText(this.rightStreamFile.getDirectory());
        rightStreamFileText.setText(this.rightStreamFile.getStreamFile());
    }

    @Override
    public Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
        if (id == OK) {
            okButton = button;
            if (hasRightStreamFile && !hasMultipleRightStreamFiles) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        }
        return button;
    }

    @Override
    protected void okPressed() {
        if (selectEditable) {
            editable = editButton.getSelection();
        } else {
            editable = false;
        }

        ignoreCase = ignoreCaseButton.getSelection();

        if (!hasRightStreamFile()) {
            threeWay = threeWayButton.getSelection();
        }

        storeScreenValues();

        // Close screen
        super.okPressed();
    }

    public Button getOkButton() {
        return okButton;
    }

    protected void createEditableLeftArea(Composite parent) {
    }

    protected void createEditableRightArea(Composite parent) {
    }

    protected void createEditableAncestorArea(Composite parent) {
    }

    protected void setAncestorVisible(boolean visible) {
    }

    protected String getLeftGroupLabel() {

        if (editButton.getSelection()) {
            return Messages.Left + " " + Messages.Editable;
        } else {
            return Messages.Left;
        }
    }

    protected void setLeftGroupLabel(String label) {
        leftGroup.setText(label);
    }

    private void createReadOnlyLeftArea(Composite parent) {

        leftGroup = new Group(parent, SWT.NONE);
        leftGroup.setText(getLeftGroupLabel());
        GridLayout leftLayout = new GridLayout(2, false);
        leftGroup.setLayout(leftLayout);
        leftGroup.setLayoutData(getGridData());

        Label leftConnectionLabel = new Label(leftGroup, SWT.NONE);
        leftConnectionLabel.setText(Messages.Connection_colon);

        leftConnectionText = WidgetFactory.createReadOnlyText(leftGroup);
        leftConnectionText.setLayoutData(getGridData());
        leftConnectionText.setText(getUIConnectionName(leftStreamFile));

        Label leftDirectoryLabel = new Label(leftGroup, SWT.NONE);
        leftDirectoryLabel.setText(Messages.Directory_colon);
        leftDirectoryText = WidgetFactory.createReadOnlyText(leftGroup);
        leftDirectoryText.setLayoutData(getGridData());
        if (leftStreamFile.isArchive()) {
            leftDirectoryText.setText(leftStreamFile.getArchiveDirectory());
        } else {
            leftDirectoryText.setText(leftStreamFile.getDirectory());
        }

        Label leftStreamFileLabel = new Label(leftGroup, SWT.NONE);
        leftStreamFileLabel.setText(Messages.Stream_file_colon);
        leftStreamFileText = WidgetFactory.createReadOnlyText(leftGroup);
        leftStreamFileText.setLayoutData(getGridData());

        if (hasMultipleRightStreamFiles()) {
            leftStreamFileText.setText(SPECIAL_STREAM_FILE_NAME_SELECTED);
        } else {
            if (leftStreamFile.isArchive()) {
                leftStreamFileText.setText(leftStreamFile.getArchiveStreamFile());
            } else {
                leftStreamFileText.setText(leftStreamFile.getStreamFile());
            }
        }

        if (leftStreamFile.isArchive()) {
            Label leftTimeLabel = new Label(leftGroup, SWT.NONE);
            leftTimeLabel.setText(Messages.Archive_colon);
            Text leftTimeText = WidgetFactory.createReadOnlyText(leftGroup);
            leftTimeText.setLayoutData(getGridData());
            leftTimeText.setText(leftStreamFile.getArchiveDate() + " - " + leftStreamFile.getArchiveTime()); //$NON-NLS-1$
        }
    }

    private void createReadOnlyRightArea(Composite parent) {

        Group rightGroup = new Group(parent, SWT.NONE);
        rightGroup.setText(Messages.Right);
        GridLayout rightLayout = new GridLayout(2, false);
        rightGroup.setLayout(rightLayout);
        rightGroup.setLayoutData(getGridData());

        Label rightConnectionLabel = new Label(rightGroup, SWT.NONE);
        rightConnectionLabel.setText(Messages.Connection_colon);

        rightConnectionText = WidgetFactory.createReadOnlyText(rightGroup);
        rightConnectionText.setLayoutData(getGridData());
        rightConnectionText.setText(getUIConnectionName(rightStreamFile));

        Label rightDirectoryLabel = new Label(rightGroup, SWT.NONE);
        rightDirectoryLabel.setText(Messages.Directory_colon);
        rightDirectoryText = WidgetFactory.createReadOnlyText(rightGroup);
        rightDirectoryText.setLayoutData(getGridData());
        if (rightStreamFile.isArchive()) {
            rightDirectoryText.setText(rightStreamFile.getArchiveDirectory());
        } else {
            rightDirectoryText.setText(rightStreamFile.getDirectory());
        }

        Label rightStreamFileLabel = new Label(rightGroup, SWT.NONE);
        rightStreamFileLabel.setText(Messages.Stream_file_colon);
        rightStreamFileText = WidgetFactory.createReadOnlyText(rightGroup);
        rightStreamFileText.setLayoutData(getGridData());

        if (hasMultipleRightStreamFiles()) {
            rightStreamFileText.setText(SPECIAL_STREAM_FILE_NAME_SELECTED);
        } else {
            if (rightStreamFile.isArchive()) {
                rightStreamFileText.setText(rightStreamFile.getArchiveStreamFile());
            } else {
                rightStreamFileText.setText(rightStreamFile.getStreamFile());
            }
        }

        if (rightStreamFile.isArchive()) {
            Label rightTimeLabel = new Label(rightGroup, SWT.NONE);
            rightTimeLabel.setText(Messages.Archive_colon);
            Text rightTimeText = WidgetFactory.createReadOnlyText(rightGroup);
            rightTimeText.setLayoutData(getGridData());
            rightTimeText.setText(rightStreamFile.getArchiveDate() + " - " + rightStreamFile.getArchiveTime()); //$NON-NLS-1$
        }
    }

    private void createReadOnlyAncestorArea(Composite parent) {

        Group ancestorGroup = new Group(parent, SWT.NONE);
        ancestorGroup.setText(Messages.Ancestor);
        GridLayout ancestorLayout = new GridLayout(2, false);
        ancestorGroup.setLayout(ancestorLayout);
        ancestorGroup.setLayoutData(getGridData());

        Label ancestorConnectionLabel = new Label(ancestorGroup, SWT.NONE);
        ancestorConnectionLabel.setText(Messages.Connection_colon);

        Text ancestorConnectionText = WidgetFactory.createReadOnlyText(ancestorGroup);
        ancestorConnectionText.setLayoutData(getGridData());
        ancestorConnectionText.setText(getUIConnectionName(ancestorStreamFile));

        Label ancestorDirectoryLabel = new Label(ancestorGroup, SWT.NONE);
        ancestorDirectoryLabel.setText(Messages.Directory_colon);
        Text ancestorDirectoryText = WidgetFactory.createReadOnlyText(ancestorGroup);
        ancestorDirectoryText.setLayoutData(getGridData());
        if (ancestorStreamFile.isArchive()) {
            ancestorDirectoryText.setText(ancestorStreamFile.getArchiveDirectory());
        } else {
            ancestorDirectoryText.setText(ancestorStreamFile.getDirectory());
        }

        Label ancestorStreamFileLabel = new Label(ancestorGroup, SWT.NONE);
        ancestorStreamFileLabel.setText(Messages.Stream_file_colon);
        Text ancestorStreamFileText = WidgetFactory.createReadOnlyText(ancestorGroup);
        ancestorStreamFileText.setLayoutData(getGridData());

        if (ancestorStreamFile.isArchive()) {
            ancestorStreamFileText.setText(ancestorStreamFile.getArchiveStreamFile());
        } else {
            ancestorStreamFileText.setText(ancestorStreamFile.getStreamFile());
        }

        if (ancestorStreamFile.isArchive()) {
            Label ancestorTimeLabel = new Label(ancestorGroup, SWT.NONE);
            ancestorTimeLabel.setText(Messages.Archive_colon);
            Text ancestorTimeText = WidgetFactory.createReadOnlyText(ancestorGroup);
            ancestorTimeText.setLayoutData(getGridData());
            ancestorTimeText.setText(ancestorStreamFile.getArchiveDate() + " - " + ancestorStreamFile.getArchiveTime()); //$NON-NLS-1$
        }
    }

    private String getUIConnectionName(StreamFile streamFile) {
        return new BasicQualifiedConnectionName(streamFile.getConnection()).getUIConnectionName();
    }

    private void createSwitchStreamFileButton(Composite parent) {

        Composite switchPanel = new Composite(parent, SWT.NONE);
        GridLayout middleLayout = new GridLayout();
        middleLayout.numColumns = 1;
        switchPanel.setLayout(middleLayout);
        switchPanel.setLayoutData(getGridData());

        Button switchStreamFileButton = WidgetFactory.createPushButton(switchPanel);
        switchStreamFileButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
        switchStreamFileButton.setImage(getSwitchImage());
        switchStreamFileButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event arg0) {
                switchLeftAndRightStreamFile(leftStreamFile, rightStreamFile);
            }
        });
    }

    protected boolean canFinish() {
        return true;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isThreeWay() {
        return threeWay;
    }

    public boolean hasLeftStreamFile() {
        return hasLeftStreamFile;
    }

    public boolean hasRightStreamFile() {
        return hasRightStreamFile;
    }

    public boolean hasMultipleRightStreamFiles() {
        return hasMultipleRightStreamFiles;
    }

    protected boolean hasAncestorStreamFile() {
        return hasAncestorStreamFile;
    }

    protected void loadScreenValues() {

        if (selectEditable) {
            editable = getDialogBoundsSettings().getBoolean(EDITABLE_PROPERTY);
        } else {
            editable = false;
        }

        ignoreCase = getDialogBoundsSettings().getBoolean(IGNORE_CASE_PROPERTY);

        if (selectEditable) {
            if (!isEditable() || isIgnoreCase()) {
                browseButton.setSelection(true);
            } else {
                browseButton.setSelection(false);
            }

            if (isEditable() && !isIgnoreCase()) {
                editButton.setSelection(true);
            } else {
                editButton.setSelection(false);
            }
            setLeftGroupLabel(getLeftGroupLabel());
        }

        if (!isIgnoreCase()) {
            dontIgnoreCaseButton.setSelection(true);
            ignoreCaseButton.setSelection(false);
        } else {
            dontIgnoreCaseButton.setSelection(false);
            ignoreCaseButton.setSelection(true);
        }

    }

    protected void storeScreenValues() {
        if (selectEditable) {
            getDialogBoundsSettings().put(EDITABLE_PROPERTY, editable);
        }
        getDialogBoundsSettings().put(IGNORE_CASE_PROPERTY, ignoreCase);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    protected void setSwitchStreamFileAllowed(boolean switchStreamFileAllowed) {
        this.switchStreamFileAllowed = switchStreamFileAllowed;
    }

}
