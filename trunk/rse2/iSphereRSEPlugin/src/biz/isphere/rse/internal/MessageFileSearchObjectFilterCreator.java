/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import biz.isphere.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.messagefilesearch.SearchResult;

import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;


public class MessageFileSearchObjectFilterCreator implements IMessageFileSearchObjectFilterCreator {

	public boolean createObjectFilter(Object connection, String filterName, SearchResult[] searchResults) {
		
		ISeriesObjectFilterString[] filterStrings = new ISeriesObjectFilterString[searchResults.length];

		for (int idx = 0; idx < searchResults.length; idx++) {
			
			ISeriesObjectFilterString filterString = new ISeriesObjectFilterString();
			filterString.setLibrary(searchResults[idx].getLibrary());
			filterString.setObject(searchResults[idx].getMessageFile());
			filterString.setObjectType("*MSGF");

			filterStrings[idx] = filterString;
			
		}
		
		if (RSEHelper.createObjectFilter((IBMiConnection)connection, filterName, filterStrings) == null) {
			return false;
		}
		else {
			return true;
		}
		
	}

}
