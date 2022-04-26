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
        JournalEntryColumnUI.JOCCID, // Added for Type5 viewer
        JournalEntryColumnUI.JOLUW, // Added for Type5 viewer
        JournalEntryColumnUI.JOXID, // Added for Type5 viewer
        JournalEntryColumnUI.JOCMTLVL, // Added for Type5 viewer
        JournalEntryColumnUI.JOSYNM, // Added for Type2 viewer 
        JournalEntryColumnUI.JOSYSSEQ,
        JournalEntryColumnUI.JOJOB,
        JournalEntryColumnUI.JOUSER, 
        JournalEntryColumnUI.JONBR, 
        JournalEntryColumnUI.JOTHDX, // Added for Type5 viewer
        JournalEntryColumnUI.JOUSPF, // Added for Type2 viewer
        JournalEntryColumnUI.JOPGM, 
        JournalEntryColumnUI.JOPGMLIB, // Added for Type5 viewer
        JournalEntryColumnUI.JOPGMDEV, // Added for Type5 viewer
        JournalEntryColumnUI.JOPGMASP, // Added for Type5 viewer
        JournalEntryColumnUI.JOLIB,
        JournalEntryColumnUI.JOOBJ,
        JournalEntryColumnUI.JOOBJIND, // Added for Type5 viewer
        JournalEntryColumnUI.JOOBJTYP, // Added for Type5 viewer
        JournalEntryColumnUI.JOFILTYP, // Added for Type5 viewer
        JournalEntryColumnUI.JOMBR,
        JournalEntryColumnUI.JOCTRR,
        JournalEntryColumnUI.JOFLAG,
        JournalEntryColumnUI.JOJID, // Added for Type4 viewer
        JournalEntryColumnUI.JORCV, // Added for Type5 viewer
        JournalEntryColumnUI.JORCVLIB, // Added for Type5 viewer
        JournalEntryColumnUI.JORCVDEV, // Added for Type5 viewer
        JournalEntryColumnUI.JORCVASP, // Added for Type5 viewer
        JournalEntryColumnUI.JOARM, // Added for Type5 viewer
        JournalEntryColumnUI.JOADF, // Added for Type5 viewer
        JournalEntryColumnUI.JORPORT, // Added for Type5 viewer
        JournalEntryColumnUI.JORADR, // Added for Type5 viewer
        JournalEntryColumnUI.JORCST, // Added for Type4 viewer
        JournalEntryColumnUI.JOTGR, // Added for Type4 viewer
        JournalEntryColumnUI.JOIGNAPY, // Added for Type4 viewer
        JournalEntryColumnUI.JOMINESD, 
        JournalEntryColumnUI.JOINCDAT, // Added for Type5 viewer
        JournalEntryColumnUI.JOESD };
    // @formatter:on

    public Type5ViewerFactory() {
        super(new HashSet<JournalEntryColumnUI>(Arrays.asList(columnNames)));
    }
}
