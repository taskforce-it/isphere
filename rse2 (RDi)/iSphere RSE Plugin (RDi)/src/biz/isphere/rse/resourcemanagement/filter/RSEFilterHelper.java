/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
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

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.internal.core.model.SystemProfileManager;

import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.AbstractSystemHelper;

@SuppressWarnings("restriction")
public class RSEFilterHelper extends AbstractSystemHelper {

    public static ISystemFilterPoolReference[] getConnectionObjectFilterPools(String connectionName) {
        return getConnectionFilterPools(getObjectSubSystem(connectionName));
    }

    public static ISystemFilterPoolReference[] getConnectionIFSFilterPools(String connectionName) {
        return getConnectionFilterPools(getIFSSubSystem(connectionName));
    }

    private static ISystemFilterPoolReference[] getConnectionFilterPools(ISubSystem subSystem) {

        List<ISystemFilterPoolReference> filterPools = new LinkedList<ISystemFilterPoolReference>();

        if (subSystem == null) {
            return new ISystemFilterPoolReference[0];
        }

        ISystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (ISystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            filterPools.add(systemFilterPoolReference);
        }

        return filterPools.toArray(new ISystemFilterPoolReference[filterPools.size()]);
    }

    // TODO: add support for IFS filters
    public static RSEFilterPool[] getFilterPools(RSEProfile rseProfile) {

        ArrayList<RSEFilterPool> allFilterPools = new ArrayList<RSEFilterPool>();

        ISystemProfile profile = SystemProfileManager.getDefault().getSystemProfile(rseProfile.getName());
        if (profile != null) {
            ISystemFilterPool[] filterPools = profile.getFilterPools(getSubSystemConfiguration());
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

            String rseFilterType = RSEFilterHelper.getRSEFilterType(filters[idx]);
            if (rseFilterType != null) {

                RSEFilter rseFilter = new RSEFilter(rseFilterPool, filters[idx].getName(), rseFilterType, filters[idx].getFilterStrings(), true,
                    filters[idx]);

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
                    ISubSystemConfiguration subSystem = getSubSystemConfiguration();
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
            String newType = RSEFilterHelper.getQSYSFilterType(type);
            if (newType != null) {
                try {
                    pool.getSystemFilterPoolManager().createSystemFilter(pool, name, filterStrings, newType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String getQSYSFilterType(String rseFilterType) {

        if (rseFilterType.equals(RSEFilter.TYPE_LIBRARY)) {
            return IQSYSFilterTypes.FILTERTYPE_LIBRARY;
        } else if (rseFilterType.equals(RSEFilter.TYPE_OBJECT)) {
            return IQSYSFilterTypes.FILTERTYPE_OBJECT;
        } else if (rseFilterType.equals(RSEFilter.TYPE_MEMBER)) {
            return IQSYSFilterTypes.FILTERTYPE_MEMBER;
        } else if (rseFilterType.equals(RSEFilter.TYPE_IFS)) {
            return IQSYSFilterTypes.FILTERTYPE_IFS;
        }

        return null;
    }

    public static String getRSEFilterType(ISystemFilter filter) {

        if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_LIBRARY)) {
            return RSEFilter.TYPE_LIBRARY;
        } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_OBJECT)) {
            return RSEFilter.TYPE_OBJECT;
        } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_MEMBER)) {
            return RSEFilter.TYPE_MEMBER;
        } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_IFS)) {
            return RSEFilter.TYPE_IFS;
        }

        return null;
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

    /**
     * Returns the filters of the subsystem identified by the specified filter
     * type of a given connection.
     * 
     * @param connectionName - Name of the RSE connection
     * @param rseFilterType - Filter type, must be one of the filter types
     *        defined in {@link RSEFilter}.
     * @return filter pools
     */
    public static ISystemFilterPool[] getFilterPools(String connectionName, String rseFilterType) {

        ISystemFilterPool[] pools = null;

        if (RSEFilter.TYPE_LIBRARY.equals(rseFilterType)) {
            pools = getQSYSObjectSubsystemFilterPools(connectionName);
        } else if (RSEFilter.TYPE_OBJECT.equals(rseFilterType)) {
            pools = getQSYSObjectSubsystemFilterPools(connectionName);
        } else if (RSEFilter.TYPE_MEMBER.equals(rseFilterType)) {
            pools = getQSYSObjectSubsystemFilterPools(connectionName);
        } else if (RSEFilter.TYPE_IFS.equals(rseFilterType)) {
            pools = getIFSSubsystemFilterPools(connectionName);
        } else {
            throw new IllegalArgumentException("Unsupported filter type: " + rseFilterType);
        }

        return pools;
    }

    private static ISystemFilterPool[] getQSYSObjectSubsystemFilterPools(String connectionName) {

        IBMiConnection connection = getConnection(connectionName);
        ISubSystem subSystem = connection.getQSYSObjectSubSystem();

        ISystemFilterPool pools[] = getFilterPoolsBySubsystem(connectionName, subSystem);

        return pools;
    }

    private static ISystemFilterPool[] getIFSSubsystemFilterPools(String connectionName) {

        ISystemFilterPool pools[] = getFilterPoolsBySubsystemId(connectionName, "com.ibm.etools.iseries.subsystems.ifs.files.ifs");

        return pools;
    }

    private static ISystemFilterPool[] getFilterPoolsBySubsystemId(String connectionName, String subSystemId) {

        ISystemFilterPool pools[] = null;

        IBMiConnection connection = getConnection(connectionName);
        ISubSystem subsystem = connection.getSubSystemByClass(subSystemId);
        if (subsystem != null) {
            pools = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
        }

        if (pools == null) {
            pools = new ISystemFilterPool[0];
        }

        return pools;
    }

    private static ISystemFilterPool[] getFilterPoolsBySubsystem(String connectionName, ISubSystem subSystem) {

        ISystemFilterPool pools[] = null;

        if (subSystem != null) {
            pools = subSystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
        }

        if (pools == null) {
            pools = new ISystemFilterPool[0];
        }

        return pools;
    }
}
