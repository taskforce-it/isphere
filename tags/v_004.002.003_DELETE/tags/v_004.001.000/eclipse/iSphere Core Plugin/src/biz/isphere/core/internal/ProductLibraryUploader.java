/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.FTP;

public class ProductLibraryUploader {

    private Shell shell;
    private AS400 system;
    private int ftpPort;
    private String iSphereLibrary;
    private String aspGroup;

    private CommandCall commandCall;

    private StatusMessageReceiver statusMessageReceiver;

    public ProductLibraryUploader(Shell shell, AS400 system, int ftpPort, String iSphereLibrary, String aspGroup) {

        this.shell = shell;
        this.system = system;
        this.ftpPort = ftpPort;
        this.iSphereLibrary = iSphereLibrary;
        this.aspGroup = aspGroup;
    }

    public void setStatusMessageReceiver(StatusMessageReceiver statusMessageReceiver) {
        this.statusMessageReceiver = statusMessageReceiver;
    }

    public boolean run() {

        boolean successfullyTransfered = false;

        try {

            String workLibrary = "QGPL";
            String saveFileName = iSphereLibrary;

            boolean ok = true;
            if (ISphereHelper.isASPGroupSpecified(aspGroup)) {
                String cpfMsg = executeCommand("SETASPGRP ASPGRP(" + aspGroup + ")", true);
                if (!cpfMsg.equals("")) {
                    setStatus(Messages.bind(Messages.Error_occurred_while_setting_the_asp_group_to_A, aspGroup));
                    ok = false;
                }
            }
            if (ok) {
                setStatus(Messages.bind(Messages.Checking_library_A_for_existence, iSphereLibrary));
                if (!checkLibraryPrecondition(iSphereLibrary, aspGroup)) {
                    setStatus("!!!   " + Messages.bind(Messages.Library_A_does_already_exist, iSphereLibrary) + "   !!!");
                } else {
                    setStatus(Messages.bind(Messages.Checking_file_B_in_library_A_for_existence, new String[] { workLibrary, saveFileName }));
                    if (!checkSaveFilePrecondition(workLibrary, saveFileName)) {
                        setStatus("!!!   "
                            + Messages.bind(Messages.File_B_in_library_A_does_already_exist, new String[] { workLibrary, saveFileName }) + "   !!!");
                    } else {

                        setStatus(Messages.bind(Messages.Creating_save_file_B_in_library_A, new String[] { workLibrary, saveFileName }));
                        if (!createSaveFile(workLibrary, saveFileName)) {
                            setStatus("!!!   "
                                + Messages.bind(Messages.Could_not_create_save_file_B_in_library_A, new String[] { workLibrary, saveFileName })
                                + "   !!!");
                        } else {

                            try {

                                setStatus(Messages.Sending_save_file_to_host);
                                setStatus(Messages.bind(Messages.Using_Ftp_port_number, new Integer(ftpPort)));
                                AS400FTP client = new AS400FTP(system);

                                URL fileUrl = FileLocator.toFileURL(ISpherePlugin.getInstallURL());
                                File file = new File(fileUrl.getPath() + "Server" + File.separator + "ISPHERE.SAVF");
                                client.setPort(ftpPort);
                                client.setDataTransferType(FTP.BINARY);
                                if (client.connect()) {
                                    client.put(file, "/QSYS.LIB/" + workLibrary + ".LIB/" + saveFileName + ".FILE");
                                    client.disconnect();
                                }

                                setStatus(Messages.bind(Messages.Restoring_library_A, iSphereLibrary));
                                if (!restoreLibrary(workLibrary, saveFileName, iSphereLibrary, aspGroup)) {
                                    setStatus("!!!   " + Messages.bind(Messages.Could_not_restore_library_A, iSphereLibrary) + "   !!!");
                                } else {
                                    successfullyTransfered = true;
                                }

                            } catch (Throwable e) {
                                setError(ExceptionHelper.getLocalizedMessage(e), e);
                                setStatus("!!!   " + Messages.Could_not_send_save_file_to_host + "   !!!");
                            } finally {

                                setStatus(Messages.bind(Messages.Deleting_object_A_B_of_type_C, new String[] { workLibrary, saveFileName, "*FILE" }));
                                deleteSaveFile(workLibrary, saveFileName);
                            }

                        }
                    }
                }
            }

        } finally {

            if (successfullyTransfered) {
                setStatus("!!!   " + Messages.bind(Messages.Library_A_successfull_transfered, iSphereLibrary) + "   !!!");
            } else {
                setStatus("!!!   " + Messages.bind(Messages.Error_occurred_while_transfering_library_A, iSphereLibrary) + "   !!!");
            }
        }

        return successfullyTransfered;
    }

