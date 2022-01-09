/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.externalapi;

import java.sql.Timestamp;

/**
 * This interface specifies the selection criteria for retrieving journal
 * entries.
 */
public interface ISelectionCriteria {

    /**
     * Returns the <i>starting</i> date and time (FROMTIME).
     * 
     * @return from date
     */
    public Timestamp getFromTime();

    /**
     * Returns the <i>ending</i> date and time (TOTIME).
     * 
     * @return from date
     */
    public Timestamp getToTime();

    /**
     * Returns an array of selected journal entry types (ENTTYP), such as PT,
     * UB, UA, DL, BR, UR, DR.<br>
     * Special values:
     * <ul>
     * <li>*ALL - The retrieval of journal entries is not limited to entries</li>
     * <li>*RCD - Only journal entries that have an entry type for record</li>
     * </ul>
     * 
     * @return array of journal entry types.
     */
    public String[] getEntryTypes();

    /**
     * Returns the maximum number of entries to retrieve. Must be set to -1 to
     * use the default value.
     * 
     * @return maximum number of entries to retrieve
     */
    public int getMaxEntries();
}
