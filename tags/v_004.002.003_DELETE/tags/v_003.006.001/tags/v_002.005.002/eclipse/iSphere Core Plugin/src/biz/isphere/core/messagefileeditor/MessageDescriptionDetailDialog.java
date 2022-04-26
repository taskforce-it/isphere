/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;

import com.ibm.as400.access.AS400;

public class MessageDescriptionDetailDialog extends XDialog {

    private AS400 as400;
    private int actionType;
    private MessageDescription _messageDescription;
    private MessageDescriptionDetail _messageDescriptionDetail;

    public MessageDescriptionDetailDialog(Shell parentShell, AS400 as400, int actionType, MessageDescription _messageDescription) {
        super(parentShell);
        this.as400 = as400;
        this.actionType = actionType;
        this._messageDescription = _messageDescription;
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        _messageDescriptionDetail = new MessageDescriptionDetail(as400, actionType, _messageDescription);
        _messageDescriptionDetail.createContents(container);
        _messageDescriptionDetail.loadSettings(getDialogBoundsSettings());

        return container;
    }

    @Override
    protected void okPressed() {
        if (_messageDescriptionDetail.processButtonPressed()) {
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
        newShell.setText(Messages.Message_description);
    }

    @Override
    public boolean close() {
        _messageDescriptionDetail.saveSettings(getDialogBoundsSettings());
        return super.close();
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        Point point = getShell().computeSize(Size.getSize(600), SWT.DEFAULT, true);
        point.y = point.y + _messageDescriptionDetail.getFieldFormatViewer().getTableHeight(4);
        return point;
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

}
