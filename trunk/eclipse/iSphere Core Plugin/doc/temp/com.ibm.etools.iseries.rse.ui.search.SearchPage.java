package com.ibm.etools.iseries.rse.ui.search;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.IBMiRSEPlugin;
import com.ibm.etools.iseries.rse.ui.IBMiUIResources;
import com.ibm.etools.iseries.rse.ui.widgets.IBMiConnectionCombo;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSMemberPrompt;
import com.ibm.etools.iseries.rse.ui.widgets.QSYSWidgetFillerFromSelection;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.api.ISeriesMessage;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.internal.ui.view.search.SystemSearchUI;
import org.eclipse.rse.internal.ui.view.search.SystemSearchViewPart;
import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.RSEUIPlugin;
import org.eclipse.rse.ui.SystemWidgetHelpers;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.rse.ui.model.ISystemRegistryUI;
import org.eclipse.rse.ui.widgets.SystemHistoryCombo;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

public class SearchPage extends DialogPage implements ISearchPage, Listener, ModifyListener {
    public static final String Copyright = "(C) Copyright IBM Corp. 2003  All Rights Reserved.";
    private static final int MIN_COLUMN = 1;
    private static final int MAX_COLUMN = 80;
    private static final int RC_OK = 0;
    private static final int RC_CANCELED = 2;
    private static final int RC_ERROR_FOUND = 3;
    private static final int RC_EMPTY_LIST = 4;
    private static final String PAGE_NAME = "AS400SearchPage";
    private static final String CASE_SENSITIVE_KEY = "AS400SearchPageCaseSensitiveKey";
    private ISearchPageContainer container;
    private boolean isCaseSensitive;
    private static boolean caseSensitive = false;

    private SystemHistoryCombo stringCombo;

    private Button caseButton;
    private IBMiConnectionCombo connectionCombo;
    private QSYSMemberPrompt memberPrompt;
    private Button allColumnsButton;
    private static boolean allColumnsButtonSelected = true;
    private Button bothColumnsButton;
    private static boolean bothColumnsButtonSelected = false;
    private Text startColumnText;
    private Text endColumnText;
    private static String startColumnTextStr = String.valueOf(1);
    private static String endColumnTextStr = String.valueOf(80);
    private Button oneColumnButton;
    private static boolean oneColumnButtonSelected = false;
    private Text oneColumnText;
    private static String oneColumnTextStr = String.valueOf(1);
    private Button dataCheckBox;
    private Button sourceCheckBox;
    private static boolean sourceCheckBoxSelected = true;
    private static boolean dataCheckBoxSelected = false;

    private String libName;

    private String objName;
    private String mbrName;
    private Vector searchTargetLibs;
    private Vector searchTargetFiles;
    private Vector searchTargetMbrs;
    private Vector searchTargetConns;
    private boolean doingMbrSearch = false;
    private boolean doingFileSearch = false;

    public static final int INPUT_VALID = 0;

    public static final int INPUT_EMPTY = -1;

    public static final int INPUT_NOT_NUMERIC = -2;

    public static final int INPUT_NOT_POSITIVE = -3;
    private boolean readyToSearch = false;

    public SearchPage() {
    }

