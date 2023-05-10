/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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
    ID (ColumnsDAO.ID.name(), ColumnsDAO.ID.description(), Messages.Tooltip_OutputFile_Rrn),
    JOENTL (ColumnsDAO.JOENTL.name(), ColumnsDAO.JOENTL.description(), Messages.Tooltip_JOENTL),
    JOSEQN (ColumnsDAO.JOSEQN.name(), ColumnsDAO.JOSEQN.description(), Messages.Tooltip_JOSEQN),
    JOCODE (ColumnsDAO.JOCODE.name(), ColumnsDAO.JOCODE.description(), Messages.Tooltip_JOCODE),
    JOENTT (ColumnsDAO.JOENTT.name(), ColumnsDAO.JOENTT.description(), Messages.Tooltip_JOENTT),
    JODATE (ColumnsDAO.JODATE.name(), ColumnsDAO.JODATE.description(), Messages.Tooltip_JODATE),
    JOTIME (ColumnsDAO.JOTIME.name(), ColumnsDAO.JOTIME.description(), Messages.Tooltip_JOTIME),
    JOTSTP (ColumnsDAO.JOTSTP.name(), ColumnsDAO.JOTSTP.description(), Messages.Tooltip_JOTSTP),
    JOJOB (ColumnsDAO.JOJOB.name(), ColumnsDAO.JOJOB.description(), Messages.Tooltip_JOJOB),
    JOUSER (ColumnsDAO.JOUSER.name(), ColumnsDAO.JOUSER.description(), Messages.Tooltip_JOUSER),
    JONBR (ColumnsDAO.JONBR.name(), ColumnsDAO.JONBR.description(), Messages.Tooltip_JONBR),
    JOPGM (ColumnsDAO.JOPGM.name(), ColumnsDAO.JOPGM.description(), Messages.Tooltip_JOPGM),
    JOPGMLIB (ColumnsDAO.JOPGMLIB.name(), ColumnsDAO.JOPGMLIB.description(), Messages.Tooltip_JOPGMLIB),
    JOPGMDEV (ColumnsDAO.JOPGMDEV.name(), ColumnsDAO.JOPGMDEV.description(), Messages.Tooltip_JOPGMDEV),
    JOPGMASP (ColumnsDAO.JOPGMASP.name(), ColumnsDAO.JOPGMASP.description(), Messages.Tooltip_JOPGMASP),
    JOOBJ (ColumnsDAO.JOOBJ.name(), ColumnsDAO.JOOBJ.description(), Messages.Tooltip_JOOBJ),
    JOLIB (ColumnsDAO.JOLIB.name(), ColumnsDAO.JOLIB.description(), Messages.Tooltip_JOLIB),
    JOMBR (ColumnsDAO.JOMBR.name(), ColumnsDAO.JOMBR.description(), Messages.Tooltip_JOMBR),
    JOCTRR (ColumnsDAO.JOCTRR.name(), ColumnsDAO.JOCTRR.description(), Messages.Tooltip_JOCTRR),
    JOFLAG (ColumnsDAO.JOFLAG.name(), ColumnsDAO.JOFLAG.description(), Messages.Tooltip_JOFLAG),
    JOCCID (ColumnsDAO.JOCCID.name(), ColumnsDAO.JOCCID.description(), Messages.Tooltip_JOCCID),
    JOUSPF (ColumnsDAO.JOUSPF.name(), ColumnsDAO.JOUSPF.description(), Messages.Tooltip_JOUSPF),
    JOSYNM (ColumnsDAO.JOSYNM.name(), ColumnsDAO.JOSYNM.description(), Messages.Tooltip_JOSYNM),
    JOJID (ColumnsDAO.JOJID.name(), ColumnsDAO.JOJID.description(), Messages.Tooltip_JOJID),
    JORCST (ColumnsDAO.JORCST.name(), ColumnsDAO.JORCST.description(), Messages.Tooltip_JORCST),
    JOTGR (ColumnsDAO.JOTGR.name(), ColumnsDAO.JOTGR.description(), Messages.Tooltip_JOTGR),
    JOINCDAT (ColumnsDAO.JOINCDAT.name(), ColumnsDAO.JOINCDAT.description(), Messages.Tooltip_JOINCDAT),
    JOIGNAPY (ColumnsDAO.JOIGNAPY.name(), ColumnsDAO.JOIGNAPY.description(), Messages.Tooltip_JOIGNAPY),
    JOMINESD (ColumnsDAO.JOMINESD.name(), ColumnsDAO.JOMINESD.description(), Messages.Tooltip_JOMINESD),
    JOOBJIND (ColumnsDAO.JOOBJIND.name(), ColumnsDAO.JOOBJIND.description(), Messages.Tooltip_JOOBJIND),
    JOSYSSEQ (ColumnsDAO.JOSYSSEQ.name(), ColumnsDAO.JOSYSSEQ.description(), Messages.Tooltip_JOSYSSEQ),
    JORCV (ColumnsDAO.JORCV.name(), ColumnsDAO.JORCV.description(), Messages.Tooltip_JORCV),
    JORCVLIB (ColumnsDAO.JORCVLIB.name(), ColumnsDAO.JORCVLIB.description(), Messages.Tooltip_JORCVLIB),
    JORCVDEV (ColumnsDAO.JORCVDEV.name(), ColumnsDAO.JORCVDEV.description(), Messages.Tooltip_JORCVDEV),
    JORCVASP (ColumnsDAO.JORCVASP.name(), ColumnsDAO.JORCVASP.description(), Messages.Tooltip_JORCVASP),
    JOARM (ColumnsDAO.JOARM.name(), ColumnsDAO.JOARM.description(), Messages.Tooltip_JOARM),
    JOTHDX (ColumnsDAO.JOTHDX.name(), ColumnsDAO.JOTHDX.description(), Messages.Tooltip_JOTHDX),
    JOADF (ColumnsDAO.JOADF.name(), ColumnsDAO.JOADF.description(), Messages.Tooltip_JOADF),
    JORPORT (ColumnsDAO.JORPORT.name(), ColumnsDAO.JORPORT.description(), Messages.Tooltip_JORPORT),
    JORADR (ColumnsDAO.JORADR.name(), ColumnsDAO.JORADR.description(), Messages.Tooltip_JORADR),
    JOLUW (ColumnsDAO.JOLUW.name(), ColumnsDAO.JOLUW.description(), Messages.Tooltip_JOLUW),
    JOXID (ColumnsDAO.JOXID.name(), ColumnsDAO.JOXID.description(), Messages.Tooltip_JOXID),
    JOOBJTYP (ColumnsDAO.JOOBJTYP.name(), ColumnsDAO.JOOBJTYP.description(), Messages.Tooltip_JOOBJTYP),
    JOFILTYP (ColumnsDAO.JOFILTYP.name(), ColumnsDAO.JOFILTYP.description(), Messages.Tooltip_JOFILTYP),
    JOCMTLVL (ColumnsDAO.JOCMTLVL.name(), ColumnsDAO.JOCMTLVL.description(), Messages.Tooltip_JOCMTLVL),
    JONVI (ColumnsDAO.JONVI.name(), ColumnsDAO.JONVI.description(), Messages.Tooltip_JONVI),
    JOESD (ColumnsDAO.JOESD.name(), ColumnsDAO.JOESD.description(), Messages.Tooltip_JOESD);

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
