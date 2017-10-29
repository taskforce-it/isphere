package biz.isphere.rse.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

import com.ibm.etools.iseries.core.IISeriesFilterTypes;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.impl.SystemProfileManagerImpl;

/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 ******************************************************************************/

public abstract class AbstractFilterCreator {

    public RSEFilterPool[] getFilterPools(String connectionName) {

        SystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName);

        List<RSEFilterPool> rseFilterPools = new ArrayList<RSEFilterPool>();
        for (SystemFilterPool filterPool : filterPools) {
            rseFilterPools.add(createRSEFilterPool(filterPool));
        }

        RSEFilterPool[] sortedFilterPoolNames = rseFilterPools.toArray(new RSEFilterPool[rseFilterPools.size()]);
        // Arrays.sort(sortedFilterPoolNames);

        return sortedFilterPoolNames;
    }

    private RSEFilterPool createRSEFilterPool(SystemFilterPool filterPool) {

        RSEFilterPool rseFilterPool = new RSEFilterPool(createRSEProfile(filterPool), filterPool.getName(), filterPool.isDefault(), filterPool);
        EList filters = filterPool.getFilters();

        for (int i = 0; i < filters.size(); i++) {
            RSEFilter rseFilter = null;
            SystemFilter filter = (SystemFilter)filters.get(i);
            if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_LIBRARY)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_OBJECT)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_MEMBER)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            }

            if (rseFilter != null) {
                rseFilterPool.addFilter(rseFilter);
            }
        }

        return rseFilterPool;
    }

    private RSEFilter createRSEFilter(RSEFilterPool rseFilterPool, SystemFilter filter) {

        RSEFilter rseFilter = new RSEFilter(rseFilterPool, filter.getName(), getRSEFilterType(filter), filter.getFilterStrings(), false, filter);
        rseFilter.setFilterStrings(filter.getFilterStrings());

        return rseFilter;
    }

    private String getRSEFilterType(SystemFilter filter) {

        if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_LIBRARY)) {
            return RSEFilter.TYPE_LIBRARY;
        } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_OBJECT)) {
            return RSEFilter.TYPE_OBJECT;
        } else if (filter.getType().equals(IISeriesFilterTypes.FILTERTYPE_MEMBER)) {
            return RSEFilter.TYPE_MEMBER;
        } else

            return null;
    }

    private RSEProfile createRSEProfile(SystemFilterPool filterPool) {

        SystemProfile[] systemProfiles = SystemProfileManagerImpl.getSystemProfileManager().getSystemProfiles();
        for (SystemProfile systemProfile : systemProfiles) {
            SystemFilterPool[] filterPools = SystemProfileManagerImpl.getSystemProfileManager().getSystemProfiles()[1].getFilterPools();
            for (SystemFilterPool pool : filterPools) {
                if (pool.equals(filterPool)) {
                    return new RSEProfile(systemProfile.getName(), systemProfile);
                }
            }
        }

        return null;
    }

}