    public void createControl(Composite parent) {
        SystemWidgetHelpers.setHelp(parent, "com.ibm.etools.iseries.rse.ui.nfag0006");

        initializeDialogUnits(parent);

        Composite main = new Composite(parent, 0);
        GridLayout mainLayout = new GridLayout();
        main.setLayout(mainLayout);
        GridData gd = new GridData(768);
        main.setLayoutData(gd);

        Composite stringComposite = new Composite(main, 0);
        GridLayout stringLayout = new GridLayout(3, false);
        stringLayout.marginWidth = 0;
        stringLayout.marginHeight = 0;
        stringComposite.setLayout(stringLayout);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 250;
        stringComposite.setLayoutData(gd);

        Label stringLabel = new Label(stringComposite, 16384);
        stringLabel.setText(

        IBMiUIResources.RESID_SEARCH_SEARCHSTRINGFLD_LABEL);
        stringLabel.setToolTipText(IBMiUIResources.RESID_SEARCH_STRING_LABEL_TOOLTIP);
        gd = new GridData(32);
        stringLabel.setLayoutData(gd);

        this.stringCombo = (this.stringCombo = new SystemHistoryCombo(stringComposite, 0, "com.ibm.etools.iseries.rse.ui.findString", 10, false));
        this.stringCombo.setTextLimit(40);
        this.stringCombo.setToolTipText(IBMiUIResources.RESID_SEARCH_STRING_COMBO_TOOLTIP);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        this.stringCombo.setLayoutData(gd);

        this.caseButton = new Button(stringComposite, 32);
        this.caseButton.setText(IBMiUIResources.RESID_SEARCH_CASE_BUTTON_LABEL);
        this.caseButton.setToolTipText(IBMiUIResources.RESID_SEARCH_CASE_BUTTON_TOOLTIP);
        gd = new GridData(256);
        gd.grabExcessHorizontalSpace = false;
        this.caseButton.setLayoutData(gd);

        this.connectionCombo = new IBMiConnectionCombo(main);

        Group scopeGroup = new Group(main, 16);
        scopeGroup.setText(IBMiUIResources.RESID_SEARCH_TARGETGROUP_LABEL);
        scopeGroup.setToolTipText(IBMiUIResources.RESID_SEARCH_TARGETGROUP_TOOLTIP);
        GridLayout scopeLayout = new GridLayout();
        scopeGroup.setLayout(scopeLayout);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        scopeGroup.setLayoutData(gd);

        this.memberPrompt = new QSYSMemberPrompt(scopeGroup, 8);

        QSYSWidgetFillerFromSelection filler = new QSYSWidgetFillerFromSelection(this.connectionCombo, this.memberPrompt);
        filler.fillWidgets();

        Composite fileTypeComposite = new Composite(scopeGroup, 0);
        GridLayout fileTypeLayout = new GridLayout(2, false);
        fileTypeLayout.marginWidth = 0;
        fileTypeLayout.makeColumnsEqualWidth = true;
        fileTypeComposite.setLayout(fileTypeLayout);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        fileTypeComposite.setLayoutData(gd);

        this.sourceCheckBox = new Button(fileTypeComposite, 32);
        this.sourceCheckBox.setText(IBMiUIResources.RESID_SEARCH_DIALOG_SRCMRB_LABEL);
        this.sourceCheckBox.setToolTipText(IBMiUIResources.RESID_SEARCH_DIALOG_SRCMRB_TOOLTIP);

        this.dataCheckBox = new Button(fileTypeComposite, 32);

        this.dataCheckBox.setText(IBMiUIResources.RESID_SEARCH_DIALOG_DTAMRB_LABEL);
        this.dataCheckBox.setToolTipText(IBMiUIResources.RESID_SEARCH_DIALOG_DTAMRB_TOOLTIP);

        Group columnGroup = new Group(main, 16);
        columnGroup.setText(IBMiUIResources.RESID_SEARCH_COLUMNSGROUP_LABEL);
        columnGroup.setToolTipText(IBMiUIResources.RESID_SEARCH_COLUMNSGROUP_TOOLTIP);
        GridLayout columnLayout = new GridLayout(1, false);
        columnGroup.setLayout(columnLayout);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        columnGroup.setLayoutData(gd);

        this.allColumnsButton = new Button(columnGroup, 16);
        this.allColumnsButton.setText(IBMiUIResources.RESID_SEARCH_ALLCOLUMNSLABEL_LABEL);
        this.allColumnsButton.setToolTipText(IBMiUIResources.RESID_SEARCH_ALLCOLUMNSLABEL_TOOLTIP);

        Composite bothColumnsComposite = new Composite(columnGroup, 0);
        GridLayout bothColumnsLayout = new GridLayout(4, false);
        bothColumnsLayout.marginWidth = 0;
        bothColumnsComposite.setLayout(bothColumnsLayout);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        bothColumnsComposite.setLayoutData(gd);

        this.bothColumnsButton = new Button(bothColumnsComposite, 16);
        this.bothColumnsButton.setText(IBMiUIResources.RESID_SEARCH_BETWEENLABEL_LABEL);
        this.bothColumnsButton.setToolTipText(IBMiUIResources.RESID_SEARCH_BOTHCOLUMNSLABEL_TOOLTIP);

        this.startColumnText = new Text(bothColumnsComposite, 2052);
        this.startColumnText.setTextLimit(5);

        this.startColumnText.setToolTipText(IBMiUIResources.RESID_SEARCH_FIRSTCOLUMN_TOOLTIP);
        gd = new GridData();
        gd.widthHint = 30;
        this.startColumnText.setLayoutData(gd);

        Label andLabel = new Label(bothColumnsComposite, 16384);
        andLabel.setText(IBMiUIResources.RESID_SEARCH_ANDLABEL_LABEL);
        andLabel.setToolTipText(IBMiUIResources.RESID_SEARCH_BOTHCOLUMNSLABEL_TOOLTIP);

        this.endColumnText = new Text(bothColumnsComposite, 2052);
        this.endColumnText.setTextLimit(5);

        this.endColumnText.setToolTipText(IBMiUIResources.RESID_SEARCH_SECONDCOLUMN_TOOLTIP);
        gd = new GridData();
        gd.widthHint = 30;
        this.endColumnText.setLayoutData(gd);

        Composite startColumnComposite = new Composite(columnGroup, 0);
        GridLayout startColumnLayout = new GridLayout(4, false);
        startColumnLayout.marginWidth = 0;
        startColumnComposite.setLayout(startColumnLayout);
        gd = new GridData(768);
        gd.grabExcessHorizontalSpace = true;
        startColumnComposite.setLayoutData(gd);

        this.oneColumnButton = new Button(startColumnComposite, 16);
        this.oneColumnButton.setText(IBMiUIResources.RESID_SEARCH_BETWEENLABEL_LABEL);
        this.oneColumnButton.setToolTipText(IBMiUIResources.RESID_SEARCH_STARTCOLUMNLABEL_TOOLTIP);

        this.oneColumnText = new Text(startColumnComposite, 2052);
        this.oneColumnText.setTextLimit(5);

        this.oneColumnText.setToolTipText(IBMiUIResources.RESID_SEARCH_FIRSTCOLUMN_TOOLTIP);
        gd = new GridData();
        gd.widthHint = 30;
        this.oneColumnText.setLayoutData(gd);

        Label eolLabel = new Label(startColumnComposite, 16384);
        eolLabel.setText(IBMiUIResources.RESID_SEARCH_EOLLABEL_LABEL);
        gd = new GridData(768);
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        eolLabel.setLayoutData(gd);
        SystemWidgetHelpers.setWizardPageMnemonics(parent);

        initialize();

        this.stringCombo.setFocus();

        addListeners();

        this.container.setPerformActionEnabled(checkAll());

        setControl(main);
    }

