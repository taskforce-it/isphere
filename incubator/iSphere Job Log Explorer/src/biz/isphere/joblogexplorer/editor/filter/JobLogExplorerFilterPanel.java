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
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;

public class JobLogExplorerFilterPanel {

    private Composite filterArea;
    private FilterData filterData;

    private List<SelectionListener> listeners;

    private Combo comboIdFilter;
    private Combo comboTypeFilter;
    private Combo comboSeverityFilter;
    private Button applyFilters;

    public JobLogExplorerFilterPanel() {

        this.listeners = new ArrayList<SelectionListener>();
        this.filterData = new FilterData();
    }

    public void createViewer(Composite parent) {

        filterArea = new Composite(parent, SWT.NONE);
        filterArea.setLayout(new GridLayout(2, false));
        filterArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Create controls
        createControls(filterArea);
    }

    private void createControls(Composite filterArea) {

        comboIdFilter = createCombo(filterArea, Messages.Label_ID, MessageModifyEvent.ID);
        comboTypeFilter = createCombo(filterArea, Messages.Label_Type, MessageModifyEvent.TYPE);
        comboSeverityFilter = createCombo(filterArea, Messages.Label_Severity, MessageModifyEvent.SEVERITY);

        applyFilters = WidgetFactory.createPushButton(filterArea, Messages.Apply_filters);
        applyFilters.addSelectionListener(new ComboSelectionListener(MessageModifyEvent.ALL));
    }

    private Combo createCombo(Composite filterArea, String label, int messageEventType) {

        Label labelText = new Label(filterArea, SWT.NONE);
        labelText.setText(label);

        Combo combo = WidgetFactory.createCombo(filterArea);

        return combo;
    }

    public void addSelectionChangedListener(SelectionListener listener) {

        listeners.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {

        listeners.remove(listener);
    }

    private void notifySelectionChangedListeners(SelectionEvent event) {

        for (SelectionListener listener : listeners) {
            listener.widgetSelected(event);
        }

    }

    public void widgetDefaultSelected(SelectionEvent event) {
        notifySelectionChangedListeners(event);
    }

    public void widgetSelected(SelectionEvent event) {
        notifySelectionChangedListeners(event);
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

    public void refreshFilters() {

        notifySelectionChangedListeners(null);
    }

    private class ComboSelectionListener extends SelectionAdapter {

        private int messageEventType;

        public ComboSelectionListener(int messageEventType) {
            this.messageEventType = messageEventType;
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent event) {

            String filterValue;
            if (event.getSource() instanceof Combo) {
                filterValue = ((Combo)event.getSource()).getText();
            } else {
                filterValue = null;
            }

            switch (messageEventType) {
            case MessageModifyEvent.ALL:
                filterData.id = getFilterValue(comboIdFilter.getText());
                filterData.type = getFilterValue(comboTypeFilter.getText());
                filterData.severity = getFilterValue(comboSeverityFilter.getText());
                break;

            case MessageModifyEvent.ID:
                filterData.id = filterValue;
                break;

            case MessageModifyEvent.TYPE:
                filterData.type = filterValue;
                break;

            case MessageModifyEvent.SEVERITY:
                filterData.severity = filterValue;
                break;

            default:
                break;
            }

            event.data = filterData;
            event.detail = messageEventType;
            notifySelectionChangedListeners(event);
        }

        private String getFilterValue(String text) {

            return text;
        }
    }
}
