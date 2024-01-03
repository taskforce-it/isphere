/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ibm.etools.iseries.rse.ui.widgets.QSYSMsgFilePrompt;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.messagefilesearch.SearchElement;
import biz.isphere.core.messagefilesearch.SearchExec;
import biz.isphere.core.messagefilesearch.SearchPostRun;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.WidgetHelper;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.search.AbstractSearchPage;
import biz.isphere.rse.search.SearchArgumentsListEditor;

public class MessageFileSearchPage extends AbstractSearchPage {

    public static final String ID = "biz.isphere.rse.messagefilesearch.MessageFileSearchPage"; //$NON-NLS-1$

    private static final String MESSAGE_FILE = "messageFile"; //$NON-NLS-1$
    private static final String LIBRARY = "library"; //$NON-NLS-1$
    private static final String INCLUDE_FIRST_LEVEL_TEXT = "includeFirstLevelText"; //$NON-NLS-1$
    private static final String INCLUDE_SECOND_LEVEL_TEXT = "includeSecondLevelText"; //$NON-NLS-1$
    private static final String INCLUDE_MESSAGE_ID = "includeMessageId"; //$NON-NLS-1$

    private static int DEFAULT_START_COLUMN = 1;
    private static int DEFAULT_END_COLUMN = SearchArgument.MAX_MESSAGE_FILE_SEARCH_COLUMN;
    private static int MAX_END_COLUMN = SearchArgument.MAX_MESSAGE_FILE_SEARCH_COLUMN;

    private QSYSMsgFilePrompt messageFilePrompt;
    private Button includeFirstLevelTextButton;
    private Button includeSecondLevelTextButton;
    private Button includeMessageIdButton;

    private Composite targetMessageFileComposite;

    public MessageFileSearchPage() {
        super();
    }

