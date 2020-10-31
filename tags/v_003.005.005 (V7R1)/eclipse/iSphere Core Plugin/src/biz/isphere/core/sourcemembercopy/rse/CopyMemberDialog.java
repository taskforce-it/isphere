/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy.rse;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.CopyMemberItemTableCellModifier;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.tableviewer.TableViewerKeyBoardSupporter;
import biz.isphere.core.swt.widgets.tableviewer.TooltipProvider;

public class CopyMemberDialog extends XDialog {

    private static final String SINGLE_QUOTE = "'";

    private CopyMemberService jobDescription;

    private Combo comboToConnection;
    private Text textToFile;
    private Text textToLibrary;
    private TableViewer tableViewer;
    private Button chkBoxReplace;
    private Button chkBoxIgnoreDataLostError;
    private Label labelNumElem;

    private boolean isValidated;
    private boolean isExecuted;

    private static final int COLUMN_FROM_LIBRARY = 0;
    private static final int COLUMN_FROM_FILE = 1;
    private static final int COLUMN_FROM_MEMBER = 2;
    private static final int COLUMN_TO_MEMBER = 3;
    private static final int COLUMN_ERROR_MESSAGE = 4;

    private static final String TABLE_COLUMN = "TABLE_COLUMN_";

    private Composite mainArea;

    public CopyMemberDialog(Shell parentShell) {
        super(parentShell);
    }

    public void setContent(CopyMemberService jobDescription) {

        this.jobDescription = jobDescription;

        setControlEnablement();
    }

    @Override
    protected void okPressed() {

        storeScreenValues();

        setErrorMessage(null);
        mainArea.update();

        try {

            setStatusMessage(Messages.Validating_dots);
            if (!validateUserInput()) {
                return;
            }

            setStatusMessage(Messages.Copying_dots);
            if (!executeCopyOperations()) {
                return;
            }

        } finally {
            setStatusMessage(Messages.EMPTY);
        }

        // super.okPressed();
    }

    @Override
    protected void cancelPressed() {

        storeScreenValues();

        super.cancelPressed();
    }

    private boolean validateUserInput() {

        Runnable validator = new Runnable() {

            public void run() {

                // TODO: fix library name validator (pass CCSID) - DONE
                Validator nameValidator = Validator.getNameInstance(jobDescription.getToConnectionCcsid());

                String fileName = getFileName();
                if (!nameValidator.validate(fileName)) {
                    setErrorMessage(Messages.bind(Messages.Invalid_file_name, fileName));
                    textToFile.setFocus();
                    isValidated = false;
                    return;
                }

                String libraryName = getLibraryName();
                if (!nameValidator.validate(libraryName)) {
                    setErrorMessage(Messages.bind(Messages.Invalid_library_name, libraryName));
                    textToLibrary.setFocus();
                    isValidated = false;
                    return;
                }

                String connectionName = getToConnectionName();
                if (!IBMiHostContributionsHandler.checkLibrary(connectionName, libraryName)) {
                    setErrorMessage(Messages.bind(Messages.Library_A_not_found, libraryName));
                    textToLibrary.setFocus();
                    isValidated = false;
                    return;
                }

                if (!IBMiHostContributionsHandler.checkFile(connectionName, libraryName, fileName)) {
                    setErrorMessage(Messages.bind(Messages.File_A_not_found, fileName));
                    textToFile.setFocus();
                    isValidated = false;
                    return;
                }

                jobDescription.setToConnection(connectionName);
                jobDescription.setToLibrary(libraryName);
                jobDescription.setToFile(fileName);

                if (!jobDescription.validate(chkBoxReplace.getSelection(), chkBoxIgnoreDataLostError.getSelection())) {
                    setErrorMessage(Messages.Validation_ended_with_errors_Request_canceled);
                    isValidated = false;
                    return;
                }

                isValidated = true;
            }
        };

        BusyIndicator.showWhile(getShell().getDisplay(), validator);

        return isValidated;
    }

    private boolean executeCopyOperations() {

        Runnable executor = new Runnable() {

            public void run() {

                if (!jobDescription.execute()) {
                    setErrorMessage(Messages.Failed_to_copy_one_or_more_items);
                    isExecuted = false;
                    return;
                }

                isExecuted = true;
            }
        };

        BusyIndicator.showWhile(getShell().getDisplay(), executor);

        return isExecuted;
    }

