/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets.internals;

import java.util.HashMap;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public class SearchComparerSQL implements ISearchComparer {

    private HashMap<String, Integer> columnMapping;
    private String whereClause;
    private RowJEP sqljep;

    public SearchComparerSQL(HashMap<String, Integer> columnMapping) {
        this.columnMapping = columnMapping;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
        this.sqljep = null;
    }

    public boolean isMatch(JobTraceEntry jobTraceEntry) {

        try {

            if (sqljep == null) {
                sqljep = new RowJEP(whereClause);
                sqljep.parseExpression(columnMapping);
            }

            return (Boolean)sqljep.getValue(jobTraceEntry.getRow());

        } catch (ParseException e) {
            return false;
        }
    }
}
