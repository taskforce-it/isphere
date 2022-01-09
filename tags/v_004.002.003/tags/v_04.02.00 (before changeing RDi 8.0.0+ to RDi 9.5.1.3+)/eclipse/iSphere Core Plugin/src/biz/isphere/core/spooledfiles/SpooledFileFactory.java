/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.base.internal.SqlHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.preferences.Preferences;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.RequestNotSupportedException;

public class SpooledFileFactory {

    public SpooledFile getSpooledFile(String connectionName, String splfName, int splfNumber, String jobName, String userName, String jobNumber)
        throws AS400Exception, AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException,
        RequestNotSupportedException {
        return getSpooledFile(connectionName, splfName, splfNumber, jobName, userName, jobNumber, null, null);
    }

    public SpooledFile getSpooledFile(String connectionName, String splfName, int splfNumber, String jobName, String userName, String jobNumber,
        String systemName, Date creationTimestamp) throws AS400Exception, AS400SecurityException, ErrorCompletingRequestException, IOException,
        InterruptedException, RequestNotSupportedException {

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        com.ibm.as400.access.SpooledFile toolboxSpooledFile;
        if (creationTimestamp == null) {
            toolboxSpooledFile = new com.ibm.as400.access.SpooledFile(system, splfName, splfNumber, jobName, userName, jobNumber);
        } else {
            String creationDate = IBMiHelper.dateToCyymmdd(creationTimestamp, null);
            String creationTime = IBMiHelper.timeToHhmmss(creationTimestamp, null);
            toolboxSpooledFile = new com.ibm.as400.access.SpooledFile(system, splfName, splfNumber, jobName, userName, jobNumber, systemName,
                creationDate, creationTime);
        }

        if (toolboxSpooledFile.getCreateDate() == null || toolboxSpooledFile.getCreateDate().length() == 0) {
            return null;
        }

        SpooledFile spooledFile = new SpooledFile();
        spooledFile.setAS400(toolboxSpooledFile.getSystem());
        spooledFile.setFile(toolboxSpooledFile.getName());
        spooledFile.setFileNumber(toolboxSpooledFile.getNumber());
        spooledFile.setJobName(toolboxSpooledFile.getJobName());
        spooledFile.setJobUser(toolboxSpooledFile.getJobUser());
        spooledFile.setJobNumber(toolboxSpooledFile.getJobNumber());
        spooledFile.setJobSystem(toolboxSpooledFile.getJobSysName());
        spooledFile.setCreationTimestamp(getCreationDate(toolboxSpooledFile), getCreationTime(toolboxSpooledFile));
        spooledFile.setStatus(toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_SPLFSTATUS));
        spooledFile.setOutputQueue(toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPUT_QUEUE));

        QSYSObjectPathName outQPathName = getOutputQueue(toolboxSpooledFile);
        if (outQPathName != null) {
            spooledFile.setOutputQueue(outQPathName.getObjectName());
            spooledFile.setOutputQueueLibrary(outQPathName.getLibraryName());
        }
        spooledFile.setOutputPriority(toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPTY));
        spooledFile.setUserData(toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_USERDATA));
        spooledFile.setFormType(toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_FORMTYPE));
        spooledFile.setCopies(toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_COPIES));
        spooledFile.setPages(toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_PAGES));
        spooledFile.setCurrentPage(0);
        spooledFile.setConnectionName(connectionName);

        return spooledFile;
    }

    public static SpooledFile[] getSpooledFiles(Shell shell, String connectionName, Connection jdbcConnection, SpooledFileFilter filter) {

        if (ISphereHelper.checkISphereLibrary(shell, connectionName)) {
            return getSpooledFiles(connectionName, jdbcConnection, filter);
        }

        return new SpooledFile[0];
    }

    /**
     * Loads and returns an unsorted list of spooled files as provided by the
     * IBM API.
     * 
     * @param connectionName - name of the RSE connection
     * @param jdbcConnection - Jdbc connection for loading the spooled files
     * @param filter - filter data that is passed to the API
     * @return unsorted but filtered list of spooled files
     */
    public static synchronized SpooledFile[] getSpooledFiles(String connectionName, Connection jdbcConnection, SpooledFileFilter filter) {

        AS400 as400 = IBMiHostContributionsHandler.getSystem(connectionName);
        String iSphereLibrary = ISpherePlugin.getISphereLibrary(connectionName);

        String currentLibrary = null;
        try {
            currentLibrary = ISphereHelper.getCurrentLibrary(as400);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not retrieve current library ***", e);
        }

        if (currentLibrary != null) {

            boolean ok = false;
            try {
                ok = ISphereHelper.setCurrentLibrary(as400, iSphereLibrary);
            } catch (Exception e1) {
                ISpherePlugin.logError("*** Could not set current library to: " + iSphereLibrary + " ***", e1);
            }

            if (ok) {

                SpooledFile[] _spooledFiles = null;

                try {

                    new SPLF_prepare().run(as400);

                    final int maxNumSpooledFilesToLoad = Preferences.getInstance().getSpooledFilesMaxFilesToLoad();
                    new SPLF_setMaxNumSplF().run(as400, maxNumSpooledFilesToLoad);

                    if (filter.getJobName() != null) {
                        new SPLF_setJob().run(as400, filter.getJobName(), filter.getUser(), filter.getJobNumber());
                    } else {

                        /*
                         * IBM documentation link:
                         * https://www.ibm.com/support/knowledgecenter
                         * /ssw_ibm_i_74/apis/QUSLSPL.htm
                         */

                        // not allowed with qualified job name
                        if (filter.getUser() != null) {
                            new SPLF_setUser().run(as400, filter.getUser());
                        }

                        // not allowed with qualified job name
                        if (filter.getOutputQueue() != null) {
                            String library;
                            if ("*ALL".equals(filter.getOutputQueue())) {
                                library = "";
                            } else {
                                if (filter.getOutputQueueLibrary() != null) {
                                    library = filter.getOutputQueueLibrary();
                                } else {
                                    library = "*LIBL";
                                }
                            }
                            new SPLF_setOutputQueue().run(as400, filter.getOutputQueue(), library);
                        }

                        // not allowed with qualified job name
                        if (filter.getUserData() != null) {
                            new SPLF_setUserData().run(as400, filter.getUserData());
                        }

                        // not allowed with qualified job name
                        if (filter.getFormType() != null) {
                            new SPLF_setFormType().run(as400, filter.getFormType());
                        }
                    }

                    if (filter.getName() != null) {
                        new SPLF_setName().run(as400, filter.getName());
                    }

                    if (filter.getStartingDate() != null || filter.getEndingDate() != null) {
                        int startDate = filter.getStartingDateIntValue();
                        int startTime = filter.getStartingTimeIntValue();
                        int endDate = filter.getEndingDateIntValue();
                        int endTime = filter.getEndingTimeIntValue();
                        new SPLF_setDateTime().run(as400, startDate, startTime, endDate, endTime);
                    }

                    int handle = new SPLF_build().run(as400);

                    if (handle > 0) {

                        SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

                        ArrayList<SpooledFile> arrayListSpooledFiles = new ArrayList<SpooledFile>();

                        PreparedStatement preparedStatementSelect = null;
                        ResultSet resultSet = null;

                        try {

                            preparedStatementSelect = jdbcConnection.prepareStatement(
                                "SELECT * FROM " + sqlHelper.getObjectName(iSphereLibrary, "SPLF") + " WHERE SFHDL = ? ORDER BY SFHDL, SFCNT",
                                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            preparedStatementSelect.setString(1, Integer.toString(handle));
                            resultSet = preparedStatementSelect.executeQuery();

                            while (resultSet.next()) {

                                if (maxNumSpooledFilesToLoad > 0 && arrayListSpooledFiles.size() >= maxNumSpooledFilesToLoad) {

                                    Display.getDefault().syncExec(new Runnable() {
                                        public void run() {
                                            DoNotAskMeAgainDialog.openInformation(Display.getDefault().getActiveShell(),
                                                DoNotAskMeAgain.TOO_MANY_SPOOLED_FILES_WARNING, Messages.bind(
                                                    Messages.Number_of_spooled_files_exceeds_maximum_number_of_spooled_files_to_load_A,
                                                    maxNumSpooledFilesToLoad));
                                        }
                                    });

                                    break;
                                }

                                SpooledFile _spooledFile = new SpooledFile();
                                _spooledFile.setAS400(as400);
                                _spooledFile.setFile(resultSet.getString("SFSPLF").trim());
                                _spooledFile.setFileNumber(resultSet.getInt("SFSPLFNBR"));
                                _spooledFile.setJobName(resultSet.getString("SFJOBNAME").trim());
                                _spooledFile.setJobUser(resultSet.getString("SFJOBUSR").trim());
                                _spooledFile.setJobNumber(resultSet.getString("SFJOBNBR").trim());
                                _spooledFile.setJobSystem(resultSet.getString("SFJOBSYS").trim());
                                _spooledFile.setCreationTimestamp(resultSet.getDate("SFCRTDATE"), resultSet.getTime("SFCRTTIME"));
                                _spooledFile.setStatus(resultSet.getString("SFSTS").trim());
                                _spooledFile.setOutputQueue(resultSet.getString("SFOUTQ").trim());
                                _spooledFile.setOutputQueueLibrary(resultSet.getString("SFOUTQLIB").trim());
                                _spooledFile.setOutputPriority(resultSet.getString("SFOUTPTY").trim());
                                _spooledFile.setUserData(resultSet.getString("SFUSRDTA").trim());
                                _spooledFile.setFormType(resultSet.getString("SFFORMTYPE").trim());
                                _spooledFile.setCopies(resultSet.getInt("SFCOPIES"));
                                _spooledFile.setPages(resultSet.getInt("SFPAGES"));
                                _spooledFile.setCurrentPage(0);
                                _spooledFile.setConnectionName(connectionName);

                                arrayListSpooledFiles.add(_spooledFile);

                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        if (resultSet != null) {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        if (preparedStatementSelect != null) {
                            try {
                                preparedStatementSelect.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        _spooledFiles = new SpooledFile[arrayListSpooledFiles.size()];
                        arrayListSpooledFiles.toArray(_spooledFiles);

                        new SPLF_clear().run(as400, handle);

                    } else {
                        String message = new SPLF_getErrorMessage().run(as400);
                        if (!StringHelper.isNullOrEmpty(message)) {
                            MessageDialogAsync.displayError(message);
                        }
                    }

                } finally {

                    try {
                        ISphereHelper.setCurrentLibrary(as400, currentLibrary);
                    } catch (Exception e) {
                        ISpherePlugin.logError("*** Could not restore current library to: " + currentLibrary + " ***", e);
                    }
                }

                if (_spooledFiles != null) {
                    return _spooledFiles;
                }

            }

        }

        return new SpooledFile[0];

    }

    private Date getCreationDate(com.ibm.as400.access.SpooledFile spooledFile) {

        String splfDate = spooledFile.getCreateDate();

        return IBMiHelper.cyymmddToDate(splfDate);
    }

    private static Time getCreationTime(com.ibm.as400.access.SpooledFile spooledFile) {

        String splfTime = spooledFile.getCreateTime();

        return new Time(IBMiHelper.hhmmssToTime(splfTime).getTime());
    }

    private QSYSObjectPathName getOutputQueue(com.ibm.as400.access.SpooledFile spooledFile) {

        try {
            QSYSObjectPathName outQPathName = new QSYSObjectPathName(spooledFile.getStringAttribute(PrintObject.ATTR_OUTPUT_QUEUE));
            return outQPathName;
        } catch (Throwable e) {
            return null;
        }
    }

}
