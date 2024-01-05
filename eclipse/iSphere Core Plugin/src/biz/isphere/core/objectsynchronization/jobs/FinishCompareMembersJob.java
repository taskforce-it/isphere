/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.Messages;
import biz.isphere.core.objectsynchronization.SYNCMBR_clear;

/**
 * This class cleans up the data stored in file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class FinishCompareMembersJob extends AbstractCompareMembersJob {

    public FinishCompareMembersJob(IProgressMonitor monitor, CompareMembersSharedJobValues sharedValues) {
        super(monitor, sharedValues);
    }

    public int getWorkCount() {
        return 2;
    }

    @Override
    public int execute(int worked) {

        getMonitor().setTaskName(Messages.Deleting_compare_data);

        CompareMembersSharedJobValues sharedValues = getSharedValues();

        if (sharedValues.getLeftHandle() != ERROR_HANDLE) {
            cleanupCompareData(sharedValues.getLeftConnectionName(), sharedValues.getLeftHandle());
            worked++;
        }

        if (sharedValues.getRightHandle() != ERROR_HANDLE) {
            cleanupCompareData(sharedValues.getRightConnectionName(), sharedValues.getRightHandle());
            worked++;
        }

        return worked;
    }

    private void cleanupCompareData(String connectionName, int handle) {

        initialize(connectionName);

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
