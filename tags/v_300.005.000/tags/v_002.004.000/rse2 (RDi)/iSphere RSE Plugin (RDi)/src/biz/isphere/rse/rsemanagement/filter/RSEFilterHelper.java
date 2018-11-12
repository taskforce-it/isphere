/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.rsemanagement.filter;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.internal.core.model.SystemProfileManager;

import biz.isphere.core.rsemanagement.filter.RSEFilter;
import biz.isphere.core.rsemanagement.filter.RSEFilterPool;

import com.ibm.etools.iseries.subsystems.qsys.IQSYSFilterTypes;

@SuppressWarnings("restriction")
public class RSEFilterHelper {

	public static RSEFilterPool[] getFilterPools() {

	    ArrayList<RSEFilterPool> allFilterPools = new ArrayList<RSEFilterPool>();
		
		ISystemProfile[] profiles = SystemProfileManager.getDefault().getSystemProfiles();
		for (int idx1 = 0; idx1 < profiles.length; idx1++) {
			ISystemFilterPool[] filterPools = profiles[idx1].getFilterPools(RSECorePlugin.getTheSystemRegistry().getSubSystemConfiguration("com.ibm.etools.iseries.subsystems.qsys.objects"));
			for (int idx2 = 0; idx2 < filterPools.length; idx2++) {
            	RSEFilterPool rseFilterPool = new RSEFilterPool(
            			profiles[idx1].getName() + "." + filterPools[idx2].getName(),
            			filterPools[idx2].isDefault(),
            			filterPools[idx2]);
				allFilterPools.add(rseFilterPool);
			}
		}
        
		RSEFilterPool[] rseFilterPools = new RSEFilterPool[allFilterPools.size()];
        allFilterPools.toArray(rseFilterPools);
        
        return rseFilterPools;
		
	}

	public static RSEFilter[] getFilters(RSEFilterPool filterPool) {

		ISystemFilter[] filters = ((ISystemFilterPool)filterPool.getOrigin()).getFilters();

		ArrayList<RSEFilter> rseFilters = new ArrayList<RSEFilter>();

        for (int idx = 0; idx < filters.length; idx++) {

        	String type;
        	if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_LIBRARY)) {
        		type = RSEFilter.TYPE_LIBRARY;
        	}
        	else if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_OBJECT)) {
        		type = RSEFilter.TYPE_OBJECT;
        	}
        	else if (filters[idx].getType().equals(IQSYSFilterTypes.FILTERTYPE_MEMBER)) {
        		type = RSEFilter.TYPE_MEMBER;
        	}
        	else {
        		type = RSEFilter.TYPE_UNKNOWN;
        	}

        	if (!type.equals(RSEFilter.TYPE_UNKNOWN)) {
                
                RSEFilter rseFilter = new RSEFilter(
                        filterPool,
                        filters[idx].getName(),
                        type,
                        filters[idx].getFilterStrings(),
                        true,
                        filters[idx]);

                rseFilters.add(rseFilter);
        	    
        	}
        	
        }
		
        RSEFilter[] _rseFilters = new RSEFilter[rseFilters.size()];
        rseFilters.toArray(_rseFilters);
		return _rseFilters;
		
	}
	 
	public static void createFilter(RSEFilterPool filterPool, String name, String type, Vector<String> filterStrings) {
		String newType = null;
		if (type.equals(RSEFilter.TYPE_LIBRARY)) {
			newType = IQSYSFilterTypes.FILTERTYPE_LIBRARY;
		}
		else if (type.equals(RSEFilter.TYPE_OBJECT)) {
			newType = IQSYSFilterTypes.FILTERTYPE_OBJECT;
		}
		else if (type.equals(RSEFilter.TYPE_MEMBER)) {
			newType = IQSYSFilterTypes.FILTERTYPE_MEMBER;
		}
		if (newType != null) {
	        try {
	            ((ISystemFilterPool)filterPool.getOrigin()).getSystemFilterPoolManager().createSystemFilter(((ISystemFilterPool)filterPool.getOrigin()), name, filterStrings, newType);
	        } catch (Exception e) {
				e.printStackTrace();
	        }
		}
	}
	
	public static void deleteFilter(RSEFilterPool filterPool, String name) {
        ISystemFilter[] filters = ((ISystemFilterPool)filterPool.getOrigin()).getSystemFilters();
        for (int idx = 0; idx < filters.length; idx++) {
            if (filters[idx].getName().equals(name)) {
            	try {
            		((ISystemFilterPool)filterPool.getOrigin()).getSystemFilterPoolManager().deleteSystemFilter(filters[idx]);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
	}
	
}
