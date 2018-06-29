/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.rse.ISphereJournalExplorerRSEPlugin;
import biz.isphere.journalexplorer.rse.Messages;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler.DisplayJournalEntriesHandler;

import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class DisplayJournalEntriesAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    private List<DataElement> selectedObjectsList;

    public DisplayJournalEntriesAction() {
        super(Messages.Display_Journal_Entries, "", null);
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISphereJournalExplorerRSEPlugin.getDefault().getImageRegistry().getDescriptor(
            ISphereJournalExplorerRSEPlugin.IMAGE_DISPLAY_JOURNAL_ENTRIES));
        selectedObjectsList = new ArrayList<DataElement>();
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    @Override
    public void run() {

        for (DataElement dataElement : selectedObjectsList) {

            if (isMember(dataElement)) {

                String connectionName = ISeriesDataElementUtil.getConnection(dataElement).getAliasName();
                String libraryName = ISeriesDataElementHelpers.getLibrary(dataElement);
                String fileName = ISeriesDataElementHelpers.getFile(dataElement);
                String memberName = ISeriesDataElementHelpers.getFile(dataElement);
                DisplayJournalEntriesHandler.handleDisplayFileJournalEntries(connectionName, libraryName, fileName, memberName);

            } else if (isFile(dataElement)) {

                String connectionName = ISeriesDataElementUtil.getConnection(dataElement).getAliasName();
                String libraryName = ISeriesDataElementHelpers.getLibrary(dataElement);
                String fileName = ISeriesDataElementHelpers.getName(dataElement);
                String memberName = "*FIRST"; //$NON-NLS-1$
                DisplayJournalEntriesHandler.handleDisplayFileJournalEntries(connectionName, libraryName, fileName, memberName);

            }
        }
    }

    public boolean supportsSelection(IStructuredSelection structuredSelection) {

        if (getObjectsFromSelection(structuredSelection) == 1) {
            return true;
        } else {
            return false;
        }
    }

    private int getObjectsFromSelection(IStructuredSelection structuredSelection) {

        selectedObjectsList.clear();

        try {

            if (structuredSelection != null && structuredSelection.size() > 0) {
                Object[] objects = structuredSelection.toArray();
                for (Object object : objects) {
                    if (object instanceof DataElement) {
                        DataElement dataElement = (DataElement)object;
                        if (isSupportedObject(dataElement)) {
                            selectedObjectsList.add(dataElement);
                        }
                    }
                }
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        return selectedObjectsList.size();
    }

    private boolean isSupportedObject(DataElement dataElement) {
        return isFile(dataElement) || isMember(dataElement);
    }

    private boolean isFile(DataElement dataElement) {

        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isFile();
    }

    private boolean isMember(DataElement dataElement) {

        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
        return type.isMember();
    }
}
