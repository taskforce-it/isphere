/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;

public class JobLogExplorerFilterPanel {

    private Composite filterArea;

    private List<SelectionListener> filterChangedListeners;

    private Combo comboIdFilter;
    private Combo comboTypeFilter;
    private Combo comboSeverityFilter;
    private Combo comboFromLibraryFilter;
    private Combo comboFromProgramFilter;
    private Combo comboFromStmtFilter;
    private Combo comboToLibraryFilter;
    private Combo comboToProgramFilter;
    private Combo comboToStmtFilter;
    private Text textSearch;

    private Button buttonUp;
    private Button buttonDown;

    private Button buttonApplyFilters;
    private Button buttonClearFilters;
    private Button buttonSelectAll;
    private Button buttonDeselectAll;

    public JobLogExplorerFilterPanel() {

        this.filterChangedListeners = new ArrayList<SelectionListener>();
    }

    private GridLayout createGridLayout(int numColumns, boolean makeColumnsEqual) {

        GridLayout gridLayout = new GridLayout(numColumns, makeColumnsEqual);
        gridLayout.marginTop = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 1;
        gridLayout.marginWidth = 5;

        return gridLayout;
    }

    public void createViewer(Composite parent) {

        filterArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(2, false);
        gridLayout.marginTop = 5;
        filterArea.setLayout(gridLayout);
        filterArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Create controls
        createFilterControls(filterArea);

        createTextSearchControls(filterArea);

        createButtons(filterArea);
    }

    public void setEnabled(boolean enabled) {
        filterArea.setEnabled(enabled);
    }