    @Override
    protected void createObjectItem(Group parent) {

        targetMessageFileComposite = new Composite(parent, SWT.BORDER);
        targetMessageFileComposite.setLayout(new GridLayout(2, false));
        targetMessageFileComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        messageFilePrompt = new QSYSMsgFilePrompt(targetMessageFileComposite, SWT.NONE, true, true);
        messageFilePrompt.setSystemConnection(getHost());
        messageFilePrompt.getLibraryCombo().setToolTipText(Messages.Enter_or_select_a_library_name);
        messageFilePrompt.getObjectCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_message_file_name);
        messageFilePrompt.getLibraryPromptLabel().setText(Messages.Library);
        messageFilePrompt.getObjectPromptLabel().setText(Messages.Message_file);
    }

    protected void createOptionItems(Group parent) {

        includeFirstLevelTextButton = WidgetFactory.createCheckbox(parent);
        includeFirstLevelTextButton.setText(Messages.IncludeFirstLevelText);
        includeFirstLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_first_level_message_text);
        GridData tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeFirstLevelTextButton.setLayoutData(tGridData);

        includeSecondLevelTextButton = WidgetFactory.createCheckbox(parent);
        includeSecondLevelTextButton.setText(Messages.IncludeSecondLevelText);
        includeSecondLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_second_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeSecondLevelTextButton.setLayoutData(tGridData);

        includeMessageIdButton = WidgetFactory.createCheckbox(parent);
        includeMessageIdButton.setText(Messages.IncludeMessageId);
        includeMessageIdButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_message_id);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 1, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeMessageIdButton.setLayoutData(tGridData);

        Link lnkHelp = new Link(parent, SWT.NONE);
        lnkHelp.setLayoutData(new GridData(SWT.NONE));
        lnkHelp.setText("<a>(" + Messages.Refer_to_help_for_details + ")</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        lnkHelp.pack();
        lnkHelp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.core.help/html/messagefilesearch/messagefilesearch.html"); //$NON-NLS-1$
            }
        });
    }

    /**
     * Add listeners to verify user input.
     */
    @Override
    protected void addListeners() {
        super.addListeners();

        WidgetHelper.addListener(messageFilePrompt, SWT.Modify, getTargetFocusListener());
        WidgetHelper.addListener(targetMessageFileComposite, SWT.MouseUp, getTargetMouseListener());

        includeFirstLevelTextButton.addListener(SWT.Selection, this);
        includeSecondLevelTextButton.addListener(SWT.Selection, this);
        includeMessageIdButton.addListener(SWT.Selection, this);
    }

    /**
     * Restores the screen values of the last search search.
     */
    @Override
    protected void loadScreenValues() {
        super.loadScreenValues();

        includeFirstLevelTextButton.setSelection(loadBooleanValue(INCLUDE_FIRST_LEVEL_TEXT, true));
        includeSecondLevelTextButton.setSelection(loadBooleanValue(INCLUDE_SECOND_LEVEL_TEXT, false));
        includeMessageIdButton.setSelection(loadBooleanValue(INCLUDE_MESSAGE_ID, false));

        messageFilePrompt.getLibraryCombo().setText(loadValue(LIBRARY, "")); //$NON-NLS-1$
        messageFilePrompt.getObjectCombo().setText(loadValue(MESSAGE_FILE, "")); //$NON-NLS-1$

        if (!isIncludeFirstLevelText() && !isIncludeSecondLevelText() && !isIncludeMessageId()) {
            includeFirstLevelTextButton.setSelection(true);
        }
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

        if (!StringHelper.isNullOrEmpty(messageFilePrompt.getLibraryName())) {
            return true;
        }

        if (!StringHelper.isNullOrEmpty(messageFilePrompt.getObjectName())) {
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

        storeValue(INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        storeValue(INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
        storeValue(INCLUDE_MESSAGE_ID, isIncludeMessageId());

        storeValue(LIBRARY, getMessageFileLibrary());
        storeValue(MESSAGE_FILE, getMessageFile());
    }

    protected SearchArgumentsListEditor createSearchArgumentsListEditor(Composite parent) {

        SearchArgumentsListEditor searchArgumentsListEditor = new SearchArgumentsListEditor(SearchOptions.ARGUMENTS_SIZE,
            SearchOptions.MAX_STRING_SIZE_MESSAGE_FILE_SEARCH, false, SearchOptionConfig.getAdditionalMessageFileSearchOptions());
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
    private String getMessageFileLibrary() {
        return messageFilePrompt.getLibraryCombo().getText();
    }

    /**
     * Returns the simple or generic name of the target object(s) that are
     * searched for the search string.
     * 
     * @return simple or generic name of the message file
     */
    private String getMessageFile() {
        return messageFilePrompt.getObjectCombo().getText();
    }

    /**
     * Returns the status of the "show records" check box.
     * 
     * @return status of the "show records" check box
     */
    private boolean isShowRecords() {
        return true;
    }

    /**
     * Returns the status of the "include first level text" check box.
     * 
     * @return status of the "include first level text" check box
     */
    private boolean isIncludeFirstLevelText() {
        return includeFirstLevelTextButton.getSelection();
    }

    /**
     * Returns the status of the "include second level text" check box.
     * 
     * @return status of the "include second level text" check box
     */
    private boolean isIncludeSecondLevelText() {
        return includeSecondLevelTextButton.getSelection();
    }

    /**
     * Returns the status of the "include message id" check box.
     * 
     * @return status of the "include message id" check box
     */
    private boolean isIncludeMessageId() {
        return includeMessageIdButton.getSelection();
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

        if (!isFilterRadioButtonSelected()) {
            if (StringHelper.isNullOrEmpty(getMessageFileLibrary())) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_library_name);
                messageFilePrompt.getLibraryCombo().setFocus();
                return false;
            }

            if (StringHelper.isNullOrEmpty(getMessageFile())) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Enter_or_select_a_simple_or_generic_message_file_name);
                messageFilePrompt.getFileCombo().setFocus();
                return false;
            }
        }

        IHost tHost = getHost();
        if (tHost == null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Connection_not_found_A, "")); //$NON-NLS-1$
            return false;
        }

        IBMiConnection tConnection = ConnectionManager.getIBMiConnection(tHost);
        String tQualifiedConnectionName = ConnectionManager.getConnectionName(tHost);
        if (!ISphereHelper.checkISphereLibrary(getShell(), tQualifiedConnectionName)) {
            return false;
        }

        try {

            storeScreenValues();

            Map<String, SearchElement> searchElements;
            if (isFilterRadioButtonSelected()) {
                searchElements = loadFilterSearchElements(tConnection, getFilter());
            } else {
                searchElements = loadMessageFileSearchElements(tConnection, getMessageFileLibrary(), getMessageFile());
            }

            if (searchElements.isEmpty()) {
                MessageDialog.openInformation(getShell(), Messages.Information, Messages.No_objects_found_that_match_the_selection_criteria);
                return false;
            }

            int startColumn = getStartColumnValue();
            int endColumn = getEndColumnValue();

            SearchOptions searchOptions = new SearchOptions(getMatchOption(), isShowRecords());
            for (SearchArgument searchArgument : getSearchArguments(startColumn, endColumn)) {
                if (!StringHelper.isNullOrEmpty(searchArgument.getString())) {
                    searchOptions.addSearchArgument(searchArgument);
                }
            }

            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_FIRST_LEVEL_TEXT, new Boolean(isIncludeFirstLevelText()));
            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_SECOND_LEVEL_TEXT, new Boolean(isIncludeSecondLevelText()));
            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_MESSAGE_ID, new Boolean(isIncludeMessageId()));

            Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(tQualifiedConnectionName);

            SearchPostRun postRun = new SearchPostRun();
            postRun.setConnection(tConnection);
            postRun.setConnectionName(tQualifiedConnectionName);
            postRun.setSearchString(searchOptions.getCombinedSearchString());
            postRun.setSearchElements(searchElements);
            postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

            new SearchExec().execute(tConnection.getAS400ToolboxObject(), tQualifiedConnectionName, jdbcConnection, searchOptions,
                new ArrayList<SearchElement>(searchElements.values()), postRun);

        } catch (Exception e) {
            if (!(e instanceof InvalidFilterException)) {
                ISpherePlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            }
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return false;
        }

        return true;
    }

    private HashMap<String, SearchElement> loadMessageFileSearchElements(IBMiConnection connection, String library, String messageFile)
        throws InterruptedException {

        HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();

        try {

            MessageFileSearchDelegate delegate = new MessageFileSearchDelegate(getShell(), connection);
            delegate.addElements(searchElements, library, messageFile);

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return searchElements;
    }

    private Map<String, SearchElement> loadFilterSearchElements(IBMiConnection connection, ISystemFilter filter) throws Exception {

        ArrayList<Object> selectedFilters = new ArrayList<Object>();
        selectedFilters.add(filter);

        MessageFileSearchFilterResolver filterResolver = new MessageFileSearchFilterResolver(getShell(), connection);
        Map<String, SearchElement> searchElements = filterResolver.resolveRSEFilter(selectedFilters);

        return searchElements;
    }

    protected void setSearchOptionsEnablement(Event anEvent) {
        super.setSearchOptionsEnablement(anEvent);

        if (!(anEvent.data instanceof SearchOptionConfig)) {
            return;
        }

        SearchOptionConfig config = (SearchOptionConfig)anEvent.data;

        includeFirstLevelTextButton.setEnabled(config.isIncludeFirstLevelTextEnabled());
        includeSecondLevelTextButton.setEnabled(config.isIncludeSecondLevelTextEnabled());
        includeMessageIdButton.setEnabled(config.isIncludeMessageIdEnabled());
    }
}
