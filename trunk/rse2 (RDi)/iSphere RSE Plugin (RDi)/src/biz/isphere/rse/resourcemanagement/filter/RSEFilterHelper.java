/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.internal.core.model.SystemProfileManager;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;

import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

@SuppressWarnings("restriction")
public class RSEFilterHelper {

    public static final String OBJECT_SUBSYSTEM_ID = "com.ibm.etools.iseries.subsystems.qsys.objects"; //$NON-NLS-1$

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

    public static ISystemFilterPoolReference[] getConnectionFilterPools(String connectionName) {

        List<ISystemFilterPoolReference> filterPools = new LinkedList<ISystemFilterPoolReference>();

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        ISubSystem subSystem = connection.getSubSystemByClass(OBJECT_SUBSYSTEM_ID);
        ISystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (ISystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            filterPools.add(systemFilterPoolReference);
        }

        return filterPools.toArray(new ISystemFilterPoolReference[filterPools.size()]);
    }

    public static ISystemFilter[] getFilterPoolFilters(String connectionName, String systemFilterPoolName) {

        List<ISystemFilter> filters = new LinkedList<ISystemFilter>();

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        ISubSystem subSystem = connection.getSubSystemByClass(OBJECT_SUBSYSTEM_ID);
        ISystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (ISystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            if (systemFilterPoolName == null || systemFilterPoolName.equals(filterPoolReferences)) {
                ISystemFilter[] poolFilters = systemFilterPoolReference.getReferencedFilterPool().getFilters();
                for (ISystemFilter poolFilter : poolFilters) {
                    if (!poolFilter.isPromptable()) {
                        filters.add(poolFilter);
                    }
                }
                break;
            }
        }

        return filters.toArray(new ISystemFilter[filters.size()]);
    }

    public static RSEFilterPool[] getFilterPools(RSEProfile rseProfile) {

        ArrayList<RSEFilterPool> allFilterPools = new ArrayList<RSEFilterPool>();

        ISystemProfile profile = SystemProfileManager.getDefault().getSystemProfile(rseProfile.getName());
        if (profile != null) {
            ISystemFilterPool[] filterPools = profile.getFilterPools(RSECorePlugin.getTheSystemRegistry().getSubSystemConfiguration(
                OBJECT_SUBSYSTEM_ID));
            for (int idx2 = 0; idx2 < filterPools.length; idx2++) {
                RSEFilterPool rseFilterPool = new RSEFilterPool(rseProfile, filterPools[idx2].getName(), filterPools[idx2].isDefault(),
                    filterPools[idx2]);
                allFilterPools.add(rseFilterPool);
            }
        }

        RSEFilterPool[] rseFilterPools = new RSEFilterPool[allFilterPools.size()];
        allFilterPools.toArray(rseFilterPools);

        return rseFilterPools;

    }

    public static RSEFilter[] getFilters(RSEProfile rseProfile) {

        ArrayList<RSEFilter> allFilters = new ArrayList<RSEFilter>();
        RSEFilterPool[] filterPools = getFilterPools(rseProfile);
        for (int idx1 = 0; idx1 < filterPools.length; idx1++) {
            RSEFilter[] filters = getFilters(filterPools[idx1]);
            for (int idx2 = 0; idx2 < filters.length; idx2++) {
                allFilters.add(filters[idx2]);
            }
        }

        RSEFilter[] _filters = new RSEFilter[allFilters.size()];
        allFilters.toArray(_filters);

        return _filters;
    }

    public static RSEFilter[] getFilters(RSEFilterPool rseFilterPool) {

        ISystemFilter[] filters = ((ISystemFilterPool)rseFilterPool.getOrigin()).getFilters();

        ArrayList<RSEFilter> rseFilters = new ArrayList<RSEFilter>();

        for (int idx = 0; idx < filters.length; idx++) {

            String type;
            if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_LIBRARY)) {
                type = RSEFilter.TYPE_LIBRARY;
            } else if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_OBJECT)) {
                type = RSEFilter.TYPE_OBJECT;
            } else if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_MEMBER)) {
                type = RSEFilter.TYPE_MEMBER;
            } else {
                type = RSEFilter.TYPE_UNKNOWN;
            }

            if (!type.equals(RSEFilter.TYPE_UNKNOWN)) {

                RSEFilter rseFilter = new RSEFilter(rseFilterPool, filters[idx].getName(), type, filters[idx].getFilterStrings(), true, filters[idx]);

                rseFilters.add(rseFilter);

            }

        }

        RSEFilter[] _rseFilters = new RSEFilter[rseFilters.size()];
        rseFilters.toArray(_rseFilters);
        return _rseFilters;

    }

    public static void createFilter(RSEFilterPool filterPool, String name, String type, Vector<String> filterStrings) {

        ISystemFilterPool pool = (ISystemFilterPool)filterPool.getOrigin();
        if (pool == null) {
            RSEFilterPool[] pools = getFilterPools(filterPool.getProfile());
            for (int idx = 0; idx < pools.length; idx++) {
                if (pools[idx].getName().equals(filterPool.getName())) {
                    pool = (ISystemFilterPool)pools[idx].getOrigin();
                }
            }
            if (pool == null) {
                ISystemProfile profile = SystemProfileManager.getDefault().getSystemProfile(filterPool.getProfile().getName());
                if (profile != null) {
                    ISubSystemConfiguration subSystem = RSECorePlugin.getTheSystemRegistry().getSubSystemConfiguration(OBJECT_SUBSYSTEM_ID);
                    ISystemFilterPoolManager manager = subSystem.getFilterPoolManager(profile);
                    try {
                        pool = manager.createSystemFilterPool(filterPool.getName(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (pool != null) {
            String newType = null;
            if (type.equals(RSEFilter.TYPE_LIBRARY)) {
                newType = IQSYSFilterTypes.FILTERTYPE_LIBRARY;
            } else if (type.equals(RSEFilter.TYPE_OBJECT)) {
                newType = IQSYSFilterTypes.FILTERTYPE_OBJECT;
            } else if (type.equals(RSEFilter.TYPE_MEMBER)) {
                newType = IQSYSFilterTypes.FILTERTYPE_MEMBER;
            }
            if (newType != null) {
                try {
                    pool.getSystemFilterPoolManager().createSystemFilter(pool, name, filterStrings, newType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void deleteFilter(RSEFilterPool filterPool, String name) {
        ISystemFilter[] filters = ((ISystemFilterPool)filterPool.getOrigin()).getSystemFilters();
        for (int idx = 0; idx < filters.length; idx++) {
            if (filters[idx].getName().equals(name)) {
                try {
                    ((ISystemFilterPool)filterPool.getOrigin()).getSystemFilterPoolManager().deleteSystemFilter(filters[idx]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ISystemFilterPool[] getFilterPools(String connectionName) {

        ISystemFilterPool pools[] = null;

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        ISubSystem subsystem = connection.getQSYSObjectSubSystem();
        if (subsystem != null) {
            pools = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
        }

        if (pools == null) {
            pools = new ISystemFilterPool[0];
        }

        return pools;
    }

}
