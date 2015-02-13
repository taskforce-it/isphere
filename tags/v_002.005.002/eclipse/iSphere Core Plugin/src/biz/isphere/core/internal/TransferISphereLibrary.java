/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.FTP;

public class TransferISphereLibrary extends Shell {

    private AS400 as400;
    private CommandCall commandCall;
    private Table tableStatus;
    private Button buttonStart;
    private String iSphereLibrary;
    private int ftpPort;
    private String hostName;

    public TransferISphereLibrary(Display display, int style, String anISphereLibrary, String aHostName, int aFtpPort) {
        super(display, style);

        setImage(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_TRANSFER_LIBRARY_32).createImage());
        
        iSphereLibrary = anISphereLibrary;
        hostName = aHostName;
        setFtpPort(aFtpPort);

        createContents();
        setLayout(new GridLayout());
    }

    private void setFtpPort(int aFtpPort) {
        if (aFtpPort <= 0) {
            ftpPort = Preferences.getInstance().getDefaultFtpPortNumber();
        } else {
            ftpPort = aFtpPort;
        }
    }

    protected void createContents() {

        setText(Messages.Transfer_iSphere_library);
        setSize(500, 250);

        buttonStart = WidgetFactory.createPushButton(this);
        buttonStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                buttonStart.setEnabled(false);
                setStatus(Messages.bind(Messages.Checking_library_A_for_existence, iSphereLibrary));
                if (!executeCommand("CHKOBJ OBJ(QSYS/" + iSphereLibrary + ") OBJTYPE(*LIB)").equals("CPF9801")) {
                    setStatus("!!!   " + Messages.bind(Messages.Library_A_does_already_exist, iSphereLibrary) + "   !!!");
                } else {
                    setStatus(Messages.bind(Messages.Checking_file_A_in_library_QGPL_for_existence, iSphereLibrary));
                    if (!executeCommand("CHKOBJ OBJ(QGPL/" + iSphereLibrary + ") OBJTYPE(*FILE)").equals("CPF9801")) {
                        setStatus("!!!   " + Messages.bind(Messages.File_A_in_library_QGPL_does_already_exist, iSphereLibrary) + "   !!!");
                    } else {
                        setStatus(Messages.bind(Messages.Creating_save_file_A_in_library_QGPL, iSphereLibrary));
                        if (!executeCommand("CRTSAVF FILE(QGPL/" + iSphereLibrary + ") TEXT('iSphere')", true).equals("")) {
                            setStatus("!!!   " + Messages.bind(Messages.Could_not_create_save_file_A_in_library_QGPL, iSphereLibrary) + "   !!!");
                        } else {
                            URL fileUrl;
                            try {
                                fileUrl = FileLocator.toFileURL(ISpherePlugin.getInstallURL());
                            } catch (IOException e) {
                                setStatus("!!!   Internal Error at checkpoint 1   !!!");
                                fileUrl = null;
                            }
                            if (fileUrl != null) {

                                File file = new File(fileUrl.getPath() + "Server\\ISPHERE");
                                setStatus(Messages.Sending_save_file_to_host);
                                setStatus(Messages.bind(Messages.Using_Ftp_port_number, new Integer(ftpPort)));
                                boolean ok = false;
                                AS400FTP client = new AS400FTP(as400);

                                try {
                                    client.setPort(ftpPort);
                                    client.setDataTransferType(FTP.BINARY);
                                    if (client.connect()) {
                                        client.put(file, "/QSYS.LIB/QGPL.LIB/" + iSphereLibrary + ".FILE");
                                        client.disconnect();
                                        ok = true;
                                    }
                                } catch (Throwable e) {
                                    ISpherePlugin.logError(Messages.Could_not_send_save_file_to_host, e);
                                    setStatus(e.getLocalizedMessage());
                                }

                                if (!ok) {
                                    setStatus("!!!   " + Messages.Could_not_send_save_file_to_host + "   !!!");
                                } else {
                                    setStatus(Messages.bind(Messages.Restoring_library_A, iSphereLibrary));
                                    String cpfMsg = executeCommand("RSTLIB SAVLIB(ISPHERE) DEV(*SAVF) SAVF(QGPL/" + iSphereLibrary + ") RSTLIB("
                                        + iSphereLibrary + ")", true);
                                    if (!cpfMsg.equals("")) {
                                        setStatus("!!!   " + Messages.bind(Messages.Could_not_restore_library_A, iSphereLibrary) + "   !!!");
                                    } else {
                                        setStatus("!!!   " + Messages.bind(Messages.Library_A_successfull_transfered, iSphereLibrary) + "   !!!");
                                    }
                                }
                            }
                        }
                        executeCommand("DLTF FILE(QGPL/" + iSphereLibrary + ")");
                    }
                }
            }
        });

        buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonStart.setText(Messages.Start_Transfer);

        tableStatus = new Table(this, SWT.BORDER);
        final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableStatus.setLayoutData(gd_tableStatus);

        final TableColumn columnStatus = new TableColumn(tableStatus, SWT.NONE);
        columnStatus.setWidth(500);
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

    private String executeCommand(String command) {
        return executeCommand(command, false);
    }

    private String executeCommand(String command, boolean logError) {
        try {
            commandCall.run(command);
            AS400Message[] messageList = commandCall.getMessageList();
            if (messageList.length > 0) {
                for (int idx = 0; idx < messageList.length; idx++) {
                    if (messageList[idx].getType() == AS400Message.ESCAPE) {
                        if (logError) {
                            setStatus(messageList[idx].getID() + ": " + messageList[idx].getText());
                        }
                        return messageList[idx].getID();
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "CPF0000";
        }
    }

    public boolean connect() {
        buttonStart.setEnabled(false);
        SignOnDialog signOnDialog = new SignOnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), hostName);
        if (signOnDialog.open() == Dialog.OK) {
            as400 = signOnDialog.getAS400();
            if (as400 != null) {
                try {
                    as400.connectService(AS400.COMMAND);
                    commandCall = new CommandCall(as400);
                    if (commandCall != null) {
                        hostName = as400.getSystemName();
                        setStatus(Messages.bind(Messages.About_to_transfer_library_A_to_host_B_using_port_C,
                            new String[] { iSphereLibrary.trim(), hostName, Integer.toString(ftpPort) }));
                        buttonStart.setEnabled(true);
                        return true;
                    }
                } catch (AS400SecurityException e) {
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

}
