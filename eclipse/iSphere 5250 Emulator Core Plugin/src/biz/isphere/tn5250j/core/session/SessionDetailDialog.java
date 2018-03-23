/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.session;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.Messages;

public class SessionDetailDialog extends Dialog {

    private SessionDetail sessionDetail;
    private String sessionDirectory;
    private int actionType;
    private Session session;

    public SessionDetailDialog(Shell parentShell, String sessionDirectory, int actionType, Session session) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.sessionDirectory = sessionDirectory;
        this.actionType = actionType;
        this.session = session;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.VERTICAL));

        sessionDetail = new SessionDetail(getShell(), sessionDirectory, actionType, session);
        sessionDetail.createContents(container);

        return container;
    }

    @Override
    protected void okPressed() {
        if (sessionDetail.processButtonPressed()) {
            super.okPressed();
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Session);
    }

    @Override
    protected Point getInitialSize() {
        Point point = getShell().computeSize(400, SWT.DEFAULT, true);
        return point;
    }

}
