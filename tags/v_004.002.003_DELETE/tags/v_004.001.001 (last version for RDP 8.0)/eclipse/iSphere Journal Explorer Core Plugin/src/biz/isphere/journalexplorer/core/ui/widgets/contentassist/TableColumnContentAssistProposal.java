/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets.contentassist;

import biz.isphere.core.swt.widgets.ContentAssistProposal;

public class TableColumnContentAssistProposal extends ContentAssistProposal {

    public TableColumnContentAssistProposal(String fieldName, String sqlType, String description) {
        super(fieldName, getLabel(fieldName, sqlType, description));
    }

    private static String getLabel(String fieldName, String sqlType, String description) {
        return fieldName + " - " + sqlType + " - " + description;
    }

}