    private void createFilterControls(Composite parent) {

        Composite combosArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(6, true);
        combosArea.setLayout(gridLayout);
        combosArea.setLayoutData(new GridData(GridData.BEGINNING));

        int horizontalSpan = 1;

        comboIdFilter = createCombo(combosArea, Messages.Label_ID, MessageModifyEvent.ID);
        comboTypeFilter = createCombo(combosArea, Messages.Label_Type, MessageModifyEvent.TYPE);
        comboSeverityFilter = createCombo(combosArea, Messages.Label_Severity, MessageModifyEvent.SEVERITY);
        comboSeverityFilter.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1));

        comboFromLibraryFilter = createCombo(combosArea, Messages.Label_From_Library, MessageModifyEvent.FROM_LIBRARY);
        comboFromProgramFilter = createCombo(combosArea, Messages.Label_From_Program, MessageModifyEvent.FROM_PROGRAM);
        comboFromStmtFilter = createCombo(combosArea, Messages.Label_From_Stmt, MessageModifyEvent.FROM_STMT);
        comboFromStmtFilter.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1));

        comboToLibraryFilter = createCombo(combosArea, Messages.Label_To_Library, MessageModifyEvent.TO_LIBRARY);
        comboToProgramFilter = createCombo(combosArea, Messages.Label_To_Program, MessageModifyEvent.TO_PROGRAM);
        comboToStmtFilter = createCombo(combosArea, Messages.Label_To_Stmt, MessageModifyEvent.TO_STMT);
        comboToStmtFilter.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1));
    }

    private Combo createCombo(Composite filterArea, String label, int messageEventType) {

        Label labelText = new Label(filterArea, SWT.NONE);
        labelText.setText(label);

        Combo combo = WidgetFactory.createCombo(filterArea);

        return combo;
    }

    private void createTextSearchControls(Composite parent) {

        Composite textSearchArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(4, false);
        textSearchArea.setLayout(gridLayout);
        textSearchArea.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));

        new Label(textSearchArea, SWT.NONE).setText(Messages.Label_Text);
        textSearch = WidgetFactory.createText(textSearchArea);
        textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textSearch.setToolTipText(Messages.Label_Text_tooltip);

        buttonUp = WidgetFactory.createPushButton(textSearchArea);
        buttonUp.setImage(ISphereJobLogExplorerPlugin.getDefault().getImage(ISphereJobLogExplorerPlugin.SEARCH_UP));
        buttonUp.setToolTipText(Messages.Search_up);
        buttonUp.addSelectionListener(new SearchUpSelectionListener());

        buttonDown = WidgetFactory.createPushButton(textSearchArea);
        buttonDown.setImage(ISphereJobLogExplorerPlugin.getDefault().getImage(ISphereJobLogExplorerPlugin.SEARCH_DOWN));
        buttonDown.setToolTipText(Messages.Search_down);
        buttonDown.addSelectionListener(new SearchDownSelectionListener());
    }

    private void createButtons(Composite parent) {

        Composite buttonsArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(0, true);
        buttonsArea.setLayout(gridLayout);
        buttonsArea.setLayoutData(new GridData(GridData.CENTER));

        buttonApplyFilters = createCommandButton(buttonsArea, Messages.Apply_filters, new ApplyFiltersSelectionListener());
        buttonClearFilters = createCommandButton(buttonsArea, Messages.Clear_filters, new ClearFiltersSelectionListener());

        createButtonSpacer(buttonsArea);

        buttonSelectAll = createCommandButton(buttonsArea, Messages.Select_all, new SelectAllSelectionListener());
        buttonDeselectAll = createCommandButton(buttonsArea, Messages.Deselect_all, new DeselectAllSelectionListener());
    }

    private void createButtonSpacer(Composite parent) {

        Composite spacer = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData();
        layoutData.heightHint = 0;
        spacer.setLayoutData(layoutData);

        GridLayout layout = (GridLayout)parent.getLayout();
        layout.numColumns++;
    }

    private Button createCommandButton(Composite parent, String label, SelectionListener listener) {

        Button button = WidgetFactory.createPushButton(parent, label);
        button.addSelectionListener(listener);
        button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = (GridLayout)parent.getLayout();
        layout.numColumns++;

        return button;
    }

    public void applyFilter() {

        Event event = new Event();
        event.display = Display.getCurrent();
        event.widget = buttonApplyFilters;
        SelectionEvent selectionEvent = new SelectionEvent(event);

        applyFilterInternally(selectionEvent);
    }

    private void applyFilterInternally(SelectionEvent event) {

        FilterData filterData = new FilterData();

        filterData.id = comboIdFilter.getText();
        filterData.type = comboTypeFilter.getText();
        filterData.severity = comboSeverityFilter.getText();
        filterData.fromLibrary = comboFromLibraryFilter.getText();
        filterData.fromProgram = comboFromProgramFilter.getText();
        filterData.fromStmt = comboFromStmtFilter.getText();
        filterData.toLibrary = comboToLibraryFilter.getText();
        filterData.toProgram = comboToProgramFilter.getText();
        filterData.toStmt = comboToStmtFilter.getText();
        filterData.text = textSearch.getText();

        event.detail = JobLogExplorerFilterPanelEvents.APPLY_FILTERS;
        event.data = filterData;
        notifyFilterChangedListeners(event);
    }

    public void addFilterChangedListener(SelectionListener listener) {

        filterChangedListeners.add(listener);
    }

    public void removeFilterChangedListener(ISelectionChangedListener listener) {

        filterChangedListeners.remove(listener);
    }

    private void notifyFilterChangedListeners(SelectionEvent event) {

        for (SelectionListener listener : filterChangedListeners) {
            listener.widgetSelected(event);
        }
    }

    public boolean isDisposed() {
        return filterArea.isDisposed();
    }

    public void setIdFilterItems(String[] idFilterItems) {
        setComboItems(comboIdFilter, idFilterItems);
    }

    public void setTypeFilterItems(String[] typeFilterItems) {
        setComboItems(comboTypeFilter, typeFilterItems);
    }

    public void setSeverityFilterItems(String[] typeFilterItems) {

        setComboItems(comboSeverityFilter, typeFilterItems);
    }

    public void setFromLibraryFilterItems(String[] fromLibraryFilterItems) {
        setComboItems(comboFromLibraryFilter, fromLibraryFilterItems);
    }

    public void setFromProgramFilterItems(String[] fromProgramFilterItems) {
        setComboItems(comboFromProgramFilter, fromProgramFilterItems);
    }

    public void setFromStmtFilterItems(String[] fromStmtFilterItems) {
        setComboItems(comboFromStmtFilter, fromStmtFilterItems);
    }

    public void setToLibraryFilterItems(String[] toLibraryFilterItems) {
        setComboItems(comboToLibraryFilter, toLibraryFilterItems);
    }

    public void setToProgramFilterItems(String[] toProgramFilterItems) {
        setComboItems(comboToProgramFilter, toProgramFilterItems);
    }

    public void setToStmtFilterItems(String[] toStmtFilterItems) {
        setComboItems(comboToStmtFilter, toStmtFilterItems);
    }

    private void setComboItems(Combo combo, String[] items) {

        String selectedItemValue = combo.getText();
        combo.setItems(items);

        if (!StringHelper.isNullOrEmpty(selectedItemValue)) {
            String[] comboItems = combo.getItems();
            for (int i = 0; i < comboItems.length; i++) {
                if (selectedItemValue.equals(comboItems[i])) {
                    combo.select(i);
                    return;
                }
            }
        }

        combo.select(0);
    }

    public void clearFilters() {

        Event e = new Event();
        e.widget = buttonClearFilters;
        SelectionEvent event = new SelectionEvent(e);
        event.detail = JobLogExplorerFilterPanelEvents.REMOVE_FILTERS;

        comboIdFilter.select(0);
        comboTypeFilter.select(0);
        comboSeverityFilter.select(0);

        comboFromLibraryFilter.select(0);
        comboFromProgramFilter.select(0);
        comboFromStmtFilter.select(0);

        comboToLibraryFilter.select(0);
        comboToProgramFilter.select(0);
        comboToStmtFilter.select(0);

        textSearch.setText(""); //$NON-NLS-1$

        notifyFilterChangedListeners(event);
    }

    private class ApplyFiltersSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {
            applyFilterInternally(event);
        }
    }

    private class ClearFiltersSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            comboIdFilter.select(0);
            comboTypeFilter.select(0);
            comboSeverityFilter.select(0);

            comboFromLibraryFilter.select(0);
            comboFromProgramFilter.select(0);
            comboFromStmtFilter.select(0);

            comboToLibraryFilter.select(0);
            comboToProgramFilter.select(0);
            comboToStmtFilter.select(0);

            textSearch.setText(""); //$NON-NLS-1$

            event.detail = JobLogExplorerFilterPanelEvents.REMOVE_FILTERS;
            event.data = null;
            notifyFilterChangedListeners(event);
        }
    }

    private class SelectAllSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobLogExplorerFilterPanelEvents.SELECT_ALL;
            event.data = null;
            notifyFilterChangedListeners(event);
        }
    }

    private class DeselectAllSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobLogExplorerFilterPanelEvents.DESELECT_ALL;
            event.data = null;
            notifyFilterChangedListeners(event);
        }
    }

    private class SearchUpSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobLogExplorerFilterPanelEvents.SEARCH_UP;
            event.data = null;
            event.text = textSearch.getText();
            notifyFilterChangedListeners(event);
        }
    }

    private class SearchDownSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobLogExplorerFilterPanelEvents.SEARCH_DOWN;
            event.data = null;
            event.text = textSearch.getText();
            notifyFilterChangedListeners(event);
        }
    }
}
