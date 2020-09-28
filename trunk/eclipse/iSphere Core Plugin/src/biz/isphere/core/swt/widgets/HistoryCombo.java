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
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.historycombo.WorkWithHistoryDialog;

public class HistoryCombo {

    private String key;
    private DialogSettingsManager dialogSettingsmanager;
    private LinkedHashSet<String> currentHistoryItems;
    private int maxSize;

    private Composite panel;
    private Combo cboHistory;
    private Button btnWworkWithHistory;

    public HistoryCombo(Composite parent, int style) {

        this.key = null;
        this.dialogSettingsmanager = null;
        this.currentHistoryItems = null;

        this.maxSize = 10;

        createPanel(parent, style);
    }

    private void createPanel(final Composite parent, int style) {

        panel = new Composite(parent, SWT.NONE);
        GridLayout panelLayout = new GridLayout(2, false);
        panelLayout.marginHeight = 0;
        panelLayout.marginWidth = 0;
        panelLayout.horizontalSpacing = 0;
        panel.setLayout(panelLayout);

        cboHistory = new Combo(panel, style);
        cboHistory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        btnWworkWithHistory = new Button(panel, SWT.ARROW_LEFT);

        boolean isDrawn = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (isDrawn) {
            final Image upArrow = new Image(parent.getDisplay(), 5, 6);
            GC gc = new GC(upArrow);
            gc.setBackground(btnWworkWithHistory.getBackground());
            gc.fillRectangle(upArrow.getBounds());
            gc.setForeground(btnWworkWithHistory.getForeground());
            gc.drawLine(0, 5, 4, 5);
            gc.drawLine(0, 4, 4, 4);
            gc.drawLine(1, 3, 3, 3);
            gc.drawLine(1, 2, 3, 2);
            gc.drawLine(2, 1, 2, 1);
            gc.drawLine(2, 0, 2, 0);
            gc.dispose();
            btnWworkWithHistory.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    upArrow.dispose();
                }
            });
            btnWworkWithHistory.setImage(upArrow);
        } else {
            btnWworkWithHistory = new Button(panel, SWT.ARROW);
            btnWworkWithHistory.setAlignment(SWT.UP);
        }

        btnWworkWithHistory.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                WorkWithHistoryDialog workWithHistoryDialog = new WorkWithHistoryDialog(parent.getShell());
                workWithHistoryDialog.setItems(cboHistory.getItems());
                if (workWithHistoryDialog.open() == Dialog.OK) {
                    setHistoryItems(workWithHistoryDialog.getItems());
                    store();
                }
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
            }
        });
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setTextLimit(int limit) {
        cboHistory.setTextLimit(limit);
    }

    public void setLayoutData(Object layoutData) {
        panel.setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return panel.getLayoutData();
    }

    public void addSelectionListener(SelectionListener listener) {
        cboHistory.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        cboHistory.removeSelectionListener(listener);
    }

    public void addModifyListener(ModifyListener listener) {
        cboHistory.addModifyListener(listener);
    }

    public void removeModifyListener(ModifyListener listener) {
        cboHistory.removeModifyListener(listener);
    }

    public void addVerifyListener(VerifyListener listener) {
        cboHistory.addVerifyListener(listener);
    }

    public void removeVerifyListener(VerifyListener listener) {
        cboHistory.removeVerifyListener(listener);
    }

    public void deselectAll() {
        cboHistory.deselectAll();
    }

    public boolean setFocus() {
        return cboHistory.setFocus();
    }

    public void setEnabled(boolean enabled) {
        cboHistory.setEnabled(enabled);
    }

    public void setToolTipText(String string) {
        cboHistory.setToolTipText(string);
    }

    public int getItemCount() {
        return cboHistory.getItemCount();
    }

    public void load(DialogSettingsManager dialogSettingsmanager, String key) {

        this.dialogSettingsmanager = dialogSettingsmanager;
        this.key = key;

        currentHistoryItems = new LinkedHashSet<String>(Arrays.asList(cboHistory.getItems()));

        int count = dialogSettingsmanager.loadIntValue(getCountKey(), 0);
        if (count > 0) {

            for (int i = 0; i < Math.min(count, maxSize); i++) {
                String historyItem = dialogSettingsmanager.loadValue(getKey(i), null);
                if (!StringHelper.isNullOrEmpty(historyItem)) {
                    currentHistoryItems.add(historyItem);
                }
            }

            setHistoryItems(currentHistoryItems);
        }
    }

    public String getText() {
        return cboHistory.getText();
    }

    public void setText(String text) {
        cboHistory.setText(text);
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

        setHistoryItems(tmpItems);
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

    private void setHistoryItems(String[] items) {

        String text = cboHistory.getText();

        currentHistoryItems.clear();
        currentHistoryItems.addAll(Arrays.asList(items));

        cboHistory.setItems(items);

        for (String item : items) {
            if (text != null && text.equals(item)) {
                cboHistory.setText(item);
                break;
            }
        }
    }

    private void setHistoryItems(Set<String> items) {

        cboHistory.setItems(items.toArray(new String[items.size()]));
    }

    private String getCountKey() {
        return key + ".count";
    }

    private String getKey(int i) {
        return key + "." + i;
    }
}
