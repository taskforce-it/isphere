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
    ID (ColumnsDAO.RRN_OUTPUT_FILE, Messages.LongFieldName_OutputFile_Rrn, Messages.Tooltip_OutputFile_Rrn),
    JOENTL (ColumnsDAO.JOENTL, Messages.LongFieldName_JOENTL, Messages.Tooltip_JOENTL),
    JOSEQN (ColumnsDAO.JOSEQN, Messages.LongFieldName_JOSEQN, Messages.Tooltip_JOSEQN),
    JOCODE (ColumnsDAO.JOCODE, Messages.LongFieldName_JOCODE, Messages.Tooltip_JOCODE),
    JOENTT (ColumnsDAO.JOENTT, Messages.LongFieldName_JOENTT, Messages.Tooltip_JOENTT),
    JODATE (ColumnsDAO.JODATE, Messages.LongFieldName_JODATE, Messages.Tooltip_JODATE),
    JOTIME (ColumnsDAO.JOTIME, Messages.LongFieldName_JOTIME, Messages.Tooltip_JOTIME),
    JOJOB (ColumnsDAO.JOJOB, Messages.LongFieldName_JOJOB, Messages.Tooltip_JOJOB),
    JOUSER (ColumnsDAO.JOUSER, Messages.LongFieldName_JOUSER, Messages.Tooltip_JOUSER),
    JONBR (ColumnsDAO.JONBR, Messages.LongFieldName_JONBR, Messages.Tooltip_JONBR),
    JOPGM (ColumnsDAO.JOPGM, Messages.LongFieldName_JOPGM, Messages.Tooltip_JOPGM),
    JOPGMLIB (ColumnsDAO.JOPGMLIB, Messages.LongFieldName_JOPGMLIB, Messages.Tooltip_JOPGMLIB),
    JOPGMDEV (ColumnsDAO.JOPGMDEV, Messages.LongFieldName_JOPGMDEV, Messages.Tooltip_JOPGMDEV),
    JOPGMASP (ColumnsDAO.JOPGMASP, Messages.LongFieldName_JOPGMASP, Messages.Tooltip_JOPGMASP),
    JOOBJ (ColumnsDAO.JOOBJ, Messages.LongFieldName_JOOBJ, Messages.Tooltip_JOOBJ),
    JOLIB (ColumnsDAO.JOLIB, Messages.LongFieldName_JOLIB, Messages.Tooltip_JOLIB),
    JOMBR (ColumnsDAO.JOMBR, Messages.LongFieldName_JOMBR, Messages.Tooltip_JOMBR),
    JOCTRR (ColumnsDAO.JOCTRR, Messages.LongFieldName_JOCTRR, Messages.Tooltip_JOCTRR),
    JOFLAG (ColumnsDAO.JOFLAG, Messages.LongFieldName_JOFLAG, Messages.Tooltip_JOFLAG),
    JOCCID (ColumnsDAO.JOCCID, Messages.LongFieldName_JOCCID, Messages.Tooltip_JOCCID),
    JOUSPF (ColumnsDAO.JOUSPF, Messages.LongFieldName_JOUSPF, Messages.Tooltip_JOUSPF),
    JOSYNM (ColumnsDAO.JOSYNM, Messages.LongFieldName_JOSYNM, Messages.Tooltip_JOSYNM),
    JOJID (ColumnsDAO.JOJID, Messages.LongFieldName_JOJID, Messages.Tooltip_JOJID),
    JORCST (ColumnsDAO.JORCST, Messages.LongFieldName_JORCST, Messages.Tooltip_JORCST),
    JOTGR (ColumnsDAO.JOTGR, Messages.LongFieldName_JOTGR, Messages.Tooltip_JOTGR),
    JOINCDAT (ColumnsDAO.JOINCDAT, Messages.LongFieldName_JOINCDAT, Messages.Tooltip_JOINCDAT),
    JOIGNAPY (ColumnsDAO.JOIGNAPY, Messages.LongFieldName_JOIGNAPY, Messages.Tooltip_JOIGNAPY),
    JOMINESD (ColumnsDAO.JOMINESD, Messages.LongFieldName_JOMINESD, Messages.Tooltip_JOMINESD),
    JOOBJIND (ColumnsDAO.JOOBJIND, Messages.LongFieldName_JOOBJIND, Messages.Tooltip_JOOBJIND),
    JOSYSSEQ (ColumnsDAO.JOSYSSEQ, Messages.LongFieldName_JOSYSSEQ, Messages.Tooltip_JOSYSSEQ),
    JORCV (ColumnsDAO.JORCV, Messages.LongFieldName_JORCV, Messages.Tooltip_JORCV),
    JORCVLIB (ColumnsDAO.JORCVLIB, Messages.LongFieldName_JORCVLIB, Messages.Tooltip_JORCVLIB),
    JORCVDEV (ColumnsDAO.JORCVDEV, Messages.LongFieldName_JORCVDEV, Messages.Tooltip_JORCVDEV),
    JORCVASP (ColumnsDAO.JORCVASP, Messages.LongFieldName_JORCVASP, Messages.Tooltip_JORCVASP),
    JOARM (ColumnsDAO.JOARM, Messages.LongFieldName_JOARM, Messages.Tooltip_JOARM),
    JOTHDX (ColumnsDAO.JOTHDX, Messages.LongFieldName_JOTHDX, Messages.Tooltip_JOTHDX),
    JOADF (ColumnsDAO.JOADF, Messages.LongFieldName_JOADF, Messages.Tooltip_JOADF),
    JORPORT (ColumnsDAO.JORPORT, Messages.LongFieldName_JORPORT, Messages.Tooltip_JORPORT),
    JORADR (ColumnsDAO.JORADR, Messages.LongFieldName_JORADR, Messages.Tooltip_JORADR),
    JOLUW (ColumnsDAO.JOLUW, Messages.LongFieldName_JOLUW, Messages.Tooltip_JOLUW),
    JOXID (ColumnsDAO.JOXID, Messages.LongFieldName_JOXID, Messages.Tooltip_JOXID),
    JOOBJTYP (ColumnsDAO.JOOBJTYP, Messages.LongFieldName_JOOBJTYP, Messages.Tooltip_JOOBJTYP),
    JOFILTYP (ColumnsDAO.JOFILTYP, Messages.LongFieldName_JOFILTYP, Messages.Tooltip_JOFILTYP),
    JOCMTLVL (ColumnsDAO.JOCMTLVL, Messages.LongFieldName_JOCMTLVL, Messages.Tooltip_JOCMTLVL),
    JONVI (ColumnsDAO.JONVI, Messages.LongFieldName_JONVI, Messages.Tooltip_JONVI),
    JOESD (ColumnsDAO.JOESD, Messages.LongFieldName_JOESD, Messages.Tooltip_JOESD);

    private static Map<String, JournalEntryColumnUI> values;

    private String columnName;
    private String columnNameLong;
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

    private JournalEntryColumnUI(String fieldName, String longFieldName, String columnDescription) {
        this.columnName = fieldName;
        this.columnNameLong = longFieldName;
        this.columnDescription = columnDescription;
    }

    public String columnName() {
        return columnName;
    }

    public String columnNameLong() {
        return columnNameLong;
    }

    public String description() {
        return columnDescription;
    }
}
