/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.adapters;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.Messages;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public class MemberRenamingRuleNumberAdapter extends AbstractMemberRenamingRuleAdapter {

    private Composite mainArea;
    private Text textDelimiter;
    private Text textMinValue;
    private Text textMaxValue;

    public void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
        super.initializeDefaultPreferences(preferenceStore);

        setDefault(MemberRenamingRuleNumber.DELIMITER, ".");
        setDefault(MemberRenamingRuleNumber.MIN_VALUE, "1");
        setDefault(MemberRenamingRuleNumber.MAX_VALUE, "99");
    }

    public void storePreferences() {

        if (isDisposed()) {
            return;
        }

        setValue(MemberRenamingRuleNumber.DELIMITER, textDelimiter.getText());
        setValue(MemberRenamingRuleNumber.MIN_VALUE, textMinValue.getText());
        setValue(MemberRenamingRuleNumber.MAX_VALUE, textMaxValue.getText());
    }

    public void loadPreferences() {

        if (isDisposed()) {
            return;
        }

        textDelimiter.setText(getString(MemberRenamingRuleNumber.DELIMITER));
        textMinValue.setText(getString(MemberRenamingRuleNumber.MIN_VALUE));
        textMaxValue.setText(getString(MemberRenamingRuleNumber.MAX_VALUE));
    }

    public void loadDefaultPreferences() {

        if (isDisposed()) {
            return;
        }

        textDelimiter.setText(getDefaultString(MemberRenamingRuleNumber.DELIMITER));
        textMinValue.setText(getDefaultString(MemberRenamingRuleNumber.MIN_VALUE));
        textMaxValue.setText(getDefaultString(MemberRenamingRuleNumber.MAX_VALUE));
    }

    public String validatePreferences() {

        if (isDisposed()) {
            return null;
        }

        if (textMinValue.getText().length() == 0) {
            textMinValue.setFocus();
            return Messages.Invalid_or_missing_numeric_value;
        }

        if (textMaxValue.getText().length() == 0) {
            textMaxValue.setFocus();
            return Messages.Invalid_or_missing_numeric_value;
        }

        return null;
    }

    /**
     * Overwritten in order to produce the adapter specific configuration
     * properties.
     */
    public Composite createComposite(Composite parent) {

        mainArea = createMainArea(parent);

        textDelimiter = createText(mainArea, Messages.Label_Delimiter_colon);
        textMinValue = createInteger(mainArea, Messages.Label_Minimum_value_colon, 9);
        textMaxValue = createInteger(mainArea, Messages.Label_Maximum_value_colon, 9);

        return mainArea;
    }

    private boolean isDisposed() {

        if (mainArea == null || mainArea.isDisposed()) {
            return true;
        }

        return false;
    }

    protected String getId() {
        return MemberRenamingRuleNumber.ID;
    }
}
