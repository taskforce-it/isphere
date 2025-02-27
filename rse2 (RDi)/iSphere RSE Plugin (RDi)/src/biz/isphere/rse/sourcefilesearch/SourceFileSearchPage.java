/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilter;
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

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSFilePrompt;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.sourcefilesearch.SearchElement;
import biz.isphere.core.sourcefilesearch.SourceFileSearchFilter;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.WidgetHelper;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.search.AbstractSearchPage;
import biz.isphere.rse.search.SearchArgumentsListEditor;

/**
 * Source file search page launched by Ctrl+H (Search - Search...).
 */
public class SourceFileSearchPage extends AbstractSearchPage {

    public static final String ID = "biz.isphere.rse.sourcefilesearch.SourceFileSearchPage"; //$NON-NLS-1$

    private static final String SOURCE_FILE = "sourceFile"; //$NON-NLS-1$
    private static final String SOURCE_MEMBER = "sourceMember"; //$NON-NLS-1$
    private static final String LIBRARY = "library"; //$NON-NLS-1$
    private static final String SHOW_RECORDS = "showRecords"; //$NON-NLS-1$

    private static int DEFAULT_START_COLUMN = 1;
    private static int DEFAULT_END_COLUMN = 100;
    private static int MAX_END_COLUMN = SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN;

    private QSYSMemberPrompt sourceFilePrompt;
    private Combo filterSrcTypeCombo;
    private Button showAllRecordsButton;

    private Composite targetSourceMemberComposite;

    public SourceFileSearchPage() {
        super();
    }

