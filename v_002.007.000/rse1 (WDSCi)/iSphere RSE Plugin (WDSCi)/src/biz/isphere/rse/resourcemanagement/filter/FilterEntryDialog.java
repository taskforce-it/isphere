/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.filter;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.resourcemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.resourcemanagement.filter.AbstractFilterEntryDialog;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterBoth;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

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
    protected int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleFilterPool,
        String workspace, String repository, RSEFilter[] resourcesWorkspace, RSEFilter[] resourcesRepository, RSEFilterBoth[] resourcesBothDifferent,
        RSEFilter[] resourcesBothEqual) {
        AbstractFilterEditingDialog dialog = new FilterEditingDialog(getShell(), editWorkspace, editRepository, editBoth, singleFilterPool,
            workspace, repository, resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);
        return dialog.open();
    }

    @Override
    protected boolean saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters) {

        try {
            XMLFilterHelper.saveFiltersToXML(toFile, singleFilterPool, filters);
            return true;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to save data to file: " + toFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_save_data_to_file_colon_A, toFile), new Status(
                IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, -1, ExceptionHelper.getLocalizedMessage(e), e));
            return false;
        }
    }

    @Override
    protected RSEFilter[] restoreFiltersFromXML(File fromFile, boolean singleFilterPool, RSEProfile profile, RSEFilterPool filterPool) {

        try {
            return XMLFilterHelper.restoreFiltersFromXML(fromFile, singleFilterPool, profile, filterPool);
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to load data from file: " + fromFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_load_data_from_file_colon_A, fromFile),
                new Status(IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, -1, ExceptionHelper.getLocalizedMessage(e), e));
            return null;
        }
    }

}
