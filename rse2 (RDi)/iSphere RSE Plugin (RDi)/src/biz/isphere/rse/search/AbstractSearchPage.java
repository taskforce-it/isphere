/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.search;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterPool;
import org.eclipse.rse.core.filters.ISystemFilterPoolReference;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.search.MatchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.WidgetHelper;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.resourcemanagement.filter.RSEFilterHelper;

public abstract class AbstractSearchPage extends XDialogPage implements ISearchPage, Listener {

    private static final String CONNECTION = "connection"; //$NON-NLS-1$

    private static final String TARGET = "target"; //$NON-NLS-1$
    private static final String TARGET_FILTER_STRING = "target.filterString"; //$NON-NLS-1$
    private static final String TARGET_SOURCE_MEMBER = "target.sourceMember"; //$NON-NLS-1$

    private static final String FILTER_POOL_NAME = "filterPoolName"; //$NON-NLS-1$
    private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$

    private static final String START_COLUMN = "startColumn"; //$NON-NLS-1$
    private static final String END_COLUMN = "endColumn"; //$NON-NLS-1$
    private static final String COLUMN_BUTTONS_SELECTION = "columnButtonsSelection"; //$NON-NLS-1$
    private static final String SEARCH_ALL_COLUMNS = "ALL"; //$NON-NLS-1$
    private static final String SEARCH_BETWEEN_COLUMNS = "BETWEEN"; //$NON-NLS-1$

    private ISearchPageContainer container;

    private ConnectionCombo connectionCombo;

    private Combo filterPoolCombo;
    private Combo filterCombo;

    private LinkedHashMap<String, ISystemFilter> filtersOfFilterPool;
    private LinkedHashMap<String, ISystemFilterPoolReference> filterPoolsOfConnection;

    private Composite targetFilterComposite;

    private TypedListener targetFocusListener;
    private TypedListener targetMouseListener;

    private Button filterRadioButton;
    private Button objectRadioButton;

    private Button allColumnsButton;
    private Button betweenColumnsButton;
    private Text startColumnText;
    private Text endColumnText;

    private SearchArgumentsListEditor searchArgumentsListEditor;

    private static final String TARGET_RADIO_BUTTON = "BUTTON";

    public AbstractSearchPage() {
        super();

        filterPoolsOfConnection = new LinkedHashMap<String, ISystemFilterPoolReference>();
        filtersOfFilterPool = new LinkedHashMap<String, ISystemFilter>();

        targetFocusListener = new TypedListener(new TargetModifyListener());
        targetMouseListener = new TypedListener(new TargetMouseListener());
    }

