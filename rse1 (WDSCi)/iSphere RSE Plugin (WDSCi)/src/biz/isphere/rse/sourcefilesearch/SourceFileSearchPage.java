/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.sourcefilesearch.SearchElement;
import biz.isphere.core.sourcefilesearch.SearchExec;
import biz.isphere.core.sourcefilesearch.SearchOptions;
import biz.isphere.core.sourcefilesearch.SearchPostRun;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.search.SearchArgumentEditor;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.widgets.IISeriesFilePromptTypes;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesConnectionCombo;
import com.ibm.etools.iseries.core.ui.widgets.ISeriesMemberPrompt;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.model.SystemConnection;

public class SourceFileSearchPage extends XDialogPage implements ISearchPage, Listener {

    private static final String START_COLUMN = "startColumn";
    private static final String END_COLUMN = "endColumn";
    private static final String SOURCE_FILE = "sourceFile";
    private static final String SOURCE_MEMBER = "sourceMember";
    private static final String LIBRARY = "library";
    private static final String MATCH_ALL = "matchAll";
    private static final String SHOW_RECORDS = "showRecords";
    private static final String COLUMN_BUTTONS_SELECTION = "columnButtonsSelection";
    private static final String NUM_CONDITIONS = "numberOfCompareConditions";
    private static final String COMPARE_CONDITION = "compareCondition";
    private static final String SEARCH_STRING = "searchString";
    private static final String CASE_SENSITIVE = "caseSensitive";

    private ISearchPageContainer container;
    private ISeriesConnectionCombo connectionCombo;
    private ISeriesMemberPrompt sourceFilePrompt;
    private Button allColumnsButton;
    private Button betweenColumnsButton;
    private Text startColumnText;
    private Text endColumnText;
    private Button showRecordsButton;
    private List<SearchArgumentEditor> searchArguments;
    private Composite searchStringGroup;
    private ScrolledComposite scrollable;
    private Button rdoMatchAll;
    private Button rdoMatchAny;

    public SourceFileSearchPage() {
        super();
    }

    public void createControl(Composite aParent) {

        initializeDialogUnits(aParent);

        Composite tMainPanel = new Composite(aParent, SWT.NONE);
        tMainPanel.setLayout(new GridLayout());
        GridData tGridData = new GridData(GridData.FILL_HORIZONTAL);
        tMainPanel.setLayoutData(tGridData);

        createSearchStringEditorGroup(tMainPanel);
        createConnectionGroup(tMainPanel);
        createSourceMemberGroup(tMainPanel);
        createColumnsGroup(tMainPanel);
        createOptionsGroup(tMainPanel);

        addListeners();

        loadScreenValues();

        setControl(tMainPanel);

    }

