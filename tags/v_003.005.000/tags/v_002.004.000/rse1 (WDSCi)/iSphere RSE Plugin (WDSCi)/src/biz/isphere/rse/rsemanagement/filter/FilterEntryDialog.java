/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.rsemanagement.filter;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.rsemanagement.filter.AbstractFilterEntryDialog;
import biz.isphere.core.rsemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.rsemanagement.filter.RSEFilter;
import biz.isphere.core.rsemanagement.filter.RSEFilterBoth;
import biz.isphere.core.rsemanagement.filter.RSEFilterPool;

public class FilterEntryDialog extends AbstractFilterEntryDialog {

	private RSEFilterPool[] filterPools;
	
	public FilterEntryDialog(Shell parentShell) {
		super(parentShell);
		filterPools = RSEFilterHelper.getFilterPools();
	}

	protected RSEFilterPool[] getFilterPools() {
		return filterPools;
	}

    @Override
    protected RSEFilter[] getFilters(RSEFilterPool filterPool) {
        return RSEFilterHelper.getFilters(filterPool);
    }

    @Override
    protected void openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, String workspace,
        String repository, RSEFilter[] resourcesWorkspace, RSEFilter[] resourcesRepository, RSEFilterBoth[] resourcesBothDifferent,
        RSEFilter[] resourcesBothEqual) {
        AbstractFilterEditingDialog dialog = 
            new FilterEditingDialog(
                    getShell(),
                    isEditWorkspace(),
                    isEditRepository(),
                    isEditBoth(),
                    workspace,
                    repository,
                    resourcesWorkspace, 
                    resourcesRepository, 
                    resourcesBothDifferent, 
                    resourcesBothEqual);
        dialog.open();
    }

    @Override
    protected boolean saveFiltersToXML(File toFile, RSEFilter[] filters) {
        return XMLFilterHelper.saveFiltersToXML(toFile, filters);
    }

    protected RSEFilter[] restoreFiltersFromXML(File fromFile, RSEFilterPool filterPool) {
        return XMLFilterHelper.restoreFiltersFromXML(fromFile, filterPool);
    }

}
