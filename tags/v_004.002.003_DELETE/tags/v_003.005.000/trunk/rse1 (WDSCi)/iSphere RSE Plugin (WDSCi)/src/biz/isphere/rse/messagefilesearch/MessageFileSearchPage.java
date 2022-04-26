/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
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
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;
import biz.isphere.rse.search.SearchArgumentEditor;
import biz.isphere.rse.search.SearchArgumentsListEditor;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMsgFilePrompt;
import com.ibm.etools.systems.core.ui.widgets.SystemHistoryCombo;
import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.filters.SystemFilterPool;
import com.ibm.etools.systems.filters.SystemFilterPoolReference;
import com.ibm.etools.systems.model.SystemConnection;

public class MessageFileSearchPage extends XDialogPage implements ISearchPage, Listener {

    public static final String ID = "biz.isphere.rse.messagefilesearch.MessageFileSearchPage"; //$NON-NLS-1$

    private static final String START_COLUMN = "startColumn"; //$NON-NLS-1$
    private static final String END_COLUMN = "endColumn"; //$NON-NLS-1$
    private static final String CONNECTION = "connection"; //$NON-NLS-1$
    private static final String TARGET = "target"; //$NON-NLS-1$
    private static final String FILTER_POOL_NAME = "filterPoolName"; //$NON-NLS-1$
    private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
    private static final String MESSAGE_FILE = "messageFile"; //$NON-NLS-1$
    private static final String LIBRARY = "library"; //$NON-NLS-1$
    private static final String COLUMN_BUTTONS_SELECTION = "columnButtonsSelection"; //$NON-NLS-1$
    private static final String INCLUDE_FIRST_LEVEL_TEXT = "includeFirstLevelText"; //$NON-NLS-1$
    private static final String INCLUDE_SECOND_LEVEL_TEXT = "includeSecondLevelText"; //$NON-NLS-1$
    private static final String INCLUDE_MESSAGE_ID = "includeMessageId"; //$NON-NLS-1$

    private static final String TARGET_FILTER_STRING = "target.filterString"; //$NON-NLS-1$
    private static final String TARGET_SOURCE_MEMBER = "target.sourceMember"; //$NON-NLS-1$

    private static int DEFAULT_START_COLUMN = 1;
    private static int DEFAULT_END_COLUMN = SearchArgument.MAX_MESSAGE_FILE_SEARCH_COLUMN;

    private static final String SEARCH_ALL_COLUMNS = "ALL"; //$NON-NLS-1$
    private static final String SEARCH_BETWEEN_COLUMNS = "BETWEEN"; //$NON-NLS-1$

    private static final String TARGET_RADIO_BUTTON = "BUTTON"; //$NON-NLS-1$

    private ISearchPageContainer container;
    private ISeriesConnectionCombo connectionCombo;
    private Combo filterPoolCombo;
    private Combo filterCombo;
    private ISeriesMsgFilePrompt messageFilePrompt;
    private Button allColumnsButton;
    private Button betweenColumnsButton;
    private Text startColumnText;
    private Text endColumnText;
    private SearchArgumentsListEditor searchArgumentsListEditor;
    private Button includeFirstLevelTextButton;
    private Button includeSecondLevelTextButton;
    private Button includeMessageIdButton;

    private LinkedHashMap<String, SystemFilterPoolReference> filterPoolsOfConnection;
    private LinkedHashMap<String, SystemFilter> filtersOfFilterPool;

    private Composite targetFilterComposite;
    private Composite targetMessageFileComposite;
    private Button filterRadioButton;
    private Button messageFileRadioButton;
    private TypedListener targetFocusListener;
    private TypedListener targetMouseListener;

    public MessageFileSearchPage() {
        super();

        filterPoolsOfConnection = new LinkedHashMap<String, SystemFilterPoolReference>();
        filtersOfFilterPool = new LinkedHashMap<String, SystemFilter>();
        targetFocusListener = new TypedListener(new TargetModifyListener());
        targetMouseListener = new TypedListener(new TargetMouseListener());
    }

