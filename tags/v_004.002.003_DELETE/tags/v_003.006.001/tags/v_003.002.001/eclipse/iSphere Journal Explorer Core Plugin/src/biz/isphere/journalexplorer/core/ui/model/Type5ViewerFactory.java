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

import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForOutputFiles;

/**
 * This class creates a "Journal Entries Viewer" widget for a *TYPE5 output file
 * of the DSPJRN command.
 * 
 * @see JournalEntriesViewerForOutputFiles
 * @see AbstractTypeViewerFactory
 */
public class Type5ViewerFactory extends AbstractTypeViewerFactory {

    // @formatter:off
    private static JournalEntryColumnUI[] columnNames = { 
        JournalEntryColumnUI.ID, 
        JournalEntryColumnUI.JOENTT, 
        JournalEntryColumnUI.JOSEQN, 
        JournalEntryColumnUI.JOCODE, 
        JournalEntryColumnUI.JOENTL,
        JournalEntryColumnUI.JODATE, 
        JournalEntryColumnUI.JOTIME,
        JournalEntryColumnUI.JOCCID,
        JournalEntryColumnUI.JOLUW,
        JournalEntryColumnUI.JOXID,
        JournalEntryColumnUI.JOCMTLVL,
        JournalEntryColumnUI.JOSYNM, 
        JournalEntryColumnUI.JOSYSSEQ, 
        JournalEntryColumnUI.JOJOB, 
        JournalEntryColumnUI.JOUSER, 
        JournalEntryColumnUI.JONBR, 
        JournalEntryColumnUI.JOTHDX, 
        JournalEntryColumnUI.JOUSPF,
        JournalEntryColumnUI.JOPGM, 
        JournalEntryColumnUI.JOPGMLIB, 
        JournalEntryColumnUI.JOPGMDEV, 
        JournalEntryColumnUI.JOPGMASP, 
        JournalEntryColumnUI.JOLIB, 
        JournalEntryColumnUI.JOOBJ,
        JournalEntryColumnUI.JOOBJIND,
        JournalEntryColumnUI.JOOBJTYP,
        JournalEntryColumnUI.JOFILTYP,
        JournalEntryColumnUI.JOMBR, 
        JournalEntryColumnUI.JOJID, 
        JournalEntryColumnUI.JORCV, 
        JournalEntryColumnUI.JORCVLIB, 
        JournalEntryColumnUI.JORCVDEV, 
        JournalEntryColumnUI.JORCVASP, 
        JournalEntryColumnUI.JOARM, 
        JournalEntryColumnUI.JOADF, 
        JournalEntryColumnUI.JORPORT, 
        JournalEntryColumnUI.JORADR, 
        JournalEntryColumnUI.JORCST, 
        JournalEntryColumnUI.JOTGR,
        JournalEntryColumnUI.JOIGNAPY, 
        JournalEntryColumnUI.JOMINESD, 
        JournalEntryColumnUI.JOINCDAT,
        JournalEntryColumnUI.JOESD };
    // @formatter:on

    public Type5ViewerFactory() {
        super(new HashSet<JournalEntryColumnUI>(Arrays.asList(columnNames)));
    }
}
