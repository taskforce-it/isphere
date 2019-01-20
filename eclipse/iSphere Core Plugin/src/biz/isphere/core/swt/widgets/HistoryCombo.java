/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;

public class HistoryCombo {

    private String key;
    private DialogSettingsManager dialogSettingsmanager;
    private LinkedHashSet<String> currentHistoryItems;
    private int maxSize;
    private Combo combo;

    public HistoryCombo(Composite parent) {
        this(parent, SWT.READ_ONLY);
    }

    public HistoryCombo(Composite parent, int style) {

        this.key = null;
        this.dialogSettingsmanager = null;
        this.currentHistoryItems = null;

        this.maxSize = 10;
        this.combo = new Combo(parent, style | SWT.DROP_DOWN);
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setLayoutData(Object layoutData) {
        combo.setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return combo.getLayoutData();
    }

    public void addSelectionListener(SelectionListener listener) {
        combo.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        combo.removeSelectionListener(listener);
    }

    public void deselectAll() {
        combo.deselectAll();
    }

    public boolean setFocus() {
        return combo.setFocus();
    }

    public void setEnabled(boolean enabled) {
        combo.setEnabled(enabled);
    }

    public void setToolTipText(String string) {
        combo.setToolTipText(string);
    }

    public int getItemCount() {
        return combo.getItemCount();
    }

    public void load(DialogSettingsManager dialogSettingsmanager, String key) {

        this.dialogSettingsmanager = dialogSettingsmanager;
        this.key = key;

        currentHistoryItems = new LinkedHashSet<String>(Arrays.asList(combo.getItems()));

        int count = dialogSettingsmanager.loadIntValue(getCountKey(), 0);
        if (count > 0) {

            for (int i = 0; i < Math.min(count, maxSize); i++) {
                String historyItem = dialogSettingsmanager.loadValue(getKey(i), null);
                if (!StringHelper.isNullOrEmpty(historyItem)) {
                    currentHistoryItems.add(historyItem);
                }
            }

            setHistoryItems();
        }
    }

    public void updateHistory(String string) {

        if (StringHelper.isNullOrEmpty(string)) {
            return;
        }

        if (currentHistoryItems.contains(string)) {
            // Remove item to sort it to first position.
            currentHistoryItems.remove(string);
        }

        String[] items = currentHistoryItems.toArray(new String[currentHistoryItems.size()]);

        String[] tmpItems;
        if (items.length >= maxSize) {
            tmpItems = new String[maxSize];
        } else {
            tmpItems = new String[items.length + 1];
        }

        System.arraycopy(items, 0, tmpItems, 1, tmpItems.length - 1);
        tmpItems[0] = string;

        currentHistoryItems.clear();
        currentHistoryItems.addAll(Arrays.asList(tmpItems));

        setHistoryItems();
    }

    public void store() {

        if (key == null || dialogSettingsmanager == null) {
            return;
        }

        String[] items = currentHistoryItems.toArray(new String[currentHistoryItems.size()]);

        dialogSettingsmanager.storeValue(getCountKey(), Integer.toString(items.length));
        for (int i = 0; i < items.length; i++) {
            dialogSettingsmanager.storeValue(getKey(i), items[i]);
        }
    }

    private void setHistoryItems() {
        combo.setItems(currentHistoryItems.toArray(new String[currentHistoryItems.size()]));
    }

    private String getCountKey() {
        return key + ".count";
    }

    private String getKey(int i) {
        return key + "." + i;
    }
}
