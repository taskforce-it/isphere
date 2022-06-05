/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import biz.isphere.core.resourcemanagement.AbstractComparator;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class UserActionComparator extends AbstractComparator {

    public UserActionComparator(AbstractResource[] resourcesWorkspaceToCompare, AbstractResource[] resourcesRepositoryToCompare) {
        super(resourcesWorkspaceToCompare, resourcesRepositoryToCompare);
    }

    @Override
    protected AbstractResource getInstanceForBothDifferent(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
        return new RSEUserActionBoth(((RSEUserAction)resourceWorkspace).getLabel(), (RSEUserAction)resourceWorkspace,
            (RSEUserAction)resourceRepository);
    }

}