    private void initialize() {
        if (this.stringCombo.getItems().length == 0) {
            this.stringCombo.getCombo().setText(IBMiUIResources.RESID_SEARCH_INITSEARCHSTR);
        } else {
            this.readyToSearch = true;
            this.stringCombo.select(0);
        }

        this.caseButton.setSelection(caseSensitive);
        this.dataCheckBox.setSelection(dataCheckBoxSelected);
        this.sourceCheckBox.setSelection(sourceCheckBoxSelected);
        this.allColumnsButton.setSelection(allColumnsButtonSelected);
        this.bothColumnsButton.setSelection(bothColumnsButtonSelected);
        this.oneColumnButton.setSelection(oneColumnButtonSelected);
        this.startColumnText.setText(startColumnTextStr);
        this.endColumnText.setText(endColumnTextStr);
        this.oneColumnText.setText(oneColumnTextStr);

        if (allColumnsButtonSelected) {
            ();
        } else if (bothColumnsButtonSelected) {
            processBothColumnsButtonSelected();
        } else {
            processStartColumnButtonSelected();
        }
        String objName = this.memberPrompt.getObjectName();

        if (objName.indexOf("*") == -1) {
            this.dataCheckBox.setEnabled(false);
            this.sourceCheckBox.setEnabled(false);
        } else {
            this.dataCheckBox.setEnabled(true);
            this.sourceCheckBox.setEnabled(true);
        }
        String searchString = this.stringCombo.getText();
        if ((searchString.toUpperCase().startsWith("X'")) && (searchString.endsWith("'"))) {
            this.caseButton.setEnabled(false);
        } else {
            this.caseButton.setEnabled(true);
        }
    }

