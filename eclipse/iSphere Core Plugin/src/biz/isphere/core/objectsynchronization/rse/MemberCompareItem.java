/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.MemberDescription;
import biz.isphere.core.objectsynchronization.TableFilterData;

public class MemberCompareItem implements Comparable<MemberCompareItem> {

    private static final int OVERRIDE_STATUS_NULL = -1;
    public static final int NO_ACTION = 1;
    public static final int LEFT_MISSING = 2;
    public static final int RIGHT_MISSING = 3;
    public static final int LEFT_EQUALS_RIGHT = 4;
    public static final int NOT_EQUAL = 5;

    private MemberDescription leftMemberDescription;
    private MemberDescription rightMemberDescription;

    private int overridenCompareStatus;
    private String memberName;

    public MemberCompareItem(MemberDescription leftMemberDescription, MemberDescription rightMemberDescription) {

        setLeftMemberDescription(leftMemberDescription);
        setRightMemberDescription(rightMemberDescription);

        clearCompareStatus();
        checkMessageDescriptions();

        if (getLeftMemberDescription() != null) {
            this.memberName = getLeftMemberDescription().getMemberName();
        } else {
            this.memberName = getRightMemberDescription().getMemberName();
        }

    }

    public String getMemberName() {

        return memberName;
    }

    public MemberDescription getLeftMemberDescription() {
        return leftMemberDescription;
    }

    public void setLeftMemberDescription(MemberDescription memberDescription) {

        if (memberName != null && memberDescription != null && !memberName.equals(memberDescription.getMemberName())) {
            throw new IllegalArgumentException("Illegal message ID: " + memberDescription.getMemberName()); //$NON-NLS-1$
        }

        this.leftMemberDescription = memberDescription;
    }

    public MemberDescription getRightMemberDescription() {
        return rightMemberDescription;
    }

    public void setRightMemberDescription(MemberDescription memberDescription) {

        if (memberName != null && memberDescription != null && !memberName.equals(memberDescription.getMemberName())) {
            throw new IllegalArgumentException("Illegal message ID: " + memberDescription.getMemberName()); //$NON-NLS-1$
        }

        this.rightMemberDescription = memberDescription;
    }

    public int getCompareStatus(CompareOptions compareOptions) {

        if (overridenCompareStatus != OVERRIDE_STATUS_NULL) {
            return overridenCompareStatus;
        }

        return compareMemberDescriptions(compareOptions);
    }

    public int compareMemberDescriptions(CompareOptions compareOptions) {

        if (getLeftMemberDescription() == null && getRightMemberDescription() == null) {
            return LEFT_EQUALS_RIGHT;
        } else if (getLeftMemberDescription() == null) {
            return LEFT_MISSING;
        } else if (getRightMemberDescription() == null) {
            return RIGHT_MISSING;
        } else if (leftEqualsRight(getLeftMemberDescription(), getRightMemberDescription(), compareOptions)) {
            return LEFT_EQUALS_RIGHT;
        } else {
            return NOT_EQUAL;
        }
    }

    public void setCompareStatus(int status, CompareOptions compareOptions) {

        if (status != LEFT_EQUALS_RIGHT && status != LEFT_MISSING && status != RIGHT_MISSING && status != NO_ACTION) {
            throw new IllegalArgumentException("Illegal status value: " + status); //$NON-NLS-1$
        }

        this.overridenCompareStatus = checkCompareStatus(status, compareOptions);
    }

    public void clearCompareStatus() {
        overridenCompareStatus = OVERRIDE_STATUS_NULL;
    }

    public boolean isSingle() {

        if (getLeftMemberDescription() == null || getRightMemberDescription() == null) {
            return true;
        }

        return false;
    }

    public boolean isDuplicate() {

        return !isSingle();
    }

    public boolean isSelected(TableFilterData filterData, CompareOptions compareOptions) {

        int compareStatus = getCompareStatus(compareOptions);

        if (isDuplicate() && !filterData.isDuplicates()) {
            return false;
        }

        if (isSingle() && !filterData.isSingles()) {
            return false;
        }

        if (compareStatus == MemberCompareItem.NO_ACTION) {
            return true;
        }

        if (compareStatus == MemberCompareItem.LEFT_MISSING && filterData.isCopyLeft()) {
            return true;
        }

        if (compareStatus == MemberCompareItem.RIGHT_MISSING && filterData.isCopyRight()) {
            return true;
        }

        if (compareStatus == MemberCompareItem.NOT_EQUAL && filterData.isCopyNotEqual()) {
            return true;
        }

        if (compareStatus == MemberCompareItem.LEFT_EQUALS_RIGHT && filterData.isEqual()) {
            return true;
        }

        return false;
    }

    private int checkCompareStatus(int status, CompareOptions compareOptions) {

        if (compareOptions == null) {
            throw new IllegalArgumentException("Parameter 'compareOptions' is [null]."); //$NON-NLS-1$
        }

        if (status == NO_ACTION && isDuplicate()) {
            status = compareMemberDescriptions(compareOptions);
        }

        return status;
    }

    private boolean leftEqualsRight(MemberDescription left, MemberDescription right, CompareOptions compareOptions) {

        if (compareOptions == null) {
            throw new IllegalArgumentException("Parameter 'compareOptions' is [null]."); //$NON-NLS-1$
        }

        // System.out.println("isIgnoreDate: " + compareOptions.isIgnoreDate());

        int rc = left.getMemberName().compareTo(right.getMemberName());
        if (rc == 0) {
            rc = left.getSourceType().compareTo(right.getSourceType());
            if (rc == 0) {
                rc = left.getChecksum().compareTo(right.getChecksum());
                if (rc == 0 && !compareOptions.isIgnoreDate()) {
                    rc = left.getLastChangedDate().compareTo(right.getLastChangedDate());
                }
            }
        }

        if (rc == 0) {
            return true;
        }

        return false;
    }

    private void checkMessageDescriptions() {

        if (leftMemberDescription == null && rightMemberDescription == null) {
            throw new RuntimeException("At least one message description must not be null."); //$NON-NLS-1$
        }

        if (leftMemberDescription != null && rightMemberDescription != null) {
            if (!leftMemberDescription.getMemberName().equals(rightMemberDescription.getMemberName())) {
                throw new RuntimeException("Message IDs do not match."); //$NON-NLS-1$
            }
        }
    }

    public int compareTo(MemberCompareItem o) {
        return memberName.compareTo(o.getMemberName());
    }

    private String getLeftFileName() {
        if (getLeftMemberDescription() == null) {
            return "null"; //$NON-NLS-1$
        }
        return getLeftMemberDescription().getLibraryName() + "/" + getLeftMemberDescription().getFileName();
    }

    private String getRightFileName() {
        if (getRightMemberDescription() == null) {
            return "null"; //$NON-NLS-1$
        }
        return getRightMemberDescription().getLibraryName() + "/" + getRightMemberDescription().getFileName();
    }

    @Override
    public String toString() {
        return getMemberName() + "[" + getLeftFileName() + ", " + getRightFileName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
