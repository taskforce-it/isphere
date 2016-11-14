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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.swt.widgets.WidgetFactory;

public class JobLogExplorerFilterPanel {

    private Composite filterArea;
    private FilterData filterData;

    private List<SelectionListener> listeners;

    private Combo comboTypeFilter;

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

        comboTypeFilter = createCombo(filterArea, "Message type:");
    }

    private Combo createCombo(Composite filterArea, String label) {

        Label labelText = new Label(filterArea, SWT.NONE);
        labelText.setText(label);

        Combo combo = WidgetFactory.createCombo(filterArea);
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                filterData.type = ((Combo)event.getSource()).getText();
                event.data = filterData;
                notifySelectionChangedListeners(event);
            }
        });

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

    public void setTypeFilterItems(String[] typeFilterItems) {

        comboTypeFilter.setItems(typeFilterItems);
        comboTypeFilter.select(0);
    }
}
