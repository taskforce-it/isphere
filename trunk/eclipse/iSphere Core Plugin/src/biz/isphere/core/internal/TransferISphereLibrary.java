/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.SecureAS400;

public class TransferISphereLibrary extends XDialog implements StatusMessageReceiver {

    private static final String ENABLED_STATUS = "ENABLED_STATUS"; //$NON-NLS-1$

    private AS400 as400;
    private CommandCall commandCall;
    private Table tableStatus;
    private ConnectionCombo comboConnections;
    private Button buttonStart;
    private Button buttonClose;
    private Button buttonJobLog;
    private String iSphereLibrary;
    private String aspGroup;
    private int ftpPort;
    private String connectionName;
    private boolean connectionsEnabled;

    private boolean uploadCompleted;
    private Job enableEscapeKeyJob;

    private Composite dialogArea;

    public TransferISphereLibrary(Shell shell, int style, String anISphereLibrary, String aASPGroup, String aConnectionName, int aFtpPort) {
        super(shell);

        iSphereLibrary = anISphereLibrary;
        aspGroup = aASPGroup;
        connectionName = aConnectionName;
        setFtpPort(aFtpPort);
        setConnectionsEnabled(true);

        setUploadCompleted(false);
    }

    private void setUploadCompleted(boolean completed) {
        this.uploadCompleted = completed;
    }

    public void setConnectionsEnabled(boolean enabled) {
        this.connectionsEnabled = enabled;
        if (comboConnections != null && !comboConnections.isDisposed()) {
            comboConnections.setEnabled(connectionsEnabled);
        }
    }

    private void setFtpPort(int aFtpPort) {
        if (aFtpPort <= 0) {
            ftpPort = Preferences.getInstance().getDefaultFtpPortNumber();
        } else {
            ftpPort = aFtpPort;
        }
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Transfer_iSphere_library);

