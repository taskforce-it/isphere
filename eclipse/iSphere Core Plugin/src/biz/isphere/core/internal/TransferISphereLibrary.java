/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

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
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.StringHelper;
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

public class TransferISphereLibrary extends Shell implements StatusMessageReceiver {

    private AS400 as400;
    private CommandCall commandCall;
    private Table tableStatus;
    private ConnectionCombo comboConnections;
    private Button buttonStart;
    private Composite buttonPanel;
    private Button buttonClose;
    private Button buttonJobLog;
    private String iSphereLibrary;
    private String aspGroup;
    private int ftpPort;
    private String connectionName;
    private boolean connectionsEnabled;

    public TransferISphereLibrary(Display display, int style, String anISphereLibrary, String aASPGroup, String aConnectionName, int aFtpPort) {
        super(display, style);

        setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_TRANSFER_LIBRARY_32));

        iSphereLibrary = anISphereLibrary;
        aspGroup = aASPGroup;
        connectionName = aConnectionName;
        setFtpPort(aFtpPort);
        setConnectionsEnabled(true);

        createContents();

        addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                if (!buttonClose.isDisposed()) {
                    event.doit = buttonClose.isEnabled();
                } else {
                    event.doit = true;
                }
            }
        });

        addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent arg0) {
                disconnectSystem();
            }
        });
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

    protected void createContents() {

        GridLayout gl_shell = new GridLayout(2, false);
        gl_shell.marginTop = 10;
        gl_shell.verticalSpacing = 10;
        setLayout(gl_shell);

        setText(Messages.Transfer_iSphere_library);
        setSize(500, 400);

        final Label labelConnections = new Label(this, SWT.NONE);
        labelConnections.setText(Messages.Connection_colon);
        labelConnections.setLayoutData(new GridData());

        comboConnections = WidgetFactory.createConnectionCombo(this, SWT.NONE);
        comboConnections.setEnabled(connectionsEnabled);
        comboConnections.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comboConnections.setText(connectionName);
        comboConnections.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                clearStatus();
                updateConnectionProperties(true);
            }
        });

        buttonStart = WidgetFactory.createPushButton(this);
        buttonStart.addSelectionListener(new TransferLibrarySelectionAdapter());
        buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonStart.setText(Messages.Start_Transfer);

        tableStatus = new Table(this, SWT.BORDER | SWT.MULTI);
        final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tableStatus.setLayoutData(gd_tableStatus);

        final TableColumn columnStatus = new TableColumn(tableStatus, SWT.NONE);
        columnStatus.setWidth(getSize().x);

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

        buttonPanel = createButtons(false);

        updateConnectionProperties(false);
    }

    private void updateConnectionProperties(boolean updateSystem) {

        connectionName = comboConnections.getText();
        if (StringHelper.isNullOrEmpty(connectionName)) {
            setStatus(Messages.Please_select_a_connection);
            return;
        }

        if (updateSystem) {
            if (!connectSystem()) {
                setStatus(Messages.bind(Messages.Connection_A_does_not_exist_or_is_currently_offline_and_cannot_be_connected, connectionName));
                return;
            }
        }

        if (as400 == null) {
            setStatus(Messages.Ready_to_transfer_the_iSphere_library);
        } else {
            setStatus(Messages.bind(Messages.About_to_transfer_library_A_ASP_group_D_to_host_B_using_port_C,
                new Object[] { iSphereLibrary, as400.getSystemName(), ftpPort, aspGroup }));
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

    private Composite createButtons(boolean printJobLogButton) {

        Composite buttonPanel = new Composite(this, SWT.NONE);
        GridLayout buttonPanelLayout = new GridLayout(1, true);
        buttonPanel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        buttonPanelLayout.marginHeight = 0;
        buttonPanelLayout.marginWidth = 0;
        buttonPanel.setLayout(buttonPanelLayout);

        if (printJobLogButton) {
            createButtonPrintJobLog(buttonPanel);
        }

        createButtonClose(buttonPanel);

        return buttonPanel;
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
    }

    private void createButtonClose(Composite buttonPanel) {
        buttonClose = WidgetFactory.createPushButton(buttonPanel);
        buttonClose.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        buttonClose.setText(Messages.btnLabel_Close);
        buttonClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }
        });
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private void printJobLog() {

        String cpfMsg = executeCommand("DSPJOBLOG JOB(*) OUTPUT(*PRINT)", true); //$NON-NLS-1$
        if (cpfMsg.equals("")) { //$NON-NLS-1$
            setStatus(Messages.Job_log_has_been_printed);
        }
    }

    private String executeCommand(String command, boolean logError) {

        try {

            if (commandCall == null) {
                commandCall = new CommandCall(as400);
            }

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

            return ""; //$NON-NLS-1$

        } catch (Exception e) {
            return "CPF0000"; //$NON-NLS-1$
        }
    }

    private void clearStatus() {
        tableStatus.removeAll();
        redraw();
    }

    public void setStatus(String message) {
        TableItem itemStatus = new TableItem(tableStatus, SWT.BORDER);
        itemStatus.setText(message);
        tableStatus.update();
        redraw();
    }

    private boolean connectSystem() {

        if (as400 != null) {
            disconnectSystem();
        }

        as400 = IBMiHostContributionsHandler.getSystem(comboConnections.getText());
        if (as400 == null) {
            commandCall = null;
            return false;
        }

        if (as400 instanceof SecureAS400) {
            as400 = new SecureAS400(as400);
        } else {
            as400 = new AS400(as400);
        }

        commandCall = new CommandCall(as400);

        return true;
    }

    private void disconnectSystem() {

        if (as400 != null) {
            as400.disconnectAllServices();
            as400 = null;
        }

        commandCall = null;
    }

    private class TransferLibrarySelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent event) {

            comboConnections.setEnabled(false);
            buttonStart.setEnabled(false);
            buttonClose.setEnabled(false);

            if (as400 == null) {
                if (!connectSystem()) {
                    clearStatus();
                    setStatus(Messages.bind(Messages.Connection_A_does_not_exist_or_is_currently_offline_and_cannot_be_connected, connectionName));
                    comboConnections.setEnabled(true);
                    buttonStart.setEnabled(true);
                    buttonClose.setEnabled(true);
                    return;
                }
            }

            ProductLibraryUploader uploader = new ProductLibraryUploader(getShell(), as400, ftpPort, iSphereLibrary, aspGroup);
            uploader.setStatusMessageReceiver(TransferISphereLibrary.this);

            buttonPanel.dispose();
            buttonPanel = createButtons(true);
            layout(true);

            if (uploader.run()) {
                comboConnections.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonClose.setEnabled(true);
                buttonClose.setFocus();
            } else {
                comboConnections.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonClose.setEnabled(true);
                buttonJobLog.setFocus();
            }
        }
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
