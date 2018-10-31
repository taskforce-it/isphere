/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.widgets.AbstractJournalEntriesViewer;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

/**
 * This view displays the properties of a journal entry record.
 */
public class JournalEntryDetailsView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalEntryDetailsView"; //$NON-NLS-1$

    private JournalEntryDetailsViewer viewer;

    public JournalEntryDetailsView() {
    }

    @Override
    public void createPartControl(Composite parent) {

        viewer = new JournalEntryDetailsViewer(parent);
        viewer.addSelectionChangedListener(this);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        createActions();
        createToolBar();
        setActionEnablement(viewer.getSelection());
    }

    @Override
    public void dispose() {
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.removeSelectionListener(this);

        super.dispose();
    };

    private void createActions() {

    }

    private void createToolBar() {
        // no toolbar defined
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Called by the WindowSelectionService, when another JournalEntry is
     * selected in one of the following views:
     * <ul>
     * <li>biz.isphere.journalexplorer.core.ui.views.JournalExplorerView</li>
     * <li>biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView</li>
     * </ul>
     */
    public void selectionChanged(IWorkbenchPart viewPart, ISelection selection) {

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionList;

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionElement;
        Object currentSelection;
        List<JournalProperties> input = new LinkedList<JournalProperties>();

        if (viewPart instanceof JournalExplorerView || viewPart instanceof JournalEntryViewerView) {

            if (viewPart instanceof JournalExplorerView) {
                AbstractJournalEntriesViewer currentViewer = ((JournalExplorerView)viewPart).getCurrentViewer();
                if (currentViewer != null) {
                    JournalEntry[] selectedItems = currentViewer.getSelectedItems();
                    selection = new StructuredSelection(new StructuredSelection(selectedItems));
                } else {
                    selection = null;
                }
            }

            if (selection instanceof IStructuredSelection) {
                structuredSelectionList = ((IStructuredSelection)selection).iterator();
                while (input.size() < 2 && structuredSelectionList.hasNext()) {
                    structuredSelectionElement = ((IStructuredSelection)structuredSelectionList.next()).iterator();
                    while (input.size() < 2 && structuredSelectionElement.hasNext()) {
                        currentSelection = structuredSelectionElement.next();
                        if (currentSelection instanceof JournalEntry) {
                            Runnable loadPropertiesJob = new LoadPropertiesRunnable(input, (JournalEntry)currentSelection);
                            BusyIndicator.showWhile(getSite().getShell().getDisplay(), loadPropertiesJob);
                        } else if (currentSelection instanceof JournalProperties) {
                            input.add((JournalProperties)currentSelection);
                        }

                    }
                }

                if (input.size() > 1) {
                    input.clear();
                }

                refreshViewer(input);
            }
        }

        setActionEnablement(viewer.getSelection());
    }

    private void refreshViewer(List<JournalProperties> input) {

        viewer.setInput(input.toArray());
        viewer.expandAll();
    }

    public void selectionChanged(SelectionChangedEvent event) {

        ITreeSelection selection = getSelection(event);
        setActionEnablement(selection);
    }

    private void setActionEnablement(ISelection selection) {
        // no actions implemented
    }

    private ITreeSelection getSelection(SelectionChangedEvent event) {

        ISelection selection = event.getSelection();
        if (selection instanceof ITreeSelection) {
            return (ITreeSelection)selection;
        }

        return null;
    }

    private class LoadPropertiesRunnable implements Runnable {

        private JournalEntry journalEntry;
        private List<JournalProperties> input;

        public LoadPropertiesRunnable(List<JournalProperties> input, JournalEntry journalEntry) {
            this.input = input;
            this.journalEntry = journalEntry;
        }

        public void run() {
            try {

                input.add(new JournalProperties(journalEntry));

                List<String> messages = new LinkedList<String>();

                // Get specific data
                if (journalEntry.isRecordEntryType()) {
                    MetaTable metatable = MetaDataCache.INSTANCE.retrieveMetaData(journalEntry);

                    if (metatable.hasNullableFields()) {
                        if (!journalEntry.hasNullIndicatorTable()) {
                            messages.add(Messages.Error_No_NULL_indicator_information_available);
                        } else if (metatable.getLastNullableFieldIndex() > journalEntry.getNullTableLength()) {
                            messages.add(Messages.Error_Field_JONVI_is_too_short_to_store_the_NULL_indicators_of_all_fields);
                        }
                    }

                    int joesdLength = journalEntry.getSpecificDataLength();
                    int recordLength = metatable.getRecordLength();
                    if (joesdLength < recordLength) {
                        messages.add(Messages.bind(Messages.Error_Field_JOESD_is_too_short_A_to_hold_the_complete_record_data_B, joesdLength,
                            recordLength));
                    }
                } else {

                    messages.add(Messages.bind(Messages.Error_Output_file_A_B_contains_records_that_are_not_a_result_of_a_record_level_operation,
                        journalEntry.getOutFileLibrary(), journalEntry.getOutFileName()));

                }

                // Get journal entry data
                MetaTable metatableOutputFile = MetaDataCache.INSTANCE.retrieveMetaData(journalEntry.getOutputFile());

                if (messages.size() > 0) {
                    StringBuilder dialogMessage = new StringBuilder();
                    for (String message : messages) {
                        if (!metatableOutputFile.hasWarningMessage(message)) {
                            if (dialogMessage.length() > 0) {
                                dialogMessage.append("\n"); //$NON-NLS-1$
                            }
                            dialogMessage.append("• "); //$NON-NLS-1$
                            dialogMessage.append(message);
                            metatableOutputFile.addWarningMessage(message);
                        }
                    }
                    if (dialogMessage.length() > 0) {
                        MessageDialog.openWarning(getViewSite().getShell(), Messages.Warning, dialogMessage.toString());
                    }
                }

            } catch (Exception e) {
                ISpherePlugin.logError("*** Error in method LoadPropertiesRunnable.run() ***", e);
                MessageDialog.openError(getViewSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }
    }
}