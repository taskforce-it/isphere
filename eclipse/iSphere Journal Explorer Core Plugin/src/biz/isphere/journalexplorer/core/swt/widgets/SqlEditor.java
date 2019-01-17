/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.swt.widgets;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.ContentAssistText;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.Messages;

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

    private boolean isButtonAddVisible;
    private boolean isButtonClearVisible;
    private boolean isButtonExecuteVisible;

    public SqlEditor(Composite parent, int style) {
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

        createContentArea();
    }

    public void addModifyListener(ModifyListener listener) {
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
    }

    @Override
    public boolean setFocus() {

        if (!StringHelper.isNullOrEmpty(textSqlEditor.getText())) {
            return textSqlEditor.setFocus();
        }

        return setFocusChecked(btnAddField);
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

    private void createContentArea() {

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        Composite wherePanel = new Composite(this, SWT.NONE);
        GridLayout wherePanelLayout = new GridLayout(2, false);
        wherePanelLayout.marginRight = wherePanelLayout.marginWidth;
        wherePanelLayout.marginHeight = 0;
        wherePanelLayout.marginWidth = 0;
        wherePanel.setLayout(wherePanelLayout);
        wherePanel.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        labelWhere = new Label(wherePanel, SWT.NONE);
        labelWhere.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        labelWhere.setText(Messages.SqlEditor_WHERE);
        labelWhere.setToolTipText(Messages.Tooltip_SqlEditor_Text);

        Label helpItem = new Label(wherePanel, SWT.NONE);
        helpItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SYSTEM_HELP));
        helpItem.addMouseListener(new DisplayHelpListener());

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

        textSqlEditor = WidgetFactory.createContentAssistText(this);// ContentAssistText(this);
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

        textSqlEditor.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {

                if (!e.doit) {
                    return;
                }

                if (e.stateMask == SWT.CTRL) {
                    switch (e.character) {
                    case ' ':
                        textSqlEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
                        break;

                    case '\032': // Ctrl-z (Undo)
                        textSqlEditor.doOperation(ITextOperationTarget.UNDO);
                    }

                }
            }
        });

        Composite executePanel = new Composite(this, SWT.NONE);
        GridLayout executePanelLayout = new GridLayout(1, false);
        executePanelLayout.marginLeft = executePanelLayout.marginWidth;
        executePanelLayout.marginHeight = 0;
        executePanelLayout.marginWidth = 0;
        executePanel.setLayout(executePanelLayout);
        executePanel.setLayoutData(new GridData(GridData.FILL_VERTICAL));

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
    }

    public void addSelectionListener(SelectionListener listener) {
        btnExecute.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        btnExecute.removeSelectionListener(listener);
    }

    public void setContentAssistProposals(ContentAssistProposal[] contentAssistProposals) {
        textSqlEditor.setContentAssistProposals(contentAssistProposals);
    }

    @Override
    public void dispose() {
        textSqlEditor.dispose();
        super.dispose();
    }

    private class DisplayHelpListener extends MouseAdapter {
        @Override
        public void mouseUp(MouseEvent event) {
            PlatformUI.getWorkbench().getHelpSystem()
                .displayHelpResource("/biz.isphere.journalexplorer.help/html/journalexplorer/sql_reference.html");
        }
    }
}
