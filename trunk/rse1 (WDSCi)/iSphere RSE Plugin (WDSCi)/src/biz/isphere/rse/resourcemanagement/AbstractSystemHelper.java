/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement;

import java.util.ArrayList;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.ibm.helper.ISeriesRSEHelper;

import com.ibm.etools.iseries.core.ISeriesSubSystemHelpers;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.impl.SystemProfileManagerImpl;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.SubSystemFactory;

public abstract class AbstractSystemHelper {

    public static final String OBJECT_SUBSYSTEM_ID = "ibm.files400"; //$NON-NLS-1$

    public static RSEProfile[] getProfiles() {

        ArrayList<RSEProfile> allProfiles = new ArrayList<RSEProfile>();

        SystemProfile[] profiles = SystemProfileManagerImpl.getSystemProfileManager().getSystemProfiles();
        for (int idx = 0; idx < profiles.length; idx++) {
            RSEProfile rseProfile = new RSEProfile(profiles[idx].getName(), profiles[idx]);
            allProfiles.add(rseProfile);
        }

        RSEProfile[] rseProfiles = new RSEProfile[allProfiles.size()];
        allProfiles.toArray(rseProfiles);

        return rseProfiles;

    }

    protected static SubSystemFactory getSubSystemConfiguration() {
        return ISeriesSubSystemHelpers.getISeriesObjectsSubSystemFactory();
    }

    protected static SubSystem getObjectSubSystem(ISeriesConnection connection) {
        return ISeriesRSEHelper.getSubSystemByClass(connection, OBJECT_SUBSYSTEM_ID);
    }

}
