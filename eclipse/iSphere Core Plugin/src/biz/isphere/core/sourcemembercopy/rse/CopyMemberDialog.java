/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy.rse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.sourcemembercopy.Columns;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.CopyMemberItemTableCellModifier;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator;
import biz.isphere.core.sourcemembercopy.IValidateMembersPostRun;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.tableviewer.TableViewerKeyBoardSupporter;
import biz.isphere.core.swt.widgets.tableviewer.TooltipProvider;

public class CopyMemberDialog extends XDialog implements IValidateMembersPostRun {

    private static final String SINGLE_QUOTE = "'";

    private static int BUTTON_COPY_ID = IDialogConstants.OK_ID;
    private static int BUTTON_RESET_ID = IDialogConstants.RETRY_ID;
    private static int BUTTON_CLOSE_CANCEL = IDialogConstants.CANCEL_ID;

    private static String USE_LOCAL_CACHE = "useLocalCache"; //$NON-NLS-1$

    private CopyMemberService copyMemberService;
    private CopyMemberValidator copyMemberValidator;

    private Combo comboToConnection;
    private Text textToFile;
    private Text textToLibrary;
    private TableViewer tableViewer;
    private Button chkBoxReplace;
    private Button chkBoxIgnoreDataLostError;
    private Button chkBoxUseLocalCache;
    private Label labelNumElem;

    private Composite mainArea;

    public CopyMemberDialog(Shell parentShell) {
        super(parentShell);
    }

    public void setContent(CopyMemberService jobDescription) {

        this.copyMemberService = jobDescription;

        setControlEnablement();
    }

    @Override
    protected void okPressed() {

        storeScreenValues();

        validateUserInputAndPerformCopyOperation();
    }

    @Override
    protected void cancelPressed() {

        if (isValidating()) {
            copyMemberValidator.cancel();
            return;
        }

        if (isCopying()) {
            copyMemberService.cancel();
            return;
        }

        storeScreenValues();

        super.cancelPressed();
    }

    protected boolean canHandleShellCloseEvent() {

        boolean canCloseDialog;

        if (isValidating()) {
            canCloseDialog = false;
        } else if (copyMemberService != null && copyMemberService.isActive()) {
            canCloseDialog = false;
        } else {
            canCloseDialog = true;
        }

        if (!canCloseDialog) {
            MessageDialog.openInformation(getShell(), Messages.E_R_R_O_R, Messages.Operation_in_progress_Cannot_close_dialog);
        }

        return canCloseDialog;
    }

    private boolean isValidating() {

        if (copyMemberValidator != null && copyMemberValidator.isActive()) {
            return true;
        }

        return false;
    }

    private boolean isCopying() {

        if (copyMemberService != null && copyMemberService.isActive()) {
            return true;
        }

        return false;
    }

    private void validateUserInputAndPerformCopyOperation() {

        tableViewer.setSelection(null);

        copyMemberService.setToConnection(getToConnectionName());
        copyMemberService.setToLibrary(getToLibraryName());
        copyMemberService.setToFile(getToFileName());
        copyMemberService.setUseLocalCache(chkBoxUseLocalCache.getSelection());

        copyMemberValidator = new CopyMemberValidator(copyMemberService, chkBoxReplace.getSelection(), chkBoxIgnoreDataLostError.getSelection(), this);

        setControlEnablement();
        copyMemberValidator.start();
    }

