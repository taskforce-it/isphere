/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import biz.isphere.core.objectsynchronization.SynchronizationResult;
import biz.isphere.core.objectsynchronization.SynchronizeMembersJob;

public interface ISynchronizeMembersPostRun {

    /**
     * Job finished successfully.
     */
    public static final String OK = "OK"; //$NON-NLS-1$

    /**
     * Job ended with errors.
     */
    public static final String ERROR = "ERROR"; //$NON-NLS-1$

    /**
     * Job has been canceled.
     */
    public static final String CANCELED = "CANCELED"; //$NON-NLS-1$

    /**
     * PostRun methods called by {@link SynchronizeMembersJob} at the end of the
     * copy member process.
     * 
     * @param status - status of the synchronization operation. See:
     *        {@link SynchronizationResult}
     * @param countCopied - number of members that have been copied
     * @param countErrors - number of members that have errors
     * @param message - error or canceled message
     */
    public void synchronizeMembersPostRun(String status, int countCopied, int countErrors, String message);
}
