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
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber2;

public class MemberRenamingRuleNumberAdapter2 extends AbstractMemberRenamingRuleAdapter {

    private Composite mainArea;
    private Text textMinValue;
    private Text textMaxValue;

    public void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
        super.initializeDefaultPreferences(preferenceStore);

        setDefault(MemberRenamingRuleNumber2.MIN_VALUE, "1");
        setDefault(MemberRenamingRuleNumber2.MAX_VALUE, "99");
    }

    public void storePreferences() {

        if (isDisposed()) {
            return;
        }

        setValue(MemberRenamingRuleNumber2.MIN_VALUE, textMinValue.getText());
        setValue(MemberRenamingRuleNumber2.MAX_VALUE, textMaxValue.getText());
    }

    public void loadPreferences() {

        if (isDisposed()) {
            return;
        }

        textMinValue.setText(getString(MemberRenamingRuleNumber2.MIN_VALUE));
        textMaxValue.setText(getString(MemberRenamingRuleNumber2.MAX_VALUE));
    }

    public void loadDefaultPreferences() {

        if (isDisposed()) {
            return;
        }

        textMinValue.setText(getDefaultString(MemberRenamingRuleNumber.MIN_VALUE));
        textMaxValue.setText(getDefaultString(MemberRenamingRuleNumber.MAX_VALUE));
    }

    public String validatePreferences() {

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

        textMinValue = createInteger(mainArea, "Messages.Minimum_value_colon", 9);
        textMaxValue = createInteger(mainArea, "Messages.Maximum_value_colon", 9);

        return mainArea;
    }

    private boolean isDisposed() {

        if (mainArea == null || mainArea.isDisposed()) {
            return true;
        }

        return false;
    }

    protected String getId() {
        return MemberRenamingRuleNumber2.ID;
    }
}
