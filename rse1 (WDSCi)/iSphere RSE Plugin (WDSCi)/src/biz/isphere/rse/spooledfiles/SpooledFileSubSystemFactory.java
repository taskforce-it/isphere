/*******************************************************************************
 * Copyright (c) 2012-2016 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.SpooledFileFilter;

import com.ibm.etools.systems.core.ui.actions.SystemBaseSubMenuAction;
import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemFactoryImpl;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.filters.SystemFilterPoolManager;
import com.ibm.etools.systems.filters.ui.actions.SystemChangeFilterAction;
import com.ibm.etools.systems.filters.ui.actions.SystemNewFilterAction;
import com.ibm.etools.systems.model.SystemConnection;
import com.ibm.etools.systems.subsystems.SubSystem;

public class SpooledFileSubSystemFactory extends DefaultSubSystemFactoryImpl {

    public static final String TYPE = "spooled file";

    public SpooledFileSubSystemFactory() {
        super();
    }

    @Override
    protected SubSystem createSubSystemInternal(SystemConnection conn) {
        return new SpooledFileSubSystem();
    }

    @Override
    public String getTranslatedFilterTypeProperty(SystemFilter selectedFilter) {
        return Messages.Spooled_File_Filter;
    }

    @Override
    protected SystemFilterPool createDefaultFilterPool(SystemFilterPoolManager mgr) {
        SystemFilterPool defaultPool = super.createDefaultFilterPool(mgr);
        Vector<String> strings = new Vector<String>();
        SpooledFileFilter splfFilter = new SpooledFileFilter();
        splfFilter.setUser("*CURRENT");
        strings.add(splfFilter.getFilterString());
        try {
            SystemFilter filter = mgr.createSystemFilter(defaultPool, Messages.My_spooled_files, strings);
            filter.setType(TYPE);
        } catch (Exception exc) {
        }
        return defaultPool;
    }

    @Override
    public boolean supportsNestedFilters() {
        return false;
    }

    @Override
    protected IAction[] getNewFilterPoolFilterActions(SystemFilterPool selectedPool, Shell shell) {
        SystemNewFilterAction filterAction = (SystemNewFilterAction)super.getNewFilterPoolFilterAction(selectedPool, shell);
        filterAction.setWizardPageTitle(Messages.Spooled_File_Filter);
        filterAction.setPage1Description(Messages.Create_a_new_spooled_file_filter);
        filterAction.setType(Messages.Spooled_File_Filter);
        filterAction.setText(Messages.Spooled_file_filter + "...");
        filterAction.setFilterStringEditPane(new SpooledFileFilterStringEditPane(shell));
        IAction[] actions = new IAction[1];
        actions[0] = filterAction;
        return actions;
    }

    @Override
    protected IAction getChangeFilterAction(SystemFilter selectedFilter, Shell shell) {
        SystemChangeFilterAction action = (SystemChangeFilterAction)super.getChangeFilterAction(selectedFilter, shell);
        action.setDialogTitle(Messages.Change_Spooled_File_Filter);
        action.setFilterStringEditPane(new SpooledFileFilterStringEditPane(shell));
        return action;
    }

    @Override
    protected Vector getAdditionalSubSystemActions(SubSystem arg0, Shell arg1) {

        Vector<IAction> actions = new Vector<IAction>();

        actions.add(

        new SystemBaseSubMenuAction(biz.isphere.rse.Messages.SplfDecoration_Menu, null) {

            @Override
            public IMenuManager populateSubMenu(IMenuManager menu) {
                
                menu.add(new SpooledFileDecorateWithUserDefinedAction());
                menu.add(new SpooledFileDecorateWithStatusAction());
                menu.add(new SpooledFileDecorateWithUserDataAction());
                menu.add(new SpooledFileDecorateWithCreationTimeAction());
                menu.add(new SpooledFileDecorateWithJobAction());
                
                return menu;
            }

        });

        return actions;
    }

    @Override
    protected Vector getAdditionalFilterActions(SystemFilter selectedFilter, Shell shell) {
        Vector actions = new Vector();
        return actions;
    }

    @Override
    public ImageDescriptor getSystemFilterImage(SystemFilter filter) {
        return ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SPOOLED_FILE_FILTER);
    }

    @Override
    public boolean showGenericShowInTableOnFilter() {
        return true;
    }

}