    public void createControl(Composite aParent) {

        initializeDialogUnits(aParent);

        Composite tMainPanel = new Composite(aParent, SWT.NONE);
        tMainPanel.setLayout(new GridLayout());
        GridData tGridData = new GridData(GridData.FILL_HORIZONTAL);
        tMainPanel.setLayoutData(tGridData);

        createSearchStringEditorGroup(tMainPanel);
        createConnectionGroup(tMainPanel);
        createSearchTargetGroup(tMainPanel);
        createColumnsGroup(tMainPanel);
        createOptionsGroup(tMainPanel);

        addListeners();

        loadScreenValues();

        setControl(tMainPanel);

    }

    private void createSearchStringEditorGroup(Composite aMainPanel) {
        searchArgumentsListEditor = new SearchArgumentsListEditor(SearchOptions.ARGUMENTS_SIZE, false, SearchOptionConfig
            .getAdditionalMessageFileSearchOptions());
        searchArgumentsListEditor.setListener(this);
        searchArgumentsListEditor.createControl(aMainPanel);
    }

    private void createConnectionGroup(Composite aMainPanel) {
        connectionCombo = new ISeriesConnectionCombo(aMainPanel);
        connectionCombo.getPromptLabel().setText(Messages.Connection);
    }

    private void createSearchTargetGroup(Composite aMainPanel) {

        Group tTargetGroup = createGroup(aMainPanel, Messages.Target, 2);

        createFilterGroup(tTargetGroup);
        createMessageFileGroup(tTargetGroup);
    }

    private void createFilterGroup(Group parent) {

        filterRadioButton = WidgetFactory.createRadioButton(parent);

        targetFilterComposite = new Composite(parent, SWT.BORDER);
        targetFilterComposite.setLayout(new GridLayout(2, false));
        targetFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        targetFilterComposite.setData(TARGET_RADIO_BUTTON, filterRadioButton);

        Label profileLabel = new Label(targetFilterComposite, SWT.NONE);
        profileLabel.setText(Messages.Filter_pool_colon);
        filterPoolCombo = WidgetFactory.createReadOnlyCombo(targetFilterComposite);
        filterPoolCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label filterLabel = new Label(targetFilterComposite, SWT.NONE);
        filterLabel.setText(Messages.Filter_colon);
        filterCombo = WidgetFactory.createReadOnlyCombo(targetFilterComposite);
        filterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void createMessageFileGroup(Composite parent) {

        messageFileRadioButton = WidgetFactory.createRadioButton(parent);

        targetMessageFileComposite = new Composite(parent, SWT.BORDER);
        targetMessageFileComposite.setLayout(new GridLayout(2, false));
        targetMessageFileComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        targetMessageFileComposite.setData(TARGET_RADIO_BUTTON, messageFileRadioButton);

        messageFilePrompt = new ISeriesMsgFilePrompt(targetMessageFileComposite, SWT.NONE, true, true);
        messageFilePrompt.setSystemConnection(connectionCombo.getSystemConnection());
        messageFilePrompt.getLibraryCombo().setToolTipText(Messages.Enter_or_select_a_library_name);
        messageFilePrompt.getObjectCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_message_file_name);
        messageFilePrompt.getLibraryPromptLabel().setText(Messages.Library);
        messageFilePrompt.getObjectPromptLabel().setText(Messages.Message_file);
    }

    private void createColumnsGroup(Composite aMainPanel) {
        Group tColumnsGroup = createGroup(aMainPanel, Messages.Columns);
        GridLayout tColumnsGroupLayout = new GridLayout(1, false);
        tColumnsGroupLayout.marginWidth = 0;
        tColumnsGroupLayout.marginHeight = 0;
        tColumnsGroup.setLayout(tColumnsGroupLayout);
        GridData tGridData = new GridData(GridData.FILL_VERTICAL);
        tGridData.horizontalAlignment = GridData.FILL;
        tGridData.grabExcessHorizontalSpace = true;
        tGridData.widthHint = 250;
        tColumnsGroup.setLayoutData(tGridData);

        Composite tAllColumnsPanel = new Composite(tColumnsGroup, SWT.NONE);
        GridLayout tAllColumnsLayout = new GridLayout(1, false);
        tAllColumnsPanel.setLayout(tAllColumnsLayout);
        allColumnsButton = WidgetFactory.createRadioButton(tAllColumnsPanel);
        allColumnsButton.setText(Messages.All_columns);
        allColumnsButton.setToolTipText(Messages.Search_all_columns);

        Composite tBetweenColumnsPanel = new Composite(tColumnsGroup, SWT.NONE);
        GridLayout tBetweenColumnsLayout = new GridLayout(4, false);
        tBetweenColumnsPanel.setLayout(tBetweenColumnsLayout);
        betweenColumnsButton = WidgetFactory.createRadioButton(tBetweenColumnsPanel);
        betweenColumnsButton.setText(Messages.Between);
        betweenColumnsButton.setToolTipText(Messages.Search_between_specified_columns);

        startColumnText = WidgetFactory.createText(tBetweenColumnsPanel);
        tGridData = new GridData();
        tGridData.widthHint = 30;
        startColumnText.setLayoutData(tGridData);
        startColumnText.setTextLimit(3);
        startColumnText.setToolTipText(Messages.Specify_start_column);

        Label tAndLabel = new Label(tBetweenColumnsPanel, SWT.LEFT);
        tAndLabel.setText(Messages.and);

        endColumnText = WidgetFactory.createText(tBetweenColumnsPanel);
        tGridData = new GridData();
        tGridData.widthHint = 30;
        endColumnText.setLayoutData(tGridData);
        endColumnText.setTextLimit(3);
        endColumnText.setToolTipText(Messages.Specify_end_column_max_132);
    }

