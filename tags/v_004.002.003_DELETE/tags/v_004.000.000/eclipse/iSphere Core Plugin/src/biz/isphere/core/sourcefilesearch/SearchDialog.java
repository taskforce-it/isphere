/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.WorkbenchJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SearchDialog extends AbstractSearchDialog<SearchElement> {

    private static final String SHOW_RECORDS = "showRecords"; //$NON-NLS-1$

    private HashMap<String, SearchElement> searchElements;
    private Button showAllRecordsButton;
    private Combo filterSrcTypeCombo;
    private RefreshJob refreshJob = new RefreshJob();

    /**
     * Constructor used by whom? CMOne?
     */
    @CMOne(info = "Do not change the constructor. May be used by CMOne.")
    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell, SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN, false, false);
        this.searchElements = searchElements;
        setListBoxEnabled(hasSearchElements());
    }

    /**
     * Constructor used by CMOne and SourceFileSearchAction.
     */
    @CMOne(info = "Do not change the constructor.")
    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN, searchArgumentsListEditor, true, SearchOptionConfig
            .getAdditionalLineModeSearchOptions());
        this.searchElements = searchElements;
        setListBoxEnabled(hasSearchElements());
    }

    private boolean hasSearchElements() {

        if (searchElements != null) {
            return true;
        }

        return false;
    }

    @Override
    protected String getTitle() {
        return Messages.iSphere_Source_File_Search;
    }

    @Override
    protected String[] getItems() {

        if (searchElements == null) {
            return new String[0];
        }

        ArrayList<String> selectedItems = new ArrayList<String>();

        SourceFileSearchFilter filter = new SourceFileSearchFilter();
        ArrayList<SearchElement> selectedSearchElements = filter.applyFilter(searchElements.values(), getSearchOptions());
        for (SearchElement searchElement : selectedSearchElements) {
            selectedItems.add(searchElement.toString());
        }

        String[] _items = new String[selectedItems.size()];
        selectedItems.toArray(_items);

        return _items;
    }

    @Override
    public ArrayList<SearchElement> getSelectedElements() {

        if (searchElements == null) {
            return new ArrayList<SearchElement>();
        }

        SourceFileSearchFilter filter = new SourceFileSearchFilter();

        return filter.applyFilter(searchElements.values(), getSearchOptions());
    }

    @Override
    protected String getSearchArgument() {
        return Preferences.getInstance().getSourceFileSearchString();
    }

    @Override
    protected void setSearchArgument(String argument) {
        Preferences.getInstance().setSourceFileSearchString(argument);
    }

    @Override
    protected void createOptionsGroup(Composite container) {

        Group groupOptions = new Group(container, SWT.NONE);
        groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        groupOptions.setText(Messages.Options);
        groupOptions.setLayout(new GridLayout(2, false));

        Label filterSrcTypeLabel = new Label(groupOptions, SWT.NONE);
        filterSrcTypeLabel.setLayoutData(new GridData());
        filterSrcTypeLabel.setText(Messages.Member_type_colon);
        filterSrcTypeLabel.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);

        filterSrcTypeCombo = WidgetFactory.createCombo(groupOptions);
        GridData filterSrcTypeGridData = new GridData();
        filterSrcTypeGridData.widthHint = 100;
        filterSrcTypeCombo.setLayoutData(filterSrcTypeGridData);
        filterSrcTypeCombo.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);
        filterSrcTypeCombo.setItems(new String[] { "*", "*BLANK" }); //$NON-NLS-1$
        filterSrcTypeCombo.select(0);
        filterSrcTypeCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshMemberList((Control)event.getSource());
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
        filterSrcTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                refreshMemberList((Control)event.getSource());
            }
        });

        showAllRecordsButton = WidgetFactory.createCheckbox(groupOptions);
        showAllRecordsButton.setText(Messages.ShowAllRecords);
        showAllRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        showAllRecordsButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
    }

    @Override
    protected void okPressed() {

        if (StringHelper.isNullOrEmpty(filterSrcTypeCombo.getText())) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_member_type);
            filterSrcTypeCombo.setFocus();
            return;
        }

        if (hasSearchElements() && getSelectedElements().size() == 0) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.No_objects_found_that_match_the_selection_criteria);
            return;
        }

        if (refreshJob != null) {
            refreshJob.cancel();
            refreshJob = null;
        }

        super.okPressed();
    }

    @Override
    protected void loadElementValues() {
        showAllRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, true));
    };

    @Override
    protected void saveElementValues() {
        storeValue(SHOW_RECORDS, isShowAllRecords());
    };

    @Override
    protected void setElementsSearchOptions(SearchOptions _searchOptions) {
        _searchOptions.setShowAllItems(isShowAllRecords());
        _searchOptions.setGenericOption(GenericSearchOption.SRCMBR_SRC_TYPE, filterSrcTypeCombo.getText());
    };

    private boolean isShowAllRecords() {
        return showAllRecordsButton.getSelection();
    }

    private void refreshMemberList(Control control) {

        int autoRefreshDelay = Preferences.getInstance().getAutoRefreshDelay();

        refreshJob.cancel();
        refreshJob.setFocusControl(control);

        if (autoRefreshDelay <= 0) {
            refreshJob.schedule();
        } else {
            refreshJob.schedule(autoRefreshDelay);
        }
    }

    private class RefreshJob extends WorkbenchJob {

        private Control focusControl;

        public RefreshJob() {
            super("Refresh Job");
            setSystem(true); // set to false to show progress to user
        }

        public void setFocusControl(Control control) {
            focusControl = control;
        }

        public IStatus runInUIThread(IProgressMonitor monitor) {
            monitor.beginTask("Refreshing", IProgressMonitor.UNKNOWN);

            refreshListArea();

            if (focusControl != null) {
                if (focusControl instanceof Text) {
                    Text text = (Text)focusControl;
                    int caretPosition = text.getCaretPosition();
                    focusControl.setFocus();
                    if (caretPosition >= 0) {
                        text.setSelection(caretPosition, caretPosition);
                    }
                } else {
                    focusControl.setFocus();
                }
            }

            monitor.done();
            return Status.OK_STATUS;
        };
    }

}
