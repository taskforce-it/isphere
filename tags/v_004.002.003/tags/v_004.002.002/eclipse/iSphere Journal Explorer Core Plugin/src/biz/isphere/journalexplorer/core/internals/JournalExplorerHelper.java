/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

import biz.isphere.core.internal.ISeries;

public final class JournalExplorerHelper {

    public static boolean isValidObjectType(String objectType) {

        if (ISeries.FILE.equals(objectType) || ISeries.DTAARA.equals(objectType) || ISeries.DTAARA.equals(objectType)) {
            return true;
        }

        return false;
    }

    public static boolean isFile(String objectType) {

        if (ISeries.FILE.equals(objectType)) {
            return true;
        }

        return false;
    }
}
