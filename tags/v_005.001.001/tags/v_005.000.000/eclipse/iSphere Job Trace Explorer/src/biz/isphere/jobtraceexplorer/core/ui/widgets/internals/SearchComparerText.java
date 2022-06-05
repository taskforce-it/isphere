/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets.internals;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceEntryColumnUI;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceViewerFactory;

public class SearchComparerText implements ISearchComparer {

    private TableColumn[] tableColumns;
    int[] indices;
    private String pattern;

    public SearchComparerText(TableColumn[] tableColumns) {
        this.tableColumns = tableColumns;
        this.indices = getTextColumnIndices();
    }

    public void setWhereClause(String pattern) {
        String trimmedPattern = pattern.trim().toLowerCase();
        if (!trimmedPattern.startsWith(StringHelper.WILDCARD_GROUP) && !trimmedPattern.endsWith(StringHelper.WILDCARD_GROUP)) {
            this.pattern = StringHelper.WILDCARD_GROUP + trimmedPattern + StringHelper.WILDCARD_GROUP;
        } else {
            this.pattern = trimmedPattern;
        }
    }

    public boolean isMatch(JobTraceEntry jobTraceEntry) {

        for (int i = 0; i < indices.length; i++) {
            TableColumn tableColumn = getColumn(indices[i]);
            String columnName = JobTraceViewerFactory.getColumnName(tableColumn);
            String uiValue = jobTraceEntry.getValueForUi(columnName);
            if (StringHelper.matchesGeneric(uiValue.toLowerCase(), pattern)) {
                return true;
            }
        }

        return false;
    }

    private TableColumn getColumn(int index) {
        return getColumns()[index];
    }

    private TableColumn[] getColumns() {
        return tableColumns;
    }

    private int[] getTextColumnIndices() {

        List<Integer> indices = new LinkedList<Integer>();

        TableColumn[] tableColumns = getColumns();
        for (int i = 0; i < tableColumns.length; i++) {
            if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.PGM_NAME)) {
                indices.add(i);
            } else if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.PGM_LIB)) {
                indices.add(i);
            } else if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.MOD_NAME)) {
                indices.add(i);
            } else if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.MOD_LIB)) {
                indices.add(i);
            } else if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.PROC_NAME)) {
                indices.add(i);
            } else if (JobTraceViewerFactory.isColumn(tableColumns[i], JobTraceEntryColumnUI.CALLER_PROC_NAME)) {
                indices.add(i);
            }

        }

        int[] tIndices = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            tIndices[i] = indices.get(i);
        }

        return tIndices;
    }
}
