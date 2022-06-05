/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import biz.isphere.joblogexplorer.model.JobLogMessage;

public class ToStatementFilter extends AbstractStringFilter {

    public ToStatementFilter(String fromStatement) {
        super(JobLogMessage.Fields.TO_STATEMENT.fieldName());
        setValue(fromStatement);
    }

    @Override
    protected String getCurrentValue(JobLogMessage jobLogMessage) {
        return jobLogMessage.getToStatement();
    }

}
