/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import biz.isphere.core.messagefilesearch.SearchResult;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;

public interface IMessageFileSearchObjectFilterCreator {

    public boolean createObjectFilter(String connectionName, String filterPoolName, String filterName, FilterUpdateType filterUpdateType,
        SearchResult[] searchResults);

    public RSEFilterPool[] getFilterPools(String connectionName);

}
