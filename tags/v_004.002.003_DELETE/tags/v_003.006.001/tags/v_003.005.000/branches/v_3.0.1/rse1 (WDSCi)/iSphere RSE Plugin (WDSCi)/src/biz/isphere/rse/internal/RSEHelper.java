/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.comm.filters.ISeriesLibraryFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.filters.SystemFilterPoolManager;
import com.ibm.etools.systems.subsystems.SubSystem;

public class RSEHelper {

    public static SystemFilter createMemberFilter(ISeriesConnection connection, String filterName, ISeriesMemberFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Member", filterName, _filterStrings);

    }

    public static SystemFilter createObjectFilter(ISeriesConnection connection, String filterName, ISeriesObjectFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Object", filterName, _filterStrings);

    }

    public static SystemFilter createLibraryFilter(ISeriesConnection connection, String filterName, ISeriesLibraryFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Library", filterName, _filterStrings);

    }

    public static SystemFilter createFilter(ISeriesConnection connection, String filterType, String filterName, Vector filterStrings) {

        SystemFilterPool filterPool = null;

        SystemFilterPool[] pools = getFilterPools(connection);



        if (pools != null) {

            if (pools.length > 1 || !pools[0].isDefault()) {
                RSESelectFilterPoolDialog selectPoolDialog = new RSESelectFilterPoolDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getShell(), pools);
                selectPoolDialog.setSelectedFilterPool(getDefaultFilterPool(connection));
                if (selectPoolDialog.open() == Dialog.OK) {
                    filterPool = selectPoolDialog.getSelectedFilterPool();
                }
            } else {
                filterPool = pools[0];
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
            SubSystem subsystem = connection.getISeriesFileSubSystem();
            SystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();

            return dftPoolMgr.createSystemFilter(filterPool, filterName, filterStrings, filterType);
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }

        return null;
    }

    private static SystemFilterPool getDefaultFilterPool(ISeriesConnection connection) {

        SystemFilterPool[] filterPools = getFilterPools(connection);
        for (SystemFilterPool filterPool : filterPools) {
            if (filterPool.isDefault()) {
                return filterPool;
            }
        }

        return null;
    }

    private static SystemFilterPool[] getFilterPools(ISeriesConnection connection) {

        SystemFilterPool pools[] = null;

        SubSystem subsystem = connection.getISeriesFileSubSystem();
        if (subsystem != null) {
            pools = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
        }

        if (pools == null) {
            pools = new SystemFilterPool[0];
        }

        return pools;
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

}
