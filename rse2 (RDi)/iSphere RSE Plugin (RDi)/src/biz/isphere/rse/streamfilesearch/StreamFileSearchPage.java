/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.streamfilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.streamfilesearch.SearchElement;
import biz.isphere.core.streamfilesearch.StreamFileSearchFilter;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.WidgetHelper;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.StreamFilePrompt;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.IFSRemoteFileHelper;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;
import biz.isphere.rse.search.AbstractSearchPage;
import biz.isphere.rse.search.SearchArgumentsListEditor;

/**
 * Source file search page launched by Ctrl+H (Search - Search...).
 */
public class StreamFileSearchPage extends AbstractSearchPage {

    public static final String ID = "biz.isphere.rse.streamfilesearch.StreamFileSearchPage"; //$NON-NLS-1$

    private static final int MAX_DEPTH = StreamFileSearchFilterResolver.MAX_DEPTH;

    private static final String STREAM_FILE = "streamFile"; //$NON-NLS-1$
    private static final String DIRECTORY = "directory"; //$NON-NLS-1$
    private static final String SHOW_RECORDS = "showRecords"; //$NON-NLS-1$

    private static int DEFAULT_START_COLUMN = 1;
    private static int DEFAULT_END_COLUMN = 100;
    private static int MAX_END_COLUMN = SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN;

    private StreamFilePrompt streamFilePrompt;
    private Combo filterSrcTypeCombo;
    private Button showAllRecordsButton;

    private Composite targetSourceMemberComposite;

    public StreamFileSearchPage() {
        super();
    }

    @Override
    protected ISystemFilterPoolReference[] loadSystemFilterPoolReferences(String connectionName) {
        return RSEFilterHelper.getConnectionIFSFilterPools(connectionName);
    }

