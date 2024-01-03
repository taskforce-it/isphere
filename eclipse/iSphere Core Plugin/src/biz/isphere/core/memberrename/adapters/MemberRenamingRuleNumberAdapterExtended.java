/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.adapters;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumberExtended;

public class MemberRenamingRuleNumberAdapterExtended extends MemberRenamingRuleNumberAdapter {

    private Text textMinNameLength;
    private Button chkIsVaryingNameLength;

    protected String getId() {
        return MemberRenamingRuleNumberExtended.ID;
    }

    @Override
    public void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
        super.initializeDefaultPreferences(preferenceStore);

        // String delimiter =
        // getDefaultString(MemberRenamingRuleNumberExtended.DELIMITER);
        // String maxvalue =
        // getDefaultString(MemberRenamingRuleNumberExtended.MAX_VALUE);

        // int maxMinLengthAllowed = 10 - (delimiter.length() +
        // maxvalue.length());
        int maxMinLengthAllowed = 4;

        setDefault(MemberRenamingRuleNumberExtended.MIN_NAME_LENGTH, maxMinLengthAllowed);
        setDefault(MemberRenamingRuleNumberExtended.IS_VARYING_NAME_LENGTH, true);
    }

    @Override
    public void storePreferences() {
        super.storePreferences();

        setValue(MemberRenamingRuleNumberExtended.MIN_NAME_LENGTH, textMinNameLength.getText());
        setValue(MemberRenamingRuleNumberExtended.IS_VARYING_NAME_LENGTH, chkIsVaryingNameLength.getSelection());
    }

    @Override
    public void loadPreferences() {
        super.loadPreferences();

        if (isDisposed()) {
            return;
        }

        textMinNameLength.setText(getString(MemberRenamingRuleNumberExtended.MIN_NAME_LENGTH));
        chkIsVaryingNameLength.setSelection(getBoolean(MemberRenamingRuleNumberExtended.IS_VARYING_NAME_LENGTH));
    }

    @Override
    public void loadDefaultPreferences() {
        super.loadDefaultPreferences();

        textMinNameLength.setText(getDefaultString(MemberRenamingRuleNumberExtended.MIN_NAME_LENGTH));
        chkIsVaryingNameLength.setSelection(getDefaultBoolean(MemberRenamingRuleNumberExtended.IS_VARYING_NAME_LENGTH));
    }

    @Override
    public String validatePreferences() {
        String errorMessage = super.validatePreferences();
        if (!StringHelper.isNullOrEmpty(errorMessage)) {
            return errorMessage;
        }

        if (isDisposed()) {
            return null;
        }

        if (StringHelper.isNullOrEmpty(textMinNameLength.getText())) {
            textMinNameLength.setFocus();
            return Messages.Invalid_or_missing_numeric_value;
        }

        int maxMinLengthValueAllowed = 10 - (getDelimiter().length() + getMaximumValue().length());
        int minLength = Integer.parseInt(textMinNameLength.getText());
        if (minLength > maxMinLengthValueAllowed) {
            textMinNameLength.setFocus();
            return Messages.bind(Messages.Error_Invalid_value_A_Value_must_be_between_B_and_C, new Object[] { textMinNameLength.getText(),
                new Integer(1), new Integer(maxMinLengthValueAllowed) });
        }

        return null;
    }

    private String getDelimiter() {
        return textDelimiter.getText();
    }

    private String getMaximumValue() {
        return textMaxValue.getText();
    }

    @Override
    public Composite createComposite(Composite parent) {
        Composite mainArea = super.createComposite(parent);

        textMinNameLength = createInteger(mainArea, Messages.Label_Minimum_name_length_colon, Messages.Tooltip_Minimum_name_length, 2);
        chkIsVaryingNameLength = createCheckbox(mainArea, Messages.Label_Is_varying_length, Messages.Tooltip_Is_variying_length);

        return mainArea;
    }

    @Override
    protected String getHelpURL() {
        return "/biz.isphere.core.help/html/copymembers/preferences/rule_numerical_extended/rule_numerical_extended.html";
    }
}
