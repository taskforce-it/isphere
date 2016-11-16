/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;

public class JobLogExplorerFilterPanel {

    private Composite filterArea;

    private List<SelectionListener> filterChangedListeners;

    private Combo comboIdFilter;
    private Combo comboTypeFilter;
    private Combo comboSeverityFilter;

    private Button buttonApplyFilters;
    private Button buttonClearFilters;
    private Button buttonSelectAll;
    private Button buttonDeselectAll;

    public JobLogExplorerFilterPanel() {

        this.filterChangedListeners = new ArrayList<SelectionListener>();
    }

    public void createViewer(Composite parent) {

        filterArea = new Composite(parent, SWT.NONE);
        filterArea.setLayout(new GridLayout(2, false));
        filterArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Create controls
        createFilterControls(filterArea);

        createButtons(parent);
    }

    private void createFilterControls(Composite filterArea) {

        comboIdFilter = createCombo(filterArea, Messages.Label_ID, MessageModifyEvent.ID);
        comboTypeFilter = createCombo(filterArea, Messages.Label_Type, MessageModifyEvent.TYPE);
        comboSeverityFilter = createCombo(filterArea, Messages.Label_Severity, MessageModifyEvent.SEVERITY);
    }

    private Combo createCombo(Composite filterArea, String label, int messageEventType) {

        Label labelText = new Label(filterArea, SWT.NONE);
        labelText.setText(label);

        Combo combo = WidgetFactory.createCombo(filterArea);

        return combo;
    }

    private void createButtons(Composite parent) {

        Composite buttonsArea = new Composite(parent, SWT.NONE);
        buttonsArea.setLayout(new GridLayout(0, true));
        buttonsArea.setLayoutData(new GridData());

        buttonApplyFilters = createCommandButton(buttonsArea, Messages.Apply_filters, new ApplyFiltersSelectionListener());
        buttonClearFilters = createCommandButton(buttonsArea, Messages.Clear_filters, new ClearFiltersSelectionListener());
        createSpacer(buttonsArea);
        buttonSelectAll = createCommandButton(buttonsArea, Messages.Select_all, new SelectAllSelectionListener());
        buttonDeselectAll = createCommandButton(buttonsArea, Messages.Deselect_all, new DeselectAllSelectionListener());
    }

    private void createSpacer(Composite parent) {

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

        comboIdFilter.setItems(idFilterItems);
        comboIdFilter.select(0);
    }

    public void setTypeFilterItems(String[] typeFilterItems) {

        comboTypeFilter.setItems(typeFilterItems);
        comboTypeFilter.select(0);
    }

    public void setSeverityFilterItems(String[] typeFilterItems) {

        comboSeverityFilter.setItems(typeFilterItems);
        comboSeverityFilter.select(0);
    }

    public void clearFilters() {

        Event e = new Event();
        e.widget = buttonClearFilters;
        SelectionEvent event = new SelectionEvent(e);
        event.detail = JobLogExplorerFilterPanelEvents.REMOVE_FILTERS;

        notifyFilterChangedListeners(event);
    }

    private class ApplyFiltersSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            FilterData filterData = new FilterData();

            filterData.id = comboIdFilter.getText();
            filterData.type = comboTypeFilter.getText();
            filterData.severity = comboSeverityFilter.getText();

            event.detail = JobLogExplorerFilterPanelEvents.APPLY_FILTERS;
            event.data = filterData;
            notifyFilterChangedListeners(event);
        }
    }

    private class ClearFiltersSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            comboIdFilter.select(0);
            comboTypeFilter.select(0);
            comboSeverityFilter.select(0);

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
}
