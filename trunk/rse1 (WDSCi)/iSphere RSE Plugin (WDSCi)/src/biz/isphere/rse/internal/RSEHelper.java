/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import biz.isphere.rse.Messages;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

import com.ibm.etools.iseries.comm.filters.ISeriesLibraryFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.core.IISeriesFilterTypes;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.filters.SystemFilterPoolManager;
import com.ibm.etools.systems.subsystems.SubSystem;

public class RSEHelper {

    public static SystemFilter createMemberFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesMemberFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IISeriesFilterTypes.FILTERTYPE_MEMBER, filterName, _filterStrings);

    }

    public static SystemFilter createObjectFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesObjectFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IISeriesFilterTypes.FILTERTYPE_OBJECT, filterName, _filterStrings);

    }

    public static SystemFilter createLibraryFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesLibraryFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IISeriesFilterTypes.FILTERTYPE_LIBRARY, filterName, _filterStrings);

    }

    private static SystemFilter createFilter(String connectionName, String filterPoolName, String filterType, String filterName,
        Vector<String> filterStrings) {

        SystemFilterPool filterPool = null;

        SystemFilterPool[] pools = RSEFilterHelper.getFilterPools(connectionName);
        if (pools != null && pools.length >= 1) {

            for (SystemFilterPool pool : pools) {
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

            if (filterPool == null) {
                RSESelectFilterPoolDialog selectPoolDialog = new RSESelectFilterPoolDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getShell(), pools);
                selectPoolDialog.setSelectedFilterPool(getDefaultFilterPool(connectionName));
                if (selectPoolDialog.open() == Dialog.OK) {
                    filterPool = selectPoolDialog.getSelectedFilterPool();
                }
            }
        } else {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R,
                Messages.No_filter_pool_available);
        }

        if (filterPool == null) {
            return null;
        }

        boolean doExtendFilter = false;
        if (filterExists(filterPool, filterName)) {
            doExtendFilter = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, Messages
                .bind(Messages.A_filter_with_name_A_already_exists_Do_you_want_to_extend_the_filter, filterName));
            if (!doExtendFilter) {
                return null;
            }
        }

        try {

            SubSystem subsystem = getConnection(connectionName).getISeriesFileSubSystem();
            SystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();

            if (!doExtendFilter) {
                return dftPoolMgr.createSystemFilter(filterPool, filterName, filterStrings, filterType);
            } else {

                SystemFilter systemFilter = filterPool.getSystemFilter(filterName);
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

            }

        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }

        return null;
    }

    private static SystemFilterPool getDefaultFilterPool(String connectionName) {

        SystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName);
        for (SystemFilterPool filterPool : filterPools) {
            if (filterPool.isDefault()) {
                return filterPool;
            }
        }

        return null;
    }

    private static boolean filterExists(SystemFilterPool filterPool, String filterName) {

        Vector<String> filterNames = filterPool.getSystemFilterNames();
        for (int idx = 0; idx < filterNames.size(); idx++) {
            if (filterNames.get(idx).equals(filterName)) {
                return true;
            }

        }

        return false;
    }

    private static ISeriesConnection getConnection(String connectionName) {

        ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);

        return connection;

    }

}