    private void createOptionsGroup(Composite aMainPanel) {
        Group tOptionsGroup = createGroup(aMainPanel, Messages.Options);
        GridLayout tOptionsGroupLayout = new GridLayout(2, false);
        tOptionsGroupLayout.marginWidth = 5;
        tOptionsGroupLayout.marginHeight = 5;
        tOptionsGroup.setLayout(tOptionsGroupLayout);
        GridData tGridData = new GridData(GridData.FILL_VERTICAL);
        tGridData.horizontalAlignment = GridData.FILL;
        tGridData.grabExcessHorizontalSpace = true;
        tGridData.widthHint = 250;
        tOptionsGroup.setLayoutData(tGridData);

        includeFirstLevelTextButton = WidgetFactory.createCheckbox(tOptionsGroup);
        includeFirstLevelTextButton.setText(Messages.IncludeFirstLevelText);
        includeFirstLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_first_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeFirstLevelTextButton.setLayoutData(tGridData);

        includeSecondLevelTextButton = WidgetFactory.createCheckbox(tOptionsGroup);
        includeSecondLevelTextButton.setText(Messages.IncludeSecondLevelText);
        includeSecondLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_second_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeSecondLevelTextButton.setLayoutData(tGridData);

        includeMessageIdButton = WidgetFactory.createCheckbox(tOptionsGroup);
        includeMessageIdButton.setText(Messages.IncludeMessageId);
        includeMessageIdButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_message_id);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 1, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeMessageIdButton.setLayoutData(tGridData);

        Link lnkHelp = new Link(tOptionsGroup, SWT.NONE);
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

    private void loadFilterPoolsOfConnection(String connectionName) {

        debugPrint("Loading filter pools of connection ..."); //$NON-NLS-1$

        filterPoolsOfConnection.clear();

        SystemFilterPoolReference[] systemFilterPoolReferences = RSEFilterHelper.getConnectionFilterPools(connectionName);
        for (SystemFilterPoolReference systemFilterPoolReference : systemFilterPoolReferences) {
            filterPoolsOfConnection.put(systemFilterPoolReference.getName(), systemFilterPoolReference);
        }

        setFilterPools();
    }

    private void loadFiltersOfFilterPool(String systemFilterPoolName) {

        debugPrint("Loading filters of filter pool ..."); //$NON-NLS-1$

        filtersOfFilterPool.clear();

        SystemFilterPoolReference systemFilterPoolReference = filterPoolsOfConnection.get(systemFilterPoolName);
        if (systemFilterPoolReference != null && !systemFilterPoolReference.isReferenceBroken()) {

            SystemFilterPool referencedFilterPool = systemFilterPoolReference.getReferencedFilterPool();
            if (referencedFilterPool != null) {
                SystemFilter[] filters = referencedFilterPool.getSystemFilters();
                for (SystemFilter filter : filters) {
                    if (!filter.isPromptable()
                        && (systemFilterPoolReference == null || filter.getParentFilterPool().getName().equals(filterPoolCombo.getText()))) {
                        filtersOfFilterPool.put(filter.getName(), filter);
                    }
                }
            }
        }

        setFilters();
    }

