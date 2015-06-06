/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.filter;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.resourcemanagement.AbstractResource;
import biz.isphere.core.resourcemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterBoth;

public class FilterEditingDialog extends AbstractFilterEditingDialog {

    public FilterEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleFilterPool, String workspace,
        String repository, RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository, RSEFilterBoth[] resourceBothDifferent,
        RSEFilter[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, singleFilterPool, workspace, repository, resourceWorkspace, resourceRepository, resourceBothDifferent,
            resourceBothEqual);
    }

    @Override
    protected void pushToWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.createFilter(filter.getFilterPool(), filter.getName(), filter.getType(), new Vector<String>(Arrays.asList(filter.getFilterStrings())));
    }

    @Override
    protected void deleteFromWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.deleteFilter(filter.getFilterPool(), filter.getName());
    }

    @Override
    protected boolean saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters) {
        return XMLFilterHelper.saveFiltersToXML(toFile, singleFilterPool, filters);
    }

}
