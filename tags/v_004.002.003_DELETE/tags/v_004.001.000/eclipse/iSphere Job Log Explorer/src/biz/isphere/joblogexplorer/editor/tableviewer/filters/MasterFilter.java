/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class MasterFilter extends ViewerFilter {

    List<IMessagePropertyFilter> filters;
    RowJEP sqljep;
    boolean isLogError;

    public MasterFilter() {
        this.filters = new ArrayList<IMessagePropertyFilter>();
    }

    private void prepareSql() throws ParseException {

        StringBuilder whereClause = new StringBuilder();

        for (IMessagePropertyFilter filter : filters) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(filter.getWhereClause());
        }

        setWhereCondition(whereClause.toString());
    }

    private void setWhereCondition(String whereClause) throws ParseException {

        isLogError = true;

        if (StringHelper.isNullOrEmpty(whereClause)) {
            sqljep = null;
            return;
        }

        HashMap<String, Integer> columnMapping = JobLogMessage.getColumnMapping();
        sqljep = new RowJEP(whereClause);
        sqljep.parseExpression(columnMapping);
    }

    public void addFilter(IMessagePropertyFilter filter) throws ParseException {

        if (filters.size() > 0 && (filters.get(0) instanceof NativeSQLFilter)) {
            throw new IllegalArgumentException("No filters allowed, when NativeSQLFilter is set."); //$NON-NLS-1$
        }

        if (filter instanceof NativeSQLFilter) {
            removeAllFilters();
        }

        filters.add(filter);

        prepareSql();
    }

    public void removeAllFilters() {
        filters.clear();
    }

    public int countFilters() {
        return filters.size();
    }

    @Override
    public boolean select(Viewer tableViewer, Object parentElement, Object element) {

        if (element instanceof JobLogMessage) {

            JobLogMessage jobLogMessage = (JobLogMessage)element;
            if (jobLogMessage.isSelected()) {
                return true;
            }

            if (sqljep == null) {

                /*
                 * Should never be used anymore.
                 */

                // System.out.println("==> Using compare filters ...");

                for (IMessagePropertyFilter filter : filters) {
                    if (!filter.xselect(tableViewer, parentElement, jobLogMessage)) {
                        return false;
                    }
                }

                return true;

            } else {

                // System.out.println("==> Using SQLJEP ...");

                try {
                    return (Boolean)sqljep.getValue(jobLogMessage.getRow());
                } catch (ParseException e) {
                    if (isLogError) {
                        ISpherePlugin.logError("*** MasterFilter: Failed to get SQL compare result ***", e); //$NON-NLS-1$
                        isLogError = false;
                    }
                }

            }

        }

        return true;
    }
}
