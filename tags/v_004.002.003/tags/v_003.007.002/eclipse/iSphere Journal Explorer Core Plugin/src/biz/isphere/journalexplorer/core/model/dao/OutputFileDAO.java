/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.OutputFile;

/**
 * This class loads the exported journal *TYPE1 to *TYPE5 data that has been
 * exported by DSPJRN to an output file. For example:
 * 
 * <pre>
 * DSPJRN JRN(library/journal) FILE((library/file)) RCVRNG(*CURCHAIN) 
 *   FROMTIME(060417 140000) TOTIME(060417 160000) ENTTYP(*RCD)    
 *   OUTPUT(*OUTFILE) OUTFILFMT(*TYPE3) OUTFILE(library/file)    
 *   ENTDTALEN(1024)
 * </pre>
 */
public class OutputFileDAO extends DAOBase {

    private AbstractTypeDAO typeDAO;
    private String whereClause;

    public OutputFileDAO(OutputFile outputFile) throws Exception {
        super(outputFile.getConnectionName());

        switch (outputFile.getType()) {
        case TYPE5:
            typeDAO = new Type5DAO(outputFile);
            break;
        case TYPE4:
            typeDAO = new Type4DAO(outputFile);
            break;
        case TYPE3:
            typeDAO = new Type3DAO(outputFile);
            break;
        case TYPE2:
            typeDAO = new Type2DAO(outputFile);
            break;
        default:
            typeDAO = new Type1DAO(outputFile);
            break;
        }
    }

    public String getSqlStatement() {
        return typeDAO.getSqlStatement();
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public JournalEntries getJournalData(String whereClause, IProgressMonitor monitor) throws Exception {
        setWhereClause(whereClause);
        return typeDAO.load(getWhereClause(), monitor);
    }
}
