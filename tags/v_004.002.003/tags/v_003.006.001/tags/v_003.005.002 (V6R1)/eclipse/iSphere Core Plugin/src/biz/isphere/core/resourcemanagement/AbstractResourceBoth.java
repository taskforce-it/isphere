/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

public abstract class AbstractResourceBoth<M extends AbstractResource> extends AbstractResource {

    private M resourceWorkspace;
    private M resourceRepository;

    public AbstractResourceBoth(M resourceWorkspace, M resourceRepository) {
        super(resourceWorkspace.isEditable() && resourceRepository.isEditable());
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
    }

    public M getResourceWorkspace() {
        return resourceWorkspace;
    }

    public M getResourceRepository() {
        return resourceRepository;
    }

}
