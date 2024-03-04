/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.core.Messages;
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.MemberDescription;
import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;

public class MemberCompareItemPropertySource implements IPropertySource {

    private static final String PROPERTY_LEFT_LIBRARY = "biz.isphere.core.objectsynchronization.leftLibrary"; //$NON-NLS-1$
    private static final String PROPERTY_LEFT_FILE = "biz.isphere.core.objectsynchronization.leftFile"; //$NON-NLS-1$
    private static final String PROPERTY_LEFT_MEMBER = "biz.isphere.core.objectsynchronization.leftMember"; //$NON-NLS-1$
    private static final String PROPERTY_RIGHT_LIBRARY = "biz.isphere.core.objectsynchronization.rightLibrary"; //$NON-NLS-1$
    private static final String PROPERTY_RIGHT_FILE = "biz.isphere.core.objectsynchronization.rightFile"; //$NON-NLS-1$
    private static final String PROPERTY_RIGHT_MEMBER = "biz.isphere.core.objectsynchronization.rightMember"; //$NON-NLS-1$
    private static final String PROPERTY_SELECTION = "biz.isphere.core.objectsynchronization.selection"; //$NON-NLS-1$
    private static final String PROPERTY_ERROR_MESSAGE = "biz.isphere.core.objectsynchronization.errorMessage"; //$NON-NLS-1$

    private MemberCompareItem memberCompareItem;
    private CompareOptions compareOptions;

    private IPropertyDescriptor[] propertyDescriptors;

    public MemberCompareItemPropertySource(MemberCompareItem memberCompareItem) {
        this.memberCompareItem = memberCompareItem;
        this.compareOptions = null;
    }

    public void setCompareOptions(CompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {

        if (propertyDescriptors == null) {

            PropertyDescriptor leftLibrary = createPropertyDescriptor(PROPERTY_LEFT_LIBRARY, Messages.Library, Messages.Left);
            PropertyDescriptor leftFile = createPropertyDescriptor(PROPERTY_LEFT_FILE, Messages.File, Messages.Left);
            PropertyDescriptor leftMember = createPropertyDescriptor(PROPERTY_LEFT_MEMBER, Messages.Member, Messages.Left);

            PropertyDescriptor rightLibrary = createPropertyDescriptor(PROPERTY_RIGHT_LIBRARY, Messages.Library, Messages.Right);
            PropertyDescriptor rightFile = createPropertyDescriptor(PROPERTY_RIGHT_FILE, Messages.File, Messages.Right);
            PropertyDescriptor rightMember = createPropertyDescriptor(PROPERTY_RIGHT_MEMBER, Messages.Member, Messages.Right);

            PropertyDescriptor selection = createPropertyDescriptor(PROPERTY_SELECTION, Messages.Selection, Messages.Other);
            PropertyDescriptor errorMessage = createPropertyDescriptor(PROPERTY_ERROR_MESSAGE, Messages.Message, Messages.Other);

            propertyDescriptors = new IPropertyDescriptor[] { leftLibrary, leftFile, leftMember, rightLibrary, rightFile, rightMember, selection,
                errorMessage };
        }
        return propertyDescriptors;
    }

    private PropertyDescriptor createPropertyDescriptor(Object id, String displayName, String category) {

        PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
        descriptor.setCategory(category);

        return descriptor;
    }

    public Object getPropertyValue(Object propertyName) {

        if (memberCompareItem == null) {
            return null;
        }

        if (PROPERTY_LEFT_LIBRARY.equals(propertyName)) {
            return getLibraryUI(memberCompareItem.getLeftMemberDescription());
        } else if (PROPERTY_LEFT_FILE.equals(propertyName)) {
            return getFileUI(memberCompareItem.getLeftMemberDescription());
        } else if (PROPERTY_LEFT_MEMBER.equals(propertyName)) {
            return getLibraryUI(memberCompareItem.getLeftMemberDescription());
        } else if (PROPERTY_RIGHT_LIBRARY.equals(propertyName)) {
            return getMemberUI(memberCompareItem.getRightMemberDescription());
        } else if (PROPERTY_RIGHT_FILE.equals(propertyName)) {
            return getLibraryUI(memberCompareItem.getRightMemberDescription());
        } else if (PROPERTY_RIGHT_MEMBER.equals(propertyName)) {
            return getMemberUI(memberCompareItem.getRightMemberDescription());
        } else if (PROPERTY_SELECTION.equals(propertyName)) {
            if (compareOptions != null) {
                return getSelectionUI();
            }
        } else if (PROPERTY_ERROR_MESSAGE.equals(propertyName)) {
            return getErrorMessage();
        }
        ;

        return null;
    }

    public void resetPropertyValue(Object propertyName) {
        return;
    }

    public void setPropertyValue(Object propertyName, Object value) {
        return;
    }

    public boolean isPropertySet(Object propertyName) {
        return false;
    }

    private String getLibraryUI(MemberDescription memberDescription) {
        if (memberDescription != null) {
            return memberDescription.getLibraryName();
        }
        return Messages.EMPTY;
    }

    private String getFileUI(MemberDescription memberDescription) {
        if (memberDescription != null) {
            return memberDescription.getFileName();
        }
        return Messages.EMPTY;
    }

    private String getMemberUI(MemberDescription memberDescription) {
        if (memberDescription != null) {
            return memberDescription.getMemberName();
        }
        return Messages.EMPTY;
    }

    private String getSelectionUI() {
        int compareStatus = memberCompareItem.getCompareStatus(compareOptions);
        switch (compareStatus) {
        case MemberCompareItem.NO_ACTION:
            return Messages.Property_No_action;
        case MemberCompareItem.LEFT_MISSING:
            return Messages.Property_Copy_to_left;
        case MemberCompareItem.RIGHT_MISSING:
            return Messages.Property_Copy_to_right;
        case MemberCompareItem.LEFT_EQUALS_RIGHT:
            return Messages.Property_Left_equals_right;
        case MemberCompareItem.NOT_EQUAL:
            return Messages.Property_Left_unequal_right;
        case MemberCompareItem.ERROR:
            int tCompareStatus = memberCompareItem.getOriginalCompareStatus(compareOptions);
            if (tCompareStatus == MemberCompareItem.LEFT_MISSING) {
                return Messages.Property_Copy_to_left;
            } else if (tCompareStatus == MemberCompareItem.RIGHT_MISSING) {
                return Messages.Property_Copy_to_right;
            }
        default:
            return Messages.EMPTY;
        }
    }

    private String getErrorMessage() {
        String message = memberCompareItem.getErrorMessage();
        if (message != null) {
            return message;
        }
        return Messages.EMPTY;
    }
}