    private boolean checkLibraryPrecondition(String iSphereLibrary, String aspGroup) {

        while (libraryExists(iSphereLibrary)) {
            if (!MessageDialog.openQuestion(
                getShell(),
                Messages.msgBox_headline_Delete_Object,
                Messages.bind(Messages.Library_A_does_already_exist, iSphereLibrary) + "\n\n"
                    + Messages.bind(Messages.Do_you_want_to_delete_library_A, iSphereLibrary))) {
                return false;
            }
            setStatus(Messages.bind(Messages.Deleting_library_A, iSphereLibrary));
            deleteLibrary(iSphereLibrary, aspGroup);
        }

        return true;
    }

    private boolean libraryExists(String iSphereLibrary) {

        if (!ISphereHelper.checkLibrary(system, iSphereLibrary)) {
            return false;
        }

        return true;
    }

    private boolean deleteLibrary(String iSphereLibrary, String aspGroup) {

        if (!executeCommand(produceDeleteLibraryCommand(iSphereLibrary, aspGroup), true).equals("")) {
            return false;
        }

        return true;
    }

    private String produceDeleteLibraryCommand(String iSphereLibrary, String aspGroup) {

        String command = "DLTLIB LIB(" + iSphereLibrary + ")";
        if (ISphereHelper.isASPGroupSpecified(aspGroup)) {
            command += " ASPDEV(*)";
        }

        return command;
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
            deleteSaveFile(workLibrary, saveFileName);
        }

        return true;
    }

    private boolean saveFileExists(String workLibrary, String saveFileName) {

        if (!ISphereHelper.checkObject(system, workLibrary, saveFileName, "*FILE")) {
            return false;
        }

        return true;
    }

    private boolean deleteSaveFile(String workLibrary, String saveFileName) {

        if (!executeCommand("DLTF FILE(" + workLibrary + "/" + saveFileName + ")", true).equals("")) {
            return false;
        }

        return true;
    }

    private boolean createSaveFile(String workLibrary, String saveFileName) {

        if (!executeCommand("CRTSAVF FILE(" + workLibrary + "/" + saveFileName + ") TEXT('iSphere')", true).equals("")) {
            return false;
        }

        return true;
    }

    private boolean restoreLibrary(String workLibrary, String saveFileName, String iSphereLibrary, String aspGroup) {

        String cpfMsg = executeCommand(produceRestoreLibraryCommand(workLibrary, saveFileName, iSphereLibrary, aspGroup), true);
        if (!cpfMsg.equals("")) {
            return false;
        }

        return true;
    }

    private String produceRestoreLibraryCommand(String workLibrary, String saveFileName, String iSphereLibrary, String aspGroup) {

        String command = "RSTLIB SAVLIB(ISPHERE) DEV(*SAVF) SAVF(" + workLibrary + "/" + saveFileName + ") RSTLIB(" + iSphereLibrary + ")";
        if (ISphereHelper.isASPGroupSpecified(aspGroup)) {
            command += " RSTASPDEV(" + aspGroup + ")";
        }

        return command;
    }

    private String executeCommand(String command, boolean logError) {

        if (commandCall == null) {
            commandCall = new CommandCall(system);
        }

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

    private Shell getShell() {
        return shell;
    }

    private void setStatus(String message) {
        statusMessageReceiver.setStatus(message);
    }

    private void setError(String message, Throwable e) {
        setError(message);
        ISpherePlugin.logError(message, e);
    }

    private void setError(String message) {
        statusMessageReceiver.setStatus(Messages.E_R_R_O_R + ": " + message);
    }
}
