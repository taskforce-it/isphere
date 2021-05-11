/*******************************************************************************
 * Copyright (c) 2012-2020 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.core.spooledfiles.SpooledFileFilter;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.handler.SaveSpooledFilesToDirectoryHandler;

import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.filters.SystemFilterReference;
import com.ibm.etools.systems.subsystems.SubSystem;

public abstract class AbstractSpooledFileSaveToDirectoryAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    private static final String SUB_MENU_ID = "biz.isphere.save.as"; //$NON-NLS-1$
    
    private String format;
    private ArrayList<Object> _selectedElements;

    public AbstractSpooledFileSaveToDirectoryAction(String text, String format, ImageDescriptor imageDescriptor) {
        super(text, "", null); //$NON-NLS-1$ 
        setContextMenuGroup("additions"); //$NON-NLS-1$
        allowOnMultipleSelection(false);
        setHelp(""); //$NON-NLS-1$
        setImageDescriptor(imageDescriptor);

        this.format = format;
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        MenuManager subMenu = getSubMenu(menu);
        subMenu.add(this);
    }

    private MenuManager getSubMenu(SystemMenuManager menu) {

        IContributionItem subMenu = menu.getMenuManager().findUsingPath(SUB_MENU_ID);
        if (subMenu == null) {
            subMenu = new MenuManager(Messages.Save_as, SUB_MENU_ID);
            menu.appendToGroup(getContextMenuGroup(), subMenu);
        }

        return (MenuManager)subMenu;
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        ArrayList<Object> _selectedElements = new ArrayList<Object>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {

            Object _object = iterSelection.next();

            if ((_object instanceof SystemFilterReference)) {

                /*
                 * Started for a filter node
                 */

                SystemFilterReference element = (SystemFilterReference)_object;
                if (SpooledFileSubSystemFactory.TYPE.equals(element.getReferencedFilter().getType())) {
                    SubSystem subSystem = getSubSystem(element);
                    if (subSystem != null && !subSystem.isOffline()) {
                        _selectedElements.add(element);
                    }
                }
            }
        }

        if (_selectedElements.isEmpty()) {
            return false;
        }

        this._selectedElements = _selectedElements;
        return true;

    }

    public void run() {

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof SystemFilterReference)) {
                SystemFilterReference filterReference = (SystemFilterReference)_object;
                if ((_object instanceof SystemFilterReference) && (getSubSystem(filterReference) instanceof SpooledFileSubSystem)) {
                    SpooledFile[] spooledFiles = loadSpooledFiles(filterReference);
                    new SaveSpooledFilesToDirectoryHandler(getShell(), format).exportSpooledFiles(spooledFiles);
                }
            }
        }
    }

    protected SpooledFile[] loadSpooledFiles(SystemFilterReference filterReference) {

        String connectionName = getConnectionName(getSubSystem(filterReference));
        Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);

        Set<SpooledFile> spooledFilesSet = new HashSet<SpooledFile>();
        Vector<SpooledFile> spooledFilesList = new Vector<SpooledFile>();

        String[] filterStrings = filterReference.getReferencedFilter().getFilterStrings();

        for (String filterString : filterStrings) {
            SpooledFileFilter spooledFileFilter = new SpooledFileFilter(filterString);
            SpooledFile[] spooledFiles = SpooledFileFactory.getSpooledFiles(connectionName, jdbcConnection, spooledFileFilter);
            for (SpooledFile spooledFile : spooledFiles) {
                if (!spooledFilesSet.contains(spooledFile)) {
                    spooledFilesSet.add(spooledFile);
                    spooledFilesList.add(spooledFile);
                }
            }
        }

        SpooledFile[] spooledFiles = spooledFilesList.toArray(new SpooledFile[spooledFilesList.size()]);

        return spooledFiles;
    }

    public String getConnectionName(SubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getSystemConnection());
    }

    private SubSystem getSubSystem(SystemFilterReference filterReference) {
        return (SubSystem)filterReference.getFilterPoolReferenceManager().getProvider();
    }

}