    private void setFilterPools() {

        debugPrint("Setting filter pools of connection ..."); //$NON-NLS-1$

        String[] poolNames = filterPoolsOfConnection.keySet().toArray(new String[filterPoolsOfConnection.keySet().size()]);
        // Arrays.sort(poolNames, new IgnoreCaseComparator());

        filterPoolCombo.setItems(poolNames);
        if (poolNames.length > 0) {
            filterPoolCombo.setText(filterPoolCombo.getItem(0));
        } else {
            filterPoolCombo.setText(""); //$NON-NLS-1$
        }

        loadFiltersOfFilterPool(filterPoolCombo.getText());
    }

    private void setFilters() {

        debugPrint("Setting filters of filter pool ..."); //$NON-NLS-1$

        String[] filterNames = filtersOfFilterPool.keySet().toArray(new String[filtersOfFilterPool.keySet().size()]);
        // Arrays.sort(filterNames, new IgnoreCaseComparator());

        filterCombo.setItems(filterNames);
        if (filterNames.length > 0) {
            filterCombo.setText(filterCombo.getItem(0));
        } else {
            filterCombo.setText(""); //$NON-NLS-1$
        }
    }

    private Group createGroup(Composite aParent, String aText) {
        return createGroup(aParent, aText, 1);
    }