    private void addListeners() {
        this.stringCombo.getCombo().addListener(24, this);
        this.memberPrompt.addLibraryModifyListener(this);
        this.memberPrompt.addFileModifyListener(this);
        this.memberPrompt.addMemberModifyListener(this);
        this.allColumnsButton.addListener(13, this);
        this.bothColumnsButton.addListener(13, this);
        this.startColumnText.addListener(24, this);
        this.endColumnText.addListener(24, this);
        this.oneColumnButton.addListener(13, this);
        this.oneColumnText.addListener(24, this);
        this.dataCheckBox.addListener(13, this);
        this.sourceCheckBox.addListener(13, this);
    }

    public boolean performAction() {
        this.container.setPerformActionEnabled(false);

        Shell shell =

        IBMiRSEPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        String searchString = this.stringCombo.getText().trim();

        if (searchString.length() == 0) {
            return false;
        }

        this.stringCombo.updateHistory(true);

        caseSensitive = this.caseButton.getSelection();
        dataCheckBoxSelected = this.dataCheckBox.getSelection();
        sourceCheckBoxSelected = this.sourceCheckBox.getSelection();
        allColumnsButtonSelected = this.allColumnsButton.getSelection();
        bothColumnsButtonSelected = this.bothColumnsButton.getSelection();
        oneColumnButtonSelected = this.oneColumnButton.getSelection();
        startColumnTextStr = this.startColumnText.getText();
        endColumnTextStr = this.endColumnText.getText();
        oneColumnTextStr = this.oneColumnText.getText();

        IBMiConnection connection = this.connectionCombo.getISeriesConnection();
        this.libName = this.memberPrompt.getLibraryName();
        this.objName = this.memberPrompt.getObjectName();
        this.mbrName = this.memberPrompt.getMemberName();
        this.memberPrompt.setLibraryName(this.libName);
        this.memberPrompt.setObjectName(this.objName);
        this.memberPrompt.setMemberName(this.mbrName);

        int columnSelection = 0;
        int startColumn = 0;
        int endColumn = 0;

        if (allColumnsButtonSelected) {
            columnSelection = 1;
            startColumn = -1;
            endColumn = -1;

        } else if (bothColumnsButtonSelected) {
            columnSelection = 2;
            startColumn = Integer.valueOf(this.startColumnText.getText()).intValue();
            endColumn = Integer.valueOf(this.endColumnText.getText()).intValue();

        } else if (oneColumnButtonSelected) {
            columnSelection = 3;
            startColumn = Integer.valueOf(this.oneColumnText.getText()).intValue();
            endColumn = -1;
        }

        SearchResultInputElement resultSet = new SearchResultInputElement(searchString);

        this.searchTargetLibs = new Vector();
        this.searchTargetFiles = new Vector();
        this.searchTargetMbrs = new Vector();
        this.searchTargetConns = new Vector();

        if (this.mbrName.equals("*")) {
            this.doingFileSearch = true;
        } else {
            this.doingMbrSearch = true;
        }
        int returnCode = 0;
        if ((this.libName.indexOf("*") == -1) && (this.objName.indexOf("*") == -1)) {
            this.searchTargetLibs.addElement(this.libName);
            this.searchTargetFiles.addElement(this.objName);
            this.searchTargetMbrs.addElement(this.mbrName);
            this.searchTargetConns.addElement(connection);
        } else {
            returnCode = buildSearchTargets(connection, shell);
        }
        if ((returnCode == 2) || (returnCode == 3)) {
            return false;
        }

        if (returnCode == 4) {

            SystemSearchViewPart searchView = SystemSearchUI.getInstance().activateSearchResultView();
            searchView.addSearchResult(resultSet);
            return true;
        }

        SearchQSYSOperation runnable = new SearchQSYSOperation(searchString,

        caseSensitive, this.searchTargetLibs.toArray(), this.searchTargetFiles.toArray(), this.searchTargetMbrs.toArray(),
            this.searchTargetConns.toArray(), columnSelection, startColumn, endColumn, resultSet, this.doingMbrSearch, this.doingFileSearch, false,
            false);

        try {
            for (int i = 0; i < this.searchTargetConns.size(); i++) {
                if (!((IBMiConnection)this.searchTargetConns.get(i)).isConnected()) {
                    ((IBMiConnection)this.searchTargetConns.get(i)).connect();
                }
            }

            IRunnableContext context = this.container.getRunnableContext();
            context.run(true, true, runnable);
        } catch (InterruptedException e) {
            SystemMessageDialog.displayExceptionMessage(shell, e);
            return false;
        } catch (InvocationTargetException e) {
            SystemMessageDialog.displayExceptionMessage(shell, e);
            return false;
        } catch (SystemMessageException e) {
            SystemMessageDialog.displayErrorMessage(shell, e.getSystemMessage());
            return false;
        }

        Object runResult = runnable.getRunResult();
        if (runResult != null) {
            if ((runResult instanceof SystemMessage)) {

                if (!((SystemMessage)runResult).getFullMessageID().equals("EVFC9104")) {
                    SystemMessageDialog.displayErrorMessage(shell, (SystemMessage)runResult);
                }

            } else if ((runResult instanceof ISeriesMessage)) {
                ISeriesMessage msg = (ISeriesMessage)runResult;

                SystemMessage sysMsg = new SystemMessage("RSE", "O", "1002", 'E', msg.getMessageText(), msg.getMessageHelp());

                SystemMessageDialog.displayErrorMessage(shell, sysMsg);

            } else if ((runResult instanceof String)) {
                String status = (String)runResult;
                if (status.equals("canceled")) {
                    SystemSearchViewPart searchView =

                    SystemSearchUI.getInstance().activateSearchResultView();
                    searchView.addSearchResult(resultSet);
                }
            }

            return false;
        }

        if (runnable.getShowResult()) {
            SystemSearchViewPart searchView = SystemSearchUI.getInstance().activateSearchResultView();
            searchView.addSearchResult(resultSet);
        }
        return true;
    }

