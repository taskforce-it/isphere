/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.filter;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.resourcemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.resourcemanagement.filter.AbstractFilterEntryDialog;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterBoth;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;

public class FilterEntryDialog extends AbstractFilterEntryDialog {
    
    public FilterEntryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected RSEProfile[] getProfiles() {
        return RSEFilterHelper.getProfiles();
    }

    @Override
    protected RSEFilterPool[] getFilterPools(RSEProfile profile) {
        return RSEFilterHelper.getFilterPools(profile);
    }

    @Override
    protected RSEFilter[] getFilters(RSEProfile profile) {
        return RSEFilterHelper.getFilters(profile);
    }

    @Override
    protected RSEFilter[] getFilters(RSEFilterPool filterPool) {
        return RSEFilterHelper.getFilters(filterPool);
    }

    @Override
    protected void openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleFilterPool, String workspace,
        String repository, RSEFilter[] resourcesWorkspace, RSEFilter[] resourcesRepository, RSEFilterBoth[] resourcesBothDifferent,
        RSEFilter[] resourcesBothEqual) {
        AbstractFilterEditingDialog dialog = 
            new FilterEditingDialog(
                    getShell(),
                    editWorkspace,
                    editRepository,
                    editBoth,
                    singleFilterPool,
                    workspace,
                    repository,
                    resourcesWorkspace, 
                    resourcesRepository, 
                    resourcesBothDifferent, 
                    resourcesBothEqual);
        dialog.open();
    }

    @Override
    protected boolean saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters) {
        return XMLFilterHelper.saveFiltersToXML(toFile, singleFilterPool, filters);
    }

    @Override
    protected RSEFilter[] restoreFiltersFromXML(File fromFile, boolean singleFilterPool, RSEProfile profile, RSEFilterPool filterPool) {
        return XMLFilterHelper.restoreFiltersFromXML(fromFile, singleFilterPool, profile, filterPool);
    }

}
