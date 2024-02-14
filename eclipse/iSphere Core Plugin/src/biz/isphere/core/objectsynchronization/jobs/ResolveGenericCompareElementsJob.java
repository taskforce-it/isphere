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
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.SYNCMBR_resolveGenericCompareElements;

/**
 * This class resolves generic compare elements stored in file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class ResolveGenericCompareElementsJob extends AbstractCompareMembersJob {

    public ResolveGenericCompareElementsJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues) {
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
            CompareOptions compareOptions = getSharedValues().getCompareOptions();

            consume(subMonitor, Messages.Task_Resolving_generic_items);
            resolveGenericCompareElements(subMonitor, sharedValues.getLeftHandle(), sharedValues.getLeftConnectionName(), SyncMbrMode.LEFT_SYSTEM,
                compareOptions.getMemberFilter());

            consume(subMonitor, Messages.Task_Resolving_generic_items);
            resolveGenericCompareElements(subMonitor, sharedValues.getRightHandle(), sharedValues.getRightConnectionName(), SyncMbrMode.RIGHT_SYSTEM,
                compareOptions.getMemberFilter());

        } finally {
            done(subMonitor);
        }
    }

    private void resolveGenericCompareElements(SubMonitor subMonitor, int handle, String connectionName, SyncMbrMode mode, String memberFilter) {

        if (!initialize(connectionName)) {
            return;
        }

        try {

            if (subMonitor.isCanceled()) {
                return;
            }

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
