/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.FTP;

public class TransferISphereLibrary extends Shell {

    private AS400 as400;
    private CommandCall commandCall;
    private Table tableStatus;
    private Button buttonStart;
    private Composite buttonPanel;
    private Button buttonClose;
    private Button buttonJobLog;
    private String iSphereLibrary;
    private int ftpPort;
    private String hostName;

    public TransferISphereLibrary(Display display, int style, String anISphereLibrary, String aHostName, int aFtpPort) {
        super(display, style);

        setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_TRANSFER_LIBRARY_32));

        iSphereLibrary = anISphereLibrary;
        hostName = aHostName;
        setFtpPort(aFtpPort);

        createContents();

        addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent arg0) {

                if (as400 != null) {
                    as400.disconnectAllServices();
                    as400 = null;
                    commandCall = null;
                }
            }
        });
    }

    private void setFtpPort(int aFtpPort) {
        if (aFtpPort <= 0) {
            ftpPort = Preferences.getInstance().getDefaultFtpPortNumber();
        } else {
            ftpPort = aFtpPort;
        }
    }

    protected void createContents() {

        GridLayout gl_shell = new GridLayout();
        gl_shell.marginTop = 10;
        gl_shell.verticalSpacing = 10;
        setLayout(gl_shell);

        setText(Messages.Transfer_iSphere_library);
        setSize(500, 330);

        buttonStart = WidgetFactory.createPushButton(this);
        buttonStart.addSelectionListener(new TransferLibrarySelectionAdapter());
        buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonStart.setText(Messages.Start_Transfer);

        tableStatus = new Table(this, SWT.BORDER | SWT.MULTI);
        final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true);
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
        GridLayout buttonPanelLayout = new GridLayout(2, true);
        buttonPanel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
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

    public void setStatus(String status) {
        TableItem itemStatus = new TableItem(tableStatus, SWT.BORDER);
        itemStatus.setText(status);
        tableStatus.update();
    }

    private boolean checkLibraryPrecondition(String iSphereLibrary) {

        while (libraryExists(iSphereLibrary)) {
            if (!MessageDialog.openQuestion(
                getShell(),
                Messages.msgBox_headline_Delete_Object,
                Messages.bind(Messages.Library_A_does_already_exist, iSphereLibrary) + "\n\n"
                    + Messages.bind(Messages.Do_you_want_to_delete_library_A, iSphereLibrary))) {
                return false;
            }
            setStatus(Messages.bind(Messages.Deleting_library_A, iSphereLibrary));
            deleteLibrary(iSphereLibrary, true);
        }

        return true;
    }

    private boolean libraryExists(String iSphereLibrary) {

        if (!ISphereHelper.checkLibrary(as400, iSphereLibrary)) {
            return false;
        }

        return true;
    }

    private boolean deleteLibrary(String iSphereLibrary, boolean logErrors) {

        if (!executeCommand("DLTLIB LIB(" + iSphereLibrary + ")", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean checkSaveFilePrecondition(String workLibrary, String saveFileName) {

        while (saveFileExists(workLibrary, saveFileName)) {
            if (!MessageDialog.openQuestion(
                getShell(),
                Messages.msgBox_headline_Delete_Object,
                Messages.bind(Messages.File_B_in_library_A_does_already_exist, new String[] { workLibrary, saveFileName }) + "\n\n"
                    + Messages.bind(Messages.Do_you_want_to_delete_object_A_B_type_C, new String[] { workLibrary, saveFileName, "*FILE" }))) {
                return false;
            }
            setStatus(Messages.bind(Messages.Deleting_object_A_B_of_type_C, new String[] { workLibrary, saveFileName, "*FILE" }));
            deleteSaveFile(workLibrary, saveFileName, true);
        }

        return true;
    }

    private boolean saveFileExists(String workLibrary, String saveFileName) {

        if (!ISphereHelper.checkObject(as400, workLibrary, saveFileName, "*FILE")) {
            return false;
        }

        return true;
    }

    private boolean deleteSaveFile(String workLibrary, String saveFileName, boolean logErrors) {

        if (!executeCommand("DLTF FILE(" + workLibrary + "/" + saveFileName + ")", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean createSaveFile(String workLibrary, String saveFileName, boolean logErrors) {

        if (!executeCommand("CRTSAVF FILE(" + workLibrary + "/" + saveFileName + ") TEXT('iSphere')", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean restoreLibrary(String workLibrary, String saveFileName, String iSphereLibrary) {

        String cpfMsg = executeCommand("RSTLIB SAVLIB(ISPHERE) DEV(*SAVF) SAVF(" + workLibrary + "/" + saveFileName + ") RSTLIB(" + iSphereLibrary
            + ")", true);
        if (!cpfMsg.equals("")) {
            return false;
        }

        return true;
    }

    private void printJobLog() {

        String cpfMsg = executeCommand("DSPJOBLOG JOB(*) OUTPUT(*PRINT)", true);
        if (cpfMsg.equals("")) {
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
                        for (int idx = 0; idx < messageList.length; idx++) {
                            setStatus(messageList[idx].getID() + ": " + messageList[idx].getText());
                        }
                    }
                    return escapeMessage.getID();
                }
            }
            return "";
        } catch (Exception e) {
            return "CPF0000";
        }
    }

    public boolean connect() {
        buttonStart.setEnabled(false);
        buttonClose.setEnabled(false);
        SignOnDialog signOnDialog = new SignOnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), hostName);
        if (signOnDialog.open() == Dialog.OK) {
            as400 = signOnDialog.getAS400();
            if (as400 != null) {
                try {
                    as400.connectService(AS400.COMMAND);
                    commandCall = new CommandCall(as400);
                    if (commandCall != null) {
                        setStatus(Messages.Server_job_colon + " " + commandCall.getServerJob().toString());
                        hostName = as400.getSystemName();
                        setStatus(Messages.bind(Messages.About_to_transfer_library_A_to_host_B_using_port_C, new String[] { iSphereLibrary.trim(),
                            hostName, Integer.toString(ftpPort) }));
                        buttonStart.setEnabled(true);
                        buttonClose.setEnabled(true);
                        return true;
                    }
                } catch (Throwable e) {
                    ISpherePlugin.logError("Failed to connect to host: " + hostName, e);
                }
            }
        }
        return false;
    }

    private class TransferLibrarySelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent event) {

            buttonStart.setEnabled(false);
            buttonClose.setEnabled(false);
            boolean successfullyTransfered = false;

            String workLibrary = "QGPL";
            String saveFileName = iSphereLibrary;

            setStatus(Messages.bind(Messages.Checking_library_A_for_existence, iSphereLibrary));
            if (!checkLibraryPrecondition(iSphereLibrary)) {
                setStatus("!!!   " + Messages.bind(Messages.Library_A_does_already_exist, iSphereLibrary) + "   !!!");
            } else {
                setStatus(Messages.bind(Messages.Checking_file_B_in_library_A_for_existence, new String[] { workLibrary, saveFileName }));
                if (!checkSaveFilePrecondition(workLibrary, saveFileName)) {
                    setStatus("!!!   " + Messages.bind(Messages.File_B_in_library_A_does_already_exist, new String[] { workLibrary, saveFileName })
                        + "   !!!");
                } else {

                    setStatus(Messages.bind(Messages.Creating_save_file_B_in_library_A, new String[] { workLibrary, saveFileName }));
                    if (!createSaveFile(workLibrary, saveFileName, true)) {
                        setStatus("!!!   "
                            + Messages.bind(Messages.Could_not_create_save_file_B_in_library_A, new String[] { workLibrary, saveFileName })
                            + "   !!!");
                    } else {

                        try {

                            setStatus(Messages.Sending_save_file_to_host);
                            setStatus(Messages.bind(Messages.Using_Ftp_port_number, new Integer(ftpPort)));
                            AS400FTP client = new AS400FTP(as400);

                            URL fileUrl = FileLocator.toFileURL(ISpherePlugin.getInstallURL());
                            File file = new File(fileUrl.getPath() + "Server" + File.separator + "ISPHERE.SAVF");
                            client.setPort(ftpPort);
                            client.setDataTransferType(FTP.BINARY);
                            if (client.connect()) {
                                client.put(file, "/QSYS.LIB/" + workLibrary + ".LIB/" + saveFileName + ".FILE");
                                client.disconnect();
                            }

                            setStatus(Messages.bind(Messages.Restoring_library_A, iSphereLibrary));
                            if (!restoreLibrary(workLibrary, saveFileName, iSphereLibrary)) {
                                setStatus("!!!   " + Messages.bind(Messages.Could_not_restore_library_A, iSphereLibrary) + "   !!!");
                            } else {
                                setStatus("!!!   " + Messages.bind(Messages.Library_A_successfull_transfered, iSphereLibrary) + "   !!!");
                                successfullyTransfered = true;
                            }

                        } catch (Throwable e) {
                            ISpherePlugin.logError(Messages.Could_not_send_save_file_to_host, e);

                            setStatus("!!!   " + Messages.Could_not_send_save_file_to_host + "   !!!");
                            setStatus(e.getLocalizedMessage());
                        } finally {

                            setStatus(Messages.bind(Messages.Deleting_object_A_B_of_type_C, new String[] { workLibrary, saveFileName, "*FILE" }));
                            deleteSaveFile(workLibrary, saveFileName, true);
                        }

                    }
                }
            }

            buttonPanel.dispose();
            buttonPanel = createButtons(true);
            layout(true);

            if (successfullyTransfered) {
                buttonStart.setEnabled(false);
                buttonClose.setEnabled(true);
                buttonClose.setFocus();
            } else {
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
