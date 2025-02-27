/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SearchDialog extends AbstractSearchDialog<SearchElement> {

    private static final String MAX_DEPTH = "maxDepth"; //$NON-NLS-1$
    private static final String SHOW_RECORDS = "showRecords"; //$NON-NLS-1$

    private Preferences preferences;

    private Map<String, SearchElement> searchElements;
    private Combo comboMaxDepth;
    private Label labelMaxDepthWarning;
    private Button showAllRecordsButton;
    private Combo filterTypeCombo;
    private RefreshJob refreshJob = new RefreshJob();

    /**
     * Constructor used by whom? CMOne?
     */
    @Deprecated
    @CMOne(info = "Do not change the constructor. May be used by CMOne.")
    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell, SearchArgument.MAX_STREAM_FILE_SEARCH_COLUMN, false, SearchOptions.MAX_STRING_SIZE, false);
        init(searchElements);
    }

    /**
     * Constructor used by CMOne.
     */
    @Deprecated
    @CMOne(info = "Do not change the constructor.")
    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, SearchArgument.MAX_STREAM_FILE_SEARCH_COLUMN, searchArgumentsListEditor, SearchOptions.MAX_STRING_SIZE, true,
            SearchOptionConfig.getAdditionalLineModeSearchOptions());
        init(searchElements);
    }

    public SearchDialog(Shell parentShell, Map<String, SearchElement> searchElements) {
        super(parentShell, SearchArgument.MAX_STREAM_FILE_SEARCH_COLUMN, false, SearchOptions.MAX_STRING_SIZE, false);
        init(searchElements);
    }

    public SearchDialog(Shell parentShell, Map<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, SearchArgument.MAX_STREAM_FILE_SEARCH_COLUMN, searchArgumentsListEditor, SearchOptions.MAX_STRING_SIZE, true,
            SearchOptionConfig.getAdditionalLineModeSearchOptions());
        init(searchElements);
    }

    private void init(Map<String, SearchElement> searchElements) {
        this.preferences = Preferences.getInstance();
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
        return Messages.iSphere_Stream_File_Search;
    }

    @CMOne(info = "Do not change the method. May be used by CMOne.")
    @Override
    public String[] getItems() {

        if (searchElements == null) {
            return new String[0];
        }

        ArrayList<String> selectedItems = new ArrayList<String>();

        // TODO: remove lines, most likely not required (Thomas Raddatz,
        // 24.2.2025)
        StreamFileSearchFilter filter = new StreamFileSearchFilter();
        ArrayList<SearchElement> selectedSearchElements = filter.applyFilter(searchElements.values(), getSearchOptions());
        // ------------------------------------------------------------------------

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

        StreamFileSearchFilter filter = new StreamFileSearchFilter();

        return filter.applyFilter(searchElements.values(), getSearchOptions());
    }

    @Override
    protected String getSearchArgument() {
        return preferences.getStreamFileSearchString();
    }

    @Override
    protected void setSearchArgument(String argument) {
        preferences.setStreamFileSearchString(argument);
    }

    @Override
    protected void createOptionsGroup(Composite container) {

        Group groupOptions = new Group(container, SWT.NONE);
        groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        groupOptions.setText(Messages.Options);
        groupOptions.setLayout(new GridLayout(3, false));

        Label filterTypeLabel = new Label(groupOptions, SWT.NONE);
        filterTypeLabel.setLayoutData(new GridData());
        filterTypeLabel.setText(Messages.Stream_file_type_colon);
        filterTypeLabel.setToolTipText(Messages.Specifies_the_generic_type_of_the_stream_files_that_are_included_in_the_search);

        filterTypeCombo = WidgetFactory.createCombo(groupOptions);
        GridData filterTypeGridData = new GridData();
        filterTypeGridData.widthHint = 100;
        filterTypeGridData.horizontalSpan = 2;
        filterTypeCombo.setLayoutData(filterTypeGridData);
        filterTypeCombo.setToolTipText(Messages.Specifies_the_generic_type_of_the_stream_files_that_are_included_in_the_search);
        filterTypeCombo.setItems(new String[] { "*", "*BLANK" }); //$NON-NLS-1$
        filterTypeCombo.select(0);
        filterTypeCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                refreshStreamFileList((Control)event.getSource());
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
        filterTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                refreshStreamFileList((Control)event.getSource());
            }
        });

        Label maxDepthLabel = new Label(groupOptions, SWT.NONE);
        maxDepthLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1));
        maxDepthLabel.setText(Messages.Max_depth_colon);
        maxDepthLabel.setToolTipText(Messages.Specifies_the_maximum_depth_of_sub_directories_included_in_the_search);

        comboMaxDepth = WidgetFactory.createIntegerCombo(groupOptions, true);
        comboMaxDepth.setToolTipText(Messages.Specifies_the_maximum_depth_of_sub_directories_included_in_the_search);
        GridData maxDepthGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
        maxDepthGridData.widthHint = 100;
        comboMaxDepth.setLayoutData(maxDepthGridData);
        comboMaxDepth.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                updateMaxDepthWarning();
            }
        });
        comboMaxDepth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateMaxDepthWarning();
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateMaxDepthWarning();
            }
        });
        comboMaxDepth.setItems(new String[] { "1", Preferences.UNLIMITTED });

        if (hasSearchElements()) {
            comboMaxDepth.setEnabled(false);
        } else {
            comboMaxDepth.setEnabled(true);
        }

        labelMaxDepthWarning = new Label(groupOptions, SWT.NONE);
        labelMaxDepthWarning.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1));
        labelMaxDepthWarning.setForeground(ColorHelper.getOrange());
        labelMaxDepthWarning.setText(Messages.Warning_Maximum_depth_set_to_more_than_one_level);

        showAllRecordsButton = WidgetFactory.createCheckbox(groupOptions);
        showAllRecordsButton.setText(Messages.ShowAllRecords);
        showAllRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        showAllRecordsButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 3, 1));
    }

    private void updateMaxDepthWarning() {
        if (labelMaxDepthWarning != null) {
            if (getMaxDepth() != 1) {
                labelMaxDepthWarning.setVisible(true);
            } else {
                labelMaxDepthWarning.setVisible(false);
            }
        }
    }

    @Override
    protected void okPressed() {

        if (StringHelper.isNullOrEmpty(filterTypeCombo.getText())) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_stream_file_type);
            filterTypeCombo.setFocus();
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

        int maxDepth;
        if (comboMaxDepth.getEnabled()) {
            maxDepth = loadIntValue(MAX_DEPTH, preferences.getStreamFileSearchMaxDepth());
        } else {
            maxDepth = preferences.getStreamFileSearchMaxDepth();
        }

        setMaxDepth(maxDepth);

        showAllRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, true));
    };

    @Override
    protected void saveElementValues() {
        storeValue(MAX_DEPTH, getMaxDepth());
        storeValue(SHOW_RECORDS, isShowAllRecords());
    };

    /**
     * Returns the current 'maximum depth' value of this dialog for searchung
     * subdirectories.
     * 
     * @return value of the maximum depth combo box
     */
    private int getMaxDepth() {
        if (Preferences.UNLIMITTED.equals(comboMaxDepth.getText())) {
            return preferences.getStreamFileSearchMaxDepthSpecialValueUnlimited();
        } else {
            return IntHelper.tryParseInt(comboMaxDepth.getText(), preferences.getStreamFileSearchMaxDepth());
        }
    }

    /**
     * Set the maximum depth value.
     * 
     * @param maxDepth
     */
    private void setMaxDepth(int maxDepth) {
        if (preferences.isStreamFileSearchUnlimitedMaxDepth(maxDepth)) {
            comboMaxDepth.setText(Preferences.UNLIMITTED);
        } else {
            comboMaxDepth.setText(Integer.toString(maxDepth));
        }
    }

    @Override
    protected void setElementsSearchOptions(SearchOptions _searchOptions) {
        _searchOptions.setShowAllItems(isShowAllRecords());
        _searchOptions.setGenericOption(GenericSearchOption.STMF_TYPE, filterTypeCombo.getText());
        _searchOptions.setGenericOption(GenericSearchOption.MAX_DEPTH, getMaxDepth());
    };

    private boolean isShowAllRecords() {
        return showAllRecordsButton.getSelection();
    }

    private void refreshStreamFileList(Control control) {

        int autoRefreshDelay = preferences.getAutoRefreshDelay();

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
