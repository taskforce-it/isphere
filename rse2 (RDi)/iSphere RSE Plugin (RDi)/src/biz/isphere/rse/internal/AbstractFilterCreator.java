/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.model.IRSEPersistableContainer;
import org.eclipse.rse.core.model.ISystemProfile;

import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

public abstract class AbstractFilterCreator {

    /**
     * Returns the filters of the subsystem identified by the specified filter
     * type of a given connection.
     * 
     * @param connectionName - Name of the RSE connection
     * @param filterType - Filter type, must be one of the filter types defined
     *        in {@link RSEFilter}.
     * @return filter pools
     */
    protected RSEFilterPool[] getFilterPools(String connectionName, String filterType) {

        ISystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName, filterType);

        List<RSEFilterPool> rseFilterPools = new ArrayList<RSEFilterPool>();
        for (ISystemFilterPool filterPool : filterPools) {
            rseFilterPools.add(createRSEFilterPool(filterPool));
        }

        RSEFilterPool[] sortedFilterPoolNames = rseFilterPools.toArray(new RSEFilterPool[rseFilterPools.size()]);
        // Arrays.sort(sortedFilterPoolNames);

        return sortedFilterPoolNames;
    }

    /**
     * Returns the filters of the object subsystem of a given connection.
     * 
     * @param connectionName - Name of the RSE connection
     * @return filter pools
     */
    public RSEFilterPool[] getFilterPools(String connectionName) {
        RSEFilterPool[] pools = getFilterPools(connectionName, RSEFilter.TYPE_OBJECT);
        return pools;
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
            } else if (filter.getType().equals(IQSYSFilterTypes.FILTERTYPE_IFS)) {
                rseFilter = createRSEFilter(rseFilterPool, filter);
            }

            if (rseFilter != null) {
                rseFilterPool.addFilter(rseFilter);
            }
        }

        return rseFilterPool;
    }

    private RSEFilter createRSEFilter(RSEFilterPool rseFilterPool, ISystemFilter filter) {

        RSEFilter rseFilter = new RSEFilter(rseFilterPool, filter.getName(), RSEFilterHelper.getRSEFilterType(filter), filter.getFilterStrings(),
            false, filter);
        rseFilter.setFilterStrings(filter.getFilterStrings());

        return rseFilter;
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