    private void createSearchStringEditorGroup(Composite aMainPanel) {

        Composite tMatchGroup = new Composite(aMainPanel, SWT.NONE);
        FillLayout tMatchGroupLayout = new FillLayout(SWT.HORIZONTAL);
        tMatchGroupLayout.marginHeight = 5;
        tMatchGroup.setLayout(tMatchGroupLayout);

        rdoMatchAll = new Button(aMainPanel, SWT.RADIO);
        rdoMatchAll.setText(Messages.MatchAllConditions);

        rdoMatchAny = new Button(aMainPanel, SWT.RADIO);
        rdoMatchAny.setText(Messages.MatchAnyCondition);

        Composite scrollableContainer = new Composite(aMainPanel, SWT.NONE);
        scrollableContainer.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 135;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        scrollableContainer.setLayoutData(gd);

        scrollable = new ScrolledComposite(scrollableContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrollable.setLayout(new GridLayout(1, false));
        scrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setExpandHorizontal(true);
        scrollable.setExpandVertical(true);

        searchStringGroup = new Composite(scrollable, SWT.NONE);
        searchStringGroup.setLayout(new GridLayout(1, false));
        searchStringGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        searchStringGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrollable.setContent(searchStringGroup);

        searchArguments = new ArrayList<SearchArgumentEditor>();
    }

    private void addSearchArgumentEditorAndLayout() {
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        addSearchArgumentEditor(null);
        searchStringGroup.layout(true);
    }

    private void addSearchArgumentEditorAndLayout(Button aButton) {
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        SearchArgumentEditor tEditor = addSearchArgumentEditor(aButton);
        searchStringGroup.layout(true);
        scrollable.setOrigin(tEditor.getBounds().x, tEditor.getBounds().y - tEditor.getBounds().height - 5);
        tEditor.setFocus();
    }

    private SearchArgumentEditor addSearchArgumentEditor(Button aButton) {
        SearchArgumentEditor tEditor = new SearchArgumentEditor();
        tEditor.createContents(searchStringGroup);
        tEditor.addSearchStringListener(this);
        tEditor.addButtonListener(this);

        if (aButton == null) {
            searchArguments.add(tEditor);
        } else {
            searchArguments.add(findSearchArgumentEditor(aButton) + 1, tEditor);
        }

        rearrangeSearchArgumentEditors();

        return tEditor;
    }

    private void removeSearchArgumentEditor(Button aButton) {
        if (searchArguments.size() == 1) {
            return;
        }
        removeSearchArgumentEditor(findSearchArgumentEditor(aButton));
    }

    private void removeSearchArgumentEditor(int anEditor) {
        if (anEditor < 0) {
            return;
        }
        searchArguments.get(anEditor).dispose();
        searchArguments.remove(anEditor);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);

        if (anEditor > searchArguments.size() - 1) {
            searchArguments.get(searchArguments.size() - 1).setFocus();
        } else {
            searchArguments.get(anEditor).setFocus();
        }
    }

    private int findSearchArgumentEditor(Button aButton) {
        for (int i = 0; i < searchArguments.size(); i++) {
            if (searchArguments.get(i).hasButton(aButton)) {
                return i;
            }
        }
        return -1;
    }

    private void rearrangeSearchArgumentEditors() {
        for (SearchArgumentEditor tEditor : searchArguments) {
            tEditor.setParent(scrollable);
        }
        for (SearchArgumentEditor tEditor : searchArguments) {
            tEditor.setParent(searchStringGroup);
        }
    }

    private void createConnectionGroup(Composite aMainPanel) {
        connectionCombo = new ISeriesConnectionCombo(aMainPanel);
        connectionCombo.getPromptLabel().setText(Messages.Connection);
    }

    private void createSourceMemberGroup(Composite aMainPanel) {
        Group tTargetGroup = createGroup(aMainPanel, Messages.Target);
        sourceFilePrompt = new ISeriesMemberPrompt(tTargetGroup, SWT.NONE, true, true, IISeriesFilePromptTypes.FILETYPE_SRC);
        sourceFilePrompt.setSystemConnection(connectionCombo.getSystemConnection());
        sourceFilePrompt.getLibraryCombo().setToolTipText(Messages.Enter_or_select_a_library_name);
        sourceFilePrompt.getObjectCombo().setToolTipText(Messages.Enter_or_select_a_simple_or_generic_message_file_name);
        sourceFilePrompt.getLibraryPromptLabel().setText(Messages.Library);
        sourceFilePrompt.setObjectPromptLabel(Messages.Source_File);
        sourceFilePrompt.setMemberPromptLabel(Messages.Source_Member);
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
        allColumnsButton = new Button(tAllColumnsPanel, SWT.RADIO);
        allColumnsButton.setText(Messages.All_columns);
        allColumnsButton.setToolTipText(Messages.Search_all_columns);

        Composite tBetweenColumnsPanel = new Composite(tColumnsGroup, SWT.NONE);
        GridLayout tBetweenColumnsLayout = new GridLayout(4, false);
        tBetweenColumnsPanel.setLayout(tBetweenColumnsLayout);
        betweenColumnsButton = new Button(tBetweenColumnsPanel, SWT.RADIO);
        betweenColumnsButton.setText(Messages.Between);
        betweenColumnsButton.setToolTipText(Messages.Search_between_specified_columns);

        startColumnText = new Text(tBetweenColumnsPanel, SWT.BORDER);
        tGridData = new GridData();
        tGridData.widthHint = 30;
        startColumnText.setLayoutData(tGridData);
        startColumnText.setTextLimit(3);
        startColumnText.setToolTipText(Messages.Specify_start_column);

        Label tAndLabel = new Label(tBetweenColumnsPanel, SWT.LEFT);
        tAndLabel.setText(Messages.and);

        endColumnText = new Text(tBetweenColumnsPanel, SWT.BORDER);
        tGridData = new GridData();
        tGridData.widthHint = 30;
        endColumnText.setLayoutData(tGridData);
        endColumnText.setTextLimit(3);
        endColumnText.setToolTipText(Messages.Specify_end_column_max_132);
    }

