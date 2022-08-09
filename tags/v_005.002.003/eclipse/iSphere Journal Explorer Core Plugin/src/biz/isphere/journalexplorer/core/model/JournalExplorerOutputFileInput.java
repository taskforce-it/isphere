/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.OutputFileDAO;

public class JournalExplorerOutputFileInput extends AbstractJournalExplorerInput {

    private static final String INPUT_TYPE = "outputfile://"; //$NON-NLS-1$

    private OutputFile outputFile;

    public JournalExplorerOutputFileInput(OutputFile outputFile, SQLWhereClause whereClause) {
        super(whereClause);
        this.outputFile = outputFile;
    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    @Override
    public String getName() {
        return QualifiedName.getName(outputFile.getLibraryName(), outputFile.getFileName());
    }

    @Override
    public String getToolTipText() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.bind(Messages.Title_Connection_A, outputFile.getConnectionName()));
        buffer.append("\n");
        buffer.append(Messages.bind(Messages.Title_File_A, getName()));

        return buffer.toString();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + outputFile.getQualifiedName();
    }

    @Override
    public JournalEntries load(IProgressMonitor monitor) throws Exception {

        OutputFileDAO journalDAO = new OutputFileDAO(outputFile);

        JournalEntries data = journalDAO.getJournalData(getWhereClause(), monitor);

        return data;
    }

}
