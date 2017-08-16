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

public class Type1ViewerFactory extends BaseTypeViewerFactory {

    // @formatter:off
    private static IJournalEntryColumn[] columnNames = { 
        IJournalEntryColumn.ID, 
        IJournalEntryColumn.JOENTT, 
        IJournalEntryColumn.JOSEQN, 
        IJournalEntryColumn.JOCODE, 
        IJournalEntryColumn.JOENTL,
        IJournalEntryColumn.JODATE, 
        IJournalEntryColumn.JOTIME, 
        IJournalEntryColumn.JOJOB, 
        IJournalEntryColumn.JOUSER, 
        IJournalEntryColumn.JONBR, 
        IJournalEntryColumn.JOPGM, 
        IJournalEntryColumn.JOLIB,
        IJournalEntryColumn.JOOBJ, 
        IJournalEntryColumn.JOMBR, 
        IJournalEntryColumn.JOMINESD, 
        IJournalEntryColumn.JOESD };
    // @formatter:on

    public Type1ViewerFactory() {
        super(new HashSet<IJournalEntryColumn>(Arrays.asList(columnNames)));
    }
}