        newShell.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_TRANSFER_LIBRARY_32));
    }

    @Override
    public boolean close() {

        if (!buttonClose.isDisposed() && buttonClose.isEnabled()) {
            boolean closeConfirmed;
            if (uploadCompleted) {
                closeConfirmed = MessageDialog.openQuestion(getShell(), Messages.Confirm_exit, Messages.Close_upload_dialog);
            } else {
                closeConfirmed = true;
            }
            if (closeConfirmed) {
                if (super.close()) {
                    disconnectSystem();
                    return true;
                }
            }
        } else {
            // Eat Esc keystrokes and 'Window Close' events.
            enableCloseDelayed();
        }
        return false;
    }

    protected Control createDialogArea(Composite parent) {

        dialogArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginTop = 10;
        gridLayout.verticalSpacing = 10;
        dialogArea.setLayout(gridLayout);
        dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Label labelConnections = new Label(dialogArea, SWT.NONE);
        labelConnections.setText(Messages.Connection_colon);
        labelConnections.setLayoutData(new GridData());

        comboConnections = WidgetFactory.createConnectionCombo(dialogArea, SWT.NONE);
        comboConnections.setEnabled(connectionsEnabled);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

        comboConnections.setLayoutData(gridData);
        comboConnections.setText(connectionName);
        comboConnections.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                connectionName = comboConnections.getText();
                clearStatus();
                setStatus(Messages.bind(Messages.Connecting_to_A, connectionName));
                if (!connectSystem()) {
                    setStatus(Messages.Operation_has_been_canceled_by_the_user);
                }
                showConnectionProperties();
            }
        });

        buttonStart = WidgetFactory.createPushButton(dialogArea);
        buttonStart.addSelectionListener(new TransferLibrarySelectionAdapter());
        buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, gridLayout.numColumns, 1));
        buttonStart.setText(Messages.Start_Transfer);

        tableStatus = new Table(dialogArea, SWT.BORDER | SWT.MULTI);
        final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true, gridLayout.numColumns, 1);
        tableStatus.setLayoutData(gd_tableStatus);

        final TableColumn columnStatus = new TableColumn(tableStatus, SWT.NONE);

        tableStatus.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event) {
                Table table = (Table)event.getSource();
                if (table.getClientArea().width > 0) {
                    // Resize the column to the width of the table
                    columnStatus.setWidth(table.getClientArea().width);
                }
            }
        });

        tableStatus.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
            }

            public void keyPressed(KeyEvent event) {
                if ((event.stateMask & SWT.CTRL) != SWT.CTRL) {
                    return;
                }
                if (event.keyCode == 'a') {
                    tableStatus.selectAll();
                }
                if (event.keyCode == 'c') {
                    copyStatusLinesToClipboard(tableStatus.getSelection());
                }
            }
        });

        tableStatus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (e.button == 1) {
                    copyStatusLinesToClipboard(tableStatus.getSelection());
                }
            }
        });

        Menu menuTableStatusContextMenu = new Menu(tableStatus);
        menuTableStatusContextMenu.addMenuListener(new TableContextMenu(tableStatus));
        tableStatus.setMenu(menuTableStatusContextMenu);

        new UIJob("Establish connection") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor arg0) {
                clearStatus();
                setStatus(Messages.bind(Messages.Connecting_to_A, connectionName));
                if (!connectSystem()) {
                    setStatus(Messages.Operation_has_been_canceled_by_the_user);
                }
                showConnectionProperties();
                return Status.OK_STATUS;
            }
        }.schedule();

        return dialogArea;
    }

    private void showConnectionProperties() {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            setStatus(Messages.Please_select_a_connection);
            return;
        }

        if (as400 == null) {
            setStatus(Messages.bind(Messages.Not_yet_connected_to_A, connectionName));
        } else {

            try {
                setStatus(Messages.Server_job_colon + " " + commandCall.getServerJob().toString()); //$NON-NLS-1$
            } catch (Throwable e) {
            }

            setStatus(Messages.bind(Messages.About_to_transfer_library_A_ASP_group_D_to_host_B_using_port_C,
                new Object[] { iSphereLibrary, as400.getSystemName(), ftpPort, aspGroup }));
        }

        if (StringHelper.isNullOrEmpty(connectionName)) {
            comboConnections.setFocus();
        } else {
            buttonStart.setFocus();
        }
    }

    protected void copyStatusLinesToClipboard(TableItem[] tableItems) {

        if (tableItems.length == 1) {
            copyStatusLineToClipboard();
        } else {
            ClipboardHelper.setTableItemsText(tableItems);
        }
    }

    protected void copyStatusLineToClipboard() {

        TableItem[] tableItems = tableStatus.getSelection();
        if (tableItems != null && tableItems.length >= 1) {
            String text = tableItems[0].getText();
            if (text.startsWith(Messages.Server_job_colon)) {
                text = text.substring(Messages.Server_job_colon.length());
            }
            ClipboardHelper.setText(text.trim());
        }
    }

    private void createButtonPrintJobLog(Composite buttonPanel) {

        GridLayout buttonPanelLayout = (GridLayout)buttonPanel.getLayout();
        buttonPanelLayout.numColumns++;

        buttonJobLog = WidgetFactory.createPushButton(buttonPanel);
        buttonJobLog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonJobLog.setText(Messages.btnLabel_Print_job_log);
        buttonJobLog.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                printJobLog();
            }
        });

        buttonJobLog.setEnabled(false);
        buttonJobLog.setVisible(buttonJobLog.isEnabled());
    }

    private void printJobLog() {

        String cpfMsg = executeCommand("DSPJOBLOG JOB(*) OUTPUT(*PRINT)", true); //$NON-NLS-1$
        if (StringHelper.isNullOrEmpty(cpfMsg)) {
            setStatus(Messages.Job_log_has_been_printed);
        }
    }

    private String executeCommand(String command, boolean logError) {

        try {

            commandCall.run(command);
            AS400Message[] messageList = commandCall.getMessageList();
            if (messageList.length > 0) {
                AS400Message escapeMessage = null;
                for (int idx = 0; idx < messageList.length; idx++) {
                    if (messageList[idx].getType() == AS400Message.ESCAPE) {
                        escapeMessage = messageList[idx];
                    }
                }
                if (escapeMessage != null) {
                    if (logError) {
                        setStatus(Messages.bind(Messages.Error_A, command));
                        for (int idx = 0; idx < messageList.length; idx++) {
                            setStatus(messageList[idx].getID() + ": " + messageList[idx].getText()); //$NON-NLS-1$
                        }
                    }
                    return escapeMessage.getID();
                }
            }

            return Messages.EMPTY;

        } catch (Exception e) {
            return "CPF0000"; //$NON-NLS-1$
        }
    }

    private void clearStatus() {
        tableStatus.removeAll();
        tableStatus.update();
    }

    public void setStatus(String message) {
        TableItem itemStatus = new TableItem(tableStatus, SWT.BORDER);
        itemStatus.setText(message);
        tableStatus.update();
        tableStatus.update();
    }

    private boolean connectSystem() {

        disableControls();

        if (as400 != null) {
            disconnectSystem();
        }

        as400 = IBMiHostContributionsHandler.getSystem(connectionName);
        if (as400 == null) {
            commandCall = null;
        } else {

            if (as400 instanceof SecureAS400) {
                as400 = new SecureAS400(as400);
            } else {
                as400 = new AS400(as400);
            }

            commandCall = new CommandCall(as400);
        }

        restoreControlsEnablement();

        if (as400 == null) {
            return false;
        } else {
            return true;
        }
    }

    private void disableControls() {

        saveEnablement(comboConnections);
        saveEnablement(buttonStart);
        saveEnablement(buttonJobLog);
        saveEnablement(buttonClose);

        comboConnections.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonJobLog.setEnabled(false);
        buttonClose.setEnabled(false);

        buttonJobLog.setVisible(buttonJobLog.isEnabled());
    }

    private void restoreControlsEnablement() {

        comboConnections.setEnabled(getPreviousEnablement(comboConnections));
        buttonStart.setEnabled(getPreviousEnablement(buttonStart));
        buttonJobLog.setEnabled(getPreviousEnablement(buttonJobLog));
        buttonClose.setEnabled(getPreviousEnablement(buttonClose));

        buttonJobLog.setVisible(buttonJobLog.isEnabled());
    }

    private void saveEnablement(Control control) {
        control.setData(ENABLED_STATUS, control.isEnabled());
    }

    private boolean getPreviousEnablement(Control control) {
        return (Boolean)control.getData(ENABLED_STATUS);
    }

    private void disconnectSystem() {

        if (as400 != null) {
            as400.disconnectAllServices();
            as400 = null;
        }

        commandCall = null;

        if (!dialogArea.isDisposed()) {
            clearStatus();
            showConnectionProperties();
        }
    }

    private class TransferLibrarySelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent event) {

            comboConnections.setEnabled(false);
            buttonStart.setEnabled(false);
            buttonClose.setEnabled(false);

            buttonJobLog.setEnabled(false);
            buttonJobLog.setVisible(buttonJobLog.isEnabled());

            clearStatus();
            if (as400 == null) {
                setStatus(Messages.bind(Messages.Connecting_to_A, connectionName));
                if (!connectSystem()) {
                    setStatus(Messages.Operation_has_been_canceled_by_the_user);
                    showConnectionProperties();
                    comboConnections.setEnabled(true);
                    buttonStart.setEnabled(true);
                    buttonClose.setEnabled(true);
                    return;
                }
            } else {
                showConnectionProperties();
            }

            ProductLibraryUploader uploader = new ProductLibraryUploader(getShell(), as400, ftpPort, iSphereLibrary, aspGroup);
            uploader.setStatusMessageReceiver(TransferISphereLibrary.this);

            if (uploader.run()) {
                comboConnections.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonClose.setFocus();
            } else {
                comboConnections.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonJobLog.setFocus();
            }

            buttonJobLog.setEnabled(true);
            buttonJobLog.setVisible(buttonJobLog.isEnabled());

            setUploadCompleted(true);
            enableCloseDelayed();
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButtonPrintJobLog(parent);
        super.createButtonsForButtonBar(parent);
    }

    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == Dialog.CANCEL) {
            buttonClose = super.createButton(parent, id, Messages.btnLabel_Close, defaultButton);
            return buttonClose;
        }
        return null;
    }

    private void enableCloseDelayed() {

        if (enableEscapeKeyJob != null) {
            // Eat Esc keystroke
            enableEscapeKeyJob.cancel();
        }

        enableEscapeKeyJob = new Job(Messages.EMPTY) {
            @Override
            protected IStatus run(IProgressMonitor arg0) {
                enableEscapeKeyJob = null;
                new UIJob(Messages.EMPTY) {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor arg0) {
                        buttonClose.setEnabled(true);
                        return Status.OK_STATUS;
                    }
                }.schedule();
                return Status.OK_STATUS;
            }
        };

        enableEscapeKeyJob.schedule(200);
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
        Point defaultSize = getShell().computeSize(Size.getSize(500), Size.getSize(350), true);
        setMinimalSize(defaultSize);
        return defaultSize;
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
     * Class that implements the context menu for the table rows.
     */
    private class TableContextMenu extends MenuAdapter {

        private Table table;
        private MenuItem menuItemCopySelected;
        private MenuItem menuItemCopyAll;

        public TableContextMenu(Table table) {
            this.table = table;
        }

        @Override
        public void menuShown(MenuEvent event) {
            destroyMenuItems();
            createMenuItems();
        }

        private Menu getMenu() {
            return table.getMenu();
        }

        private void destroyMenuItems() {
            if (!((menuItemCopySelected == null) || (menuItemCopySelected.isDisposed()))) {
                menuItemCopySelected.dispose();
            }
            if (!((menuItemCopyAll == null) || (menuItemCopyAll.isDisposed()))) {
                menuItemCopyAll.dispose();
            }
        }

        private void createMenuItems() {

            createMenuItemCopySelected();
        }

        private void createMenuItemCopySelected() {

            menuItemCopySelected = new MenuItem(getMenu(), SWT.NONE);
            menuItemCopySelected.setText(Messages.Copy);
            menuItemCopySelected.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    copyStatusLinesToClipboard(table.getSelection());
                }
            });

            menuItemCopyAll = new MenuItem(getMenu(), SWT.NONE);
            menuItemCopyAll.setText(Messages.Copy_all);
            menuItemCopyAll.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    copyStatusLinesToClipboard(table.getItems());
                }
            });
        }
    }
}
