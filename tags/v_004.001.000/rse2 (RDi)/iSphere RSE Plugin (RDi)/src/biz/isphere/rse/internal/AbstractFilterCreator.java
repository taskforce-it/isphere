package biz.isphere.rse.internal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.model.IRSEPersistableContainer;
import org.eclipse.rse.core.model.ISystemProfile;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;

/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

public abstract class AbstractFilterCreator {

    public RSEFilterPool[] getFilterPools(String connectionName) {

        ISystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName);

        List<RSEFilterPool> rseFilterPools = new ArrayList<RSEFilterPool>();
        for (ISystemFilterPool filterPool : filterPools) {
            rseFilterPools.add(createRSEFilterPool(filterPool));
        }

        RSEFilterPool[] sortedFilterPoolNames = rseFilterPools.toArray(new RSEFilterPool[rseFilterPools.size()]);
        // Arrays.sort(sortedFilterPoolNames);

        return sortedFilterPoolNames;
    }

    private RSEFilterPool createRSEFilterPool(ISystemFilterPool filterPool) {

        RSEFilterPool rseFilterPool = new RSEFilterPool(createRSEProfile(filterPool), filterPool.getName(), filterPool.isDefault(), filterPool);
        ISystemFilter[] filters = filterPool.getFilters();

        for (ISystemFilter filter : filters) {
            RSEFilter rseFilter = null;
            if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_LIBRARY)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_OBJECT)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_MEMBER)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            }

            if (rseFilter != null) {
                rseFilterPool.addFilter(rseFilter);
            }
        }

        return rseFilterPool;
    }

    private RSEFilter createRSEFilter(RSEFilterPool rseFilterPool, ISystemFilter filter) {

        RSEFilter rseFilter = new RSEFilter(rseFilterPool, filter.getName(), getRSEFilterType(filter), filter.getFilterStrings(), false, filter);
        rseFilter.setFilterStrings(filter.getFilterStrings());

        return rseFilter;
    }

    private String getRSEFilterType(ISystemFilter filter) {

        if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_LIBRARY)) {
            return RSEFilter.TYPE_LIBRARY;
        } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_OBJECT)) {
            return RSEFilter.TYPE_OBJECT;
        } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_MEMBER)) {
            return RSEFilter.TYPE_MEMBER;
        } else

            return null;
    }

    private RSEProfile createRSEProfile(ISystemFilterPool filterPool) {

        RSEProfile rseProfile = null;

        IRSEPersistableContainer parentProfile = filterPool.getPersistableParent();
        if (parentProfile instanceof ISystemProfile) {
            ISystemProfile systemProfile = (ISystemProfile)parentProfile;
            rseProfile = new RSEProfile(systemProfile.getName(), systemProfile);
        }

        return rseProfile;
    }

}