    private void createOptionsGroup(Composite aMainPanel) {
        Group tOptionsGroup = createGroup(aMainPanel, Messages.Options);
        GridLayout tOptionsGroupLayout = new GridLayout(1, false);
        tOptionsGroupLayout.marginWidth = 5;
        tOptionsGroupLayout.marginHeight = 5;
        tOptionsGroup.setLayout(tOptionsGroupLayout);
        GridData tGridData = new GridData(GridData.FILL_VERTICAL);
        tGridData.horizontalAlignment = GridData.FILL;
        tGridData.grabExcessHorizontalSpace = true;
        tGridData.widthHint = 250;
        tOptionsGroup.setLayoutData(tGridData);

        showRecordsButton = new Button(tOptionsGroup, SWT.CHECK);
        showRecordsButton.setText(Messages.ShowRecords);
        showRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        tGridData = new GridData(SWT.HORIZONTAL);
        tGridData.grabExcessHorizontalSpace = false;
        showRecordsButton.setLayoutData(tGridData);
    }

    private Group createGroup(Composite aParent, String aText) {
        Group tGroup = new Group(aParent, SWT.SHADOW_ETCHED_IN);
        tGroup.setText(aText);
        GridLayout scopeLayout = new GridLayout();
        tGroup.setLayout(scopeLayout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        tGroup.setLayoutData(gd);
        return tGroup;
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
        rdoMatchAll.addListener(SWT.Selection, this);
        rdoMatchAny.addListener(SWT.Selection, this);
        allColumnsButton.addListener(SWT.Selection, this);
        betweenColumnsButton.addListener(SWT.Selection, this);
        startColumnText.addListener(SWT.Modify, this);
        startColumnText.addVerifyListener(new NumericOnlyVerifyListener());
        endColumnText.addListener(SWT.Modify, this);
        endColumnText.addVerifyListener(new NumericOnlyVerifyListener());
    }

    /**
     * Restores the screen values of the last search search.
     */
    private void loadScreenValues() {
        rdoMatchAll.setSelection(loadBooleanValue(MATCH_ALL, true));
        rdoMatchAny.setSelection(!rdoMatchAll.getSelection());

        int numConditions = loadIntValue(NUM_CONDITIONS, 1);
        for (int i = 0; i < numConditions; i++) {
            try {
                addSearchArgumentEditorAndLayout();
                searchArguments.get(i)
                    .setCompareCondition(IntHelper.tryParseInt(loadValue(COMPARE_CONDITION + "_" + i, ""), SearchArgument.CONTAINS));
                searchArguments.get(i).setSearchString(loadValue(SEARCH_STRING + "_" + i, "Enter search string here"));
                searchArguments.get(i).setCase(loadBooleanValue(CASE_SENSITIVE + "_" + i, false));
            } catch (Throwable e) {
                // ignore all errors
            }
        }
        searchArguments.get(0).setFocus();

        showRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, false));
        sourceFilePrompt.getLibraryCombo().setText(loadValue(LIBRARY, ""));
        sourceFilePrompt.getObjectCombo().setText(loadValue(SOURCE_FILE, ""));
        sourceFilePrompt.getMemberCombo().setText(loadValue(SOURCE_MEMBER, ""));

