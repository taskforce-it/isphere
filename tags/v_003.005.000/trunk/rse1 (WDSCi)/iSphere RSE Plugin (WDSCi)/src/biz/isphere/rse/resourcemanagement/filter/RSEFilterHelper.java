/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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

import org.eclipse.emf.common.util.EList;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.AbstractSystemHelper;

import com.ibm.etools.iseries.core.IISeriesFilterTypes;
import com.ibm.etools.iseries.core.ISeriesSubSystemHelpers;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.filters.SystemFilterPoolManager;
import com.ibm.etools.systems.filters.SystemFilterPoolReference;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.impl.SystemProfileManagerImpl;
import com.ibm.etools.systems.subsystems.SubSystem;

public class RSEFilterHelper extends AbstractSystemHelper {

    public static final String OBJECT_SUBSYSTEM_ID = "ibm.files400"; //$NON-NLS-1$

    public static SystemFilterPoolReference[] getConnectionFilterPools(String connectionName) {

        List<SystemFilterPoolReference> filterPools = new LinkedList<SystemFilterPoolReference>();

        ISeriesConnection connection = getConnection(connectionName);
        SubSystem subSystem = getObjectSubSystem(connection);
        SystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (SystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            filterPools.add(systemFilterPoolReference);
        }

        return filterPools.toArray(new SystemFilterPoolReference[filterPools.size()]);
    }

    public static SystemFilter[] getFilterPoolFilters(String connectionName, String systemFilterPoolName) {

        List<SystemFilter> filters = new LinkedList<SystemFilter>();

        ISeriesConnection connection = getConnection(connectionName);
        SubSystem subSystem = getObjectSubSystem(connection);
        SystemFilterPoolReference[] filterPoolReferences = subSystem.getSystemFilterPoolReferenceManager().getSystemFilterPoolReferences();
        for (SystemFilterPoolReference systemFilterPoolReference : filterPoolReferences) {
            if (systemFilterPoolName == null || systemFilterPoolName.equals(filterPoolReferences)) {
                SystemFilter[] poolFilters = systemFilterPoolReference.getReferencedFilterPool().getSystemFilters();
                for (SystemFilter poolFilter : poolFilters) {
                    if (!poolFilter.isPromptable()) {
                        filters.add(poolFilter);
                    }
                }
                break;
            }
        }

        return filters.toArray(new SystemFilter[filters.size()]);
    }

    public static RSEFilterPool[] getFilterPools(RSEProfile rseProfile) {

        ArrayList<RSEFilterPool> allFilterPools = new ArrayList<RSEFilterPool>();

        SystemProfile profile = SystemProfileManagerImpl.getSystemProfileManager().getSystemProfile(rseProfile.getName());
        if (profile != null) {
            SystemFilterPool[] filterPools = profile.getFilterPools(getSubSystemConfiguration());
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

        EList filters = ((SystemFilterPool)rseFilterPool.getOrigin()).getFilters();

        ArrayList<RSEFilter> rseFilters = new ArrayList<RSEFilter>();

        for (int idx = 0; idx < filters.size(); idx++) {

            SystemFilter filter = (SystemFilter)filters.get(idx);

            String type;
            if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_LIBRARY)) {
                type = RSEFilter.TYPE_LIBRARY;
            } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_OBJECT)) {
                type = RSEFilter.TYPE_OBJECT;
            } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_MEMBER)) {
                type = RSEFilter.TYPE_MEMBER;
            } else {
                type = RSEFilter.TYPE_UNKNOWN;
            }

            if (!type.equals(RSEFilter.TYPE_UNKNOWN)) {

                RSEFilter rseFilter = new RSEFilter(rseFilterPool, filter.getName(), type, filter.getFilterStrings(), true, filter);

                rseFilters.add(rseFilter);

            }

        }

        RSEFilter[] _rseFilters = new RSEFilter[rseFilters.size()];
        rseFilters.toArray(_rseFilters);
        return _rseFilters;

    }

    public static void createFilter(RSEFilterPool filterPool, String name, String type, Vector<String> filterStrings) {

        SystemFilterPool pool = (SystemFilterPool)filterPool.getOrigin();
        if (pool == null) {
            RSEFilterPool[] pools = getFilterPools(filterPool.getProfile());
            for (int idx = 0; idx < pools.length; idx++) {
                if (pools[idx].getName().equals(filterPool.getName())) {
                    pool = (SystemFilterPool)pools[idx].getOrigin();
                }
            }
            if (pool == null) {

                SystemProfile profile = SystemProfileManagerImpl.getSystemProfileManager().getSystemProfile(filterPool.getProfile().getName());
                if (profile != null) {
                    SystemFilterPoolManager manager = ISeriesSubSystemHelpers.getISeriesObjectsSubSystemFactory().getFilterPoolManager(profile);
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
                newType = IISeriesFilterTypes.FILTERTYPE_LIBRARY;
            } else if (type.equals(RSEFilter.TYPE_OBJECT)) {
                newType = IISeriesFilterTypes.FILTERTYPE_OBJECT;
            } else if (type.equals(RSEFilter.TYPE_MEMBER)) {
                newType = IISeriesFilterTypes.FILTERTYPE_MEMBER;
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
        SystemFilter[] filters = ((SystemFilterPool)filterPool.getOrigin()).getSystemFilters();
        for (int idx = 0; idx < filters.length; idx++) {
            if (filters[idx].getName().equals(name)) {
                try {
                    ((SystemFilterPool)filterPool.getOrigin()).getSystemFilterPoolManager().deleteSystemFilter(filters[idx]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SystemFilterPool[] getFilterPools(String connectionName) {

        SystemFilterPool pools[] = null;

        ISeriesConnection connection = getConnection(connectionName);
        SubSystem subsystem = connection.getISeriesFileSubSystem();
        if (subsystem != null) {
            pools = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
        }

        if (pools == null) {
            pools = new SystemFilterPool[0];
        }

        return pools;
    }

    public static SystemFilterPool getDefaultFilterPool(String connectionName) {

        SystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName);
        for (SystemFilterPool filterPool : filterPools) {
            if (filterPool.isDefault()) {
                return filterPool;
            }
        }

        return null;
    }

    private static ISeriesConnection getConnection(String connectionName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);

        return connection;

    }

}
