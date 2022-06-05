/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement;

import java.util.ArrayList;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.internal.core.model.SystemProfileManager;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public abstract class AbstractSystemHelper {

    private static final String OBJECT_SUBSYSTEM_ID = "com.ibm.etools.iseries.subsystems.qsys.objects"; //$NON-NLS-1$

    public static RSEProfile getProfile(String name) {

        if (name == null) {
            return null;
        }

        RSEProfile[] profiles = getProfiles();
        for (RSEProfile profile : profiles) {
            if (name.equals(profile.getName())) {
                return profile;
            }
        }

        return null;
    }

    public static RSEProfile[] getProfiles() {

        ArrayList<RSEProfile> allProfiles = new ArrayList<RSEProfile>();

        ISystemProfile[] profiles = SystemProfileManager.getDefault().getSystemProfiles();
        for (int idx = 0; idx < profiles.length; idx++) {
            RSEProfile rseProfile = new RSEProfile(profiles[idx].getName(), profiles[idx]);
            allProfiles.add(rseProfile);
        }

        RSEProfile[] rseProfiles = new RSEProfile[allProfiles.size()];
        allProfiles.toArray(rseProfiles);

        return rseProfiles;

    }

    protected static ISubSystemConfiguration getSubSystemConfiguration() {
        return RSECorePlugin.getTheSystemRegistry().getSubSystemConfiguration(OBJECT_SUBSYSTEM_ID);
    }

    protected static ISubSystem getObjectSubSystem(IBMiConnection connection) {
        return connection.getSubSystemByClass(OBJECT_SUBSYSTEM_ID);
    }

}
