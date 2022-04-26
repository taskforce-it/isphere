/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibm.helper;

import biz.isphere.core.internal.ISeries;

import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.model.SystemConnection;

public final class ISeriesDataElementHelper {

    public static SystemConnection getConnection(DataElement dataElement) {
        return ISeriesDataElementUtil.getConnection(dataElement);
    }

    public static String getConnectionName(DataElement dataElement) {
        return ISeriesDataElementUtil.getConnection(dataElement).getAliasName();
    }

    public static String getName(DataElement dataElement) {
        if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementUtil.getFile(dataElement);
        }
        return ISeriesDataElementUtil.getName(dataElement);
    }

    public static String getLibrary(DataElement dataElement) {
        return ISeriesDataElementUtil.getLibrary(dataElement);
    }

    public static String getFile(DataElement dataElement) {
        if (isFile(dataElement) || isSourceFile(dataElement)) {
            return ISeriesDataElementUtil.getName(dataElement);
        } else if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementUtil.getFile(dataElement);
        }
        throw new IllegalArgumentException("Data element is not a member:" // //$NON-NLS-1$
            + dataElement.getType());
    }

    public static String getMember(DataElement dataElement) {
        if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementUtil.getName(dataElement);
        }
        throw new IllegalArgumentException("Data element is not a member:" // //$NON-NLS-1$
            + dataElement.getType());
    }

    public static String getMemberType(DataElement dataElement) {
        if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementHelpers.getType(dataElement);
        }
        throw new IllegalArgumentException("Data element is not a member:" // //$NON-NLS-1$
            + dataElement.getType());
    }

    public static String getMemberText(DataElement dataElement) {
        if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementHelpers.getDescription(dataElement);
        }
        throw new IllegalArgumentException("Data element is not a member:" // //$NON-NLS-1$
            + dataElement.getType());
    }

    public static String getDescription(DataElement dataElement) {
        return ISeriesDataElementHelpers.getDescription(dataElement);
    }

    public static boolean isLibrary(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isLibrary();
        }
        return false;
    }

    public static boolean isFile(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isFile();
        }
        return false;
    }

    public static boolean isSourceFile(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);

            return type.isSourceFile();
        }
        return false;
    }

    public static boolean isMember(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isMember();
        }
        return false;
    }

    public static boolean isSourceMember(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isSourceMember();
        }
        return false;
    }

    public static boolean isDataMember(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isDataMember();
        }
        return false;
    }

    public static boolean isMessageFile(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isMessageFile();
        }
        return false;
    }

    public static boolean isJournal(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.JOURNAL);
        }
        return false;
    }

    public static boolean isBindingDirectory(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.BNDDIR);
        }
        return false;
    }

    public static boolean isDataArea(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.DTAARA);
        }
        return false;
    }

    public static boolean isDataQueue(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.DTAQ);
        }
        return false;
    }

    public static boolean isServiceProgram(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.SRVPGM);
        }
        return false;
    }

    public static boolean isProgramModule(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
            return type.isProgramModule();
        }
        return false;
    }

    public static boolean isUserSpace(Object object) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, ISeries.USRSPC);
        }
        return false;
    }

    public static boolean matchesType(Object object, String objectType) {
        if (isDataElement(object)) {
            DataElement dataElement = (DataElement)object;
            return matchesType(dataElement, objectType);
        }
        return false;
    }

    private static boolean matchesType(DataElement dataElement, String objectType) {

        ISeriesDataElementDescriptorType descriptorType = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        if (descriptorType.isObject()) {
            String strType = ISeriesDataElementHelpers.getType(dataElement);
            if (objectType.equals(strType)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDataElement(Object object) {
        if (object instanceof DataElement) {
            return true;
        }
        return false;
    }
}
