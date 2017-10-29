/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import biz.isphere.core.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.messagefilesearch.SearchResult;

import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;

public class MessageFileSearchObjectFilterCreator extends AbstractFilterCreator implements IMessageFileSearchObjectFilterCreator {

    public boolean createObjectFilter(String connectionName, String filterPoolName, String filterName, SearchResult[] searchResults) {

        ISeriesObjectFilterString[] filterStrings = new ISeriesObjectFilterString[searchResults.length];

        for (int idx = 0; idx < searchResults.length; idx++) {

            ISeriesObjectFilterString filterString = new ISeriesObjectFilterString();
            filterString.setLibrary(searchResults[idx].getLibrary());
            filterString.setObject(searchResults[idx].getMessageFile());
            filterString.setObjectType(ISeries.MSGF);

            filterStrings[idx] = filterString;
        }

        if (RSEExportToFilterHelper.createOrUpdateObjectFilter(connectionName, filterPoolName, filterName, filterStrings) == null) {
            return false;
        } else {
            return true;
        }
    }
}
