/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;

import com.ibm.as400.access.AS400;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ObjectHelper;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapter;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapterExtended;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;

public class MemberRenamingRuleNumberExtended extends MemberRenamingRuleNumber {

    private static final long serialVersionUID = -5933410792210974740L;

    public static String ID = "biz.isphere.core.memberrename.rules.number.extended"; //$NON-NLS-1$

    public static String MIN_NAME_LENGTH = "minNameLength"; //$NON-NLS-1$
    public static String IS_VARYING_NAME_LENGTH = "isVaryingNameLength"; //$NON-NLS-1$

    /*
     * Only used by JUnit tests
     */
    private int minNameLength;
    private boolean isVaryingNameLength;

    public MemberRenamingRuleNumberExtended() {
        super(null);
    }

    @Override
    public String getLabel() {
        return Messages.Label_Renaming_rule_Numerical_Extended;
    }

    /**
     * @return returns <code>true</code>, when length of the new member name is
     *         of varying length, else <code>false</code>.
     */
    public boolean isVaryingNameLengthEnabled() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return isVaryingNameLength;
        }
        return adapter.getBoolean(IS_VARYING_NAME_LENGTH);
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param enabled - specifies whether the new member name can be of varying
     *        length.
     */
    public void setVaryingNameLengthEnabled(boolean enabled) {
        this.isVaryingNameLength = enabled;
    }

    /**
     * @return minimum length of the new member name.
     */
    public int getMinNameLength() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return minNameLength;
        }
        return getAdapter().getInteger(MIN_NAME_LENGTH);
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param MinNameLength - specifies the minimum length of a new member name.
     */
    public void setMinNameLength(int MinNameLength) {
        this.minNameLength = MinNameLength;
    }

    @Override
    public MemberRenamingRuleNumberAdapterExtended getAdapter() {
        return (MemberRenamingRuleNumberAdapterExtended)super.getAdapter();
    }

    @Override
    public String getBaseMemberName() {

        String memberName = super.getBaseMemberName();

        return memberName;
    }

    @Override
    public String getNextName() throws NoMoreNamesAvailableException, PropertyVetoException {

        String nextMemberName = super.getNextName();

        if (nextMemberName.length() > 10) {
            if (canShortenName()) {
                try {
                    String baseMemberName = getBaseMemberName();
                    if (baseMemberName.length() >= getMinNameLength()) {
                        int nameLength;
                        if (!isVaryingNameLengthEnabled()) {
                            nameLength = getMinNameLength();
                        } else {
                            nameLength = getBaseMemberName().length() - (nextMemberName.length() - 10);
                        }
                        if (nameLength <= baseMemberName.length()) {
                            baseMemberName = baseMemberName.substring(0, nameLength);
                            nextMemberName = shortenName(getSystem(), getBaseMemberLibrary(), getBaseMemberFile(), baseMemberName);
                        } else {
                            // keep the member name unchanged
                            System.out.println("keep the member name unchanged");
                        }
                    }
                } catch (Exception e) {
                    // ignore error
                    e.printStackTrace();
                }
            } else {
                // Name too long. Actor will throw exception.
                return nextMemberName;
            }
        }

        return nextMemberName;
    }

    private boolean canShortenName() {

        if (getMinNameLength() >= 0) {
            return true;
        }

        return false;
    }

    private String shortenName(AS400 system, String libraryName, String fileName, String baseMemberName) throws Exception {

        MemberRenamingRuleNumberExtended rule = ObjectHelper.cloneVO(this);
        rule.setDelimiter(getDelimiter());
        rule.setMinValue(getMinValue());
        rule.setMaxValue(getMaxValue());
        rule.setFillGapsEnabled(isFillGapsEnabled());
        rule.minNameLength = -1;

        rule.initialize(system, libraryName, fileName, baseMemberName);

        return rule.getNextName();
    }
}
