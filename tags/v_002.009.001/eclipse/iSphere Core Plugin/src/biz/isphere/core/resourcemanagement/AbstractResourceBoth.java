/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

public abstract class AbstractResourceBoth extends AbstractResource {

    private AbstractResource resourceWorkspace;
    private AbstractResource resourceRepository;

    public AbstractResourceBoth(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
        super(resourceWorkspace.isEditable() && resourceRepository.isEditable());
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
    }

    public AbstractResource getResourceWorkspace() {
        return resourceWorkspace;
    }

    public AbstractResource getResourceRepository() {
        return resourceRepository;
    }

}
