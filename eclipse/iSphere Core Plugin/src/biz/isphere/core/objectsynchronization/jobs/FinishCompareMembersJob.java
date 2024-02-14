/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import org.eclipse.core.runtime.SubMonitor;

import biz.isphere.core.Messages;
import biz.isphere.core.objectsynchronization.SYNCMBR_clear;

/**
 * This class cleans up the data stored in file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class FinishCompareMembersJob extends AbstractCompareMembersJob {

    public FinishCompareMembersJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues) {
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

            if (sharedValues.getLeftHandle() != ERROR_HANDLE) {
                consume(subMonitor, Messages.Task_Cleaning_up);
                cleanupCompareData(sharedValues.getLeftConnectionName(), sharedValues.getLeftHandle());
            }

            if (sharedValues.getRightHandle() != ERROR_HANDLE) {
                consume(subMonitor, Messages.Task_Cleaning_up);
                cleanupCompareData(sharedValues.getRightConnectionName(), sharedValues.getRightHandle());
            }

        } finally {
            done(subMonitor);
        }
    }

    private void cleanupCompareData(String connectionName, int handle) {

        if (!initialize(connectionName)) {
            return;
        }

        try {

            if (!setCurrentLibrary()) {
                return;
            }

            new SYNCMBR_clear().run(getSystem(), handle);

        } finally {
            restoreCurrentLibrary();
        }
    }
}
