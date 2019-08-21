/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.Vector;

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

        SubSystem subsystem = connection.getISeriesFileSubSystem();

        if (subsystem != null) {

            SystemFilterPool pools[] = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();

            if (pools != null) {

                SystemFilterPool defaultPool = null;
                for (int idx = 0; idx < pools.length; idx++) {
                    if (pools[idx].isDefault()) {
                        defaultPool = pools[idx];
                        break;
                    }
                }

                if (defaultPool != null) {

                    Vector filterNames = defaultPool.getSystemFilterNames();
                    for (int idx = 0; idx < filterNames.size(); idx++) {
                        if (((String)filterNames.get(idx)).equals(filterName)) {
                            return null;
                        }
                    }

                    SystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();

                    try {
                        return dftPoolMgr.createSystemFilter(defaultPool, filterName, filterStrings, filterType);
                    } catch (Exception e) {
                    }

                }

            }

        }

        return null;

    }

}
