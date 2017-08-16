/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import java.util.Arrays;
import java.util.HashSet;

public class Type5ViewerFactory extends BaseTypeViewerFactory {

    // @formatter:off
    private static IJournalEntryColumn[] columnNames = { 
        IJournalEntryColumn.ID, 
        IJournalEntryColumn.JOENTT, 
        IJournalEntryColumn.JOSEQN, 
        IJournalEntryColumn.JOCODE, 
        IJournalEntryColumn.JOENTL,
        IJournalEntryColumn.JODATE, 
        IJournalEntryColumn.JOTIME,
        IJournalEntryColumn.JOCCID,
        IJournalEntryColumn.JOLUW,
        IJournalEntryColumn.JOXID,
        IJournalEntryColumn.JOCMTLVL,
        IJournalEntryColumn.JOSYNM, 
        IJournalEntryColumn.JOSYSSEQ, 
        IJournalEntryColumn.JOJOB, 
        IJournalEntryColumn.JOUSER, 
        IJournalEntryColumn.JONBR, 
        IJournalEntryColumn.JOTHDX, 
        IJournalEntryColumn.JOUSPF,
        IJournalEntryColumn.JOPGM, 
        IJournalEntryColumn.JOPGMLIB, 
        IJournalEntryColumn.JOPGMDEV, 
        IJournalEntryColumn.JOPGMASP, 
        IJournalEntryColumn.JOLIB, 
        IJournalEntryColumn.JOOBJ,
        IJournalEntryColumn.JOOBJIND,
        IJournalEntryColumn.JOOBJTYP,
        IJournalEntryColumn.JOFILTYP,
        IJournalEntryColumn.JOMBR, 
        IJournalEntryColumn.JOJID, 
        IJournalEntryColumn.JORCV, 
        IJournalEntryColumn.JORCVLIB, 
        IJournalEntryColumn.JORCVDEV, 
        IJournalEntryColumn.JORCVASP, 
        IJournalEntryColumn.JOARM, 
        IJournalEntryColumn.JOADF, 
        IJournalEntryColumn.JORPORT, 
        IJournalEntryColumn.JORADR, 
        IJournalEntryColumn.JORCST, 
        IJournalEntryColumn.JOTGR,
        IJournalEntryColumn.JOIGNAPY, 
        IJournalEntryColumn.JOMINESD, 
        IJournalEntryColumn.JOINCDAT,
        IJournalEntryColumn.JOESD };
    // @formatter:on

    public Type5ViewerFactory() {
        super(new HashSet<IJournalEntryColumn>(Arrays.asList(columnNames)));
    }
}
