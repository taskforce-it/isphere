/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.search;

import org.eclipse.rse.ui.widgets.SystemHistoryCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import biz.isphere.core.search.AbstractSearchArgumentEditor;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.rse.Messages;

public class SearchArgumentEditor extends AbstractSearchArgumentEditor {

    private SystemHistoryCombo txtSearchString;

    public SearchArgumentEditor() {
        this(false);
    }

    public SearchArgumentEditor(boolean regularExpressions) {
        setRegularExpressionsOption(regularExpressions);
    }
    
    @Override
    public void addSearchStringListener(Listener aListener) {
        txtSearchString.getCombo().addListener(SWT.Modify, aListener);
    }

    @Override
    protected Composite createSearchStringCombo(Composite aContainer, int aStyle, String aKey, int aMaxComboEntries, boolean aReadOnly) {
        txtSearchString = new SystemHistoryCombo(aContainer, aStyle, aKey, aMaxComboEntries, aReadOnly);
        txtSearchString.setTextLimit(SearchOptions.MAX_STRING_SIZE);
        txtSearchString.setToolTipText(Messages.Enter_or_select_search_string);
        txtSearchString.getCombo().setData(TEXT_SEARCH_STRING);
        return txtSearchString;
    }

    @Override
    public String getSearchString() {
        return txtSearchString.getText();
    }

    @Override
    public void setSearchString(String aString) {
        txtSearchString.setText(aString);
    }
}
