/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public abstract class AbstractSearchArgumentEditor {

    private static final String TEXT_SEARCH_STRING_KEY = "biz.isphere.rse.search.SearchArgumentEditor.findString";
    public static final String TEXT_SEARCH_STRING = "TEXT_SEARCH_STRING";
    public static final String BUTTON_REMOVE = "BUTTON_REMOVE";
    public static final String BUTTON_ADD = "BUTTON_ADD";

    private Composite txtSearchString;
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

        cboCondition = WidgetFactory.createReadOnlyCombo(container);
        GridData gd_cboCondition = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gd_cboCondition.widthHint = 100;
        cboCondition.add(Messages.Contains);
        cboCondition.add(Messages.Contains_not);
        cboCondition.setLayoutData(gd_cboCondition);
        cboCondition.setSize(92, 21);
        cboCondition.setText(Messages.Contains);
        cboCondition.setToolTipText(Messages.Specify_how_to_search_for_the_string);

        txtSearchString = createSearchStringCombo(container, SWT.NONE, TEXT_SEARCH_STRING_KEY, 10, false);
        txtSearchString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSearchString.setSize(76, 19);

        btnCaseSensitive = WidgetFactory.createCheckbox(container);
        btnCaseSensitive.setText(Messages.Case_sensitive);
        btnCaseSensitive.setToolTipText(Messages.Specify_whether_case_should_be_considered_during_search);

        btnAdd = WidgetFactory.createPushButton(container);
        GridData gd_btnAdd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnAdd.widthHint = 40;
        btnAdd.setLayoutData(gd_btnAdd);
        btnAdd.setSize(68, 23);
        btnAdd.setText("+");
        btnAdd.setToolTipText(Messages.Add_search_condition);
        btnAdd.setData(BUTTON_ADD);

        btnRemove = WidgetFactory.createPushButton(container);
        GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnRemove.widthHint = 40;
        btnRemove.setLayoutData(gd_btnRemove);
        // btnRemove.setBounds(0, 0, 68, 23);
        btnAdd.setSize(68, 23);
        btnRemove.setText("-");
        btnRemove.setToolTipText(Messages.Remove_search_condition);
        btnRemove.setData(BUTTON_REMOVE);
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

    protected abstract Composite createSearchStringCombo(Composite aContainer, int aStyle, String aKey, int aMaxComboEntries, boolean aReadOnly);

    public abstract void addSearchStringListener(Listener aListener);

    public abstract String getSearchString();

    public abstract void setSearchString(String aString);

}
