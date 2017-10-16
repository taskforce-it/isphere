/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.Messages;

public class SqlEditor extends Composite {

    private Text textSqlEditor;
    private Button btnExecute;

    public SqlEditor(Composite parent, int style) {
        super(parent, style);
        createContentArea();
    }

    public void setWhereClause(String whereClause) {

        if (textSqlEditor == null || textSqlEditor.isDisposed()) {
            return;
        }

        if (whereClause != null) {
            textSqlEditor.setText(whereClause);
        } else {
            textSqlEditor.setText(""); //$NON-NLS-1$
        }
    }

    public String getWhereClause() {
        return textSqlEditor.getText();
    }

    private void createContentArea() {

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        Label labelWhere = new Label(this, SWT.NONE);
        labelWhere.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        labelWhere.setText(Messages.SqlEditor_WHERE);
        labelWhere.setToolTipText(Messages.Tooltip_SqlEditor_Text);

        textSqlEditor = WidgetFactory.createMultilineText(this, true, false);
        textSqlEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
        textSqlEditor.setToolTipText(Messages.Tooltip_SqlEditor_Text);
        textSqlEditor.addKeyListener(new KeyListener() {

            public void keyReleased(KeyEvent event) {
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

            public void keyPressed(KeyEvent event) {
                if (isCtrlEnter(event)) {
                    event.doit = false;
                }
            }

            private boolean isCtrlEnter(KeyEvent event) {
                return event.stateMask == SWT.CTRL && event.character == SWT.CR;
            }
        });

        btnExecute = WidgetFactory.createPushButton(this, "&Execute");
        btnExecute.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
    }

    public void addSelectionListener(SelectionListener listener) {
        btnExecute.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        btnExecute.removeSelectionListener(listener);
    }
}
