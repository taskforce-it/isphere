/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibm.helper;

import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.model.SystemConnection;

public final class ISeriesDataElementHelper {

    public static SystemConnection getConnection(DataElement dataElement) {
        return ISeriesDataElementUtil.getConnection(dataElement);
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

    public static String getMember(DataElement dataElement) {
        if (isMember(dataElement) || isSourceMember(dataElement)) {
            return ISeriesDataElementUtil.getName(dataElement);
        }
        throw new IllegalArgumentException("Data element is not a member:" // //$NON-NLS-1$
            + dataElement.getType());
    }

    public static boolean isLibrary(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isLibrary();
    }

    public static boolean isFile(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isFile();
    }

    public static boolean isSourceFile(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isSourceFile();
    }

    public static boolean isMember(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isMember();
    }

    public static boolean isSourceMember(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isSourceMember();
    }

    public static boolean isMessageFile(DataElement dataElement) {
        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isMessageFile();
    }

    public static boolean isJournal(DataElement dataElement) {
        return "*JRN".equals(dataElement.getType()); //$NON-NLS-1$
    }
}