    public void createControl(Composite aParent) {

        initializeDialogUnits(aParent);

        ScrolledComposite scrollableArea = new ScrolledComposite(aParent, SWT.V_SCROLL | SWT.H_SCROLL);
        scrollableArea.setLayout(new GridLayout(1, false));
        scrollableArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollableArea.setExpandHorizontal(true);
        scrollableArea.setExpandVertical(true);

        Composite tMainPanel = new Composite(scrollableArea, SWT.NONE);
        tMainPanel.setLayout(new GridLayout());
        GridData tGridData = new GridData(GridData.FILL_HORIZONTAL);
        tMainPanel.setLayoutData(tGridData);

        createSearchStringEditorGroup(tMainPanel);
        createConnectionGroup(tMainPanel);
        createSearchTargetGroup(tMainPanel);
        createColumnsGroup(tMainPanel);
        createOptionsGroup(tMainPanel);

        tMainPanel.layout();
        scrollableArea.setContent(tMainPanel);
        scrollableArea.setMinSize(tMainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        addListeners();

        loadScreenValues();

        setFilterSelection();

        setControl(tMainPanel);

    }

    protected void createColumnsGroup(Composite parent) {

        Group tColumnsGroup = createGroup(parent, Messages.Columns);
        GridLayout tColumnsGroupLayout = new GridLayout(1, false);
        tColumnsGroupLayout.marginWidth = 0;
        tColumnsGroupLayout.marginHeight = 0;
        tColumnsGroup.setLayout(tColumnsGroupLayout);
        GridData tGridData = new GridData(GridData.FILL_VERTICAL);
        tGridData.horizontalAlignment = GridData.FILL;
        tGridData.grabExcessHorizontalSpace = true;
        tGridData.widthHint = 250;
        tColumnsGroup.setLayoutData(tGridData);

        createColumnItems(tColumnsGroup);
    }

    protected void createColumnItems(Group parent) {

        Composite tAllColumnsPanel = new Composite(parent, SWT.NONE);
        GridLayout tAllColumnsLayout = new GridLayout(1, false);
        tAllColumnsPanel.setLayout(tAllColumnsLayout);
        allColumnsButton = WidgetFactory.createRadioButton(tAllColumnsPanel);
        allColumnsButton.setText(Messages.All_columns);
        allColumnsButton.setToolTipText(Messages.Search_all_columns);

        Composite tBetweenColumnsPanel = new Composite(parent, SWT.NONE);
        GridLayout tBetweenColumnsLayout = new GridLayout(4, false);
        tBetweenColumnsPanel.setLayout(tBetweenColumnsLayout);
        betweenColumnsButton = WidgetFactory.createRadioButton(tBetweenColumnsPanel);
        betweenColumnsButton.setText(Messages.Between);
        betweenColumnsButton.setToolTipText(Messages.Search_between_specified_columns);

        createBetweenColumnsPanelItems(tBetweenColumnsPanel);
    }

    protected void createBetweenColumnsPanelItems(Composite parent) {

        startColumnText = createStartColumnItem(parent);

        Label tAndLabel = new Label(parent, SWT.LEFT);
        tAndLabel.setText(Messages.and);

        endColumnText = createEndColumnItem(parent);
    }

    private Text createStartColumnItem(Composite parent) {

        Text startColumnText = WidgetFactory.createText(parent);
        GridData tGridData = new GridData();
        tGridData.widthHint = 30;
        startColumnText.setLayoutData(tGridData);
        startColumnText.setTextLimit(3);
        startColumnText.setToolTipText(Messages.Specify_start_column);

        return startColumnText;
    }

    protected Text createEndColumnItem(Composite parent) {

        Text endColumnText = WidgetFactory.createText(parent);
        GridData tGridData = new GridData();
        tGridData.widthHint = 30;
        endColumnText.setLayoutData(tGridData);
        endColumnText.setTextLimit(3);
        endColumnText.setToolTipText(Messages.Specify_end_column_max_132);

        return endColumnText;
    }

    protected void createOptionsGroup(Composite parent) {

        Group tOptionsGroup = createGroup(parent, Messages.Options);
        GridLayout tOptionsGroupLayout = new GridLayout(2, false);
        tOptionsGroupLayout.marginWidth = 5;
        tOptionsGroupLayout.marginHeight = 5;
        tOptionsGroup.setLayout(tOptionsGroupLayout);
        GridData tGridData = new GridData(GridData.FILL_VERTICAL);
        tGridData.horizontalAlignment = GridData.FILL;
        tGridData.grabExcessHorizontalSpace = true;
        tGridData.widthHint = 250;
        tGridData.horizontalSpan = 2;
        tOptionsGroup.setLayoutData(tGridData);

        createOptionItems(tOptionsGroup);
    }

    protected abstract void createOptionItems(Group parent);

    protected void createSearchStringEditorGroup(Composite aMainPanel) {

        searchArgumentsListEditor = createSearchArgumentsListEditor(aMainPanel);
    }

    protected abstract SearchArgumentsListEditor createSearchArgumentsListEditor(Composite parent);

    protected void createConnectionGroup(Composite parent) {

        Composite connectionGroup = new Composite(parent, SWT.NONE);
        GridLayout connectionGroupLayout = new GridLayout(2, false);
        connectionGroupLayout.marginHeight = 0;
        connectionGroupLayout.marginWidth = 0;
        connectionGroup.setLayout(connectionGroupLayout);
        connectionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label connectionLabel = new Label(connectionGroup, SWT.NONE);
        connectionLabel.setText(Messages.Connection);

        connectionCombo = WidgetFactory.createConnectionCombo(connectionGroup);
        connectionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    protected void createSearchTargetGroup(Composite aMainPanel) {

        Group tTargetGroup = createGroup(aMainPanel, Messages.Target, 2);

        createFilterGroup(tTargetGroup);
        createObjectGroup(tTargetGroup);
    }

    protected void createFilterGroup(Group parent) {

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

    protected void createObjectGroup(Group parent) {
        objectRadioButton = WidgetFactory.createRadioButton(parent);
        parent.setData(TARGET_RADIO_BUTTON, objectRadioButton);

        createObjectItem(parent);
    }

    protected abstract void createObjectItem(Group parent);

    protected Group createGroup(Composite aParent, String aText) {
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

    /**
     * Add listeners to verify user input.
     */
    protected void addListeners() {

        connectionCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                debugPrint("Connection: -> SelectionListener"); //$NON-NLS-1$
                loadFilterPoolsOfConnection(ConnectionManager.getConnectionName(getHost()));
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

        WidgetHelper.addListener(targetFilterComposite, SWT.MouseUp, targetMouseListener);

        filterPoolCombo.addListener(SWT.Modify, targetFocusListener);
        filterCombo.addListener(SWT.Modify, targetFocusListener);

        allColumnsButton.addListener(SWT.Selection, this);
        betweenColumnsButton.addListener(SWT.Selection, this);

        startColumnText.addListener(SWT.Modify, this);
        startColumnText.addVerifyListener(new NumericOnlyVerifyListener());
        endColumnText.addListener(SWT.Modify, this);
        endColumnText.addVerifyListener(new NumericOnlyVerifyListener());
    }

    protected IHost getHost() {
        return getHost(connectionCombo.getQualifiedConnectionName());
    }

    private IHost getHost(String qualifiedConnectionName) {

        if (StringHelper.isNullOrEmpty(qualifiedConnectionName)) {
            return null;
        }

        try {
            IBMiConnection ibMiConnection = ConnectionManager.getIBMiConnection(qualifiedConnectionName);
            if (ibMiConnection == null) {
                return null;
            }

            IHost host = ibMiConnection.getHost();

            return host;

        } catch (Exception e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    /**
     * Returns the name of the RSE connection.
     * 
     * @return name of the RSE connection
     */
    protected String getConnectionName() {
        return connectionCombo.getQualifiedConnectionName();
    }

    protected ISystemFilter getFilter() {
        return filtersOfFilterPool.get(filterCombo.getText());
    }

    protected TypedListener getTargetFocusListener() {
        return targetFocusListener;
    }

    protected TypedListener getTargetMouseListener() {
        return targetMouseListener;
    }

    protected boolean isFilterRadioButtonSelected() {
        return filterRadioButton.getSelection();
    }

    /**
     * Returns the search string the target objects are searched for.
     * 
     * @return search argument
     */
    private String getCombinedSearchString() {
        StringBuilder tBuffer = new StringBuilder();
        for (SearchArgument tSearchArgument : getSearchArguments(0, 0)) {
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
    protected boolean isSearchStringEmpty() {
        if (getCombinedSearchString().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the status of the "match option" radio buttons.
     * 
     * @return status of the "match option" radio buttons
     */
    protected MatchOption getMatchOption() {
        return searchArgumentsListEditor.getMatchOption();
    }

    protected SearchArgument[] getSearchArguments(int startColumn, int endColumn) {
        List<SearchArgument> searchArguments = searchArgumentsListEditor.getSearchArguments(startColumn, endColumn);
        return searchArguments.toArray(new SearchArgument[searchArguments.size()]);
    }

    protected void loadScreenValues() {

        searchArgumentsListEditor.loadScreenValues(getDialogSettings());

        if (loadValue(CONNECTION, null) != null) {
            // IBMiConnection connection =
            // ConnectionManager.getIBMiConnection(loadValue(CONNECTION, null));
            String qualifiedConnectionName = loadValue(CONNECTION, null);
            if (qualifiedConnectionName != null) {
                debugPrint("loadScreenValues(): setting connection"); //$NON-NLS-1$
                connectionCombo.setQualifiedConnectionName(qualifiedConnectionName);
            } else {
                debugPrint("loadScreenValues(): setting connection - FAILED"); //$NON-NLS-1$
            }
        } else {
            if (connectionCombo.getItemCount() > 0) {
                debugPrint("loadScreenValues(): setting connection"); //$NON-NLS-1$
                connectionCombo.select(0);
            } else {
                debugPrint("loadScreenValues(): setting connection - FAILED"); //$NON-NLS-1$
            }
        }

        if (!StringHelper.isNullOrEmpty(connectionCombo.getQualifiedConnectionName())) {
            loadFilterPoolsOfConnection(connectionCombo.getQualifiedConnectionName());
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

        loadColumnButtonSelection();
    }

    protected void setFilterSelection() {

        if (hasSearchedObject() && TARGET_SOURCE_MEMBER.equals(loadValue(TARGET, TARGET_SOURCE_MEMBER))) {
            filterRadioButton.setSelection(false);
            objectRadioButton.setSelection(true);
        } else {
            filterRadioButton.setSelection(true);
            objectRadioButton.setSelection(false);
        }
    }

    protected abstract boolean hasSearchedObject();

    /**
     * Restores the status of the "columns" buttons and text fields.
     */
    protected void loadColumnButtonSelection() {

        String tColumnButtonsSelection = loadValue(COLUMN_BUTTONS_SELECTION, SEARCH_ALL_COLUMNS);
        if (SEARCH_ALL_COLUMNS.equals(tColumnButtonsSelection)) {
            allColumnsButton.setSelection(true);
            processAllColumnsButtonSelected();
        } else {
            betweenColumnsButton.setSelection(true);
            processBetweenColumnsButtonSelected();
        }
        startColumnText.setText(loadValue(START_COLUMN, Integer.toString(getDefaultStartColumnValue())));
        endColumnText.setText(loadValue(END_COLUMN, Integer.toString(getDefaultEndColumnValue())));
    }

    protected abstract int getDefaultStartColumnValue();

    protected abstract int getDefaultEndColumnValue();

    protected abstract int getMaxEndColumnValue();

    /**
     * Saved the status of the "columns" buttons and text fields.
     */
    protected void saveColumnButtonsSelection() {

        if (allColumnsButton.getSelection()) {
            storeValue(COLUMN_BUTTONS_SELECTION, SEARCH_ALL_COLUMNS);
        } else {
            storeValue(COLUMN_BUTTONS_SELECTION, SEARCH_BETWEEN_COLUMNS);
            storeValue(START_COLUMN, getNumericFieldContent(startColumnText));
            storeValue(END_COLUMN, getNumericFieldContent(endColumnText));
        }
    }

    protected void setFilterSelected(boolean isFilterSelected) {

        if (isFilterSelected) {
            filterRadioButton.setSelection(false);
            objectRadioButton.setSelection(true);
        } else {
            filterRadioButton.setSelection(true);
            objectRadioButton.setSelection(false);
        }
    }

    protected void loadFilterPoolsOfConnection(String connectionName) {

        debugPrint("Loading filter pools of connection ..."); //$NON-NLS-1$

        filterPoolsOfConnection.clear();

        ISystemFilterPoolReference[] systemFilterPoolReferences = loadSystemFilterPoolReferences(connectionName);
        for (ISystemFilterPoolReference systemFilterPoolReference : systemFilterPoolReferences) {
            filterPoolsOfConnection.put(systemFilterPoolReference.getName(), systemFilterPoolReference);
        }

        setFilterPools();
    }

    protected ISystemFilterPoolReference[] loadSystemFilterPoolReferences(String connectionName) {
        return RSEFilterHelper.getConnectionObjectFilterPools(connectionName);
    }

    protected void storeScreenValues() {

        searchArgumentsListEditor.storeScreenValues(getDialogSettings());

        storeValue(CONNECTION, getConnectionName());
        if (objectRadioButton.getSelection()) {
            storeValue(TARGET, TARGET_SOURCE_MEMBER);
        } else {
            storeValue(TARGET, TARGET_FILTER_STRING);
        }

        storeValue(FILTER_POOL_NAME, filterPoolCombo.getText());
        storeValue(FILTER_NAME, filterCombo.getText());

        saveColumnButtonsSelection();
    }

    private void setTargetRadioButtonsSelected(Widget widget) {

        if (widget instanceof Composite) {
            Object data = widget.getData(TARGET_RADIO_BUTTON);
            if (data == filterRadioButton) {
                filterRadioButton.setSelection(true);
                objectRadioButton.setSelection(false);
            } else if (data == objectRadioButton) {
                filterRadioButton.setSelection(false);
                objectRadioButton.setSelection(true);
            } else {
                setTargetRadioButtonsSelected(((Composite)widget).getParent());
            }
        } else if (widget instanceof Control) {
            setTargetRadioButtonsSelected(((Control)widget).getParent());
        }
    }

    protected void setFilterPools() {

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

    private void loadFiltersOfFilterPool(String systemFilterPoolName) {

        debugPrint("Loading filters of filter pool ..."); //$NON-NLS-1$

        filtersOfFilterPool.clear();

        ISystemFilterPoolReference systemFilterPoolReference = filterPoolsOfConnection.get(systemFilterPoolName);
        if (systemFilterPoolReference != null && !systemFilterPoolReference.isReferenceBroken()) {

            ISystemFilterPool referencedFilterPool = systemFilterPoolReference.getReferencedFilterPool();
            if (referencedFilterPool != null) {
                ISystemFilter[] filters = referencedFilterPool.getFilters();
                for (ISystemFilter filter : filters) {
                    if (!filter.isPromptable()
                        && (systemFilterPoolReference == null || filter.getParentFilterPool().getName().equals(filterPoolCombo.getText()))) {
                        filtersOfFilterPool.put(filter.getName(), filter);
                    }
                }
            }
        }

        setFilters();
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
            setPerformActionEnabled(false);
        } else {
            setPerformActionEnabled(checkAll());
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
    }

    /**
     * Executed on every widget event to check the input values.
     * 
     * @return <code>true</code> on success, else <true>false</code>.
     */
    private boolean checkAll() {

        int startColumn = getNumericFieldContent(startColumnText);
        int endColumn = getNumericFieldContent(endColumnText);
        SearchArgument[] tSearchArguments = getSearchArguments(startColumn, endColumn);
        for (SearchArgument tSearchArgument : tSearchArguments) {
            if (StringHelper.isNullOrEmpty(tSearchArgument.getString())) {
                return false;
            }
        }

        if (getHost() == null || IBMiConnection.getConnection(getHost()) == null) {
            return false;
        }

        if (allColumnsButton.getSelection()) {
            return true;
        }

        if (betweenColumnsButton.getSelection()) {
            if (queryNumericFieldContent(startColumnText) != 0) {
                return false;
            }
            if (queryNumericFieldContent(endColumnText) != 0) {
                return false;
            }
            if (getNumericFieldContent(endColumnText) <= getMaxEndColumnValue()) {
                return true;
            }
        }

        return false;
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
            startColumnText.setText(Integer.toString(getDefaultStartColumnValue()));
        }

        if (StringHelper.isNullOrEmpty(endColumnText.getText())) {
            endColumnText.setText(Integer.toString(getDefaultEndColumnValue()));
        }
    }

    protected int getStartColumnValue() {

        if (allColumnsButton.getSelection()) {
            return 1;
        } else {
            return getNumericFieldContent(startColumnText);
        }
    }

    protected int getEndColumnValue() {

        if (allColumnsButton.getSelection()) {
            return getMaxEndColumnValue();
        } else {
            return getNumericFieldContent(endColumnText);
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
    protected int queryNumericFieldContent(Text aTextField) {
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
    protected int getNumericFieldContent(Text textField) {
        return IntHelper.tryParseInt(textField.getText(), 0);
    }

    /**
     * Implementation of the ISearchPage" interface.
     */
    public void setContainer(ISearchPageContainer aContainer) {
        container = aContainer;
    }

    protected void setPerformActionEnabled(boolean enabled) {
        container.setPerformActionEnabled(enabled);
    }

    protected void debugPrint(String message) {
        // System.out.println(message);
    }

    protected class TargetModifyListener implements ModifyListener {

        public TargetModifyListener() {
        }

        public void modifyText(ModifyEvent event) {
            debugPrint("Selecting target radio button: " + event.getSource().getClass().getSimpleName()); //$NON-NLS-1$
            setTargetRadioButtonsSelected(event.widget);
        }

    }

    protected class TargetMouseListener extends MouseAdapter {

        public TargetMouseListener() {
        }

        @Override
        public void mouseUp(MouseEvent event) {
            debugPrint("Clicking target radio button: " + event.getSource().getClass().getSimpleName()); //$NON-NLS-1$
            setTargetRadioButtonsSelected(event.widget);
        }

    }
}
