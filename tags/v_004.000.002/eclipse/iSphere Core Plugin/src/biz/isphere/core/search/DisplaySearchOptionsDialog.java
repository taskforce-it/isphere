/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class DisplaySearchOptionsDialog extends Dialog {

    private static final int COLUMN_OPERATOR = 0;
    private static final int COLUMN_ARGUMENT = 1;
    private static final int COLUMN_FROM = 2;
    private static final int COLUMN_TO = 3;
    private static final int COLUMN_CASE = 4;
    private static final int COLUMN_REGEX = 5;

    private static final int COLUMN_KEY = 0;
    private static final int COLUMN_VALUE = 1;

    private Text textConnection;
    private Text textMatch;
    private Button cbShowAllMatches;

    private ISearchResultTab searchResultTab;

    public DisplaySearchOptionsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        parent.getShell().setText(Messages.Search_Options);

        Composite dialogArea = (Composite)super.createDialogArea(parent);

        dialogArea.setLayout(new GridLayout(2, false));

        createHeader(dialogArea);

        TableViewer tableViewerSearchArguments = null;
        TableViewer tableViewerGenericSearchOptions = null;
        if (searchResultTab.hasSearchOptions()) {
            SearchOptions searchOptions = searchResultTab.getSearchOptions();
            tableViewerSearchArguments = createTableViewerSearchArguments(dialogArea);
            if (searchOptions.hasGenericOptions()) {
                tableViewerGenericSearchOptions = createTableViewerGenericSearchOptions(dialogArea);
            }
        }

        // Set input data
        textConnection.setText(searchResultTab.getConnectionName());
        if (searchResultTab.hasSearchOptions()) {
            textMatch.setText(searchResultTab.getSearchOptions().getMatchOption().getLabel()); // TODO:
                                                                                               // label
            cbShowAllMatches.setSelection(searchResultTab.getSearchOptions().isShowAllItems());
        }

        if (searchResultTab.hasSearchOptions()) {
            if (tableViewerSearchArguments != null) {
                SearchOptions searchOptions = searchResultTab.getSearchOptions();
                tableViewerSearchArguments.setInput(searchOptions.getSearchArguments());
                if (tableViewerGenericSearchOptions != null) {
                    tableViewerGenericSearchOptions.setInput(searchOptions.getGenericOptions());
                }

            }
        }

        return dialogArea;
    }

    private void createHeader(Composite parent) {

        textConnection = createTextWithLabel(parent, Messages.Connection_colon);
        textMatch = createTextWithLabel(parent, Messages.Conditions_to_match_colon);
        cbShowAllMatches = createCheckbox(parent, Messages.Show_all_matches_colon);
    }

    private TableViewer createTableViewerSearchArguments(Composite parent) {

        createLabel(parent, Messages.Search_arguments_colon);

        TableViewer tableViewerSearchArguments = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
        tableViewerSearchArguments.setLabelProvider(new LabelProviderSearchArguments());
        tableViewerSearchArguments.setContentProvider(new ContentProviderSearchArguments());

        Table tableViewerSearchArgumentsTable = tableViewerSearchArguments.getTable();
        tableViewerSearchArgumentsTable.setLinesVisible(true);
        tableViewerSearchArgumentsTable.setHeaderVisible(true);
        GridData tableViewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tableViewerLayoutData.heightHint = 100;
        tableViewerSearchArgumentsTable.setLayoutData(tableViewerLayoutData);

        TableColumn tableColumnStatement = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnStatement.setWidth(100);
        tableColumnStatement.setText(Messages.Condition);

        TableColumn tableColumnArgument = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnArgument.setWidth(100);
        tableColumnArgument.setText(Messages.Argument);

        TableColumn tableColumnFrom = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnFrom.setWidth(50);
        tableColumnFrom.setText(Messages.From_column_colon);

        TableColumn tableColumnTo = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnTo.setWidth(50);
        tableColumnTo.setText(Messages.To_column_colon);

        TableColumn tableColumnCase = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnCase.setWidth(100);
        tableColumnCase.setText(Messages.Case_sensitive);

        TableColumn tableColumnRegEx = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnRegEx.setWidth(100);
        tableColumnRegEx.setText(Messages.Regular_expression);

        return tableViewerSearchArguments;
    }

    private TableViewer createTableViewerGenericSearchOptions(Composite parent) {

        createLabel(parent, Messages.Additional_Options_colon);

        TableViewer tableViewerGenericSearchOptions = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
        tableViewerGenericSearchOptions.setLabelProvider(new LabelProviderGenericSearchOptions());
        tableViewerGenericSearchOptions.setContentProvider(new ContentProviderGenericSearchOptions());

        Table tableViewerSearchArgumentsTable = tableViewerGenericSearchOptions.getTable();
        tableViewerSearchArgumentsTable.setLinesVisible(true);
        tableViewerSearchArgumentsTable.setHeaderVisible(true);
        GridData tableViewerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tableViewerLayoutData.heightHint = 100;
        tableViewerSearchArgumentsTable.setLayoutData(tableViewerLayoutData);

        TableColumn tableColumnStatement = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnStatement.setWidth(200);
        tableColumnStatement.setText(Messages.Condition);

        TableColumn tableColumnArgument = new TableColumn(tableViewerSearchArgumentsTable, SWT.NONE);
        tableColumnArgument.setWidth(100);
        tableColumnArgument.setText(Messages.Argument);

        return tableViewerGenericSearchOptions;
    }

    private Text createTextWithLabel(Composite parent, String label) {

        new Label(parent, SWT.NONE).setText(label);
        Text text = WidgetFactory.createReadOnlyText(parent);
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        return text;
    }

    private void createLabel(Composite parent, String label) {

        Label labelHeadline = new Label(parent, SWT.NONE);
        labelHeadline.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        labelHeadline.setText(label);
    }

    private Button createCheckbox(Composite parent, String label) {

        new Label(parent, SWT.NONE).setText(label);
        Button checkbox = WidgetFactory.createReadOnlyCheckbox(parent);
        checkbox.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        return checkbox;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, Dialog.CANCEL, IDialogConstants.CLOSE_LABEL, false);
    }

    public void setInput(ISearchResultTab input) {
        searchResultTab = input;
    }

    private class LabelProviderSearchArguments extends LabelProvider implements ITableLabelProvider {

        private static final String UNKNOWN = "*UNKNOWN"; //$NON-NLS-1$

        public String getColumnText(Object element, int columnIndex) {

            SearchArgument searchArgument = (SearchArgument)element;

            switch (columnIndex) {
            case COLUMN_OPERATOR:
                return searchArgument.getOperatorAsText();
            case COLUMN_ARGUMENT:
                return searchArgument.getString();
            case COLUMN_FROM:
                return Integer.toString(searchArgument.getFromColumn());
            case COLUMN_TO:
                return Integer.toString(searchArgument.getToColumn());
            case COLUMN_CASE:
                return searchArgument.getCaseSensitive();
            case COLUMN_REGEX:
                return searchArgument.getRegularExpression();
            }

            return UNKNOWN;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderSearchArguments implements IStructuredContentProvider {

        private SearchArgument[] input;

        public Object[] getElements(Object inputElement) {
            return input;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            if (newInput == null) {
                input = null;
                return;
            }

            List<?> listInput = (List<?>)newInput;
            input = listInput.toArray(new SearchArgument[listInput.size()]);
        }

    }

    private class LabelProviderGenericSearchOptions extends LabelProvider implements ITableLabelProvider {

        private static final String UNKNOWN = "*UNKNOWN"; //$NON-NLS-1$

        public String getColumnText(Object element, int columnIndex) {

            GenericSearchOption genericSearchOption = (GenericSearchOption)element;

            switch (columnIndex) {
            case COLUMN_KEY:
                return genericSearchOption.getKeyAsText();
            case COLUMN_VALUE:
                return genericSearchOption.getValueAsText();
            }

            return UNKNOWN;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderGenericSearchOptions implements IStructuredContentProvider {

        private GenericSearchOption[] input;

        public Object[] getElements(Object inputElement) {
            return input;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            if (newInput == null) {
                input = null;
                return;
            }

            input = (GenericSearchOption[])newInput;
        }

    }
}