    @Override
    protected void createObjectItem(Group parent) {

        targetSourceMemberComposite = new Composite(parent, SWT.BORDER);
        targetSourceMemberComposite.setLayout(new GridLayout(2, false));
        targetSourceMemberComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sourceFilePrompt = new QSYSMemberPrompt(targetSourceMemberComposite, SWT.NONE, true, true, QSYSFilePrompt.FILETYPE_SRC);
        sourceFilePrompt.setSystemConnection(getHost());
        sourceFilePrompt.getLibraryCombo().setToolTipText(Messages.Enter_or_select_a_library_name);
        sourceFilePrompt.getObjectCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_file_name);
        sourceFilePrompt.getMemberCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_member_name);
        sourceFilePrompt.getLibraryPromptLabel().setText(Messages.Library);
        sourceFilePrompt.setObjectPromptLabel(Messages.Source_File);
        sourceFilePrompt.setMemberPromptLabel(Messages.Source_Member);
    }

    protected Text createEndColumnItem(Composite parent) {

        Text endColumnText = super.createEndColumnItem(parent);
        endColumnText.setToolTipText(Messages.Specify_end_column_max_228);

        return endColumnText;
    }

    protected void createOptionItems(Group parent, int numColumns) {

        Label filterSrcTypeLabel = new Label(parent, SWT.NONE);
        filterSrcTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1));
        filterSrcTypeLabel.setText(Messages.Member_type_colon);
        filterSrcTypeLabel.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);

        filterSrcTypeCombo = WidgetFactory.createCombo(parent);
        GridData filterSrcTypeGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, numColumns - 1, 1);
        filterSrcTypeGridData.widthHint = 100;
        filterSrcTypeCombo.setLayoutData(filterSrcTypeGridData);
        filterSrcTypeCombo.setToolTipText(Messages.Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search);
        filterSrcTypeCombo.setItems(new String[] { "*", "*BLANK" }); //$NON-NLS-1$ //$NON-NLS-2$
        filterSrcTypeCombo.select(0);

        showAllRecordsButton = WidgetFactory.createCheckbox(parent);
        showAllRecordsButton.setText(Messages.ShowAllRecords);
        showAllRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        showAllRecordsButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, numColumns, 1));
    }

    /**
     * Add listeners to verify user input.
     */
    @Override
    protected void addListeners() {
        super.addListeners();

        WidgetHelper.addListener(sourceFilePrompt, SWT.Modify, getTargetFocusListener());
        WidgetHelper.addListener(targetSourceMemberComposite, SWT.MouseUp, getTargetMouseListener());
    }

    /**
     * Restores the screen values of the last search search.
     */
    @Override
    protected void loadScreenValues() {
        super.loadScreenValues();

        showAllRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, true));

        sourceFilePrompt.getLibraryCombo().setText(loadValue(LIBRARY, "")); //$NON-NLS-1$
        sourceFilePrompt.getObjectCombo().setText(loadValue(SOURCE_FILE, "")); //$NON-NLS-1$
        sourceFilePrompt.getMemberCombo().setText(loadValue(SOURCE_MEMBER, "")); //$NON-NLS-1$
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

        if (!StringHelper.isNullOrEmpty(sourceFilePrompt.getLibraryName())) {
            return true;
        }

        if (!StringHelper.isNullOrEmpty(sourceFilePrompt.getFileName())) {
            return true;
        }

        if (!StringHelper.isNullOrEmpty(sourceFilePrompt.getMemberName())) {
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

        storeValue(LIBRARY, getSourceFileLibrary());
        storeValue(SOURCE_FILE, getSourceFile());
        storeValue(SOURCE_MEMBER, getSourceMember());
    }

    protected SearchArgumentsListEditor createSearchArgumentsListEditor(Composite parent) {

        SearchArgumentsListEditor searchArgumentsListEditor = new SearchArgumentsListEditor(SearchOptions.ARGUMENTS_SIZE,
            SearchOptions.MAX_STRING_SIZE_SOURCE_FILE_SEARCH, true, SearchOptionConfig.getAdditionalLineModeSearchOptions());
        searchArgumentsListEditor.setListener(this);
        searchArgumentsListEditor.createControl(parent);

        return searchArgumentsListEditor;
    }

    /**
     * Returns the simple or generic name of the libraries containing the target
     * objects that will be searched.
     * 
     * @return name of the library
     */
    private String getSourceFileLibrary() {
        return sourceFilePrompt.getLibraryCombo().getText();
    }

    /**
     * Returns the simple or generic name of the target object(s) that are
     * searched for the search string.
     * 
     * @return simple or generic name of the message file
     */
    private String getSourceFile() {
        return sourceFilePrompt.getObjectCombo().getText();
    }

    /**
     * Returns the simple or generic source member name of the source file(s)
     * that are searched for the search string.
     * 
     * @return simple or generic member name of the source file
     */
    private String getSourceMember() {
        return sourceFilePrompt.getMemberCombo().getText();
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

        String sourceFileLibrary = getSourceFileLibrary();
        String sourceFile = getSourceFile();

        if (!isFilterRadioButtonSelected()) {
            if (StringHelper.isNullOrEmpty(sourceFileLibrary)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_library_name);
                sourceFilePrompt.getLibraryCombo().setFocus();
                return false;
            }

            if (StringHelper.isNullOrEmpty(sourceFile)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_file_name);
                sourceFilePrompt.getFileCombo().setFocus();
                return false;
            }

            if (StringHelper.isNullOrEmpty(getSourceMember())) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_member_name);
                sourceFilePrompt.getMemberCombo().setFocus();
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
        String tQualifiedConnectionName = ConnectionManager.getConnectionName(tConnection);

        if (!ISphereHelper.checkISphereLibrary(getShell(), tQualifiedConnectionName)) {
            return false;
        }

        if (!isFilterRadioButtonSelected()) {

            if (!isGenericOrSpecialValue(sourceFileLibrary)) {

                AS400 system = IBMiHostContributionsHandler.getSystem(tQualifiedConnectionName);
                if (!ISphereHelper.checkLibrary(system, sourceFileLibrary)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Library_A_not_found, sourceFileLibrary));
                    sourceFilePrompt.getLibraryCombo().setFocus();
                    return false;
                }

                if (!isGenericOrSpecialValue(sourceFile) && !ISphereHelper.checkObject(system, sourceFileLibrary, sourceFile, ISeries.FILE)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                        Messages.bind(Messages.File_A_in_library_B_not_found, sourceFile, sourceFileLibrary));
                    sourceFilePrompt.getObjectCombo().setFocus();
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

            searchOptions.setGenericOption(GenericSearchOption.Key.SRCMBR_SRC_TYPE, getSourceType());

            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            if (Preferences.getInstance().isSourceFileSearchBatchResolveEnabled()) {

                if (isFilterRadioButtonSelected()) {
                    ArrayList<Object> selectedFilters = new ArrayList<Object>();
                    selectedFilters.add(getFilter());
                    new RSESearchExec(workbenchWindow, tConnection).resolveAndExecute(selectedFilters, searchOptions);
                } else {
                    new RSESearchExec(workbenchWindow, tConnection).resolveAndExecute(sourceFileLibrary, sourceFile, getSourceMember(),
                        searchOptions);
                }
            } else {

                Map<String, SearchElement> searchElements;

                if (isFilterRadioButtonSelected()) {
                    searchElements = loadFilterSearchElements(tConnection, getFilter());
                } else {
                    searchElements = loadSourceMemberSearchElements(tConnection, sourceFileLibrary, sourceFile, getSourceMember());
                }

                SourceFileSearchFilter filter = new SourceFileSearchFilter();
                ArrayList<SearchElement> selectedElements = filter.applyFilter(searchElements.values(), searchOptions);

                if (selectedElements.size() == 0) {
                    MessageDialog.openInformation(getShell(), Messages.Information, Messages.No_objects_found_that_match_the_selection_criteria);
                    return false;
                }

                new RSESearchExec(workbenchWindow, tConnection).execute(selectedElements, searchOptions);
            }

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

    private HashMap<String, SearchElement> loadSourceMemberSearchElements(IBMiConnection connection, String library, String sourceFile,
        String sourceMember) throws InterruptedException {

        HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();

        try {

            SourceFileSearchDelegate delegate = new SourceFileSearchDelegate(getShell(), connection);
            delegate.addElements(searchElements, library, sourceFile, sourceMember);

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return searchElements;
    }

    private Map<String, SearchElement> loadFilterSearchElements(IBMiConnection connection, ISystemFilter filter) throws Exception {

        ArrayList<Object> selectedFilters = new ArrayList<Object>();
        selectedFilters.add(filter);

        SourceFileSearchFilterResolver filterResolver = new SourceFileSearchFilterResolver(getShell(), connection);
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