    public void setContainer(ISearchPageContainer container) {
        this.container = container;
    }

    private void readConfiguration() {
        IDialogSettings pageSettings = getDialogSettings();
        this.isCaseSensitive = pageSettings.getBoolean("AS400SearchPageCaseSensitiveKey");
    }

    private IDialogSettings getDialogSettings() {
        IDialogSettings settings = IBMiRSEPlugin.getDefault().getDialogSettings();
        IDialogSettings pageSettings = settings.getSection("AS400SearchPage");

        if (pageSettings == null) {
            pageSettings = settings.addNewSection("AS400SearchPage");
        }

        return pageSettings;
    }

    private void writeConfiguration() {
        IDialogSettings pageSettings = getDialogSettings();
        pageSettings.put("AS400SearchPageCaseSensitiveKey", this.isCaseSensitive);
    }

    public void handleEvent(Event event) {
        Widget widget = event.widget;
        int type = event.type;

        boolean result = true;

        if ((widget == this.stringCombo.getCombo()) && (type == 24)) {
            this.readyToSearch = true;
            result = processStringComboModified();

        } else if ((widget == this.allColumnsButton) && (type == 13)) {
            ();

        } else if ((widget == this.bothColumnsButton) && (type == 13)) {
            processBothColumnsButtonSelected();

        } else if ((widget == this.startColumnText) && (type == 24)) {
            result = processStartColumnTextModified();

        } else if ((widget == this.endColumnText) && (type == 24)) {
            result = processEndColumnTextModified();

        } else if ((widget == this.oneColumnButton) && (type == 13)) {
            processStartColumnButtonSelected();

        } else if ((widget == this.oneColumnText) && (type == 24)) {
            result = processOneColumnTextModified();
        }

        if (!result) {
            this.container.setPerformActionEnabled(false);
        } else {
            this.container.setPerformActionEnabled(checkAll());
        }
    }

    private boolean isStringComboEmpty() {
        return this.stringCombo.getText().trim().length() == 0;
    }

