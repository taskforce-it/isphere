/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.internal.ISeries;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.Job;

public class LibraryListItem extends AbstractListItem {

    public LibraryListItem(AS400 system, Image image, String objectTypeFilter) {
        super(system, image, ISeries.SPCVAL_LIBL, objectTypeFilter);
    }

    @Override
    public String getLabel() {
        return super.getName();
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public AbstractListItem[] resolveChildren() {

        List<AbstractListItem> children = new LinkedList<AbstractListItem>();

        try {

            AS400 system = getSystem();
            Job[] jobs = system.getJobs(AS400.COMMAND);
            if (jobs.length > 1) {
                children.add(new ErrorItem("More than 1 job!")); //$NON-NLS-1$
            } else {

                Job job = jobs[0];
                if (job.getCurrentLibraryExistence()) {
                    children.add(ListItemsFactory.createCurrentLibrary(getSystem(), getObjectTypeFilter()));
                }
                for (String library : job.getUserLibraryList()) {
                    children.add(ListItemsFactory.createLibrary(getSystem(), library, getObjectTypeFilter()));
                }
            }

        } catch (Exception e) {
            children.add(new ErrorItem(ExceptionHelper.getLocalizedMessage(e)));
        }

        return children.toArray(new AbstractListItem[children.size()]);
    }
}