    @Override
    public boolean close() {

        return super.close();
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Copy_Members_headline);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(3, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label labelToConnection = new Label(mainArea, SWT.NONE);
        labelToConnection.setText("To connection:");

        String[] connections = IBMiHostContributionsHandler.getConnectionNames();
        if (connections != null) {
            comboToConnection = WidgetFactory.createReadOnlyCombo(mainArea);
            comboToConnection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            comboToConnection.setItems(connections);
        } else {
            comboToConnection = null;
        }

        Label textInfo = new Label(mainArea, SWT.NONE);
        textInfo.setAlignment(SWT.RIGHT);
        textInfo.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 1, 3));
        textInfo.setText(Messages.bind(Messages.CopyMemberDialog_Info, SINGLE_QUOTE + Messages.To_member_colhdg + SINGLE_QUOTE));

        textToFile = createNameField(mainArea, Messages.To_file_colon);
        textToLibrary = createNameField(mainArea, Messages.To_library_colon);

        tableViewer = new TableViewer(mainArea, SWT.FULL_SELECTION | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        addTableColumn(tableViewer, Messages.Library);
        addTableColumn(tableViewer, Messages.File);
        addTableColumn(tableViewer, Messages.Member);
        addTableColumn(tableViewer, Messages.To_member_colhdg);
        addTableColumn(tableViewer, Messages.Result, 400);

        tableViewer.setCellModifier(new CopyMemberItemTableCellModifier(tableViewer));
        tableViewer.setContentProvider(new ContentProviderMemberItems());
        tableViewer.setLabelProvider(new LabelProviderMemberItems());

        TableViewerKeyBoardSupporter supporter = new TableViewerKeyBoardSupporter(tableViewer, true);
        supporter.startSupport();

        labelNumElem = new Label(mainArea, SWT.NONE);
        labelNumElem.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, ((GridLayout)mainArea.getLayout()).numColumns, 1));
        int numItems;
        if (jobDescription != null) {
            numItems = jobDescription.getItems().length;
        } else {
            numItems = 0;
        }
        labelNumElem.setText(Messages.Items_colon + " " + numItems); //$NON-NLS-1$

        new Label(mainArea, SWT.NONE).setVisible(false);

        chkBoxReplace = WidgetFactory.createCheckbox(mainArea);
        chkBoxReplace.setText(Messages.Replace_existing_members);
        chkBoxReplace.setLayoutData(new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false, 3, 1));

        chkBoxIgnoreDataLostError = WidgetFactory.createCheckbox(mainArea);
        chkBoxIgnoreDataLostError.setText(Messages.Ignore_data_lost_error);
        chkBoxIgnoreDataLostError.setLayoutData(new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false, 3, 1));

        createStatusLine(mainArea);

        loadScreenValues();

        setControlEnablement();

        return mainArea;
    }

    @Override
    protected Control createContents(Composite parent) {

        Control control = super.createContents(parent);

        setControlEnablement();

        return control;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button btnReset = createButton(parent, IDialogConstants.RETRY_ID, Messages.Reset, false);
        btnReset.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                jobDescription.reset();
                setControlEnablement();
                setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(Messages.Copy);
    }

    @Override
    public void setFocus() {

        if (StringHelper.isNullOrEmpty(comboToConnection.getText())) {
            comboToConnection.setFocus();
        } else if (StringHelper.isNullOrEmpty(textToFile.getText())) {
            textToFile.setFocus();
        } else if (StringHelper.isNullOrEmpty(textToLibrary.getText())) {
            textToLibrary.setFocus();
        } else {
            comboToConnection.setFocus();
        }
    }

    /**
     * Restores the screen values of the last copy operation.
     */
    private void loadScreenValues() {

        if (comboToConnection != null) {
            comboToConnection.setText(jobDescription.getToConnectionName());
        }

        if (jobDescription.getFromLibraryNamesCount() == 1) {
            textToLibrary.setText(jobDescription.getFromLibraryNames()[0]);
        } else {
            textToLibrary.setText(Messages.EMPTY);
        }

        if (jobDescription.getFromFileNamesCount() == 1) {
            textToFile.setText(jobDescription.getFromFileNames()[0]);
        } else {
            textToFile.setText(Messages.EMPTY);
        }

        tableViewer.setInput(jobDescription);

        loadColumnWidth(COLUMN_FROM_LIBRARY, 120);
        loadColumnWidth(COLUMN_FROM_FILE, 120);
        loadColumnWidth(COLUMN_FROM_MEMBER, 120);
        loadColumnWidth(COLUMN_TO_MEMBER, 120);
        loadColumnWidth(COLUMN_ERROR_MESSAGE, 400);
    }

    private void loadColumnWidth(int tableColumnIndex, int defaultWidth) {

        int width = loadIntValue(TABLE_COLUMN + tableColumnIndex, defaultWidth);

        Table table = tableViewer.getTable();
        table.getColumn(tableColumnIndex).setWidth(width);
    }

    /**
     * Stores the screen values that are preserved for the next copy operation.
     */
    private void storeScreenValues() {

        storeColumnWidth(COLUMN_FROM_LIBRARY);
        storeColumnWidth(COLUMN_FROM_FILE);
        storeColumnWidth(COLUMN_FROM_MEMBER);
        storeColumnWidth(COLUMN_TO_MEMBER);
        storeColumnWidth(COLUMN_ERROR_MESSAGE);
    }

    private void storeColumnWidth(int tableColumnIndex) {

        Table table = tableViewer.getTable();
        int width = table.getColumn(tableColumnIndex).getWidth();

        storeValue(TABLE_COLUMN + tableColumnIndex, width);
    }

    private Text createNameField(Composite mainArea, String label) {
        Label labelToFile = new Label(mainArea, SWT.NONE);
        labelToFile.setText(label);

        Text text = WidgetFactory.createUpperCaseText(mainArea);
        text.setLayoutData(new GridData(120, SWT.DEFAULT));
        text.setTextLimit(10);

        return text;
    }

    private TableColumn addTableColumn(TableViewer table, String label) {
        return addTableColumn(table, label, 120);
    }

    private TableColumn addTableColumn(TableViewer tableViewer, String label, int width) {
        TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setWidth(width);
        column.setText(label);

        return column;
    }

    private String getFileName() {
        return textToFile.getText();
    }

    private String getLibraryName() {
        return textToLibrary.getText();
    }

    private String getToConnectionName() {

        if (comboToConnection == null) {
            return jobDescription.getToConnectionName();
        }

        return comboToConnection.getText();
    }

    private void setControlEnablement() {

        if (jobDescription == null || !jobDescription.haveItemsToCopy() || jobDescription.isActive()) {
            setButtonEnablement(getButton(IDialogConstants.OK_ID), false);
            setButtonLabel(getButton(IDialogConstants.CANCEL_ID), IDialogConstants.CLOSE_LABEL);
            setButtonEnablement(getButton(IDialogConstants.RETRY_ID), true);
        } else {
            setButtonEnablement(getButton(IDialogConstants.OK_ID), true);
            setButtonLabel(getButton(IDialogConstants.CANCEL_ID), IDialogConstants.CANCEL_LABEL);
            setButtonEnablement(getButton(IDialogConstants.RETRY_ID), false);
        }

        if (jobDescription.isActive()) {
            setButtonEnablement(getButton(IDialogConstants.CANCEL_ID), false);
            setButtonEnablement(getButton(IDialogConstants.RETRY_ID), false);
        } else {
            setButtonEnablement(getButton(IDialogConstants.CANCEL_ID), true);
        }
    }

    private void setButtonEnablement(Button button, boolean enabled) {
        if (button == null) {
            return;
        }

        button.setEnabled(enabled);
    }

    private void setButtonLabel(Button button, String label) {
        if (button == null) {
            return;
        }

        button.setText(label);
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(910, 600);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    /**
     * Content provider for the member list table.
     */
    private class ContentProviderMemberItems implements IStructuredContentProvider, CopyMemberService.ModifiedListener {

        private TableViewer viewer;

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof CopyMemberService) {
                return ((CopyMemberService)inputElement).getItems();
            }
            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            this.viewer = (TableViewer)viewer;

            if (oldInput != null) {
                ((CopyMemberService)oldInput).removeModifiedListener(ContentProviderMemberItems.this);
            }

            if (newInput != null) {
                ((CopyMemberService)newInput).addModifiedListener(ContentProviderMemberItems.this);
            }
        }

        public void modified(CopyMemberItem item) {
            if (item == null) {
                viewer.refresh(true);
            } else {
                viewer.update(item, null);
            }
            mainArea.update();
            setControlEnablement();
        }
    }

    /**
     * Content provider for the member list table.
     */
    private class LabelProviderMemberItems extends LabelProvider implements TooltipProvider, ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {

            CopyMemberItem member = (CopyMemberItem)element;

            switch (columnIndex) {
            case COLUMN_FROM_LIBRARY:
                return member.getFromLibrary();
            case COLUMN_FROM_FILE:
                return member.getFromFile();
            case COLUMN_FROM_MEMBER:
                return member.getFromMember();
            case COLUMN_TO_MEMBER:
                return member.getToMember();
            case COLUMN_ERROR_MESSAGE:
                return getErrorMessage(member);

            default:
                return Messages.EMPTY;
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getTooltipText(Object element, int columnIndex) {

            CopyMemberItem member = (CopyMemberItem)element;

            switch (columnIndex) {
            case COLUMN_ERROR_MESSAGE:
                return getErrorMessage(member);
            default:
                return null;
            }
        }

        private String getErrorMessage(CopyMemberItem member) {
            if (member.isCopied()) {
                return Messages.C_O_P_I_E_D;
            } else if (!StringHelper.isNullOrEmpty(member.getErrorMessage())) {
                return member.getErrorMessage();
            } else {
                return Messages.EMPTY;
            }
        }
    }
}
