/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.ui.PlatformUI;

import biz.isphere.rse.Messages;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

import com.ibm.etools.iseries.comm.filters.ISeriesLibraryFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSEHelper {

    public static ISystemFilter createMemberFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesMemberFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_MEMBER, filterName, _filterStrings);

    }

    public static ISystemFilter createObjectFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesObjectFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_OBJECT, filterName, _filterStrings);

    }

    public static ISystemFilter createLibraryFilter(String connectionName, String filterPoolName, String filterName,
        ISeriesLibraryFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connectionName, filterPoolName, IQSYSFilterTypes.FILTERTYPE_LIBRARY, filterName, _filterStrings);

    }

    private static ISystemFilter createFilter(String connectionName, String filterPoolName, String filterType, String filterName,
        Vector<String> filterStrings) {

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

        if (filterExists(filterPool, filterName)) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.A_filter_with_name_A_already_exists, filterName));
            return null;
        }

        try {

            ISubSystem subsystem = getConnection(connectionName).getQSYSObjectSubSystem();
            ISystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();

            return dftPoolMgr.createSystemFilter(filterPool, filterName, filterStrings, filterType);

        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }

        return null;
    }

    private static ISystemFilterPool getDefaultFilterPool(String connectionName) {

        ISystemFilterPool[] filterPools = RSEFilterHelper.getFilterPools(connectionName);
        for (ISystemFilterPool filterPool : filterPools) {
            if (filterPool.isDefault()) {
                return filterPool;
            }
        }

        return null;
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

}