    private int queryTextFieldContents(Text textField) {
        String text = textField.getText();

        if (text.equals("")) {
            return -1;
        }

        int number = 0;

        try {
            number = Integer.valueOf(text).intValue();

            if (number <= 0) {
                return -3;
            }

            return 0;
        } catch (NumberFormatException localNumberFormatException) {
        }

        return -2;
    }

    private boolean processStringComboModified() {
        String searchString = this.stringCombo.getText();
        if ((searchString.toUpperCase().startsWith("X'")) && (searchString.endsWith("'"))) {
            this.caseButton.setEnabled(false);
        } else
            this.caseButton.setEnabled(true);
        return !isStringComboEmpty();
    }

    private void () {
        this.bothColumnsButton.setSelection(false);
        this.oneColumnButton.setSelection(false);

        this.startColumnText.setEnabled(false);
        this.endColumnText.setEnabled(false);
        this.oneColumnText.setEnabled(false);
    }

    private void processBothColumnsButtonSelected() {
        this.allColumnsButton.setSelection(false);
        this.oneColumnButton.setSelection(false);

        this.startColumnText.setEnabled(true);
        this.endColumnText.setEnabled(true);

        this.oneColumnText.setEnabled(false);
    }

    private boolean processStartColumnTextModified() {
        return queryTextFieldContents(this.startColumnText) == 0;
    }

    private boolean processEndColumnTextModified() {
        return queryTextFieldContents(this.endColumnText) == 0;
    }

    private void processStartColumnButtonSelected() {
        this.allColumnsButton.setSelection(false);
        this.bothColumnsButton.setSelection(false);

        this.oneColumnText.setEnabled(true);

        this.startColumnText.setEnabled(false);
        this.endColumnText.setEnabled(false);
    }

    private boolean processOneColumnTextModified() {
        return queryTextFieldContents(this.oneColumnText) == 0;
    }

    private boolean checkAll() {
        if (!this.readyToSearch) {
            return false;
        }
        if (isStringComboEmpty()) {
            return false;
        }
        if (this.connectionCombo.getISeriesConnection() == null) {
            return false;
        }
        if ((this.memberPrompt.validateMbrInput() != null) || (this.memberPrompt.validateObjInput() != null)
            || (this.memberPrompt.validateLibInput() != null)) {
            return false;
        }
        this.libName = this.memberPrompt.getLibraryName();
        this.objName = this.memberPrompt.getObjectName();

        if (this.objName.indexOf('*') != -1) {
            if ((!this.dataCheckBox.getSelection()) && (!this.sourceCheckBox.getSelection())) {
                return false;
            }
        }
        if (this.allColumnsButton.getSelection()) {
            return true;
        }

        if (this.bothColumnsButton.getSelection()) {

            return (queryTextFieldContents(this.startColumnText) == 0) && (queryTextFieldContents(this.endColumnText) == 0);
        }

        if (this.oneColumnButton.getSelection()) {
            return queryTextFieldContents(this.oneColumnText) == 0;
        }

        return false;
    }

    public void modifyText(ModifyEvent e) {
        boolean result = true;
        String objName = this.memberPrompt.getObjectName();
        if (objName.indexOf("*") == -1) {
            this.dataCheckBox.setEnabled(false);
            this.sourceCheckBox.setEnabled(false);
        } else {
            this.dataCheckBox.setEnabled(true);
            this.sourceCheckBox.setEnabled(true);
        }
        if (e.widget == this.memberPrompt.getMemberCombo()) {
            result = this.memberPrompt.validateMbrInput() == null;

        } else if (e.widget == this.memberPrompt.getObjectCombo()) {
            result = this.memberPrompt.validateObjInput() == null;

        } else if (e.widget == this.memberPrompt.getLibraryCombo()) {
            result = this.memberPrompt.validateLibInput() == null;
        }

        if (!result) {
            this.container.setPerformActionEnabled(false);
        } else {
            this.container.setPerformActionEnabled(checkAll());
        }
    }

