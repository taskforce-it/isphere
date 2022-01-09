/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.FilterUpdateType;
import biz.isphere.rse.Messages;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

import com.ibm.etools.iseries.comm.filters.ISeriesLibraryFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSEExportToFilterHelper {

    public static ISystemFilter createOrUpdateMemberFilter(String connectionName, String filterPoolName, String filterName,
        FilterUpdateType filterUpdateType, ISeriesMemberFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_MEMBER, filterName, filterUpdateType, _filterStrings);

    }

    public static ISystemFilter createOrUpdateObjectFilter(String connectionName, String filterPoolName, String filterName,
        FilterUpdateType filterUpdateType, ISeriesObjectFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_OBJECT, filterName, filterUpdateType, _filterStrings);

    }

    public static ISystemFilter createOrUpdateLibraryFilter(String connectionName, String filterPoolName, String filterName,
        FilterUpdateType filterUpdateType, ISeriesLibraryFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_LIBRARY, filterName, filterUpdateType, _filterStrings);

    }

    private static ISystemFilter createFilter(String connectionName, String filterPoolName, String filterType, String filterName,
        FilterUpdateType filterUpdateType, Vector<String> filterStrings) {

        ISystemFilterPool filterPool = null;

        ISystemFilterPool[] pools = RSEFilterHelper.getFilterPools(connectionName);
        if (pools != null && pools.length >= 1) {

            for (ISystemFilterPool pool : pools) {
                if (filterPoolName != null) {
                    if (pool.getName().equals(filterPoolName)) {
                        filterPool = pool;
                    }
                } else {
                    if (pool.isDefault()) {
                        filterPool = pool;
                    }
                }
            }
        }

        if (filterPool == null) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R,
                Messages.No_filter_pool_available);
            return null;
        }

        try {

            ISubSystem subsystem = getSubSystem(getConnection(connectionName));
            ISystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();

            if (filterExists(filterPool, filterName)) {
                if (filterUpdateType == FilterUpdateType.REPLACE) {
                    removeAllFilterStrings(filterPool, filterName);
                }
                updateFilterStrings(dftPoolMgr, filterPool, filterName, filterStrings);
            } else {
                return dftPoolMgr.createSystemFilter(filterPool, filterName, filterStrings, filterType);
            }

        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }

        return null;
    }

    private static void updateFilterStrings(ISystemFilterPoolManager dftPoolMgr, ISystemFilterPool filterPool, String filterName,
        Vector<String> filterStrings) {

        ISystemFilter systemFilter = filterPool.getSystemFilter(filterName);
        boolean isCaseSensitive = systemFilter.areStringsCaseSensitive();
        String[] existingFiltersStrings = systemFilter.getFilterStrings();
        if (!isCaseSensitive) {
            for (int i = 0; i < existingFiltersStrings.length; i++) {
                existingFiltersStrings[i] = existingFiltersStrings[i].toLowerCase();
            }
        }

        Set<String> existingFiltersSet = new HashSet<String>(Arrays.asList(existingFiltersStrings));
        String tFilterString;
        for (String filterString : filterStrings) {
            if (!isCaseSensitive) {
                tFilterString = filterString.toLowerCase();
            } else {
                tFilterString = filterString;
            }
            if (!existingFiltersSet.contains(tFilterString)) {
                systemFilter.addFilterString(filterString);
            }
        }

        dftPoolMgr.getProvider().filterEventFilterUpdated(filterPool.getSystemFilter(filterName));
    }

    private static void removeAllFilterStrings(ISystemFilterPool filterPool, String filterName) {

        ISystemFilter filter = filterPool.getSystemFilter(filterName);
        int count = filter.getFilterStringCount();
        while (count > 0) {
            filter.removeFilterString(0);
            count--;
        }
    }

    private static boolean filterExists(ISystemFilterPool filterPool, String filterName) {

        String[] filterNames = filterPool.getSystemFilterNames();
        for (int idx = 0; idx < filterNames.length; idx++) {
            if (filterNames[idx].equals(filterName)) {
                return true;
            }
        }

        return false;
    }

    private static IBMiConnection getConnection(String connectionName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);

        return connection;
    }

    private static ISubSystem getSubSystem(IBMiConnection connection) {
        return connection.getQSYSObjectSubSystem();
    }

}
