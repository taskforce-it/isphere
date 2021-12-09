/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapter2;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.preferences.Preferences;

public class MemberRenamingRuleNumber2 extends AbstractMemberRenamingRule {

    public static String ID = "biz.isphere.core.memberrename.rules.number2";
    public static String MIN_VALUE = "minValue";
    public static String MAX_VALUE = "maxValue";

    private static String NUMBERS = "0123456789"; //$NON-NLS-1$

    /*
     * Only used by JUnit tests
     */
    private int minValue;
    private int maxValue;

    /*
     * Used when computing the next member name
     */
    private int currentValue;
    private String baseMemberName;

    public MemberRenamingRuleNumber2() {
        super("Messages.Number_2");

        setMinValue(1);
        setMaxValue(99);
    }

    public int getMinValue() {
        MemberRenamingRuleNumberAdapter2 adapter = getAdapter();
        if (adapter == null) {
            return minValue;
        }
        return getAdapter().getInteger(MIN_VALUE);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        MemberRenamingRuleNumberAdapter2 adapter = getAdapter();
        if (adapter == null) {
            return maxValue;
        }
        return getAdapter().getInteger(MAX_VALUE);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getBaseName(String memberName) throws NoMoreNamesAvailableException, PropertyVetoException {

        initializeBaseOldName(memberName);

        String nextMemberNamePath = String.format("%s", baseMemberName);

        return nextMemberNamePath;
    }

    public String getNextName(String currentMemberName) throws NoMoreNamesAvailableException, PropertyVetoException {

        initializeBaseOldName(currentMemberName);

        if (currentValue >= getMaxValue()) {
            throw new NoMoreNamesAvailableException();
        }

        String nextMemberNamePath = formatName(baseMemberName, currentValue + 1);

        return nextMemberNamePath;
    }

    public MemberRenamingRuleNumberAdapter2 getAdapter() {

        Preferences preferences = Preferences.getInstance();
        if (preferences == null) {
            // JUnit test
            return null;
        }

        MemberRenamingRuleNumberAdapter2 adapter = (MemberRenamingRuleNumberAdapter2)preferences.getMemberRenamingRuleAdapter(this.getClass());
        return adapter;
    }

    public String formatName(String memberName) throws PropertyVetoException {

        initializeBaseOldName(memberName);

        String formattedMemberName = formatName(baseMemberName, currentValue);

        return formattedMemberName;
    }

    private String formatName(String baseOldName, int currentCount) throws PropertyVetoException {
        int numSpaces = Integer.toString(getMaxValue()).length();
        return String.format("%s%s", baseOldName, StringHelper.getFixLengthLeading(Integer.toString(currentCount), numSpaces, "0"));
    }

    private void initializeBaseOldName(String oldMemberName) {

        int i;

        i = oldMemberName.length() - 1;
        while (i >= 0) {
            String ch = oldMemberName.substring(i, i + 1);
            if (NUMBERS.indexOf(ch) == -1) {
                i++;
                break;
            }
            i--;
        }

        if (i == oldMemberName.length()) {
            baseMemberName = oldMemberName;
            currentValue = getMinValue() - 1;
        } else {
            if (i <= -1) {
                // Hypothetical, because member names cannot start with a
                // digit
                baseMemberName = "";
                currentValue = Integer.parseInt(oldMemberName);
            } else {
                baseMemberName = oldMemberName.substring(0, i);
                currentValue = Integer.parseInt(oldMemberName.substring(i));
            }
        }
    }
}
