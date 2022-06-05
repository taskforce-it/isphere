/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import biz.isphere.core.messagefilecompare.TableFilterData;
import biz.isphere.core.messagefileeditor.MessageDescription;

public class MessageFileCompareItem implements Comparable<MessageFileCompareItem> {

    private static final int OVERRIDE_STATUS_NULL = -1;
    public static final int NO_ACTION = 1;
    public static final int LEFT_MISSING = 2;
    public static final int RIGHT_MISSING = 3;
    public static final int LEFT_EQUALS_RIGHT = 4;
    public static final int NOT_EQUAL = 5;

    private MessageDescription leftMessageDescription;
    private MessageDescription rightMessageDescription;
    private int overridenCompareStatus;

    private String messageId;

    public MessageFileCompareItem(MessageDescription leftMessageDescription, MessageDescription rightMessageDescription) {

        setLeftMessageDescription(leftMessageDescription);
        setRightMessageDescription(rightMessageDescription);

        clearCompareStatus();
        checkMessageDescriptions();

        if (getLeftMessageDescription() != null) {
            this.messageId = getLeftMessageDescription().getMessageId();
        } else {
            this.messageId = getRightMessageDescription().getMessageId();
        }

    }

    public String getMessageId() {

        return messageId;
    }

    public MessageDescription getLeftMessageDescription() {
        return leftMessageDescription;
    }

    public void setLeftMessageDescription(MessageDescription messageDescription) {

        if (messageId != null && messageDescription != null && !messageId.equals(messageDescription.getMessageId())) {
            throw new IllegalArgumentException("Illegal message ID: " + messageDescription.getMessageId()); //$NON-NLS-1$
        }

        this.leftMessageDescription = messageDescription;
    }

    public MessageDescription getRightMessageDescription() {
        return rightMessageDescription;
    }

    public void setRightMessageDescription(MessageDescription messageDescription) {

        if (messageId != null && messageDescription != null && !messageId.equals(messageDescription.getMessageId())) {
            throw new IllegalArgumentException("Illegal message ID: " + messageDescription.getMessageId()); //$NON-NLS-1$
        }

        this.rightMessageDescription = messageDescription;
    }

    public int getCompareStatus() {

        if (overridenCompareStatus != OVERRIDE_STATUS_NULL) {
            return overridenCompareStatus;
        }

        return compareMessageDescriptions();
    }

    public int compareMessageDescriptions() {

        if (getLeftMessageDescription() == null && getRightMessageDescription() == null) {
            return LEFT_EQUALS_RIGHT;
        } else if (getLeftMessageDescription() == null) {
            return LEFT_MISSING;
        } else if (getRightMessageDescription() == null) {
            return RIGHT_MISSING;
        } else if (leftEqualsRight(getLeftMessageDescription(), getRightMessageDescription())) {
            return LEFT_EQUALS_RIGHT;
        } else {
            return NOT_EQUAL;
        }
    }

    public void setCompareStatus(int status) {

        if (status != LEFT_EQUALS_RIGHT && status != LEFT_MISSING && status != RIGHT_MISSING && status != NO_ACTION) {
            throw new IllegalArgumentException("Illegal status value: " + status); //$NON-NLS-1$
        }

        this.overridenCompareStatus = checkStatus(status);
    }

    public void clearCompareStatus() {
        overridenCompareStatus = OVERRIDE_STATUS_NULL;
    }

    public boolean isSingle() {

        if (getLeftMessageDescription() == null || getRightMessageDescription() == null) {
            return true;
        }

        return false;
    }

    public boolean isDuplicate() {

        return !isSingle();
    }

    public boolean isSelected(TableFilterData filterData) {

        int compareStatus = getCompareStatus();

        if (isDuplicate() && !filterData.isDuplicates()) {
            return false;
        }

        if (isSingle() && !filterData.isSingles()) {
            return false;
        }

        if (compareStatus == MessageFileCompareItem.NO_ACTION) {
            return true;
        }

        if (compareStatus == MessageFileCompareItem.LEFT_MISSING && filterData.isCopyLeft()) {
            return true;
        }

        if (compareStatus == MessageFileCompareItem.RIGHT_MISSING && filterData.isCopyRight()) {
            return true;
        }

        if (compareStatus == MessageFileCompareItem.NOT_EQUAL && filterData.isCopyNotEqual()) {
            return true;
        }

        if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT && filterData.isEqual()) {
            return true;
        }

        return false;
    }

    private int checkStatus(int status) {

        if (status == NO_ACTION && isDuplicate()) {
            status = compareMessageDescriptions();
        }

        return status;
    }

    private boolean leftEqualsRight(MessageDescription left, MessageDescription right) {

        String leftText = leftMessageDescription.asComparableText(-1);
        String rightText = rightMessageDescription.asComparableText(-1);

        return compareText(leftText, rightText);
    }

    private boolean compareText(String string1, String string2) {

        if (string1 == null && string2 != null) {
            return false;
        }

        if (string2 == null && string1 != null) {
            return false;
        }

        return string1.equals(string2);
    }

    private void checkMessageDescriptions() {

        if (leftMessageDescription == null && rightMessageDescription == null) {
            throw new RuntimeException("At least one message description must not be null."); //$NON-NLS-1$
        }

        if (leftMessageDescription != null && rightMessageDescription != null) {
            if (!leftMessageDescription.getMessageId().equals(rightMessageDescription.getMessageId())) {
                throw new RuntimeException("Message IDs do not match."); //$NON-NLS-1$
            }
        }
    }

    public int compareTo(MessageFileCompareItem o) {
        return messageId.compareTo(o.getMessageId());
    }

    private String getLeftMessageFileName() {
        if (getLeftMessageDescription() == null) {
            return "null";
        }
        return getLeftMessageDescription().getLibrary() + "/" + getLeftMessageDescription().getMessageFile();
    }

    private String getRightMessageFileName() {
        if (getRightMessageDescription() == null) {
            return "null";
        }
        return getRightMessageDescription().getLibrary() + "/" + getRightMessageDescription().getMessageFile();
    }

    @Override
    public String toString() {
        return getMessageId() + "[" + getLeftMessageFileName() + ", " + getRightMessageFileName() + "]";
    }
}