    public void returnResult(final int errorItem, final String errorMessage) {

        copyMemberValidator = null;

        new UIJob(Messages.EMPTY) {

            @Override
            public IStatus runInUIThread(IProgressMonitor arg0) {

                if (errorMessage != null) {

                    setErrorMessage(errorMessage);

                    switch (errorItem) {
                    case CopyMemberValidator.ERROR_TO_CONNECTION:
                        comboToConnection.setFocus();
                        break;

                    case CopyMemberValidator.ERROR_TO_LIBRARY:
                        textToLibrary.setFocus();
                        break;

                    case CopyMemberValidator.ERROR_TO_FILE:
                        textToFile.setFocus();
                        break;

                    case CopyMemberValidator.ERROR_CANCELED:
                        textToFile.setFocus();
                        break;

                    default:
                        break;
                    }

                    setControlEnablement();

                } else {
                    setErrorMessage(null);
                    copyMemberService.execute();
                }

                return Status.OK_STATUS;
            }
        }.schedule();
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
            comboToConnection.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setControlEnablement();
                }
            });
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

        addTableColumn(tableViewer.getTable(), Columns.FROM_LIBRARY);
        addTableColumn(tableViewer.getTable(), Columns.FROM_FILE);
        addTableColumn(tableViewer.getTable(), Columns.FROM_MEMBER);
        addTableColumn(tableViewer.getTable(), Columns.TO_MEMBER);
        addTableColumn(tableViewer.getTable(), Columns.ERROR_MESSAGE, 400);

        tableViewer.setCellModifier(new CopyMemberItemTableCellModifier(tableViewer));
        tableViewer.setContentProvider(new ContentProviderMemberItems());
        tableViewer.setLabelProvider(new LabelProviderMemberItems());

        TableViewerKeyBoardSupporter supporter = new TableViewerKeyBoardSupporter(tableViewer, true);
        supporter.startSupport();

        labelNumElem = new Label(mainArea, SWT.NONE);
        labelNumElem.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, ((GridLayout)mainArea.getLayout()).numColumns, 1));
        int numItems;
        if (copyMemberService != null) {
            numItems = copyMemberService.getItems().length;
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

        chkBoxUseLocalCache = WidgetFactory.createCheckbox(mainArea);
        chkBoxUseLocalCache.setText("Use local cache (uncommitted feature)");
        chkBoxUseLocalCache.setLayoutData(new GridData(SWT.BEGINNING, SWT.DEFAULT, false, false, 3, 1));

        createStatusLine(mainArea);

        loadScreenValues();

        return mainArea;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button btnReset = createButton(parent, BUTTON_RESET_ID, Messages.Reset, false);
        btnReset.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                copyMemberService.reset();
                setControlEnablement();
                setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        super.createButtonsForButtonBar(parent);
        getButton(BUTTON_COPY_ID).setText(Messages.Copy);
    }

    @Override
    protected Control createContents(Composite parent) {

        Control control = super.createContents(parent);

        // Enable control after dialog area and buttons have been created.
        setControlEnablement();

        return control;
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
            comboToConnection.setText(copyMemberService.getToConnectionName());
        }

        if (copyMemberService.getFromLibraryNamesCount() == 1) {
            textToLibrary.setText(copyMemberService.getFromLibraryNames()[0]);
        } else {
            textToLibrary.setText(Messages.EMPTY);
        }

        if (copyMemberService.getFromFileNamesCount() == 1) {
            textToFile.setText(copyMemberService.getFromFileNames()[0]);
        } else {
            textToFile.setText(Messages.EMPTY);
        }

        tableViewer.setInput(copyMemberService);

        boolean useLocalCache = getDialogSettingsManager().loadBooleanValue(USE_LOCAL_CACHE, false);
        chkBoxUseLocalCache.setSelection(useLocalCache);
    }

    /**
     * Stores the screen values that are preserved for the next copy operation.
     */
    private void storeScreenValues() {

        getDialogSettingsManager().storeValue(USE_LOCAL_CACHE, chkBoxUseLocalCache.getSelection());
    }

    private Text createNameField(Composite mainArea, String label) {
        Label labelToFile = new Label(mainArea, SWT.NONE);
        labelToFile.setText(label);

        Text text = WidgetFactory.createUpperCaseText(mainArea);
        text.setLayoutData(new GridData(120, SWT.DEFAULT));
        text.setTextLimit(10);

        return text;
    }

    private TableColumn addTableColumn(Table table, Columns column) {
        return addTableColumn(table, column, column.width);
    }

    private TableColumn addTableColumn(Table table, Columns column, int width) {

        TableColumn tableColumn = getDialogSettingsManager().createResizableTableColumn(tableViewer.getTable(), SWT.LEFT, column.name, width);
        tableColumn.setText(column.label);

        return tableColumn;
    }

    private String getToFileName() {
        return textToFile.getText();
    }

    private String getToLibraryName() {
        return textToLibrary.getText();
    }

    private String getToConnectionName() {

        if (comboToConnection == null) {
            return copyMemberService.getToConnectionName();
        }

        return comboToConnection.getText();
    }

    private void setControlEnablement() {

        if (copyMemberService == null) {
            setButtonEnablement(getButton(BUTTON_COPY_ID), false);
            setButtonEnablement(getButton(BUTTON_RESET_ID), false);
            setButtonEnablement(getButton(BUTTON_CLOSE_CANCEL), true);
            setButtonLabel(getButton(BUTTON_CLOSE_CANCEL), IDialogConstants.CLOSE_LABEL);
            setControlsEnables(true);
        } else {

            if (isValidating()) {
                setButtonEnablement(getButton(BUTTON_COPY_ID), false);
                setButtonEnablement(getButton(BUTTON_RESET_ID), false);
                setButtonEnablement(getButton(BUTTON_CLOSE_CANCEL), true);
                setButtonLabel(getButton(BUTTON_CLOSE_CANCEL), IDialogConstants.CANCEL_LABEL);
                setControlsEnables(false);
                setErrorMessage(null);
                setStatusMessage(Messages.Validating_dots);
            } else if (isCopying()) {
                setButtonEnablement(getButton(BUTTON_COPY_ID), false);
                setButtonEnablement(getButton(BUTTON_RESET_ID), false);
                setButtonEnablement(getButton(BUTTON_CLOSE_CANCEL), true);
                setButtonLabel(getButton(BUTTON_CLOSE_CANCEL), IDialogConstants.CANCEL_LABEL);
                setControlsEnables(false);
                setErrorMessage(null);
                setStatusMessage(Messages.Copying_dots);
            } else {

                if (copyMemberService.hasItemsToCopy()) {
                    setButtonEnablement(getButton(BUTTON_COPY_ID), true);
                } else {
                    setButtonEnablement(getButton(BUTTON_COPY_ID), false);
                }

                if (copyMemberService.getItemsCopiedCount() > 0) {
                    setButtonEnablement(getButton(BUTTON_RESET_ID), true);
                    setButtonEnablement(getButton(BUTTON_CLOSE_CANCEL), true);
                } else {
                    setButtonEnablement(getButton(BUTTON_RESET_ID), false);
                    setButtonEnablement(getButton(BUTTON_CLOSE_CANCEL), true);
                }

                if (copyMemberService.getItemsCopiedCount() > 0 && copyMemberService.hasItemsToCopy()) {
                    setButtonLabel(getButton(BUTTON_CLOSE_CANCEL), IDialogConstants.CANCEL_LABEL);
                } else {
                    setButtonLabel(getButton(BUTTON_CLOSE_CANCEL), IDialogConstants.CLOSE_LABEL);
                }

                setControlsEnables(true);

                if (copyMemberService.isCanceled()) {
                    setErrorMessage(Messages.Operation_has_been_canceled_by_the_user);
                } else {
                    setStatusMessage(Messages.EMPTY);
                }
            }
        }
    }

    private void setControlsEnables(boolean enabled) {

        if (mainArea == null) {
            return;
        }

        comboToConnection.setEnabled(enabled);
        textToFile.setEnabled(enabled);
        textToLibrary.setEnabled(enabled);
        tableViewer.getTable().setEnabled(enabled);
        chkBoxReplace.setEnabled(enabled);
        chkBoxIgnoreDataLostError.setEnabled(enabled);

        if (enabled) {
            chkBoxUseLocalCache.setEnabled(!comboToConnection.getText().equals(copyMemberService.getFromConnectionName()));
        } else {
            chkBoxUseLocalCache.setEnabled(enabled);
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

        public void modified(final CopyMemberItem item) {

            if (Display.getCurrent() == null) {
                UIJob job = new UIJob("") {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor arg0) {
                        updateStatus(item);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            } else {
                updateStatus(item);
            }
        }

        private void updateStatus(CopyMemberItem item) {

            if (item == null) {
                viewer.refresh(true);
            } else {
                viewer.update(item, null);
                if (isCopying()) {
                    viewer.reveal(item);
                    viewer.setSelection(new StructuredSelection(item));
                } else if (isValidating()) {
                    if (item.isError() && viewer.getSelection().isEmpty()) {
                        viewer.reveal(item);
                        viewer.setSelection(new StructuredSelection(item));
                    }
                }
            }
            setControlEnablement();
            mainArea.update();
        }
    }

    /**
     * Content provider for the member list table.
     */
    private class LabelProviderMemberItems extends LabelProvider implements TooltipProvider, ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {

            CopyMemberItem member = (CopyMemberItem)element;

            if (columnIndex == Columns.FROM_LIBRARY.ordinal()) {
                return member.getFromLibrary();
            } else if (columnIndex == Columns.FROM_FILE.ordinal()) {
                return member.getFromFile();
            } else if (columnIndex == Columns.FROM_MEMBER.ordinal()) {
                return member.getFromMember();
            } else if (columnIndex == Columns.TO_MEMBER.ordinal()) {
                return member.getToMember();
            } else if (columnIndex == Columns.ERROR_MESSAGE.ordinal()) {
                return getErrorMessage(member);
            } else {
                return Messages.EMPTY;
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getTooltipText(Object element, int columnIndex) {

            CopyMemberItem member = (CopyMemberItem)element;

            if (columnIndex == Columns.ERROR_MESSAGE.ordinal()) {
                return getErrorMessage(member);
            } else {
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
