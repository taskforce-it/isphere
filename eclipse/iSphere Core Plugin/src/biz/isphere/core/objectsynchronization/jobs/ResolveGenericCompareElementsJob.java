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
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.SYNCMBR_resolveGenericCompareElements;

/**
 * This class resolves generic compare elements stored in file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class ResolveGenericCompareElementsJob extends AbstractCompareMembersJob {

    public ResolveGenericCompareElementsJob(IProgressMonitor monitor, CompareMembersSharedJobValues sharedValues) {
        super(monitor, sharedValues);
    }

    public int getWorkCount() {
        return 2;
    }

    @Override
    public int execute(int worked) {

        getMonitor().setTaskName(Messages.Resolving_generic_compare_items);

        CompareMembersSharedJobValues sharedValues = getSharedValues();
        CompareOptions compareOptions = getSharedValues().getCompareOptions();

        resolveGenericCompareElements(sharedValues.getLeftHandle(), sharedValues.getLeftConnectionName(), SyncMbrMode.LEFT_SYSTEM,
            compareOptions.getMemberFilter());
        worked++;

        resolveGenericCompareElements(sharedValues.getRightHandle(), sharedValues.getRightConnectionName(), SyncMbrMode.RIGHT_SYSTEM,
            compareOptions.getMemberFilter());
        worked++;

        return worked;
    }

    private void resolveGenericCompareElements(int handle, String connectionName, SyncMbrMode mode, String memberFilter) {

        initialize(connectionName);

        try {

            if (handle == ERROR_HANDLE) {
                return;
            }

            if (!setCurrentLibrary()) {
                return;
            }

            new SYNCMBR_resolveGenericCompareElements().run(getSystem(), handle, mode.mode(), memberFilter);

        } finally {
            restoreCurrentLibrary();
        }

    }
}
