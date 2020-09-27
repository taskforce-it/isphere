/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler.SelectedFile;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler.SelectedJournal;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;
import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedJournal;
import biz.isphere.rse.ibm.helper.ISeriesDataElementHelper;

import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

/**
 * This action is used when the user requests loading journal entries from a
 * file or member.
 */
public class DisplayJournalEntriesAction extends ISeriesSystemBaseAction
		implements ISystemDynamicPopupMenuExtension {

	private List<DataElement> selectedObjectsList;

	public DisplayJournalEntriesAction() {
		super(Messages.Display_Journal_Entries, "", null);
		setContextMenuGroup("additions");
		allowOnMultipleSelection(true);
		setHelp("");
		setImageDescriptor(ISphereJournalExplorerRSEPlugin
				.getDefault()
				.getImageRegistry()
				.getDescriptor(
						ISphereJournalExplorerRSEPlugin.IMAGE_DISPLAY_JOURNAL_ENTRIES));
		selectedObjectsList = new ArrayList<DataElement>();
	}

	public void populateMenu(Shell shell, SystemMenuManager menu,
			IStructuredSelection selection, String menuGroup) {
		setShell(shell);
		menu.add(getContextMenuGroup(), this);
	}

	@Override
	public void run() {

		List<ISelectedFile> selectedFiles = new ArrayList<ISelectedFile>();
		List<ISelectedJournal> selectedJournals = new ArrayList<ISelectedJournal>();

		for (DataElement dataElement : selectedObjectsList) {

			ISelectedFile selectedFile = null;
			ISelectedJournal selectedJournal = null;

			if (ISeriesDataElementHelper.isMember(dataElement)) {

				String connectionName = ISeriesDataElementUtil.getConnection(
						dataElement).getAliasName();
				String libraryName = ISeriesDataElementHelper
						.getLibrary(dataElement);
				String fileName = ISeriesDataElementHelper.getName(dataElement);
				String memberName = ISeriesDataElementHelper
						.getMember(dataElement);

				selectedFile = new SelectedFile(connectionName, libraryName,
						fileName, memberName);

			} else if (ISeriesDataElementHelper.isFile(dataElement)) {

				String connectionName = ISeriesDataElementUtil.getConnection(
						dataElement).getAliasName();
				String libraryName = ISeriesDataElementHelper
						.getLibrary(dataElement);
				String fileName = ISeriesDataElementHelper.getName(dataElement);
				String memberName = "*ALL"; //$NON-NLS-1$

				selectedFile = new SelectedFile(connectionName, libraryName,
						fileName, memberName);
			} else if (ISeriesDataElementHelper.isJournal(dataElement)) {

				String connectionName = ISeriesDataElementUtil.getConnection(
						dataElement).getAliasName();
				String libraryName = ISeriesDataElementHelper
						.getLibrary(dataElement);
				String journalName = ISeriesDataElementHelper
						.getName(dataElement);

				selectedJournal = new SelectedJournal(connectionName,
						libraryName, journalName);
			}

			if (selectedFile != null) {
				selectedFiles.add(selectedFile);
			}

			if (selectedJournal != null) {
				selectedJournals.add(selectedJournal);
			}
		}

		if (!selectedFiles.isEmpty()) {
			DisplayJournalEntriesHandler
					.handleDisplayFileJournalEntries(selectedFiles
							.toArray(new ISelectedFile[selectedFiles.size()]));
		}

		if (!selectedJournals.isEmpty()) {
			DisplayJournalEntriesHandler
					.handleDisplayJournalEntries(selectedJournals
							.toArray(new ISelectedJournal[selectedJournals
									.size()]));
		}
	}

	public boolean supportsSelection(IStructuredSelection structuredSelection) {

		if (getObjectsFromSelection(structuredSelection) > 0) {
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
						DataElement dataElement = (DataElement) object;
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
		return ISeriesDataElementHelper.isFile(dataElement)
				|| ISeriesDataElementHelper.isMember(dataElement)
				|| ISeriesDataElementHelper.isJournal(dataElement);
	}
}
