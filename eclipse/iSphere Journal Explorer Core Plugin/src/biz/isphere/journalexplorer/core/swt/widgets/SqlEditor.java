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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.ContentAssistText;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.Messages;

public class SqlEditor extends Composite {

    private ContentAssistText textSqlEditor;
    private Button btnClear;
    private Button btnAddField;
    private Button btnExecute;

    public SqlEditor(Composite parent, int style) {
        super(parent, style);
        createContentArea();
    }

    @Override
    public boolean setFocus() {

        if (!StringHelper.isNullOrEmpty(textSqlEditor.getText())) {
            return textSqlEditor.setFocus();
        }

        return btnAddField.setFocus();
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

    private void createContentArea() {

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        Composite leftPanel = new Composite(this, SWT.NONE);
        GridLayout leftPanelLayout = new GridLayout(1, false);
        leftPanelLayout.marginHeight = 0;
        leftPanelLayout.marginWidth = 0;
        leftPanel.setLayout(leftPanelLayout);
        leftPanel.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        Composite wherePanel = new Composite(leftPanel, SWT.NONE);
        GridLayout wherePanelLayout = new GridLayout(1, false);
        wherePanelLayout.marginHeight = 0;
        wherePanelLayout.marginWidth = 0;
        wherePanel.setLayout(wherePanelLayout);
        wherePanel.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        Label labelWhere = new Label(wherePanel, SWT.NONE);
        labelWhere.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        labelWhere.setText(Messages.SqlEditor_WHERE);
        labelWhere.setToolTipText(Messages.Tooltip_SqlEditor_Text);

        Composite addFieldPanel = new Composite(leftPanel, SWT.NONE);
        GridLayout addPanelLayout = new GridLayout(1, false);
        addPanelLayout.marginHeight = 0;
        addPanelLayout.marginWidth = 0;
        addFieldPanel.setLayout(addPanelLayout);

        btnClear = WidgetFactory.createPushButton(addFieldPanel, Messages.ButtonLabel_Clear);
        btnClear.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textSqlEditor.setText("");
                textSqlEditor.getTextWidget().setFocus();
            }
        });

        btnAddField = WidgetFactory.createPushButton(addFieldPanel, Messages.ButtonLabel_AddField);
        btnAddField.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
        btnAddField.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                textSqlEditor.getTextWidget().setFocus();
                textSqlEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
            }
        });

        textSqlEditor = WidgetFactory.createContentAssistText(this);// ContentAssistText(this);
        textSqlEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
        textSqlEditor.setHint(Messages.Tooltip_SqlEditor_Text);
        textSqlEditor.setTraverseEnabled(true);
        textSqlEditor.enableAutoActivation(true);
        textSqlEditor.enableAutoInsert(true);
        textSqlEditor.getTextWidget().setToolTipText(Messages.Tooltip_SqlEditor_Text);
        textSqlEditor.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {

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

        textSqlEditor.getTextWidget().addKeyListener(new KeyAdapter() {

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

        btnExecute = WidgetFactory.createPushButton(this, Messages.ButtonLabel_Execute);
        btnExecute.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
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
}
