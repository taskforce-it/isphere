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
import biz.isphere.core.Messages;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapter;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.preferences.Preferences;

public class MemberRenamingRuleNumber extends AbstractMemberRenamingRule {

    public static String ID = "biz.isphere.core.memberrename.rules.number"; //$NON-NLS-1$
    public static String DELIMITER = "delimiter"; //$NON-NLS-1$
    public static String MIN_VALUE = "minValue"; //$NON-NLS-1$
    public static String MAX_VALUE = "maxValue"; //$NON-NLS-1$

    /*
     * Only used by JUnit tests
     */
    private String delimiter;
    private int minValue;
    private int maxValue;

    /*
     * Used when computing the next member name
     */
    private int currentValue;
    private String baseMemberName;

    public MemberRenamingRuleNumber() {
        super(Messages.Label_Renaming_rule_Numerical);
    }

    public String getDelimiter() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return delimiter;
        }
        return adapter.getString(DELIMITER);
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter.trim();
    }

    public int getMinValue() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return minValue;
        }
        return getAdapter().getInteger(MIN_VALUE);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return maxValue;
        }
        return getAdapter().getInteger(MAX_VALUE);
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getBaseName(String memberName) throws NoMoreNamesAvailableException, PropertyVetoException {

        initializeBaseName(memberName);

        String nextMemberNamePath = String.format("%s%s", baseMemberName, getDelimiter()); //$NON-NLS-1$

        return nextMemberNamePath;
    }

    public String getNextName(String currentMemberName) throws NoMoreNamesAvailableException, PropertyVetoException {

        initializeBaseName(currentMemberName);

        if (currentValue >= getMaxValue()) {
            throw new NoMoreNamesAvailableException();
        }

        String nextMemberNamePath = formatName(baseMemberName, currentValue + 1);

        return nextMemberNamePath;
    }

    public MemberRenamingRuleNumberAdapter getAdapter() {

        Preferences preferences = Preferences.getInstance();
        if (preferences == null) {
            // JUnit test0
            return null;
        }

        MemberRenamingRuleNumberAdapter adapter = (MemberRenamingRuleNumberAdapter)preferences.getMemberRenamingRuleAdapter(this.getClass());
        return adapter;
    }

    private String formatName(String baseOldName, int currentCount) throws PropertyVetoException {
        int numSpaces = Integer.toString(getMaxValue()).length();
        return String.format("%s%s%s", baseOldName, getDelimiter(), StringHelper.getFixLengthLeading(Integer.toString(currentCount), numSpaces, "0")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void initializeBaseName(String oldMemberName) {

        if (getDelimiter() == null || getDelimiter().length() == 0) {

            throw new RuntimeException("Invalid delimter. Delimiter must not be empty."); //$NON-NLS-1$

        } else {

            int i = oldMemberName.lastIndexOf(getDelimiter());

            if (i <= -1) {
                baseMemberName = oldMemberName;
                currentValue = getMinValue() - 1;
            } else {
                baseMemberName = oldMemberName.substring(0, i);
                currentValue = Integer.parseInt(oldMemberName.substring(i + getDelimiter().length()));
            }
        }
    }
}
