/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.rsemanagement.filter;

import biz.isphere.core.rsemanagement.AbstractComparator;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterComparator extends AbstractComparator {

	public FilterComparator(AbstractResource[] resourcesWorkspaceToCompare, AbstractResource[] resourcesRepositoryToCompare) {
		super(resourcesWorkspaceToCompare, resourcesRepositoryToCompare);
	}

	@Override
	protected AbstractResource getInstanceForBothDifferent(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
		return new RSEFilterBoth(((RSEFilter)resourceWorkspace).getName(), (RSEFilter)resourceWorkspace, (RSEFilter)resourceRepository);
	}

}