        loadColumnButtonsSelection();
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    private void storeScreenValues() {
        storeValue(MATCH_ALL, rdoMatchAll.getSelection());

        storeValue(NUM_CONDITIONS, searchArguments.size());
        for (int i = 0; i < searchArguments.size(); i++) {
            storeValue(COMPARE_CONDITION + "_" + i, searchArguments.get(i).getCompareCondition());
            storeValue(SEARCH_STRING + "_" + i, searchArguments.get(i).getSearchString());
            storeValue(CASE_SENSITIVE + "_" + i, searchArguments.get(i).isCaseSensitive());
        }

        storeValue(SHOW_RECORDS, isShowRecords());
        storeValue(LIBRARY, getSourceFileLibrary());
        storeValue(SOURCE_FILE, getSourceFile());
        storeValue(SOURCE_MEMBER, getSourceMember());

        saveColumnButtonsSelection();
    }

    /**
     * Restores the status of the "columns" buttons and text fields.
     */
    private void loadColumnButtonsSelection() {
        String tColumnButtonsSelection = loadValue(COLUMN_BUTTONS_SELECTION, "ALL");
        if ("ALL".equals(tColumnButtonsSelection)) {
            allColumnsButton.setSelection(true);
            processAllColumnsButtonSelected();
        } else {
            betweenColumnsButton.setSelection(true);
            processBetweenColumnsButtonSelected();
        }
        startColumnText.setText(loadValue(START_COLUMN, "1"));
        endColumnText.setText(loadValue(END_COLUMN, "100"));
    }

    /**
     * Saved the status of the "columns" buttons and text fields.
     */
    private void saveColumnButtonsSelection() {
        if (allColumnsButton.getSelection()) {
            storeValue(COLUMN_BUTTONS_SELECTION, "ALL");
        } else {
            storeValue(COLUMN_BUTTONS_SELECTION, "BETWEEN");
            storeValue(START_COLUMN, getNumericFieldContent(startColumnText));
            storeValue(END_COLUMN, getNumericFieldContent(endColumnText));
        }
    }

