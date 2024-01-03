/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.base.comparators.QSYSObjectPathNameComparator;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapter;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;

public class MemberRenamingRuleNumber extends AbstractMemberRenamingRule<MemberRenamingRuleNumberAdapter> {

    private static final long serialVersionUID = 614340387390813036L;

    public static String ID = "biz.isphere.core.memberrename.rules.number"; //$NON-NLS-1$

    public static String DELIMITER = "delimiter"; //$NON-NLS-1$
    public static String MIN_VALUE = "minValue"; //$NON-NLS-1$
    public static String MAX_VALUE = "maxValue"; //$NON-NLS-1$

    private Pattern memberNameFilterPattern;

    /*
     * Only used by JUnit tests
     */
    private String delimiter;
    private int minValue;
    private int maxValue;
    private boolean isFillGaps = false;

    /*
     * Used when computing the next member name
     */
    private NameProperties nameProperties;

    public MemberRenamingRuleNumber() {
        super(Messages.Label_Renaming_rule_Numerical);
    }

    protected MemberRenamingRuleNumber(String label) {
        super(label);
    }

    /**
     * @return returns <code>true</code>, when gaps in existing member names are
     *         filled, else <code>false</code>.
     */
    public boolean isFillGapsEnabled() {
        return isFillGaps;
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param enabled - specifies whether gaps in the list of existing backup
     *        member names are skip or not.
     */
    public void setFillGapsEnabled(boolean enabled) {
        this.isFillGaps = enabled;
    }

    /**
     * @return delimiter used for producing a backup member name.
     */
    public String getDelimiter() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return delimiter;
        }
        return adapter.getString(DELIMITER);
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param delimiter - specifies the delimiter used for producing a backup
     *        member name.
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter.trim();
    }

    /**
     * @return starting value of the extension that is added to the original
     *         member name, when producing a backup name.
     */
    public int getMinValue() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return minValue;
        }
        return getAdapter().getInteger(MIN_VALUE);
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param minValue - specifies the starting value of the extension that is
     *        added to the original member name, when producing a backup name.
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * @return maximum value of the extension that is added to the original
     *         member name, when producing a backup name.
     */
    public int getMaxValue() {
        MemberRenamingRuleNumberAdapter adapter = getAdapter();
        if (adapter == null) {
            return maxValue;
        }
        return getAdapter().getInteger(MAX_VALUE);
    }

    /**
     * Used by JUnit tests only.
     * 
     * @param maxValue - specifies the maximum value of the extension that is
     *        added to the original member name, when producing a backup name.
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void initialize(AS400 system, String libraryName, String fileName, String memberName) throws AS400Exception, PropertyVetoException,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {
        super.initialize(system, libraryName, fileName, memberName);

        if (getMemberNameFilter().length() <= 10) {

            String memberNameFilterMask = "^" + getBaseMemberName() + getDelimiter() + "[0-9]{" + getLengthOfExtension() + "}$"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            memberNameFilterMask = memberNameFilterMask.replaceAll("\\.", "\\\\.");

            this.memberNameFilterPattern = Pattern.compile(memberNameFilterMask);

            String[] membersOnSystemPaths = loadMemberList(getBaseMemberLibrary(), getBaseMemberFile(), getMemberNameFilter());

            calculateLastMemberNameUsedOnSystem(membersOnSystemPaths);

        } else {
            calculateLastMemberNameUsedOnSystem(null);
        }
    }

    public String getNextName() throws NoMoreNamesAvailableException, PropertyVetoException {

        if (nameProperties.currentValue >= getMaxValue()) {
            throw new NoMoreNamesAvailableException();
        }

        if (nameProperties.currentValue <= getMinValue() - 1) {
            nameProperties.currentValue = getMinValue() - 1;
        }

        String nextMemberName = null;
        while (nextMemberName == null) {

            nameProperties.currentValue++;

            nextMemberName = formatName(getBaseMemberName(), nameProperties.currentValue);
            if (nextMemberName.length() > 10) {
                // Name too long. Actor will throw exception.
                return nextMemberName;
            }

            if (exists(getSystem(), getBaseMemberLibrary(), getBaseMemberFile(), nextMemberName)) {
                // May happen, when skip gaps is disabled.
                nextMemberName = null;
            }
        }

        return nextMemberName;
    }

    protected boolean exists(AS400 system, String libraryName, String fileName, String nextMemberName) {
        return ISphereHelper.checkMember(system, libraryName, fileName, nextMemberName);
    }

    private String getMemberNameFilter() {
        return getBaseMemberName() + getDelimiter() + "*"; //$NON-NLS-1$
    }

    /*
     * Exported for JUnit tests only.
     */
    public boolean isMatchingName(String memberName) {

        if (memberNameFilterPattern == null) {
            return false;
        }

        return memberNameFilterPattern.matcher(memberName).matches();
    }

    /*
     * Exported for JUnit tests only.
     */
    public void calculateLastMemberNameUsedOnSystem(String[] existingMemberPaths) {

        // Initialize properties to their starting values.
        this.nameProperties = retrieveNameProperties(null);

        if (existingMemberPaths == null || existingMemberPaths.length == 0 || isFillGapsEnabled()) {
            return;
        }

        // Determine the last (highest) member name found on the system.
        List<QSYSObjectPathName> existingQSYSMemberNames = new ArrayList<QSYSObjectPathName>();
        for (int i = 0; i < existingMemberPaths.length; i++) {
            QSYSObjectPathName qsysMemberPath = new QSYSObjectPathName(existingMemberPaths[i]);
            if (isMatchingName(qsysMemberPath.getMemberName())) {
                existingQSYSMemberNames.add(qsysMemberPath);
            }
        }

        if (existingQSYSMemberNames.size() <= 0) {
            return;
        }

        QSYSObjectPathName[] existingQSYSMemberNamesArray = existingQSYSMemberNames.toArray(new QSYSObjectPathName[existingQSYSMemberNames.size()]);
        Arrays.sort(existingQSYSMemberNamesArray, new QSYSObjectPathNameComparator());
        QSYSObjectPathName lastQSYSMemberNameUsed = existingQSYSMemberNamesArray[existingQSYSMemberNamesArray.length - 1];

        // Set properties to the last (highest) member name found on the system.
        this.nameProperties = retrieveNameProperties(lastQSYSMemberNameUsed.getMemberName());
    }

    private String formatName(String memberName, int currentCount) throws PropertyVetoException {

        int numSpaces = getLengthOfExtension();
        String extension = StringHelper.getFixLengthLeading(Integer.toString(currentCount), numSpaces, "0"); //$NON-NLS-1$

        return String.format("%s%s%s", memberName, getDelimiter(), extension); //$NON-NLS-1$
    }

    private int getLengthOfExtension() {
        return Integer.toString(getMaxValue()).length();
    }

    private NameProperties retrieveNameProperties(String memberName) {

        int currentValue;

        if (getDelimiter() == null || getDelimiter().length() == 0) {

            throw new IllegalArgumentException("Invalid delimter. Delimiter must not be empty"); //$NON-NLS-1$

        } else {

            if (!StringHelper.isNullOrEmpty(memberName)) {

                int i = memberName.lastIndexOf(getDelimiter());
                if (i >= 0) {

                    String extension = memberName.substring(i + getDelimiter().length());
                    currentValue = Integer.parseInt(extension);

                    return new NameProperties(currentValue);
                }
            }

            currentValue = getMinValue() - 1;

            return new NameProperties(currentValue);
        }
    }

    private class NameProperties implements Serializable {

        private static final long serialVersionUID = -8447362726714216951L;

        protected int currentValue;

        public NameProperties(int currentValue) {
            this.currentValue = currentValue;
        }
    }
}