    private Group createGroup(Composite aParent, String aText, int numColumns) {
        Group tGroup = new Group(aParent, SWT.SHADOW_ETCHED_IN);
        tGroup.setText(aText);
        GridLayout scopeLayout = new GridLayout();
        scopeLayout.numColumns = numColumns;
        tGroup.setLayout(scopeLayout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        tGroup.setLayoutData(gd);
        return tGroup;
    }

    private void setTargetRadioButtonsSelected(Widget widget) {

        if (widget instanceof SystemHistoryCombo) {
            setTargetRadioButtonsSelected(((SystemHistoryCombo)widget).getParent());
        } else if (widget instanceof Combo) {
            setTargetRadioButtonsSelected(((Combo)widget).getParent());
        } else if (widget instanceof Label) {
            setTargetRadioButtonsSelected(((Label)widget).getParent());
        } else if (widget instanceof Composite) {
            Object data = widget.getData(TARGET_RADIO_BUTTON);
            if (data == filterRadioButton) {
                filterRadioButton.setSelection(true);
                messageFileRadioButton.setSelection(false);
            } else if (data == messageFileRadioButton) {
                filterRadioButton.setSelection(false);
                messageFileRadioButton.setSelection(true);
            } else {
                setTargetRadioButtonsSelected(((Composite)widget).getParent());
            }
        }
    }

    /**
     * Implementation of the ISearchPage" interface.
     */
    public void setContainer(ISearchPageContainer aContainer) {
        container = aContainer;
    }

    /**
     * Add listeners to verify user input.
     */
    private void addListeners() {

        connectionCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                debugPrint("Connection: -> SelectionListener"); //$NON-NLS-1$
                loadFilterPoolsOfConnection(connectionCombo.getText());
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        filterPoolCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                debugPrint("Filter Pool: -> SelectionListener"); //$NON-NLS-1$
                loadFiltersOfFilterPool(filterPoolCombo.getText());
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        filterPoolCombo.addListener(SWT.Modify, targetFocusListener);
        filterCombo.addListener(SWT.Modify, targetFocusListener);
        WidgetHelper.addListener(messageFilePrompt, SWT.Modify, targetFocusListener);

        WidgetHelper.addListener(targetFilterComposite, SWT.MouseUp, targetMouseListener);
        WidgetHelper.addListener(targetMessageFileComposite, SWT.MouseUp, targetMouseListener);

        allColumnsButton.addListener(SWT.Selection, this);
        betweenColumnsButton.addListener(SWT.Selection, this);
        startColumnText.addListener(SWT.Modify, this);
        startColumnText.addVerifyListener(new NumericOnlyVerifyListener());
        endColumnText.addListener(SWT.Modify, this);
        endColumnText.addVerifyListener(new NumericOnlyVerifyListener());
        includeFirstLevelTextButton.addListener(SWT.Selection, this);
        includeSecondLevelTextButton.addListener(SWT.Selection, this);
        includeMessageIdButton.addListener(SWT.Selection, this);
    }

    /**
     * Restores the screen values of the last search search.
     */
    private void loadScreenValues() {

        searchArgumentsListEditor.loadScreenValues(getDialogSettings());

        includeFirstLevelTextButton.setSelection(loadBooleanValue(INCLUDE_FIRST_LEVEL_TEXT, true));
        includeSecondLevelTextButton.setSelection(loadBooleanValue(INCLUDE_SECOND_LEVEL_TEXT, false));
        includeMessageIdButton.setSelection(loadBooleanValue(INCLUDE_MESSAGE_ID, false));

        if (loadValue(CONNECTION, null) != null) {
            ISeriesConnection connection = ISeriesConnection.getConnection(loadValue(CONNECTION, null));
            if (connection != null) {
                debugPrint("loadScreenValues(): setting connection"); //$NON-NLS-1$
                connectionCombo.select(connection);
            } else {
                debugPrint("loadScreenValues(): setting connection - FAILED"); //$NON-NLS-1$
            }
        } else {
            if (connectionCombo.getItems().length > 0) {
                debugPrint("loadScreenValues(): setting connection"); //$NON-NLS-1$
                connectionCombo.select(0);
            } else {
                debugPrint("loadScreenValues(): setting connection - FAILED"); //$NON-NLS-1$
            }
        }

        int i;
        i = findFilterPoolIndex(loadValue(FILTER_POOL_NAME, "")); //$NON-NLS-1$
        if (i >= 0) {
            debugPrint("loadScreenValues(): setting filter pool"); //$NON-NLS-1$
            filterPoolCombo.select(i);
            loadFiltersOfFilterPool(filterPoolCombo.getText());
        } else {
            if (filterPoolsOfConnection.size() > 0) {
                debugPrint("loadScreenValues(): setting filter pool"); //$NON-NLS-1$
                filterPoolCombo.select(0);
                loadFiltersOfFilterPool(filterPoolCombo.getText());
            } else {
                debugPrint("loadScreenValues(): setting filter pool - FAILED"); //$NON-NLS-1$
            }
        }

        i = findFilterIndex(loadValue(FILTER_NAME, "")); //$NON-NLS-1$
        if (i >= 0) {
            debugPrint("loadScreenValues(): setting filter"); //$NON-NLS-1$
            filterCombo.select(i);
        } else {
            if (filtersOfFilterPool.size() > 0) {
                debugPrint("loadScreenValues(): setting filter"); //$NON-NLS-1$
                filterCombo.select(0);
            } else {
                debugPrint("loadScreenValues(): setting filter - FAILED"); //$NON-NLS-1$
            }
        }

        messageFilePrompt.getLibraryCombo().setText(loadValue(LIBRARY, "")); //$NON-NLS-1$
        messageFilePrompt.getObjectCombo().setText(loadValue(MESSAGE_FILE, "")); //$NON-NLS-1$

        loadColumnButtonsSelection();

        if (!isIncludeFirstLevelText() && !isIncludeSecondLevelText() && !isIncludeMessageId()) {
            includeFirstLevelTextButton.setSelection(true);
        }

        if (hasMessageFile() && TARGET_SOURCE_MEMBER.equals(loadValue(TARGET, TARGET_SOURCE_MEMBER))) {
            filterRadioButton.setSelection(false);
            messageFileRadioButton.setSelection(true);
        } else {
            filterRadioButton.setSelection(true);
            messageFileRadioButton.setSelection(false);
        }
    }

    private boolean hasMessageFile() {

        if (!StringHelper.isNullOrEmpty(messageFilePrompt.getLibraryName())) {
            return true;
        }

        if (!StringHelper.isNullOrEmpty(messageFilePrompt.getObjectName())) {
            return true;
        }

        return false;
    }

    private int findFilterPoolIndex(String filterPoolName) {

        String[] filterPoolItems = filterPoolCombo.getItems();
        for (int i = 0; i < filterPoolItems.length; i++) {
            String filterPoolItem = filterPoolItems[i];
            if (filterPoolItem.equals(filterPoolName)) {
                return i;
            }
        }

        return -1;
    }

    private int findFilterIndex(String filterName) {

        String[] filterItems = filterCombo.getItems();
        for (int i = 0; i < filterItems.length; i++) {
            String filterPoolItem = filterItems[i];
            if (filterPoolItem.equals(filterName)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    private void storeScreenValues() {

        searchArgumentsListEditor.storeScreenValues(getDialogSettings());

        storeValue(INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        storeValue(INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
        storeValue(INCLUDE_MESSAGE_ID, isIncludeMessageId());
        storeValue(CONNECTION, getConnectionName());
        if (messageFileRadioButton.getSelection()) {
            storeValue(TARGET, TARGET_SOURCE_MEMBER);
        } else {
            storeValue(TARGET, TARGET_FILTER_STRING);
        }

        storeValue(FILTER_POOL_NAME, filterPoolCombo.getText());
        storeValue(FILTER_NAME, filterCombo.getText());

        storeValue(LIBRARY, getMessageFileLibrary());
        storeValue(MESSAGE_FILE, getMessageFile());

        saveColumnButtonsSelection();
    }

    /**
     * Restores the status of the "columns" buttons and text fields.
     */
    private void loadColumnButtonsSelection() {
        String tColumnButtonsSelection = loadValue(COLUMN_BUTTONS_SELECTION, SEARCH_ALL_COLUMNS);
        if (SEARCH_ALL_COLUMNS.equals(tColumnButtonsSelection)) {
            allColumnsButton.setSelection(true);
            processAllColumnsButtonSelected();
        } else {
            betweenColumnsButton.setSelection(true);
            processBetweenColumnsButtonSelected();
        }
        startColumnText.setText(loadValue(START_COLUMN, Integer.toString(DEFAULT_START_COLUMN)));
        endColumnText.setText(loadValue(END_COLUMN, Integer.toString(DEFAULT_END_COLUMN)));
    }

    /**
     * Saved the status of the "columns" buttons and text fields.
     */
    private void saveColumnButtonsSelection() {
        if (allColumnsButton.getSelection()) {
            storeValue(COLUMN_BUTTONS_SELECTION, SEARCH_ALL_COLUMNS);
        } else {
            storeValue(COLUMN_BUTTONS_SELECTION, SEARCH_BETWEEN_COLUMNS);
            storeValue(START_COLUMN, getNumericFieldContent(startColumnText));
            storeValue(END_COLUMN, getNumericFieldContent(endColumnText));
        }
    }

    /**
     * Returns the search string the message files are searched for.
     * 
     * @return search argument
     */
    private String getCombinedSearchString() {
        StringBuilder tBuffer = new StringBuilder();
        for (SearchArgument tSearchArgument : searchArgumentsListEditor.getSearchArguments(0, 0)) {
            if (tSearchArgument.getString().trim().length() > 0) {
                if (tBuffer.length() > 0) {
                    tBuffer.append("/"); //$NON-NLS-1$
                }
                tBuffer.append(tSearchArgument.getString());
            }
        }
        return tBuffer.toString();
    }

    /**
     * Checks whether the user entered a search string.
     * 
     * @return <code>true</code> if the search string is present, else
     *         <code>false</code>
     */
    private boolean isSearchStringEmpty() {
        if (getCombinedSearchString().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the name of the RSE connection.
     * 
     * @return name of the RSE connection
     */
    private String getConnectionName() {
        return connectionCombo.getText();
    }

    private SystemFilter getFilter() {
        return filtersOfFilterPool.get(filterCombo.getText());
    }

    /**
     * Returns the simple or generic name of the libraries that are searched for
     * the message file.
     * 
     * @return name of the library
     */
    private String getMessageFileLibrary() {
        return messageFilePrompt.getLibraryCombo().getText();
    }

    /**
     * Returns the simple or generic name of the message file(s) that are
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
     * Returns the status of the "match option" radio buttons.
     * 
     * @return status of the "match option" radio buttons
     */
    private String getMatchOption() {
        return searchArgumentsListEditor.getMatchOption();
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

        if (messageFileRadioButton.getSelection()) {
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

        storeScreenValues();

        try {

            StructuredSelection tSelection = (StructuredSelection)connectionCombo.getSelection();
            SystemConnection tHost = (SystemConnection)tSelection.getFirstElement();

            ISeriesConnection tConnection = ISeriesConnection.getConnection(tHost);
            if (!ISphereHelper.checkISphereLibrary(getShell(), tConnection.getConnectionName())) {
                return false;
            }

            HashMap<String, SearchElement> searchElements;
            if (filterRadioButton.getSelection()) {
                searchElements = loadFilterSearchElements(tConnection, getFilter());
            } else {
                searchElements = loadMessageFileSearchElements(tConnection, getMessageFileLibrary(), getMessageFile());
            }

            if (searchElements.isEmpty()) {
                MessageDialog.openInformation(getShell(), Messages.Information, Messages.No_objects_found_that_match_the_selection_criteria);
                return false;
            }

            SearchPostRun postRun = new SearchPostRun();
            postRun.setConnection(tConnection);
            postRun.setConnectionName(tConnection.getConnectionName());
            postRun.setSearchString(getCombinedSearchString());
            postRun.setSearchElements(searchElements);
            postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

            int startColumn;
            int endColumn;
            if (allColumnsButton.getSelection()) {
                startColumn = -1;
                endColumn = -1;
            } else {
                startColumn = getNumericFieldContent(startColumnText);
                endColumn = getNumericFieldContent(endColumnText);
            }

            SearchOptions searchOptions = new SearchOptions(getMatchOption(), isShowRecords());
            for (SearchArgument searchArgument : searchArgumentsListEditor.getSearchArguments(startColumn, endColumn)) {
                if (!StringHelper.isNullOrEmpty(searchArgument.getString())) {
                    searchOptions.addSearchArgument(searchArgument);
                }
            }
            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_FIRST_LEVEL_TEXT, new Boolean(isIncludeFirstLevelText()));
            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_SECOND_LEVEL_TEXT, new Boolean(isIncludeSecondLevelText()));
            searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_MESSAGE_ID, new Boolean(isIncludeMessageId()));

            Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(tConnection.getConnectionName());

            new SearchExec().execute(tConnection.getAS400ToolboxObject(getShell()), tConnection.getHostName(), jdbcConnection, searchOptions,
                new ArrayList<SearchElement>(searchElements.values()), postRun);

        } catch (Exception e) {
            ISpherePlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return true;
    }

    private HashMap<String, SearchElement> loadMessageFileSearchElements(ISeriesConnection connection, String library, String messageFile)
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

    private HashMap<String, SearchElement> loadFilterSearchElements(ISeriesConnection connection, SystemFilter filter) throws InterruptedException {

        HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();

        try {

            MessageFileSearchDelegate delegate = new MessageFileSearchDelegate(getShell(), connection);
            delegate.addElementsFromFilterString(searchElements, filter.getFilterStrings());

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return searchElements;
    }

    /**
     * Handles "modify" and "selection" events to enable/disable widgets and
     * error checking.
     */
    public void handleEvent(Event anEvent) {
        Widget widget = anEvent.widget;
        int type = anEvent.type;

        boolean result = true;

        if ((widget == allColumnsButton) && (type == SWT.Selection)) {
            processAllColumnsButtonSelected();
        } else if ((widget == betweenColumnsButton) && (type == SWT.Selection)) {
            processBetweenColumnsButtonSelected();
        } else if ((widget == startColumnText) && (type == SWT.Modify)) {
            result = processStartColumnTextModified();
        } else if ((widget == endColumnText) && (type == SWT.Modify)) {
            result = processEndColumnTextModified();
        } else if (!widget.isDisposed() && widget.getData() == SearchArgumentEditor.TEXT_SEARCH_STRING && (type == SWT.Modify)) {
            result = !isSearchStringEmpty();
        }

        if (!result) {
            container.setPerformActionEnabled(false);
        } else {
            container.setPerformActionEnabled(checkAll());
        }

        setSearchOptionsEnablement(anEvent);
    }

    protected void setSearchOptionsEnablement(Event anEvent) {

        if (!(anEvent.data instanceof SearchOptionConfig)) {
            return;
        }

        SearchOptionConfig config = (SearchOptionConfig)anEvent.data;

        allColumnsButton.setEnabled(config.isColumnRangeEnabled());
        betweenColumnsButton.setEnabled(config.isColumnRangeEnabled());
        startColumnText.setEnabled(config.isColumnRangeEnabled());
        endColumnText.setEnabled(config.isColumnRangeEnabled());

        includeFirstLevelTextButton.setEnabled(config.isIncludeFirstLevelTextEnabled());
        includeSecondLevelTextButton.setEnabled(config.isIncludeSecondLevelTextEnabled());
        includeMessageIdButton.setEnabled(config.isIncludeMessageIdEnabled());
    }

    /**
     * Executed when the "All columns" radio button has been selected.
     */
    private void processAllColumnsButtonSelected() {
        betweenColumnsButton.setSelection(false);

        startColumnText.setEnabled(false);
        endColumnText.setEnabled(false);
    }

    /**
     * Executed when the "Between" radio button has been selected.
     */
    private void processBetweenColumnsButtonSelected() {
        allColumnsButton.setSelection(false);

        startColumnText.setEnabled(true);
        endColumnText.setEnabled(true);

        if (StringHelper.isNullOrEmpty(startColumnText.getText())) {
            startColumnText.setText(Integer.toString(DEFAULT_START_COLUMN));
        }

        if (StringHelper.isNullOrEmpty(endColumnText.getText())) {
            endColumnText.setText(Integer.toString(DEFAULT_END_COLUMN));
        }
    }

    /**
     * Executed when the value of the "Start column" text widget changes in
     * order to check the current value.
     * 
     * @return zero on success, else negative response code indicating the type
     *         of the error
     */
    private boolean processStartColumnTextModified() {
        return queryNumericFieldContent(startColumnText) == 0;
    }

    /**
     * Executed when the value of the "End column" text widget changes in order
     * to check the current value.
     * 
     * @return zero on success, else negative response code indicating the type
     *         of the error
     */
    private boolean processEndColumnTextModified() {
        return queryNumericFieldContent(endColumnText) == 0;
    }

    /**
     * Executed on every widget event to check the input values.
     * 
     * @return <code>true</code> on success, else <true>false</code>.
     */
    private boolean checkAll() {

        int startColumn = getNumericFieldContent(startColumnText);
        int endColumn = getNumericFieldContent(endColumnText);
        List<SearchArgument> tSearchArguments = searchArgumentsListEditor.getSearchArguments(startColumn, endColumn);
        for (SearchArgument tSearchArgument : tSearchArguments) {
            if (StringHelper.isNullOrEmpty(tSearchArgument.getString())) {
                return false;
            }
        }

        if (connectionCombo.getISeriesConnection() == null) {
            return false;
        }

        if (betweenColumnsButton.getSelection()) {
            if (queryNumericFieldContent(startColumnText) != 0) {
                return false;
            }
            if (queryNumericFieldContent(endColumnText) != 0) {
                return false;
            }
            if (getNumericFieldContent(endColumnText) > SearchArgument.MAX_MESSAGE_FILE_SEARCH_COLUMN) {
                return false;
            }
        }

        if (!isIncludeFirstLevelText() && !isIncludeSecondLevelText() && !isIncludeMessageId()) {
            return false;
        }

        return true;
    }

    /**
     * Checks a field for the following conditions:
     * <ol>
     * <li>content must not be empty (rc = -1)</li>
     * <li>content must be a numeric value (rc = -2)</li>
     * <li>numeric value must be greater zero (rc = -3)</li>
     * </ol>
     * 
     * @param aTextField - field that is checked
     * @return return 0 if the field content matches the rules, else negative
     *         value indicating the error
     */
    private int queryNumericFieldContent(Text aTextField) {
        String tText = aTextField.getText();
        if (StringHelper.isNullOrEmpty(tText)) {
            return -1; // empty
        }

        int number = 0;
        try {
            number = Integer.valueOf(tText).intValue();
            if (number <= 0) {
                return -3; // negative
            }
            return 0;
        } catch (NumberFormatException localNumberFormatException) {
            return -2; // error!
        }
    }

    /**
     * Return the content of a numeric field.
     * 
     * @param textField - numeric field widget
     * @return numeric value
     */
    private int getNumericFieldContent(Text textField) {
        return IntHelper.tryParseInt(textField.getText(), 0);
    }

    private void debugPrint(String message) {
        // Xystem.out.println(message);
    }

    private class TargetModifyListener implements ModifyListener {

        public void modifyText(ModifyEvent event) {
            debugPrint("Selecting target radio button: " + event.getSource().getClass().getSimpleName()); //$NON-NLS-1$
            setTargetRadioButtonsSelected(event.widget);
        }

    }

    private class TargetMouseListener extends MouseAdapter {

        @Override
        public void mouseUp(MouseEvent event) {
            debugPrint("Clicking target radio button: " + event.getSource().getClass().getSimpleName()); //$NON-NLS-1$
            setTargetRadioButtonsSelected(event.widget);
        }

    }
}
