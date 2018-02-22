/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForOutputFiles;

/**
 * This class creates a "Journal Entries Viewer" widget for a *TYPE3 output file
 * of the DSPJRN command. The *TYPE3 viewer has the same fields as the *TYPE2
 * viewer. The only difference between a *TYPE2 and a *TYPE4 output file is,
 * that fields <i>Date</i> and <i>Time</i> have been replaced with a
 * <i>Timestamp</i> field. But that does not affect the UI.
 * 
 * @see JournalEntriesViewerForOutputFiles
 * @see AbstractTypeViewerFactory
 */
public class Type3ViewerFactory extends Type2ViewerFactory {

    public Type3ViewerFactory() {
    }
}
