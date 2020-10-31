/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import java.util.ArrayList;

public abstract class AbstractComparator {

    private ArrayList<AbstractResource> resourcesWorkspace = new ArrayList<AbstractResource>();
    private ArrayList<AbstractResource> resourcesRepository = new ArrayList<AbstractResource>();
    private ArrayList<AbstractResource> resourcesBothDifferent = new ArrayList<AbstractResource>();
    private ArrayList<AbstractResource> resourcesBothEqual = new ArrayList<AbstractResource>();

    public AbstractComparator(AbstractResource[] resourcesWorkspaceToCompare, AbstractResource[] resourcesRepositoryToCompare) {

        for (int idx1 = 0; idx1 < resourcesWorkspaceToCompare.length; idx1++) {

            for (int idx2 = 0; idx2 < resourcesRepositoryToCompare.length; idx2++) {

                if (resourcesRepositoryToCompare[idx2] != null
                    && resourcesWorkspaceToCompare[idx1].getKey().equals(resourcesRepositoryToCompare[idx2].getKey())) {

                    if (resourcesWorkspaceToCompare[idx1].getValue().equals(resourcesRepositoryToCompare[idx2].getValue())) {
                        resourcesBothEqual.add(resourcesWorkspaceToCompare[idx1]);
                    } else {
                        resourcesBothDifferent
                            .add(getInstanceForBothDifferent(resourcesWorkspaceToCompare[idx1], resourcesRepositoryToCompare[idx2]));
                    }

                    resourcesWorkspaceToCompare[idx1] = null;
                    resourcesRepositoryToCompare[idx2] = null;

                    break;

                }

            }

        }

        for (int idx = 0; idx < resourcesWorkspaceToCompare.length; idx++) {
            if (resourcesWorkspaceToCompare[idx] != null) {
                resourcesWorkspace.add(resourcesWorkspaceToCompare[idx]);
            }
        }

        for (int idx = 0; idx < resourcesRepositoryToCompare.length; idx++) {
            if (resourcesRepositoryToCompare[idx] != null) {
                resourcesRepository.add(resourcesRepositoryToCompare[idx]);
            }
        }

    }

    protected abstract AbstractResource getInstanceForBothDifferent(AbstractResource resourceWorkspace, AbstractResource resourceRepository);

    public ArrayList<AbstractResource> getResourcesWorkspace() {
        return resourcesWorkspace;
    }

    public ArrayList<AbstractResource> getResourcesRepository() {
        return resourcesRepository;
    }

    public ArrayList<AbstractResource> getResourcesBothDifferent() {
        return resourcesBothDifferent;
    }

    public ArrayList<AbstractResource> getResourcesBothEqual() {
        return resourcesBothEqual;
    }

}
