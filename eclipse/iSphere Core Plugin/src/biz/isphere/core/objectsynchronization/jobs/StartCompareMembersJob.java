/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.SYNCMBR_getHandle;

/**
 * This class is the first job of the synchronize members task. It stores the
 * objects (file or library) that are compared in file SYNCMBRW.
 * <p>
 * The compare members jobs are executed in the following sequence:
 * <ol>
 * <li>{@link StartCompareMembersJob}
 * <li>{@link ResolveGenericCompareElementsJob}
 * <li>{@link UpdateCompareElementsJob}
 * <li>{@link LoadCompareMembersJob}
 * <li>{@link FinishCompareMembersJob}
 * </ol>
 */
public class StartCompareMembersJob extends AbstractCompareMembersJob {

    private static final String ALL_OBJECTS = "*";

    private RemoteObject leftObject;
    private RemoteObject rightObject;
    private String objectType;

    public StartCompareMembersJob(IProgressMonitor monitor, CompareMembersSharedJobValues sharedValues, RemoteObject leftObject,
        RemoteObject rightObject) {
        super(monitor, sharedValues);

        this.leftObject = leftObject;
        this.rightObject = rightObject;

        this.objectType = leftObject.getObjectType();
    }

    public int getWorkCount() {
        return 2;
    }

    public int execute(int worked) {

        CompareMembersSharedJobValues sharedValues = getSharedValues();

        sharedValues.setLeftHandle(null, ERROR_HANDLE);
        sharedValues.setRightHandle(null, ERROR_HANDLE);

        getMonitor().setTaskName(Messages.Setting_compare_items);

        int handle = doSystem(leftObject.getConnectionName(), SyncMbrMode.LEFT_SYSTEM);
        if (handle != ERROR_HANDLE) {
            sharedValues.setLeftHandle(leftObject.getConnectionName(), handle);
            worked++;
            handle = doSystem(rightObject.getConnectionName(), SyncMbrMode.RIGHT_SYSTEM);
            if (handle != ERROR_HANDLE) {
                sharedValues.setRightHandle(rightObject.getConnectionName(), handle);
                worked++;
            } else {
                ISpherePlugin.logError("*** Could not get SYNCMBR handle for *RIGHT system ***", null);
            }
        } else {
            ISpherePlugin.logError("*** Could not get SYNCMBR handle for *LEFT system ***", null);
        }

        return worked;
    }

    private int doSystem(String connectionName, SyncMbrMode mode) {

        initialize(connectionName);

        try {

            if (!setCurrentLibrary()) {
                return ERROR_HANDLE;
            }

            int handle = new SYNCMBR_getHandle().run(getSystem());
            if (handle <= 0) {
                return ERROR_HANDLE;
            }

            String sqlInsert;
            if (SyncMbrMode.LEFT_SYSTEM.equals(mode)) {
                sqlInsert = getSqlInsertLeftSystems(handle);
            } else if (SyncMbrMode.RIGHT_SYSTEM.equals(mode)) {
                sqlInsert = getSqlInsertRightSystems(handle);
            } else {
                throw new IllegalArgumentException("Unsupported mode value: " + mode.mode()); //$NON-NLS-1$
            }

            Statement statementInsert = null;

            try {
                statementInsert = getJdbcConnection().createStatement();
                statementInsert.executeUpdate(sqlInsert);
            } catch (SQLException e) {
                ISpherePlugin.logError("*** Could not insert compare elements into SYNCMBRW ***", e); //$NON-NLS-1$
            }

            if (statementInsert != null) {
                try {
                    statementInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return handle;

        } finally {
            restoreCurrentLibrary();
        }
    }

    private String getSqlInsertLeftSystems(int handle) {

        StringBuffer sqlInsert = new StringBuffer();
        sqlInsert
            .append("INSERT INTO " + getSqlHelper().getObjectName(getISphereLibrary(), "SYNCMBRW") + " (XWHDL, XWMBR, XWLEFTLIB, XWLEFTFILE) VALUES");

        sqlInsert.append("('");
        sqlInsert.append(handle);
        sqlInsert.append("', '");
        sqlInsert.append(ALL_OBJECTS); // All members.

        if (ISeries.FILE.equals(objectType)) {
            sqlInsert.append("', '");
            sqlInsert.append(leftObject.getLibrary()); // Left library.
            sqlInsert.append("', '");
            sqlInsert.append(leftObject.getName()); // Left source file.
        } else if (ISeries.LIB.equals(objectType)) {
            sqlInsert.append("', '");
            sqlInsert.append(leftObject.getName()); // Left library.
            sqlInsert.append("', '");
            sqlInsert.append(ALL_OBJECTS); // All left source files.
        } else {
            throw new IllegalArgumentException("Incorrect object type: " + objectType);
        }

        sqlInsert.append("')");

        return sqlInsert.toString();
    }

    private String getSqlInsertRightSystems(int handle) {

        StringBuffer sqlInsert = new StringBuffer();
        sqlInsert
            .append("INSERT INTO " + getSqlHelper().getObjectName(getISphereLibrary(), "SYNCMBRW") + " (XWHDL, XWMBR, XWRGHTLIB, XWRGHTFILE) VALUES");

        sqlInsert.append("('");
        sqlInsert.append(handle);
        sqlInsert.append("', '");
        sqlInsert.append(ALL_OBJECTS); // All members.

        if (ISeries.FILE.equals(objectType)) {
            sqlInsert.append("', '");
            sqlInsert.append(rightObject.getLibrary()); // Right library.
            sqlInsert.append("', '");
            sqlInsert.append(rightObject.getName()); // Right source file.
        } else if (ISeries.LIB.equals(objectType)) {
            sqlInsert.append("', '");
            sqlInsert.append(rightObject.getName()); // Right library.
            sqlInsert.append("', '");
            sqlInsert.append(ALL_OBJECTS); // All right files.
        } else {
            throw new IllegalArgumentException("Incorrect object type: " + objectType);
        }

        sqlInsert.append("')");

        return sqlInsert.toString();
    }

    @Override
    protected boolean isSameSystem() {

        boolean isSameSystem;
        // TODO: fix compare to getSystemName()
        //@formatter:off
        // if (leftObject.getSystem().getSystemName().equals(rightObject.getSystem().getSystemName())) {
        //@formatter:on
        if (leftObject.getConnectionName().equals(rightObject.getConnectionName())) {
            isSameSystem = true;
        } else {
            isSameSystem = false;
        }

        getSharedValues().setSameSystem(isSameSystem);

        return isSameSystem;
    }
}