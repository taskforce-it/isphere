/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.ui.SystemMenuManager;
import org.eclipse.rse.ui.filters.actions.SystemChangeFilterAction;
import org.eclipse.rse.ui.filters.actions.SystemNewFilterAction;
import org.eclipse.rse.ui.view.SubSystemConfigurationAdapter;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

public class SpooledFileSubSystemConfigurationAdapter extends SubSystemConfigurationAdapter {

    @Override
    protected IAction[] getNewFilterPoolFilterActions(SystemMenuManager menu, IStructuredSelection selection, Shell shell, String menuGroup,
        ISubSystemConfiguration config, ISystemFilterPool selectedPool) {

        SystemNewFilterAction filterAction = (SystemNewFilterAction)super.getNewFilterPoolFilterAction(config, selectedPool, shell);
        filterAction.setWizardPageTitle(Messages.Spooled_File_Filter);
        filterAction.setPage1Description(Messages.Create_a_new_spooled_file_filter);
        filterAction.setType(Messages.Spooled_File_Filter);
        filterAction.setText(Messages.Spooled_file_filter + "...");
        filterAction.setFilterStringEditPane(new SpooledFileFilterStringEditPane(shell));

        // ISystemFilterPoolManager[] filterPoolManager =
        // config.getSystemFilterPoolManagers();
        // ISystemFilterPool[] poolsToSelectFrom = null;
        // int i = 0;
        // if (i < filterPoolManager.length) {
        // poolsToSelectFrom = filterPoolManager[i].getSystemFilterPools();
        // }
        //
        // if (poolsToSelectFrom != null) {
        // filterAction.setAllowFilterPoolSelection(poolsToSelectFrom);
        // }

        IAction[] actions = new IAction[1];
        actions[0] = filterAction;
        return actions;

    }

    @Override
    protected IAction getChangeFilterAction(ISubSystemConfiguration factory, ISystemFilter selectedFilter, Shell shell) {
        SystemChangeFilterAction action = (SystemChangeFilterAction)super.getChangeFilterAction(factory, selectedFilter, shell);
        action.setDialogTitle(Messages.Change_Spooled_File_Filter);
        action.setFilterStringEditPane(new SpooledFileFilterStringEditPane(shell));
        return action;
    }

    @Override
    public ImageDescriptor getSystemFilterImage(ISystemFilter filter) {
        return ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SPOOLED_FILE_FILTER);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Vector getAdditionalFilterActions(ISubSystemConfiguration config, ISystemFilter selectedFilter, Shell shell) {
        Vector actions = new Vector();
        actions.add(getChangeFilterAction(config, selectedFilter, shell));
        return actions;
    }

}