/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import java.util.HashMap;
import java.util.Map;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;

/**
 * This class defines the UI names for the columns of a journal entry record.
 */
public enum JournalEntryColumnUI {
    ID (ColumnsDAO.RRN_OUTPUT_FILE, Messages.Tooltip_OutputFile_Rrn),
    JOENTL (ColumnsDAO.JOENTL, Messages.Tooltip_JOENTL),
    JOSEQN (ColumnsDAO.JOSEQN, Messages.Tooltip_JOSEQN),
    JOCODE (ColumnsDAO.JOCODE, Messages.Tooltip_JOCODE),
    JOENTT (ColumnsDAO.JOENTT, Messages.Tooltip_JOENTT),
    JODATE (ColumnsDAO.JODATE, Messages.Tooltip_JODATE),
    JOTIME (ColumnsDAO.JOTIME, Messages.Tooltip_JOTIME),
    JOJOB (ColumnsDAO.JOJOB, Messages.Tooltip_JOJOB),
    JOUSER (ColumnsDAO.JOUSER, Messages.Tooltip_JOUSER),
    JONBR (ColumnsDAO.JONBR, Messages.Tooltip_JONBR),
    JOPGM (ColumnsDAO.JOPGM, Messages.Tooltip_JOPGM),
    JOPGMLIB (ColumnsDAO.JOPGMLIB, Messages.Tooltip_JOPGMLIB),
    JOPGMDEV (ColumnsDAO.JOPGMDEV, Messages.Tooltip_JOPGMDEV),
    JOPGMASP (ColumnsDAO.JOPGMASP, Messages.Tooltip_JOPGMASP),
    JOOBJ (ColumnsDAO.JOOBJ, Messages.Tooltip_JOOBJ),
    JOLIB (ColumnsDAO.JOLIB, Messages.Tooltip_JOLIB),
    JOMBR (ColumnsDAO.JOMBR, Messages.Tooltip_JOMBR),
    JOCTRR (ColumnsDAO.JOCTRR, Messages.Tooltip_JOCTRR),
    JOFLAG (ColumnsDAO.JOFLAG, Messages.Tooltip_JOFLAG),
    JOCCID (ColumnsDAO.JOCCID, Messages.Tooltip_JOCCID),
    JOUSPF (ColumnsDAO.JOUSPF, Messages.Tooltip_JOUSPF),
    JOSYNM (ColumnsDAO.JOSYNM, Messages.Tooltip_JOSYNM),
    JOJID (ColumnsDAO.JOJID, Messages.Tooltip_JOJID),
    JORCST (ColumnsDAO.JORCST, Messages.Tooltip_JORCST),
    JOTGR (ColumnsDAO.JOTGR, Messages.Tooltip_JOTGR),
    JOINCDAT (ColumnsDAO.JOINCDAT, Messages.Tooltip_JOINCDAT),
    JOIGNAPY (ColumnsDAO.JOIGNAPY, Messages.Tooltip_JOIGNAPY),
    JOMINESD (ColumnsDAO.JOMINESD, Messages.Tooltip_JOMINESD),
    JOOBJIND (ColumnsDAO.JOOBJIND, Messages.Tooltip_JOOBJIND),
    JOSYSSEQ (ColumnsDAO.JOSYSSEQ, Messages.Tooltip_JOSYSSEQ),
    JORCV (ColumnsDAO.JORCV, Messages.Tooltip_JORCV),
    JORCVLIB (ColumnsDAO.JORCVLIB, Messages.Tooltip_JORCVLIB),
    JORCVDEV (ColumnsDAO.JORCVDEV, Messages.Tooltip_JORCVDEV),
    JORCVASP (ColumnsDAO.JORCVASP, Messages.Tooltip_JORCVASP),
    JOARM (ColumnsDAO.JOARM, Messages.Tooltip_JOARM),
    JOTHDX (ColumnsDAO.JOTHDX, Messages.Tooltip_JOTHDX),
    JOADF (ColumnsDAO.JOADF, Messages.Tooltip_JOADF),
    JORPORT (ColumnsDAO.JORPORT, Messages.Tooltip_JORPORT),
    JORADR (ColumnsDAO.JORADR, Messages.Tooltip_JORADR),
    JOLUW (ColumnsDAO.JOLUW, Messages.Tooltip_JOLUW),
    JOXID (ColumnsDAO.JOXID, Messages.Tooltip_JOXID),
    JOOBJTYP (ColumnsDAO.JOOBJTYP, Messages.Tooltip_JOOBJTYP),
    JOFILTYP (ColumnsDAO.JOFILTYP, Messages.Tooltip_JOFILTYP),
    JOCMTLVL (ColumnsDAO.JOCMTLVL, Messages.Tooltip_JOCMTLVL),
    JOESD (ColumnsDAO.JOESD, Messages.Tooltip_JOESD);

    private static Map<String, JournalEntryColumnUI> values;

    private String columnName;
    private String columnDescription;

    static {
        values = new HashMap<String, JournalEntryColumnUI>();
        for (JournalEntryColumnUI journalEntryType : JournalEntryColumnUI.values()) {
            values.put(journalEntryType.columnName(), journalEntryType);
        }
    }

    public static JournalEntryColumnUI find(String columnName) {
        return values.get(columnName);
    }

    private JournalEntryColumnUI(String fieldName, String columnDescription) {
        this.columnName = fieldName;
        this.columnDescription = columnDescription;
    }

    public String columnName() {
        return columnName;
    }

    public String description() {
        return columnDescription;
    }
}
