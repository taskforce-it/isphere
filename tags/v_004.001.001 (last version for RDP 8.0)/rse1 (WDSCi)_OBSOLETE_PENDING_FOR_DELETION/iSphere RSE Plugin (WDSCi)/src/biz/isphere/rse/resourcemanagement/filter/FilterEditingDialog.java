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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.resourcemanagement.AbstractResource;
import biz.isphere.core.resourcemanagement.filter.AbstractFilterEditingDialog;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterBoth;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.resourcemanagement.useraction.RSEUserActionHelper;

public class FilterEditingDialog extends AbstractFilterEditingDialog {

    public FilterEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleFilterPool,
        String workspace, String repository, RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository, RSEFilterBoth[] resourceBothDifferent,
        RSEFilter[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, singleFilterPool, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
    }

    @Override
    protected void pushToWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.createFilter(filter.getFilterPool(), filter.getName(), filter.getType(), new Vector<String>(Arrays.asList(filter
            .getFilterStrings())));
    }

    @Override
    protected void deleteFromWorkspace(AbstractResource resource) {
        RSEFilter filter = (RSEFilter)resource;
        RSEFilterHelper.deleteFilter(filter.getFilterPool(), filter.getName());
    }

    @Override
    protected void updateWorkspace(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
        deleteFromWorkspace(resourceWorkspace);
        pushToWorkspace(resourceRepository);
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

}
