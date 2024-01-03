/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import com.ibm.etools.iseries.comm.filters.ISeriesIFSFilterString;

import biz.isphere.core.internal.FilterUpdateType;
import biz.isphere.core.internal.IStreamFileSearchIFSFilterCreator;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.streamfilesearch.SearchResult;

public class StreamFileSearchIFSFilterCreator extends AbstractFilterCreator implements IStreamFileSearchIFSFilterCreator {

    public RSEFilterPool[] getFilterPools(String connectionName) {
        RSEFilterPool[] pools = getFilterPools(connectionName, RSEFilter.TYPE_IFS);
        return pools;
    }

    public boolean createIFSFilter(String connectionName, String filterPoolName, String filterName, FilterUpdateType filterUpdateType,
        SearchResult[] searchResults) {

        ISeriesIFSFilterString[] filterStrings = new ISeriesIFSFilterString[searchResults.length];

        for (int idx = 0; idx < searchResults.length; idx++) {

            ISeriesIFSFilterString filterString = new ISeriesIFSFilterString();
            filterString.setPath(searchResults[idx].getDirectory());
            filterString.setFile(searchResults[idx].getStreamFile());

            filterStrings[idx] = filterString;
        }

        if (RSEExportToFilterHelper.createOrUpdateIFSFilter(connectionName, filterPoolName, filterName, filterUpdateType, filterStrings) == null) {
            return false;
        } else {
            return true;
        }
    }
}
