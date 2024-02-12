/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import java.sql.Timestamp;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.MemberDescription;
import biz.isphere.core.objectsynchronization.TableFilterData;
import biz.isphere.core.objectsynchronization.properties.MemberCompareItemPropertySource;

public class MemberCompareItem implements Comparable<MemberCompareItem>, IAdaptable {

    private static final int OVERRIDE_STATUS_NULL = -1;

    public static final int NO_ACTION = 1;
    public static final int LEFT_MISSING = 2;
    public static final int RIGHT_MISSING = 3;
    public static final int LEFT_EQUALS_RIGHT = 4;
    public static final int NOT_EQUAL = 5;
    public static final int ERROR = 6;

    private MemberDescription leftMemberDescription;
    private MemberDescription rightMemberDescription;

    private CompareOptions compareOptions;
    private int overridenCompareStatus;
    private int oldOverridenCompareStatus;
    private String memberName;
    private String errorMessage;

    private MemberCompareItemPropertySource propertySource;

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

        if (memberDescription == null) {
            return;
        }

        if (memberName != null && memberDescription != null && !memberName.equals(memberDescription.getMemberName())) {
            throw new IllegalArgumentException("Illegal message ID: " + memberDescription.getMemberName()); //$NON-NLS-1$
        }

        this.leftMemberDescription = memberDescription;
    }

    public void setLeftMemberDescription(String connectionName, String libraryName, String fileName, String memberName, String srcType,
        Timestamp lastChanged, Long checksum, String text) {

        MemberDescription memberDescription = new MemberDescription();
        memberDescription.setConnectionName(connectionName);
        memberDescription.setLibraryName(libraryName);
        memberDescription.setFileName(fileName);
        memberDescription.setMemberName(memberName);
        memberDescription.setSourceType(srcType);
        memberDescription.setLastChangedDate(lastChanged);
        memberDescription.setChecksum(checksum);
        memberDescription.setText(text);

        setLeftMemberDescription(memberDescription);
    }

    public MemberDescription getRightMemberDescription() {
        return rightMemberDescription;
    }

    public void setRightMemberDescription(MemberDescription memberDescription) {

        if (memberDescription != null) {
            String newMemberName = memberDescription.getMemberName();
            if (memberName != null && !memberName.equals(newMemberName)) {
                throw new IllegalArgumentException(
                    "Member name of member description (" + newMemberName + ") does not match member name: " + memberName); //$NON-NLS-1$
            }
        }

        this.rightMemberDescription = memberDescription;
    }

    public void setRightMemberDescription(String connectionName, String libraryName, String fileName, String memberName, String srcType,
        Timestamp lastChanged, Long checksum, String text) {

        MemberDescription memberDescription = new MemberDescription();
        memberDescription.setConnectionName(connectionName);
        memberDescription.setLibraryName(libraryName);
        memberDescription.setFileName(fileName);
        memberDescription.setMemberName(memberName);
        memberDescription.setSourceType(srcType);
        memberDescription.setLastChangedDate(lastChanged);
        memberDescription.setChecksum(checksum);
        memberDescription.setText(text);

        setRightMemberDescription(memberDescription);
    }

    public int getCompareStatus(CompareOptions compareOptions) {

        setCompareOptions(compareOptions);

        if (overridenCompareStatus != OVERRIDE_STATUS_NULL) {
            return overridenCompareStatus;
        }

        return compareMemberDescriptions(compareOptions);
    }

    public int getOriginalCompareStatus(CompareOptions compareOptions) {
        if (oldOverridenCompareStatus != 0) {
            return oldOverridenCompareStatus;
        }
        return getCompareStatus(compareOptions);
    }

    public int compareMemberDescriptions(CompareOptions compareOptions) {

        setCompareOptions(compareOptions);

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void resetErrorStatus() {
        clearErrorStatus();
    }

    public void setErrorStatus(String errorMessage) {

        if (this.oldOverridenCompareStatus != 0) {
            throw new IllegalArgumentException("Error status not set: overridenCompareStatus <> 0");
        }

        if (StringHelper.isNullOrEmpty(errorMessage)) {
            clearErrorStatus();
        } else {
            this.oldOverridenCompareStatus = overridenCompareStatus;
            this.overridenCompareStatus = ERROR;
            this.errorMessage = errorMessage;
        }
    }

    public boolean isError() {

        if (overridenCompareStatus == ERROR) {
            return true;
        }

        return false;
    }

    private void clearErrorStatus() {

        if (overridenCompareStatus != ERROR) {
            return; // there is no error
        }

        this.errorMessage = null;
        this.overridenCompareStatus = oldOverridenCompareStatus;
        this.oldOverridenCompareStatus = 0;
    }

    public void setCompareStatus(int status, CompareOptions compareOptions) {

        if (status != LEFT_EQUALS_RIGHT && status != LEFT_MISSING && status != RIGHT_MISSING && status != NO_ACTION) {
            throw new IllegalArgumentException("Illegal status value: " + status); //$NON-NLS-1$
        }

        clearErrorStatus();

        setCompareOptions(compareOptions);

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

        setCompareOptions(compareOptions);

        int compareStatus = getCompareStatus(compareOptions);

        if (compareStatus == MemberCompareItem.ERROR) {
            return true;
        }

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

        setCompareOptions(compareOptions);

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

        setCompareOptions(compareOptions);

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

    private void setCompareOptions(CompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }

    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (propertySource == null) {
                propertySource = new MemberCompareItemPropertySource(this);
                propertySource.setCompareOptions(compareOptions);
            }
            return propertySource;
        }
        return null;
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