    private int buildSearchTargets(IBMiConnection connection, Shell shell) {
        String filterString;
        String filterString;
        if (this.doingFileSearch) {
            ISeriesObjectFilterString objFilterStr = new ISeriesObjectFilterString();
            objFilterStr.setLibrary(this.libName);
            objFilterStr.setObject(this.objName);
            objFilterStr.setObjectType("*FILE");
            if (this.objName.indexOf("*") != -1) {
                String attributes = "";
                if ((dataCheckBoxSelected) && (sourceCheckBoxSelected)) {
                    attributes = "*FILE:PF-SRC *FILE:PF-DTA";
                } else if (sourceCheckBoxSelected) {
                    attributes = "*FILE:PF-SRC";
                } else if (dataCheckBoxSelected) attributes = "*FILE:PF-DTA";
                objFilterStr.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
            }
            filterString = objFilterStr.toString();

        } else {
            ISeriesMemberFilterString mbrFilterStr = new ISeriesMemberFilterString();
            mbrFilterStr.setLibrary(this.libName);
            mbrFilterStr.setFile(this.objName);
            mbrFilterStr.setMember(this.mbrName);
            if (this.objName.indexOf("*") != -1) {
                String attributes = "";
                if ((this.dataCheckBox.getSelection()) && (this.sourceCheckBox.getSelection())) {
                    attributes = "*FILE:PF*";
                } else if (this.sourceCheckBox.getSelection()) {
                    attributes = "*FILE:PF*-SRC";
                } else if (this.dataCheckBox.getSelection()) attributes = "*FILE:PF*-DTA";
                mbrFilterStr.setObjectType(attributes);
            }
            filterString = mbrFilterStr.toString();
        }

        QSYSObjectSubSystem subsystem = connection.getQSYSObjectSubSystem();
        IRunnableContext context = this.container.getRunnableContext();

        RSEUIPlugin.getTheSystemRegistryUI().setRunnableContext(getShell(), context);

        Object[] children = (Object[])null;
        try {
            children = subsystem.resolveFilterString(filterString, null);

            RSEUIPlugin.getTheSystemRegistryUI().clearRunnableContext();
        } catch (InterruptedException localInterruptedException) {
            return 2;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(shell, e);
            IBMiRSEPlugin.logError("Exception when resolving file to be saerched", e);
            return 3;
        }
        if ((children == null) || (children.length == 0)) {
            return 4;
        }

        int total = children.length;

        if (total > 0) {
            Object firstObj = children[0];
            if ((firstObj instanceof SystemMessageObject)) {
                SystemMessageDialog.displayErrorMessage(shell, ((SystemMessageObject)firstObj).getMessage());
                return 3;
            }
        }

        for (int idx = 0; idx < children.length; idx++) {
            IQSYSResource element = (IQSYSResource)children[idx];

            this.libName = element.getLibrary();
            if (this.doingMbrSearch) {
                this.objName = ((IQSYSMember)element).getFile();
                this.mbrName = element.getName();
            } else {
                this.objName = element.getName();
                this.mbrName = "*";
            }
            this.searchTargetLibs.addElement(this.libName);
            this.searchTargetFiles.addElement(this.objName);
            this.searchTargetMbrs.addElement(this.mbrName);
            this.searchTargetConns.addElement(connection);
        }
        return 0;
    }

    public void setVisible(boolean visible) {
        Shell shell = getShell();

        Point currentSize = shell.getSize();

        Point requiredSize = shell.computeSize(-1, -1, true);

        if (requiredSize.x < 500) {
            requiredSize.x = 500;
        }
        if (mustResize(currentSize, requiredSize)) {
            shell.setSize(requiredSize);
        }

        this.container.setPerformActionEnabled(checkAll());

        super.setVisible(visible);
    }

    private boolean mustResize(Point currentSize, Point requiredSize) {
        return (currentSize.x != requiredSize.x) || (currentSize.y != requiredSize.y);
    }
}

/*
 * Location:
 * C:\Programme_x86\IBM\SDPShared\plugins\com.ibm.etools.iseries.rse.ui_8
 * .0.5.v20111208_1655\runtime\rseui.jar Qualified Name:
 * com.ibm.etools.iseries.rse.ui.search.SearchPage Java Class Version: 6 (50.0)
 * JD-Core Version: 0.7.0.1
 */