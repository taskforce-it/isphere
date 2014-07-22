/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.sourcefilesearch.SearchOptions;
import biz.isphere.rse.Messages;

import com.ibm.etools.systems.core.ui.widgets.SystemHistoryCombo;

public class SearchArgumentEditor {

    public static final String TEXT_SEARCH_STRING = "TEXT_SEARCH_STRING";
    public static final String BUTTON_REMOVE = "BUTTON_REMOVE";
    public static final String BUTTON_ADD = "BUTTON_ADD";
    
    private SystemHistoryCombo txtSearchString;
    private Button btnRemove;
    private Button btnAdd;
    private Composite container;
    private Button btnCaseSensitive;
    private Combo cboCondition;

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(final Composite aParent) {

        container = new Composite(aParent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gl_tContainer = new GridLayout(5, false);
        gl_tContainer.horizontalSpacing = 10;
        container.setLayout(gl_tContainer);

        cboCondition = new Combo(container, SWT.READ_ONLY);
        GridData gd_cboCondition = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gd_cboCondition.widthHint = 100;
        cboCondition.add(Messages.Contains);
        cboCondition.add(Messages.Contains_not);
        cboCondition.setLayoutData(gd_cboCondition);
        cboCondition.setSize(92, 21);
        cboCondition.setText(Messages.Contains);
        cboCondition.setToolTipText(Messages.Specify_how_to_search_for_the_string);

        txtSearchString = new SystemHistoryCombo(container, 0, "biz.isphere.core.search.SearchArgumentEditor.findString", 10, false);
        txtSearchString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSearchString.setSize(76, 19);
        txtSearchString.setTextLimit(SearchOptions.MAX_STRING_SIZE);
        txtSearchString.setToolTipText(Messages.Enter_or_select_search_string);
        txtSearchString.getCombo().setData(TEXT_SEARCH_STRING);

        btnCaseSensitive = new Button(container, SWT.CHECK);
        btnCaseSensitive.setText(Messages.Case_sensitive);
        btnCaseSensitive.setToolTipText(Messages.Specify_whether_case_should_be_considered_during_search);

        btnAdd = new Button(container, SWT.NONE);
        GridData gd_btnAdd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnAdd.widthHint = 40;
        btnAdd.setLayoutData(gd_btnAdd);
        btnAdd.setSize(68, 23);
        btnAdd.setText("+");
        btnAdd.setToolTipText(Messages.Add_search_condition);
        btnAdd.setData(BUTTON_ADD);

        btnRemove = new Button(container, SWT.NONE);
        GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnRemove.widthHint = 40;
        btnRemove.setLayoutData(gd_btnRemove);
        // btnRemove.setBounds(0, 0, 68, 23);
        btnAdd.setSize(68, 23);
        btnRemove.setText("-");
        btnRemove.setToolTipText(Messages.Remove_search_condition);
        btnRemove.setData(BUTTON_REMOVE);
    }

    public void addSearchStringListener(Listener aListener) {
        txtSearchString.getCombo().addListener(SWT.Modify, aListener);
    }

    public void addButtonListener(Listener aListener) {
        btnAdd.addListener(SWT.Selection, aListener);
        btnRemove.addListener(SWT.Selection, aListener);
    }

    public boolean hasButton(Button aButton) {
        if (btnAdd.equals(aButton) || btnRemove.equals(aButton)) {
            return true;
        }
        return false;
    }

    public void dispose() {
        container.dispose();
    }

    public void setParent(Composite aParent) {
        container.setParent(aParent);
    }

    public void setFocus() {
        txtSearchString.setFocus();
    }

    public int getCompareCondition() {
        if (Messages.Contains.equals(cboCondition.getText())) {
            return SearchOptions.CONTAINS;
        } else {
            return SearchOptions.CONTAINS_NOT;
        }
    }

    public void setCompareCondition(int aCondition) {
        if (aCondition == SearchOptions.CONTAINS) {
            cboCondition.setText(Messages.Contains);
        } else {
            cboCondition.setText(Messages.Contains_not);
        }
    }

    public String getSearchString() {
        return txtSearchString.getText();
    }

    public void setSearchString(String aString) {
        txtSearchString.setText(aString);
    }

    public boolean isCaseSensitive() {
        return btnCaseSensitive.getSelection();
    }

    public void setCase(boolean anIsCaseSensitive) {
        btnCaseSensitive.setSelection(anIsCaseSensitive);
    }

    public Rectangle getBounds() {
        return container.getBounds();
    }

    public void setAddButtonEnablement(boolean anIsEnabled) {
        btnAdd.setEnabled(anIsEnabled);
    }
    
    public SearchArgument getSearchArgument() {
        return new SearchArgument(getSearchString(), isCaseSensitive(), getCompareCondition());
    }
}
