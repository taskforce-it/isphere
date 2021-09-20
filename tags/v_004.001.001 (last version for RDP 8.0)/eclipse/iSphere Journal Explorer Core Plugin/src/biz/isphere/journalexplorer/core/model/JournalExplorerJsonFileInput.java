/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.json.JsonImporter;

public class JournalExplorerJsonFileInput extends AbstractJournalExplorerInput {

    private static final String INPUT_TYPE = "file://"; //$NON-NLS-1$

    private String connectionName;
    private File file;

    public JournalExplorerJsonFileInput(String connectionName, String path, SQLWhereClause whereClause) {
        this(connectionName, new File(path), whereClause);
    }

    public JournalExplorerJsonFileInput(String connectionName, File file, SQLWhereClause whereClause) {
        super(whereClause);
        this.connectionName = connectionName;
        this.file = file;
    }

    public String getPath() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return file.getPath();
    }

    @Override
    public String getName() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return file.getName();
    }

    @Override
    public String getToolTipText() {
        return getPath();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + getPath();
    }

    @Override
    public JournalEntries load(IProgressMonitor monitor) throws Exception {

        JsonImporter<JournalEntries> importer = new JsonImporter<JournalEntries>(JournalEntries.class);

        JournalEntries data = importer.execute(null, getPath());

        // Overwrite connection name, if passed in
        data.finalizeJsonLoading(this.connectionName, getWhereClause());

        return data;
    }

}