    @Override
    protected void createObjectItem(Group parent) {

        targetSourceMemberComposite = new Composite(parent, SWT.BORDER);
        targetSourceMemberComposite.setLayout(new GridLayout(2, false));
        targetSourceMemberComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        streamFilePrompt = new StreamFilePrompt(targetSourceMemberComposite, SWT.NONE);
        streamFilePrompt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        streamFilePrompt.setConnection(getHost());
        streamFilePrompt.getDirectoryWidget().setToolTipText(Messages.Enter_or_select_an_IFS_directory);
        streamFilePrompt.getStreamFileWidget().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_stream_file_name);
    }

    protected Text createEndColumnItem(Composite parent) {

        Text endColumnText = super.createEndColumnItem(parent);
        endColumnText.setToolTipText(Messages.Specify_end_column_max_228);

        return endColumnText;
    }

    protected void createOptionItems(Group parent) {

        Label filterSrcTypeLabel = new Label(parent, SWT.NONE);
        filterSrcTypeLabel.setLayoutData(new GridData());
        filterSrcTypeLabel.setText(Messages.Member_type_colon);
        filterSrcTypeLabel.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);

        filterSrcTypeCombo = WidgetFactory.createCombo(parent);
        GridData filterSrcTypeGridData = new GridData();
        filterSrcTypeGridData.widthHint = 100;
        filterSrcTypeCombo.setLayoutData(filterSrcTypeGridData);
        filterSrcTypeCombo.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);
        filterSrcTypeCombo.setItems(new String[] { "*", "*BLANK" }); //$NON-NLS-1$ //$NON-NLS-2$
        filterSrcTypeCombo.select(0);

        showAllRecordsButton = WidgetFactory.createCheckbox(parent);
        showAllRecordsButton.setText(Messages.ShowAllRecords);
        showAllRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        showAllRecordsButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
    }

    /**
     * Add listeners to verify user input.
     */
    @Override
    protected void addListeners() {
        super.addListeners();

        WidgetHelper.addListener(streamFilePrompt, SWT.Modify, getTargetFocusListener());
        WidgetHelper.addListener(targetSourceMemberComposite, SWT.MouseUp, getTargetMouseListener());
    }

    /**
     * Restores the screen values of the last search search.
     */
    @Override
    protected void loadScreenValues() {
        super.loadScreenValues();

        showAllRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, true));

        streamFilePrompt.setDirectoryName(loadValue(DIRECTORY, "")); //$NON-NLS-1$
        streamFilePrompt.setStreamFileName(loadValue(STREAM_FILE, "")); //$NON-NLS-1$
    }

    protected int getDefaultStartColumnValue() {
        return DEFAULT_START_COLUMN;
    }

    protected int getDefaultEndColumnValue() {
        return DEFAULT_END_COLUMN;
    }

    protected int getMaxEndColumnValue() {
        return MAX_END_COLUMN;
    }

    @Override
    protected boolean hasSearchedObject() {

        if (!StringHelper.isNullOrEmpty(streamFilePrompt.getDirectoryName())) {
            return true;
        }

        if (!StringHelper.isNullOrEmpty(streamFilePrompt.getStreamFileName())) {
            return true;
        }

        return false;
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    @Override
    protected void storeScreenValues() {
        super.storeScreenValues();

        storeValue(SHOW_RECORDS, isShowAllRecords());

        storeValue(DIRECTORY, getDirectoryName());
        storeValue(STREAM_FILE, getStreamFileName());
    }

    protected SearchArgumentsListEditor createSearchArgumentsListEditor(Composite parent) {

        SearchArgumentsListEditor searchArgumentsListEditor = new SearchArgumentsListEditor(SearchOptions.ARGUMENTS_SIZE,
            SearchOptions.MAX_STRING_SIZE, true, SearchOptionConfig.getAdditionalLineModeSearchOptions());
        searchArgumentsListEditor.setListener(this);
        searchArgumentsListEditor.createControl(parent);

        return searchArgumentsListEditor;
    }

    /**
     * Returns the simple or generic name of the directories containing the
     * target objects that will be searched.
     * 
     * @return name of the directory
     */
    private String getDirectoryName() {
        return streamFilePrompt.getDirectoryName();
    }

    /**
     * Returns the simple or generic name of the stream file(s) that are
     * searched for the search string.
     * 
     * @return simple or generic name of the stream file
     */
    private String getStreamFileName() {
        return streamFilePrompt.getStreamFileName();
    }

    /**
     * Returns the *generic* source member type of the members that are included
     * in the search.
     * 
     * @return simple or generic source type of the members that are searched
     */
    private String getSourceType() {
        return filterSrcTypeCombo.getText();
    }

    /**
     * Returns the status of the "show records" check box.
     * 
     * @return status of the "show records" check box
     */
    private boolean isShowAllRecords() {
        return showAllRecordsButton.getSelection();
    }

    /**
     * Overridden to let {@link XDialogPage} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    protected AbstractUIPlugin getPlugin() {
        return ISphereRSEPlugin.getDefault();
    }

    /**
     * Performs the actual search search task.
     */
    public boolean performAction() {

        String directory = getDirectoryName();
        String streamFile = getStreamFileName();

        if (!isFilterRadioButtonSelected()) {
            if (StringHelper.isNullOrEmpty(directory)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_an_IFS_directory);
                streamFilePrompt.getDirectoryWidget().setFocus();
                return false;
            }

            if (StringHelper.isNullOrEmpty(streamFile)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_stream_file_name);
                streamFilePrompt.getStreamFileWidget().setFocus();
                return false;
            }
        }

        if (StringHelper.isNullOrEmpty(getSourceType())) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_member_type);
            filterSrcTypeCombo.setFocus();
            return false;
        }

        IHost tHost = getHost();
        if (tHost == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Connection_not_found_A, "")); //$NON-NLS-1$
            return false;
        }

        IBMiConnection tConnection = ConnectionManager.getIBMiConnection(tHost);

        if (!isFilterRadioButtonSelected()) {

            if (!isGenericOrSpecialValue(directory)) {

                if (!IFSRemoteFileHelper.checkRemoteDirectory(tConnection, directory)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Directory_not_found_A, directory));
                    streamFilePrompt.getDirectoryWidget().setFocus();
                    return false;
                }

                if (!isGenericOrSpecialValue(streamFile) && !IFSRemoteFileHelper.checkRemoteStreamFile(tConnection, directory, streamFile)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                        Messages.bind(Messages.Stream_file_B_not_found_in_directory_A, directory, streamFile));
                    streamFilePrompt.getStreamFileWidget().setFocus();
                    return false;
                }
            }
        }

        try {

            storeScreenValues();

            int startColumn = getStartColumnValue();
            int endColumn = getEndColumnValue();

            SearchOptions searchOptions = new SearchOptions(getMatchOption(), isShowAllRecords());
            for (SearchArgument searchArgument : getSearchArguments(startColumn, endColumn)) {
                if (!StringHelper.isNullOrEmpty(searchArgument.getString())) {
                    searchOptions.addSearchArgument(searchArgument);
                }
            }

            searchOptions.setGenericOption(GenericSearchOption.Key.STMF_TYPE, getSourceType());

            StreamFileSearchFilter streamFileSearchFilter = new StreamFileSearchFilter(searchOptions);

            Map<String, SearchElement> searchElements;
            if (isFilterRadioButtonSelected()) {
                searchElements = loadFilterSearchElements(tConnection, streamFileSearchFilter);
            } else {
                searchElements = loadStreamFileSearchElements(tConnection, directory, streamFile, streamFileSearchFilter);
            }

            if (searchElements.size() == 0) {
                MessageDialog.openInformation(getShell(), Messages.Information, Messages.No_objects_found_that_match_the_selection_criteria);
                return false;
            }

            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            ArrayList<SearchElement> selectedElements = new ArrayList<SearchElement>(searchElements.values());

            new RSESearchExec(workbenchWindow, tConnection).execute(selectedElements, searchOptions);

        } catch (Exception e) {
            if (!(e instanceof InvalidFilterException)) {
                ISpherePlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            }
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return false;
        }

        return true;
    }

    private boolean isGenericOrSpecialValue(String string) {

        if (string.indexOf("*") >= 0) { //$NON-NLS-1$
            return true;
        }

        return false;
    }

    private Map<String, SearchElement> loadStreamFileSearchElements(IBMiConnection connection, String directory, String streamFile,
        StreamFileSearchFilter filter) throws InterruptedException {

        HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();

        try {

            StreamFileSearchDelegate delegate = new StreamFileSearchDelegate(getShell(), connection);
            delegate.addElements(searchElements, directory, streamFile, filter, MAX_DEPTH);

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return searchElements;
    }

    private Map<String, SearchElement> loadFilterSearchElements(IBMiConnection connection, StreamFileSearchFilter filter) throws Exception {

        ArrayList<Object> selectedFilters = new ArrayList<Object>();
        selectedFilters.add(getFilter());

        StreamFileSearchFilterResolver filterResolver = new StreamFileSearchFilterResolver(getShell(), connection, filter);
        Map<String, SearchElement> searchElements = filterResolver.resolveRSEFilter(selectedFilters);

        return searchElements;
    }

    protected void setSearchOptionsEnablement(Event anEvent) {
        super.setSearchOptionsEnablement(anEvent);

        if (!(anEvent.data instanceof SearchOptionConfig)) {
            return;
        }

        // SearchOptionConfig config = (SearchOptionConfig)anEvent.data;

        filterSrcTypeCombo.setEnabled(true);
        showAllRecordsButton.setEnabled(true);
    }
}
