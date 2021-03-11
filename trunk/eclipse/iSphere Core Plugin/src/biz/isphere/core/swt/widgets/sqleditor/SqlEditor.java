/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.sqleditor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.ContentAssistText;
import biz.isphere.core.swt.widgets.HistoryCombo;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SqlEditor extends Composite {

    public static final int BUTTON_ADD = SWT.BUTTON1;
    public static final int BUTTON_CLEAR = SWT.BUTTON2;
    public static final int BUTTON_EXECUTE = SWT.BUTTON3;
    public static final int BUTTON_NONE = SWT.BUTTON4;

    private ContentAssistText textSqlEditor;

    private Button btnClear;
    private Button btnAddField;
    private Button btnExecute;
    private Label labelWhere;
    private Label labelHistory;
    private Label helpItem;
    private HistoryCombo cboHistory;
    private SelectionListener selectionListener;
    private Combo cboTableName;

    private boolean isButtonAddVisible;
    private boolean isButtonClearVisible;
    private boolean isButtonExecuteVisible;

    private String historyKey;
    private DialogSettingsManager dialogSettings;
    private boolean showTableControl;

    private List<SelectionListener> btnExecuteSelectionListeners;

    public SqlEditor(Composite parent, String historyKey, DialogSettingsManager dialogSettingsManager, int style) {
        this(parent, historyKey, dialogSettingsManager, false, style);
    }

    public SqlEditor(Composite parent, String historyKey, DialogSettingsManager dialogSettingsManager, boolean showTableControl, int style) {
        super(parent, style);

        if (!isStyle(style, BUTTON_NONE)) {
            if (hasButtonStyle(style)) {
                isButtonAddVisible = isStyle(style, BUTTON_ADD);
                isButtonClearVisible = isStyle(style, BUTTON_CLEAR);
                isButtonExecuteVisible = isStyle(style, BUTTON_EXECUTE);
            } else {
                isButtonAddVisible = true;
                isButtonClearVisible = true;
                isButtonExecuteVisible = true;
            }
        }

        this.historyKey = historyKey;
        this.dialogSettings = dialogSettingsManager;
        this.showTableControl = showTableControl;

        this.btnExecuteSelectionListeners = new LinkedList<SelectionListener>();

        this.setLayout(createLayout());

        createContentArea(this);
    }

    public void addModifyListener(ModifyListener listener) {
        if (showTableControl) {
            cboTableName.addModifyListener(listener);
        }
        textSqlEditor.addModifyListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        setEnabledChecked(btnAddField, enabled);
        btnClear.setEnabled(enabled);
        btnExecute.setEnabled(enabled);
        textSqlEditor.setEnabled(enabled);
        labelWhere.setEnabled(enabled);
        labelHistory.setEnabled(enabled);
        helpItem.setEnabled(enabled);
    }

    @Override
    public boolean setFocus() {

        return textSqlEditor.setFocus();
    }

    public String getTableName() {

        if (showTableControl) {
            return cboTableName.getText();
        } else {
            return null;
        }
    }

    public void setTableName(String name) {
        if (showTableControl) {
            cboTableName.setText(name);
        }
    }

    public void setTableNameItems(String[] items) {
        if (showTableControl) {
            cboTableName.setItems(items);
        }
    }

    public void selectTableName(int index) {
        if (showTableControl) {
            cboTableName.select(index);
        }
    }

    public void setWhereClause(String whereClause) {

        if (textSqlEditor == null || textSqlEditor.isDisposed()) {
            return;
        }

        if (whereClause != null) {
            textSqlEditor.setText(whereClause.trim());
        } else {
            textSqlEditor.setText(""); //$NON-NLS-1$
        }
    }

    public String getWhereClause() {
        return textSqlEditor.getText().trim();
    }

    private boolean isStyle(int style, int flag) {

        if ((style & flag) == flag) {
            return true;
        }

        return false;
    }

    private boolean hasButtonStyle(int style) {

        if (isStyle(style, BUTTON_ADD) || isStyle(style, BUTTON_CLEAR) || isStyle(style, BUTTON_EXECUTE)) {
            return true;
        }

        return false;
    }

    private void setEnabledChecked(Button control, boolean enabled) {

        if (control != null && !control.isDisposed()) {
            control.setEnabled(enabled);
        }
    }

    private boolean setFocusChecked(Button control) {

        if (control != null && !control.isDisposed()) {
            return control.setFocus();
        }

        return false;
    }

    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout(3, false);
        return layout;
    }

    protected void createContentArea(Composite parent) {

        if (showTableControl) {
            createTableEditorControl(parent, 100);
        }

        int numColumns = ((GridLayout)getLayout()).numColumns;

        Composite wherePanel = new Composite(parent, SWT.NONE);
        wherePanel.setLayout(createLayout(2, SWT.DEFAULT, 0, SWT.DEFAULT));
        GridData gridData = new GridData(GridData.FILL_VERTICAL);
        gridData.widthHint = 100;
        wherePanel.setLayoutData(gridData);

        labelHistory = new Label(wherePanel, SWT.NONE);
        labelHistory.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
        labelHistory.setText(Messages.SqlEditor_History);
        labelHistory.setToolTipText(Messages.SqlEditor_History_Tooltip);

        labelWhere = new Label(wherePanel, SWT.NONE);
        labelWhere.setLayoutData(new GridData());
        labelWhere.setText(Messages.SqlEditor_WHERE);
        labelWhere.setToolTipText(Messages.Tooltip_SqlEditor_Text);

        helpItem = DisplaySQLHelpListener.createLabel(wherePanel);

        new Label(wherePanel, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        btnAddField = WidgetFactory.createPushButton(wherePanel, Messages.ButtonLabel_AddField);
        btnAddField.setToolTipText(Messages.ButtonTooltip_AddField);
        btnAddField.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));
        btnAddField.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textSqlEditor.setFocus();
                textSqlEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
            }
        });

        Composite editorPanel = new Composite(parent, SWT.NONE);
        editorPanel.setLayout(createLayout(1, SWT.DEFAULT, 0, 0));
        editorPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, numColumns - 2, 1));

        selectionListener = new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                textSqlEditor.setText(((Combo)event.getSource()).getText());
                textSqlEditor.setFocus();
                cboHistory.deselectAll();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        };

        cboHistory = WidgetFactory.createReadOnlyHistoryCombo(editorPanel);
        cboHistory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cboHistory.addSelectionListener(selectionListener);
        cboHistory.setToolTipText(Messages.SqlEditor_History_Tooltip);
        refreshHistory();

        textSqlEditor = WidgetFactory.createContentAssistText(editorPanel);
        textSqlEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
        textSqlEditor.setHint(Messages.Tooltip_SqlEditor_Text);
        textSqlEditor.setTraverseEnabled(true);
        textSqlEditor.enableAutoActivation(true);
        textSqlEditor.enableAutoInsert(true);
        textSqlEditor.setContentAssistProposals(new ContentAssistProposal[0]);
        textSqlEditor.setToolTipText(Messages.Tooltip_SqlEditor_Text);
        textSqlEditor.addVerifyKeyListener(new VerifyKeyListener() {

            public void verifyKey(VerifyEvent event) {
                if (isCtrlEnter(event)) {
                    Event keyUpEvent = new Event();
                    keyUpEvent.character = event.character;
                    keyUpEvent.data = event.data;
                    keyUpEvent.display = event.display;
                    keyUpEvent.doit = event.doit;
                    keyUpEvent.keyCode = event.keyCode;
                    keyUpEvent.stateMask = event.stateMask;
                    keyUpEvent.time = event.time;
                    keyUpEvent.widget = event.widget;
                    btnExecute.notifyListeners(SWT.Selection, keyUpEvent);
                    event.doit = false;
                }
            }

            private boolean isCtrlEnter(KeyEvent event) {
                return event.stateMask == SWT.CTRL && event.character == SWT.CR;
            }
        });

        Composite executePanel = new Composite(parent, SWT.NONE);
        executePanel.setLayout(createLayout(1, 0, SWT.DEFAULT, SWT.DEFAULT));
        GridData gridData2 = new GridData(GridData.FILL_VERTICAL);
        gridData2.widthHint = gridData.widthHint;
        executePanel.setLayoutData(gridData2);

        btnClear = WidgetFactory.createPushButton(executePanel, Messages.ButtonLabel_Clear);
        btnClear.setToolTipText(Messages.ButtonTooltip_Clear);
        btnClear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textSqlEditor.setText("");
                textSqlEditor.setFocus();
            }
        });

        new Label(executePanel, SWT.NONE).setLayoutData(new GridData(GridData.FILL_VERTICAL));

        btnExecute = WidgetFactory.createPushButton(executePanel, Messages.ButtonLabel_Execute);
        btnExecute.setToolTipText(Messages.ButtonTooltip_Execute);
        btnExecute.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnAddField.setVisible(isButtonAddVisible);
        btnAddField.setEnabled(isButtonAddVisible);

        btnClear.setVisible(isButtonClearVisible);
        btnClear.setEnabled(isButtonClearVisible);

        btnExecute.setVisible(isButtonExecuteVisible);
        btnExecute.setEnabled(isButtonExecuteVisible);

        btnExecute.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                event.data = getWhereClause();
                notifySelectionListeners(event);
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    private void createTableEditorControl(Composite parent, int widthHint) {

        Composite wherePanel = new Composite(parent, SWT.NONE);
        GridLayout wherePanelLayout = new GridLayout(1, false);
        wherePanelLayout.marginRight = wherePanelLayout.marginWidth;
        wherePanelLayout.marginHeight = 0;
        wherePanelLayout.marginWidth = 0;
        wherePanel.setLayout(wherePanelLayout);
        GridData gd_wherePanel = new GridData(GridData.FILL_VERTICAL);
        gd_wherePanel.widthHint = widthHint;
        wherePanel.setLayoutData(gd_wherePanel);

        Label labelTableName = new Label(wherePanel, SWT.NONE);
        labelTableName.setText(Messages.SqlEditor_TableName);
        labelTableName.setToolTipText(Messages.SqlEditor_TableName_Tooltip);

        Composite editorPanel = new Composite(parent, SWT.NONE);
        GridLayout editorPanelLayout = new GridLayout(2, false);
        editorPanelLayout.marginRight = wherePanelLayout.marginWidth;
        editorPanelLayout.marginHeight = 0;
        editorPanelLayout.marginWidth = 0;
        editorPanelLayout.verticalSpacing = -1;
        editorPanel.setLayout(editorPanelLayout);
        GridData gd_editorPanel = new GridData(GridData.FILL_HORIZONTAL);
        gd_editorPanel.horizontalSpan = 2;
        editorPanel.setLayoutData(gd_editorPanel);

        cboTableName = WidgetFactory.createUpperCaseCombo(editorPanel);
        GridData gd_tableName = new GridData();
        gd_tableName.widthHint = 200;
        cboTableName.setLayoutData(gd_tableName);
        cboTableName.setToolTipText(Messages.SqlEditor_TableName_Tooltip);
        // cboTableName.setItems(getTables(data.getJournaledFiles()));
        cboTableName.select(0);

        Button btnClear = WidgetFactory.createPushButton(editorPanel);
        btnClear.setText("X"); // //$NON-NLS-1$
        btnClear.setToolTipText(Messages.SqlEditor_ClearTableName_Tooltip);
        btnClear.setLayoutData(new GridData());
        btnClear.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                cboTableName.select(0);
            }
        });

    }

    private GridLayout createLayout(int numColumns, int marginLeft, int margingRight, int verticalSpacing) {

        GridLayout layout = new GridLayout(numColumns, false);

        layout.numColumns = numColumns;
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        if (marginLeft != SWT.DEFAULT) layout.marginLeft = marginLeft;
        if (margingRight != SWT.DEFAULT) layout.marginRight = margingRight;
        if (verticalSpacing != SWT.DEFAULT) layout.verticalSpacing = verticalSpacing;

        return layout;
    }

    public void setBtnExecuteLabel(String label) {
        btnExecute.setText(label);
    }

    public void setBtnExecuteToolTipText(String tooltip) {
        btnExecute.setToolTipText(tooltip);
    }

    public void addSelectionListener(SelectionListener listener) {
        btnExecuteSelectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        btnExecuteSelectionListeners.remove(listener);
    }

    private void notifySelectionListeners(SelectionEvent event) {
        event.data = textSqlEditor.getText();
        for (SelectionListener listener : btnExecuteSelectionListeners) {
            listener.widgetSelected(event);
        }
    }

    public void setContentAssistProposals(ContentAssistProposal[] contentAssistProposals) {
        textSqlEditor.setContentAssistProposals(contentAssistProposals);
    }

    public void refreshHistory() {
        cboHistory.load(dialogSettings, historyKey);
    }

    public void storeHistory() {
        cboHistory.updateHistory(textSqlEditor.getText());
        cboHistory.store();
    }

    @Override
    public void dispose() {
        cboHistory.removeSelectionListener(selectionListener);
        textSqlEditor.dispose();
        super.dispose();
    }
}
