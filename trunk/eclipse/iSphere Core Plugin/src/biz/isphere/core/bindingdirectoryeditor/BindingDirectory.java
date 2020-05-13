/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;

public class BindingDirectory {

    public static ArrayList<BindingDirectoryEntry> getEntries(String level, AS400 _as400, Connection _jdbcConnection, String _connection,
        String _library, String _bindingDirectory) {

        ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>();

        try {

            SqlHelper sqlHelper = new SqlHelper(_jdbcConnection);

            String command = "DSPBNDDIR BNDDIR(" + _library + "/" + _bindingDirectory
                + ") OUTPUT(*OUTFILE) OUTFILE(QTEMP/XBNDDIRX) OUTMBR(*FIRST *REPLACE)";
            StringBuffer buffer = new StringBuffer(Integer.toString(command.length()));
            int number = 10 - buffer.length();
            for (int idx = 0; idx < number; idx++) {
                buffer.insert(0, "0");
            }
            buffer.append(".00000");
            String commandLength = buffer.toString();

            Statement statement = _jdbcConnection.createStatement();
            statement.executeUpdate("CALL " + sqlHelper.getObjectName("QSYS", "QCMDEXC") + "('" + command + "'," + commandLength + ")");

            PreparedStatement preparedStatementSelect = null;
            ResultSet resultSet = null;
            try {
                preparedStatementSelect = _jdbcConnection.prepareStatement("SELECT * FROM " + sqlHelper.getObjectName("QTEMP", "XBNDDIRX"),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = preparedStatementSelect.executeQuery();

                resultSet.beforeFirst();
                while (resultSet.next()) {

                    BindingDirectoryEntry entry = new BindingDirectoryEntry();
                    entry.setConnection(_connection);
                    entry.setLibrary(resultSet.getString("BNOLNM").trim());
                    entry.setObject(resultSet.getString("BNOBNM").trim());
                    entry.setObjectType(resultSet.getString("BNOBTP").trim());
                    if (level.compareTo("V6R1M0") >= 0) {
                        entry.setActivation(resultSet.getString("BNOACT").trim());
                    }

                    _bindingDirectoryEntries.add(entry);

                }

            } catch (SQLException e) {
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                }
            }
            if (preparedStatementSelect != null) {
                try {
                    preparedStatementSelect.close();
                } catch (SQLException e1) {
                }
            }

        } catch (SQLException e) {
        }

        return _bindingDirectoryEntries;

    }

    public static boolean addEntries(String level, AS400 _as400, String _library, String _bindingDirectory, ArrayList<BindingDirectoryEntry> entries) {

        if (entries.size() > 0) {

            for (int idx = 0; idx < entries.size(); idx++) {

                BindingDirectoryEntry entry = entries.get(idx);

                if (!entry.isMatch()) {

                    StringBuffer buffer = new StringBuffer("ADDBNDDIRE BNDDIR(" + _library + "/" + _bindingDirectory + ") OBJ(");
                    buffer.append("(" + entry.getLibrary() + "/" + entry.getObject() + " " + entry.getObjectType()
                        + (level.compareTo("V6R1M0") >= 0 && entry.getObjectType().equals("*SRVPGM") ? " " + entry.getActivation() : "") + ")");
                    buffer.append(")");

                    if (!runCommand(_as400, buffer.toString())) {
                        ISpherePlugin.logError("Could not execute command: " + buffer.toString(), null);
                    }

                }

            }

        }

        return true;

    }

    public static boolean removeEntries(String level, AS400 _as400, String _library, String _bindingDirectory,
        ArrayList<BindingDirectoryEntry> entries) {

        if (entries.size() > 0) {

            for (int idx = 0; idx < entries.size(); idx++) {

                BindingDirectoryEntry entry = entries.get(idx);

                if (!entry.isMatch()) {

                    StringBuffer buffer = new StringBuffer("RMVBNDDIRE BNDDIR(" + _library + "/" + _bindingDirectory + ") OBJ(");
                    buffer.append("(" + entry.getLibrary() + "/" + entry.getObject() + " " + entry.getObjectType() + ")");
                    buffer.append(")");

                    if (!runCommand(_as400, buffer.toString())) {
                        ISpherePlugin.logError("Could not execute command: " + buffer.toString(), null);
                    }

                }

            }

        }

        return true;

    }

    public static boolean saveChanges(String level, AS400 _as400, Connection _jdbcConnection, String _connection, String _library,
        String _bindingDirectory, ArrayList<BindingDirectoryEntry> entriesNew) {

        ArrayList<BindingDirectoryEntry> entriesOld = getEntries(level, _as400, _jdbcConnection, _connection, _library, _bindingDirectory);

        for (int idxNew = 0; idxNew < entriesNew.size(); idxNew++) {
            BindingDirectoryEntry entryNew = entriesNew.get(idxNew);
            entryNew.setMatch(false);
        }

        for (int idxOld = 0; idxOld < entriesOld.size(); idxOld++) {
            BindingDirectoryEntry entryOld = entriesOld.get(idxOld);
            entryOld.setMatch(false);
        }

        for (int idxNew = 0; idxNew < entriesNew.size(); idxNew++) {

            BindingDirectoryEntry entryNew = entriesNew.get(idxNew);

            if (!entryNew.isMatch()) {

                for (int idxOld = 0; idxOld < entriesOld.size(); idxOld++) {

                    BindingDirectoryEntry entryOld = entriesOld.get(idxOld);

                    if (!entryOld.isMatch()) {

                        if (entryOld.getLibrary().equals(entryNew.getLibrary()) && entryOld.getObject().equals(entryNew.getObject())
                            && entryOld.getObjectType().equals(entryNew.getObjectType())) {
                            if (level.compareTo("V6R1M0") >= 0 && entryOld.getObjectType().equals("*SRVPGM")) {
                                if (entryOld.getActivation().equals(entryNew.getActivation())) {
                                    entryOld.setMatch(true);
                                    entryNew.setMatch(true);
                                }
                            } else {
                                entryOld.setMatch(true);
                                entryNew.setMatch(true);
                            }
                            break;
                        }

                    }

                }

            }

        }

        System.out.println("Remove: #" + entriesOld.size() + ", added: #" + entriesNew.size());

        if (!BindingDirectory.removeEntries(level, _as400, _library, _bindingDirectory, entriesOld)) {
            return false;
        } else {
            if (!BindingDirectory.addEntries(level, _as400, _library, _bindingDirectory, entriesNew)) {
                return false;
            }
        }

        return true;
    }

    public static boolean runCommand(AS400 as400, String command) {

        CommandCall commandCall = new CommandCall(as400);
        try {
            if (!commandCall.run(command)) {
                AS400Message[] messageList = commandCall.getMessageList();
                if (messageList.length > 0) {
                    showErrorMessage(messageList[0].getText());
                    return false;
                } else {
                    showErrorMessage(Messages.Unknown_error_occured);
                    return false;
                }
            }
        } catch (AS400SecurityException e) {
            showErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (ErrorCompletingRequestException e) {
            showErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (IOException e) {
            showErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (InterruptedException e) {
            showErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (PropertyVetoException e) {
            showErrorMessage(Messages.Unknown_error_occured);
            return false;
        }

        return true;

    }

    public static void showErrorMessage(String message) {

        MessageBox errorBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
        errorBox.setText(Messages.E_R_R_O_R);
        errorBox.setMessage(message);
        errorBox.open();

    }

}
