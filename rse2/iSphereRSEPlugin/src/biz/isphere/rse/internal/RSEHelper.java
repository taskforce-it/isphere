/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.Vector;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolManager;
import org.eclipse.rse.core.subsystems.ISubSystem;

import com.ibm.etools.iseries.comm.filters.ISeriesLibraryFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class RSEHelper {

	public static ISystemFilter createMemberFilter(
			IBMiConnection connection, 
			String filterName,
			ISeriesMemberFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Member", filterName, _filterStrings);
		
	}

	public static ISystemFilter createObjectFilter(
			IBMiConnection connection, 
			String filterName,
			ISeriesObjectFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Object", filterName, _filterStrings);
		
	}

	public static ISystemFilter createLibraryFilter(
			IBMiConnection connection, 
			String filterName,
			ISeriesLibraryFilterString[] filterStrings) {

        Vector<String> _filterStrings = new Vector<String>();
        for (int idx = 0; idx < filterStrings.length; idx++) {
            _filterStrings.add(filterStrings[idx].toString());
        }

        return createFilter(connection, "Library", filterName, _filterStrings);
		
	}
	
	public static ISystemFilter createFilter(
			IBMiConnection connection, 
			String filterType, 
			String filterName, 
			Vector<String> filterStrings) {
		
        ISubSystem subsystem = connection.getQSYSObjectSubSystem();
        
        if (subsystem != null) {
        	
            ISystemFilterPool pools[] = subsystem.getFilterPoolReferenceManager().getReferencedSystemFilterPools();
            
            if (pools != null) {
            	
            	ISystemFilterPool defaultPool = null;
                for (int idx = 0; idx < pools.length; idx++) {
                    if (pools[idx].isDefault()) {
                        defaultPool = pools[idx];
                        break;
                    }
                }
            	
                if (defaultPool != null) {
                    
                    String[] filterNames = defaultPool.getSystemFilterNames();
                    for (int idx = 0; idx < filterNames.length; idx++) {
                    	if (filterNames[idx].equals(filterName)) {
                    		return null;
                    	}
                    }
                	
                    ISystemFilterPoolManager dftPoolMgr = subsystem.getFilterPoolReferenceManager().getDefaultSystemFilterPoolManager();
                    
                    try {
                        return dftPoolMgr.createSystemFilter(defaultPool, filterName, filterStrings, filterType);
                    }
                    catch(Exception e) {
                    }
                	
                }
                
            }
        	
        }
        
        return null;
        
	}
	
}
