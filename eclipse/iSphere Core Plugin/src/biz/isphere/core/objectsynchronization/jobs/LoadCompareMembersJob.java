/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.eclipse.core.runtime.SubMonitor;

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.objectsynchronization.MemberDescription;

/**
 * This class loads the members that are compared from file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class LoadCompareMembersJob extends AbstractCompareMembersJob {

    private static final MemberDescription[] EMPTY_RESULT = new MemberDescription[0];

    private MemberDescription[] leftMembers;
    private MemberDescription[] rightMembers;

    public LoadCompareMembersJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues) {
        super(monitor, sharedValues);
    }

    protected int getNumWorkItems() {
        return 2;
    }

    @Override
    protected void execute(SubMonitor monitor) {

        SubMonitor subMonitor = split(monitor, 2);

        try {

            CompareMembersSharedJobValues sharedValues = getSharedValues();

            consume(subMonitor, Messages.Task_Loading_compare_data);
            leftMembers = loadMemberDescriptions(subMonitor, sharedValues.getLeftConnectionName(), sharedValues.getLeftHandle(),
                SyncMbrMode.LEFT_SYSTEM);

            consume(subMonitor, Messages.Task_Loading_compare_data);
            rightMembers = loadMemberDescriptions(subMonitor, sharedValues.getRightConnectionName(), sharedValues.getRightHandle(),
                SyncMbrMode.RIGHT_SYSTEM);

        } finally {
            done(subMonitor);
        }
    }

    public MemberDescription[] getLeftMembers() {
        return leftMembers;
    }

    public MemberDescription[] getRightMembers() {
        return rightMembers;
    }

    private MemberDescription[] loadMemberDescriptions(SubMonitor subMonitor, String connectionName, int handle, SyncMbrMode mode) {

        if (!initialize(connectionName)) {
            return EMPTY_RESULT;
        }

        try {

            if (subMonitor.isCanceled()) {
                return EMPTY_RESULT;
            }

            if (!setCurrentLibrary()) {
                return EMPTY_RESULT;
            }

            return doLoadMemberDescriptions(connectionName, handle, mode);

        } finally {
            restoreCurrentLibrary();
        }
    }

    private MemberDescription[] doLoadMemberDescriptions(String connectionName, int handle, SyncMbrMode mode) {

        ArrayList<MemberDescription> arrayListSearchResults = new ArrayList<MemberDescription>();

        Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
        SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

        PreparedStatement preparedStatementSelect = null;
        ResultSet resultSet = null;

        try {

            if (handle == ERROR_HANDLE) {
                return EMPTY_RESULT;
            }

            final int LIBRARY = 1;
            final int FILE = 2;
            final int MEMBER = 3;
            final int SRC_TYPE = 4;
            final int LAST_CHANGED = 5;
            final int CHECKSUM = 6;
            final int TEXT = 7;

            if (SyncMbrMode.LEFT_SYSTEM.equals(mode)) {
                preparedStatementSelect = jdbcConnection.prepareStatement(
                    "SELECT XWLEFTLIB, XWLEFTFILE, XWMBR, XWLEFTTYPE, XWLEFTLCHG, XWLEFTCRC, XWLEFTTEXT FROM "
                        + sqlHelper.getObjectName(getISphereLibrary(), "SYNCMBRW")
                        + " WHERE XWHDL = ? ORDER BY XWHDL, XWLEFTLIB, XWLEFTFILE, XWMBR, XWLEFTTYPE",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } else if (SyncMbrMode.RIGHT_SYSTEM.equals(mode)) {
                preparedStatementSelect = jdbcConnection.prepareStatement(
                    "SELECT XWRGHTLIB, XWRGHTFILE, XWMBR, XWRGHTTYPE, XWRGHTLCHG, XWRGHTCRC, XWRGHTTEXT FROM "
                        + sqlHelper.getObjectName(getISphereLibrary(), "SYNCMBRW")
                        + " WHERE XWHDL = ? ORDER BY XWHDL, XWRGHTLIB, XWRGHTFILE, XWMBR, XWRGHTTYPE",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } else {
                throw new IllegalArgumentException("Incorrect mode: " + mode.mode()); //$NON-NLS-1$
            }

            preparedStatementSelect.setString(1, Integer.toString(handle));
            resultSet = preparedStatementSelect.executeQuery();

            String library;
            String file;
            String member;
            String srcType;
            Timestamp lastChanged;
            long checksum;
            String text;

            while (resultSet.next()) {

                library = resultSet.getString(LIBRARY).trim();
                file = resultSet.getString(FILE).trim();
                member = resultSet.getString(MEMBER).trim();
                srcType = resultSet.getString(SRC_TYPE).trim();
                lastChanged = resultSet.getTimestamp(LAST_CHANGED);
                checksum = resultSet.getLong(CHECKSUM);
                text = resultSet.getString(TEXT);

                MemberDescription memberDescription = new MemberDescription();
                memberDescription.setConnectionName(connectionName);
                memberDescription.setLibraryName(library);
                memberDescription.setFileName(file);
                memberDescription.setMemberName(member);
                memberDescription.setSourceType(srcType);
                memberDescription.setLastChangedDate(lastChanged);
                memberDescription.setChecksum(checksum);
                memberDescription.setText(text);

                arrayListSearchResults.add(memberDescription);

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

        return arrayListSearchResults.toArray(new MemberDescription[arrayListSearchResults.size()]);
    }

}