    /**
     * Returns the search string the source files are searched for.
     * 
     * @return search argument
     */
    private String getCombinedSearchString() {
        StringBuilder tBuffer = new StringBuilder();
        for (SearchArgumentEditor tArgument : searchArguments) {
            if (tArgument.getSearchString().trim().length() > 0) {
                if (tBuffer.length() > 0) {
                    tBuffer.append("/");
                }
                tBuffer.append(tArgument.getSearchString());
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
     * Returns the simple or generic name of the libraries that are searched for
     * the source files that contain the source members.
     * 
     * @return name of the library
     */
    private String getSourceFileLibrary() {
        return sourceFilePrompt.getLibraryCombo().getText();
    }

    /**
     * Returns the simple or generic name of the source file(s) that are
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
     * Returns the status of the "show records" check box.
     * 
     * @return status of the "show records" check box
     */
    private boolean isShowRecords() {
        return showRecordsButton.getSelection();
    }

    /**
     * Returns the status of the "is match all" radio button.
     * 
     * @return status of the "is match all" radio button
     */
    private boolean isMatchAll() {
        return rdoMatchAll.getSelection();
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
        storeScreenValues();

        try {

            StructuredSelection tSelection = (StructuredSelection)connectionCombo.getSelection();
            SystemConnection tHost = (SystemConnection)tSelection.getFirstElement();

            ISeriesConnection tConnection = ISeriesConnection.getConnection(tHost);
            if (!ISphereHelper.checkISphereLibrary(getShell(), tConnection.getAS400ToolboxObject(getShell()))) {
                return false;
            }
            HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();
            Object[] tObjects = tConnection.listMembers(getShell(), getSourceFileLibrary(), getSourceFile(), getSourceMember());
            for (Object tObject : tObjects) {
                if (tObject instanceof ISeriesMember) {
                    addElement(searchElements, ((ISeriesMember)tObject).getDataElement());
                }
            }

            if (searchElements.isEmpty()) {
                MessageDialog.openInformation(getShell(), "Information", Messages.No_objects_found_that_match_the_selection_criteria);
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

            SearchOptions searchOptions = new SearchOptions(isMatchAll(), isShowRecords());
            for (SearchArgumentEditor editor : searchArguments) {
                if (!StringHelper.isNullOrEmpty(editor.getSearchString())) {
                    searchOptions.addSearchArgument(new SearchArgument(editor.getSearchString(), startColumn, endColumn, editor.isCaseSensitive(), editor
                        .getCompareCondition()));
                }
            }

            new SearchExec().execute(tConnection.getAS400ToolboxObject(getShell()), tConnection.getJDBCConnection(null, false), searchOptions,
                new ArrayList<SearchElement>(searchElements.values()), postRun);

        } catch (Exception e) {
            ISpherePlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            String message;
            if (e.getLocalizedMessage() == null) {
                message = e.getClass().getName() + " - " + getClass().getName();
            } else {
                message = e.getLocalizedMessage();
            }
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        }

        return true;
    }

    /**
     * Adds an element to the list of elements that are searched for a given
     * search string.
     * 
     * @param aSearchElements - list of elements that are searched
     * @param aSourceMember - message file that is added to the list
     */
    private void addElement(HashMap<String, SearchElement> aSearchElements, DataElement aSourceMember) {
        String tKey = ISeriesDataElementHelpers.getLibrary(aSourceMember) + "-" + ISeriesDataElementHelpers.getFile(aSourceMember) + "-"
            + ISeriesDataElementHelpers.getName(aSourceMember);
        if (!aSearchElements.containsKey(tKey)) {
            SearchElement aSearchElement = new SearchElement();
            aSearchElement.setLibrary(ISeriesDataElementHelpers.getLibrary(aSourceMember));
            aSearchElement.setFile(ISeriesDataElementHelpers.getFile(aSourceMember));
            aSearchElement.setMember(ISeriesDataElementHelpers.getName(aSourceMember));
            aSearchElement.setDescription(ISeriesDataElementHelpers.getDescription(aSourceMember));
            aSearchElements.put(tKey, aSearchElement);
        }
    }

    /**
     * Handles "modify" and "selection" events to enable/disable widgets and
     * error checking.
     */
    public void handleEvent(Event anEvent) {
        Widget widget = anEvent.widget;
        int type = anEvent.type;

        boolean result = true;

        if ((widget == rdoMatchAll) && (type == SWT.Selection)) {
            processMatchButtonsSelected();
        } else if ((widget == rdoMatchAny) && (type == SWT.Selection)) {
            processMatchButtonsSelected();
        } else if ((widget == allColumnsButton) && (type == SWT.Selection)) {
            processAllColumnsButtonSelected();
        } else if ((widget == betweenColumnsButton) && (type == SWT.Selection)) {
            processBetweenColumnsButtonSelected();
        } else if ((widget == startColumnText) && (type == SWT.Modify)) {
            result = processStartColumnTextModified();
        } else if ((widget == endColumnText) && (type == SWT.Modify)) {
            result = processEndColumnTextModified();
        } else if (widget.getData() == SearchArgumentEditor.TEXT_SEARCH_STRING && (type == SWT.Modify)) {
            result = !isSearchStringEmpty();
        } else if (widget.getData() == SearchArgumentEditor.BUTTON_ADD && (type == SWT.Selection)) {
            addSearchArgumentEditorAndLayout((Button)widget);
        } else if (widget.getData() == SearchArgumentEditor.BUTTON_REMOVE && (type == SWT.Selection)) {
            removeSearchArgumentEditor((Button)widget);
        }

        if (!result) {
            container.setPerformActionEnabled(false);
        } else {
            container.setPerformActionEnabled(checkAll());
        }
    }

    /**
     * Executed when the "Match all" or "Match any" radio button has been
     * selected.
     */
    private void processMatchButtonsSelected() {
        // if (rdoMatchAll.getSelection()) {
        // rdoMatchAny.setSelection(false);
        // } else if (rdoMatchAny.getSelection()) {
        // rdoMatchAll.setSelection(false);
        // }
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
            startColumnText.setText("1");
        }

        if (StringHelper.isNullOrEmpty(endColumnText.getText())) {
            endColumnText.setText("132");
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

        for (SearchArgumentEditor tEditor : searchArguments) {
            if (StringHelper.isNullOrEmpty(tEditor.getSearchString())) {
                return false;
            }
        }

        if (connectionCombo.getISeriesConnection() == null) {
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
            if (getNumericFieldContent(endColumnText) <= 132) {
                return true;
            }
        }

        return false;
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
}
