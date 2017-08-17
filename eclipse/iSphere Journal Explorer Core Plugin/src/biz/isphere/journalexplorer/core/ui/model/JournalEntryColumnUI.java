/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;

/**
 * This class defines the UI names for the columns of a journal entry record.
 */
public enum JournalEntryColumnUI {
    ID (ColumnsDAO.RRN_OUTPUT_FILE),
    JOENTL (ColumnsDAO.JOENTL),
    JOSEQN (ColumnsDAO.JOSEQN),
    JOCODE (ColumnsDAO.JOCODE),
    JOENTT (ColumnsDAO.JOENTT),
    JODATE (ColumnsDAO.JODATE),
    JOTIME (ColumnsDAO.JOTIME),
    JOJOB (ColumnsDAO.JOJOB),
    JOUSER (ColumnsDAO.JOUSER),
    JONBR (ColumnsDAO.JONBR),
    JOPGM (ColumnsDAO.JOPGM),
    JOPGMLIB (ColumnsDAO.JOPGMLIB),
    JOPGMDEV (ColumnsDAO.JOPGMDEV),
    JOPGMASP (ColumnsDAO.JOPGMASP),
    JOOBJ (ColumnsDAO.JOOBJ),
    JOLIB (ColumnsDAO.JOLIB),
    JOMBR (ColumnsDAO.JOMBR),
    JOCTRR (ColumnsDAO.JOCTRR),
    JOFLAG (ColumnsDAO.JOFLAG),
    JOCCID (ColumnsDAO.JOCCID),
    JOUSPF (ColumnsDAO.JOUSPF),
    JOSYNM (ColumnsDAO.JOSYNM),
    JOJID (ColumnsDAO.JOJID),
    JORCST (ColumnsDAO.JORCST),
    JOTGR (ColumnsDAO.JOTGR),
    JOINCDAT (ColumnsDAO.JOINCDAT),
    JOIGNAPY (ColumnsDAO.JOIGNAPY),
    JOMINESD (ColumnsDAO.JOMINESD),
    JOOBJIND (ColumnsDAO.JOOBJIND),
    JOSYSSEQ (ColumnsDAO.JOSYSSEQ),
    JORCV (ColumnsDAO.JORCV),
    JORCVLIB (ColumnsDAO.JORCVLIB),
    JORCVDEV (ColumnsDAO.JORCVDEV),
    JORCVASP (ColumnsDAO.JORCVASP),
    JOARM (ColumnsDAO.JOARM),
    JOTHDX (ColumnsDAO.JOTHDX),
    JOADF (ColumnsDAO.JOADF),
    JORPORT (ColumnsDAO.JORPORT),
    JORADR (ColumnsDAO.JORADR),
    JOLUW (ColumnsDAO.JOLUW),
    JOXID (ColumnsDAO.JOXID),
    JOOBJTYP (ColumnsDAO.JOOBJTYP),
    JOFILTYP (ColumnsDAO.JOFILTYP),
    JOCMTLVL (ColumnsDAO.JOCMTLVL),
    JOESD (ColumnsDAO.JOESD);

    private String columnName;

    private JournalEntryColumnUI(String fieldName) {
        this.columnName = fieldName;
    }

    public String columnName() {
        return columnName;
    }
}
