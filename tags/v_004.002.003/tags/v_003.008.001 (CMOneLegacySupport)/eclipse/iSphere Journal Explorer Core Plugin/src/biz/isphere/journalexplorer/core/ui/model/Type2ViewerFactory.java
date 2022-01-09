/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import java.util.Arrays;
import java.util.HashSet;

import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForOutputFilesTab;

/**
 * This class creates a "Journal Entries Viewer" widget for a *TYPE2 output file
 * of the DSPJRN command.
 * 
 * @see JournalEntriesViewerForOutputFilesTab
 * @see AbstractTypeViewerFactory
 */
public class Type2ViewerFactory extends AbstractTypeViewerFactory {

    // @formatter:off
    private static JournalEntryColumnUI[] columnNames = { 
        JournalEntryColumnUI.ID, 
        JournalEntryColumnUI.JOENTT, 
        JournalEntryColumnUI.JOSEQN, 
        JournalEntryColumnUI.JOCODE,
        JournalEntryColumnUI.JOENTL,
        JournalEntryColumnUI.JODATE, 
        JournalEntryColumnUI.JOTIME,
        JournalEntryColumnUI.JOSYNM, // Added for Type2 viewer
        JournalEntryColumnUI.JOJOB,
        JournalEntryColumnUI.JOUSER, 
        JournalEntryColumnUI.JONBR, 
        JournalEntryColumnUI.JOUSPF, // Added for Type2 viewer
        JournalEntryColumnUI.JOPGM, 
        JournalEntryColumnUI.JOLIB,
        JournalEntryColumnUI.JOOBJ,
        JournalEntryColumnUI.JOMBR,
        JournalEntryColumnUI.JOCTRR,
        JournalEntryColumnUI.JOFLAG,
        JournalEntryColumnUI.JOCCID,
        JournalEntryColumnUI.JOMINESD, 
        JournalEntryColumnUI.JOESD };
    // @formatter:on

    public Type2ViewerFactory() {
        super(new HashSet<JournalEntryColumnUI>(Arrays.asList(columnNames)));
    }
}
