/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibm.helper;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.subsystems.SubSystem;

public final class ISeriesRSEHelper {

    public static SubSystem getSubSystemByClass(ISeriesConnection connection, String factoryID) {

        SubSystem[] allSubSystems = connection.getSubSystems();

        for (int loop = 0; loop < allSubSystems.length; loop++) {
            if (factoryID.equals(allSubSystems[loop].getFactoryId())) {
                return allSubSystems[loop];
            }
        }
        return null;
    }

}
