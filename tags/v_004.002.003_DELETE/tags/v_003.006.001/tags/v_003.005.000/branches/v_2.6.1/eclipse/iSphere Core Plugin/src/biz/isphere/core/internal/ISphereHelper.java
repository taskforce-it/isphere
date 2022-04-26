/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.IOException;
import java.text.DecimalFormat;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class ISphereHelper {

    public static boolean checkISphereLibrary(Shell shell, AS400 as400) {

        Boolean isValidLibrary = null;

        String messageId = null;
        try {
            messageId = executeCommand(as400, "CHKOBJ OBJ(QSYS/" + ISpherePlugin.getISphereLibrary() + ") OBJTYPE(*LIB)");
        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        if (messageId == null || !messageId.equals("")) {

            String text = Messages.E_R_R_O_R;
            String message = Messages.bind(Messages.iSphere_library_A_does_not_exist_on_system_B_Please_transfer_iSphere_library_A_to_system_B,
                new String[] { ISpherePlugin.getISphereLibrary(), as400.getSystemName() });
            // .iSphere_library_A_does_not_exist_on_system_B_Please_transfer_iSphere_library_A_to_system_B;
            // message = message.replace("&1",
            // ISpherePlugin.getISphereLibrary());
            // message = message.replace("&2", as400.getSystemName());
            new DisplayMessage(shell, text, message).start();

            isValidLibrary = Boolean.FALSE;
            return isValidLibrary.booleanValue();

        }

        String dataAreaISphereContent = null;
        CharacterDataArea dataAreaISphere = new CharacterDataArea(as400, "/QSYS.LIB/" + ISpherePlugin.getISphereLibrary() + ".LIB/ISPHERE.DTAARA");
        try {
            dataAreaISphereContent = dataAreaISphere.read();
        } catch (AS400SecurityException e) {
            e.printStackTrace();
        } catch (ErrorCompletingRequestException e) {
            e.printStackTrace();
        } catch (IllegalObjectTypeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectDoesNotExistException e) {
            e.printStackTrace();
        }
        if (dataAreaISphereContent == null) {

            String text = Messages.E_R_R_O_R;
            String message = Messages.bind(Messages.Specified_iSphere_library_A_on_System_B_is_not_a_iSphere_library,
                new String[] { ISpherePlugin.getISphereLibrary(), as400.getSystemName() });
            // String message =
            // Messages.Specified_iSphere_library_A_on_System_B_is_not_a_iSphere_library;
            // message = message.replace("&1",
            // ISpherePlugin.getISphereLibrary());
            // message = message.replace("&2", as400.getSystemName());
            new DisplayMessage(shell, text, message).start();

            isValidLibrary = Boolean.FALSE;
            return isValidLibrary.booleanValue();

        }

        String serverProvided = dataAreaISphereContent.substring(7, 13);
        String clientProvided = comparableVersion(ISpherePlugin.getDefault().getVersion());
        String serverNeedsClient = dataAreaISphereContent.substring(21, 27);
        String clientNeedsServer = comparableVersion(ISpherePlugin.getDefault().getMinServerVersion());

        if (serverProvided.compareTo(clientNeedsServer) < 0) {

            String text = Messages.E_R_R_O_R;
            String message = Messages
                .bind(
                    Messages.iSphere_library_A_on_System_B_is_of_version_C_but_at_least_version_D_is_needed_Please_transfer_the_current_iSphere_library_A_to_system_B,
                    new String[] { ISpherePlugin.getISphereLibrary(), as400.getSystemName(), getVersionFormatted(serverProvided),
                        getVersionFormatted(clientNeedsServer) });
            // String message =
            // Messages.iSphere_library_A_on_System_B_is_of_version_C_but_at_least_version_D_is_needed_Please_transfer_the_current_iSphere_library_A_to_system_B;
            // message = message.replace("&1",
            // ISpherePlugin.getISphereLibrary());
            // message = message.replace("&2", as400.getSystemName());
            // message = message.replace("&3",
            // getVersionFormatted(serverProvided));
            // message = message.replace("&4",
            // getVersionFormatted(clientNeedsServer));
            new DisplayMessage(shell, text, message).start();

            isValidLibrary = Boolean.FALSE;
            return isValidLibrary.booleanValue();

        }

        if (clientProvided.compareTo(serverNeedsClient) < 0) {

            String text = Messages.E_R_R_O_R;
            String message = Messages
                .bind(
                    Messages.The_current_installed_iSphere_client_is_of_version_A_but_the_iSphere_server_needs_at_least_version_B_Please_install_the_current_iSphere_client,
                    new String[] { getVersionFormatted(clientProvided), getVersionFormatted(serverNeedsClient) });
            // String message =
            // Messages.The_current_installed_iSphere_client_is_of_version_A_but_the_iSphere_server_needs_at_least_version_B_Please_install_the_current_iSphere_client;
            // message = message.replace("&1",
            // getVersionFormatted(clientProvided));
            // message = message.replace("&2",
            // getVersionFormatted(serverNeedsClient));
            new DisplayMessage(shell, text, message).start();

            isValidLibrary = Boolean.FALSE;
            return isValidLibrary.booleanValue();

        }

        isValidLibrary = Boolean.TRUE;
        return isValidLibrary.booleanValue();

    }

    private static String getVersionFormatted(String aVersionNumber) {
        return Integer.parseInt(aVersionNumber.substring(0, 2)) + "." + Integer.parseInt(aVersionNumber.substring(2, 4)) + "."
            + Integer.parseInt(aVersionNumber.substring(4, 6));
    }

    public static String executeCommand(AS400 as400, String command) throws Exception {

        CommandCall commandCall = new CommandCall(as400);
        commandCall.run(command);
        AS400Message[] messageList = commandCall.getMessageList();
        if (messageList.length > 0) {
            for (int idx = 0; idx < messageList.length; idx++) {
                if (messageList[idx].getType() == AS400Message.ESCAPE) {
                    return messageList[idx].getID();
                }
            }
        }
        return "";
    }

    public static String getCurrentLibrary(AS400 _as400) throws Exception {

        String currentLibrary = null;

        Job[] jobs = _as400.getJobs(AS400.COMMAND);

        if (jobs.length == 1) {

            if (!jobs[0].getCurrentLibraryExistence()) {
                currentLibrary = "*CRTDFT";
            } else {
                currentLibrary = jobs[0].getCurrentLibrary();
            }

        }

        return currentLibrary;

    }

    public static boolean setCurrentLibrary(AS400 _as400, String currentLibrary) throws Exception {

        String command = "CHGCURLIB CURLIB(" + currentLibrary + ")";
        CommandCall commandCall = new CommandCall(_as400);

        if (commandCall.run(command)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Changes a given version string of type "v.r.m" to a comparable version
     * string of type "VVRRMM".
     * 
     * @param version Version String of type "v.r.m".
     * @return Comparable version String.
     */
    public static String comparableVersion(String version) {
        String comparableVersion = version;
        String[] parts = new String[3];
        parts = comparableVersion.split("\\.");
        DecimalFormat formatter = new DecimalFormat("00");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null) {
                parts[i] = formatter.format(0L);
            } else {
                parts[i] = formatter.format(IntHelper.tryParseInt(parts[i], 0));
            }
            if (i == 0) {
                comparableVersion = parts[i];
            } else {
                comparableVersion = comparableVersion + parts[i];
            }
        }
        return comparableVersion;
    }

    public static boolean canTransformSpooledFile(AS400 as400) {

        if (ISpherePlugin.getDefault().getIBMiRelease(as400).compareTo("V5R3M0") <= 0) {
            return false;
        }

        return true;
    }
}
