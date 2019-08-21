/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse.internal;

import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.ui.SystemWidgetHelpers;
import org.eclipse.rse.ui.filters.SystemFilterStringEditPane;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.messagesubsystem.rse.Messages;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilterStringEditPaneDelegate;

public class QueuedMessageFilterStringEditPane extends SystemFilterStringEditPane {

    private QueuedMessageFilterStringEditPaneDelegate delegate;

    public QueuedMessageFilterStringEditPane(Shell shell) {
        super(shell);

        delegate = new QueuedMessageFilterStringEditPaneDelegate();
    }

    @Override
    public Control createContents(Composite parent) {

        int nbrColumns = 2;
        Composite composite_prompts = SystemWidgetHelpers.createComposite(parent, nbrColumns);

        delegate.createContents(composite_prompts);

        resetFields();
        doInitializeFields();

        ModifyListener keyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateStringInput();
            }
        };

        delegate.addModifyListener(keyListener);

        return composite_prompts;
    }

    @Override
    public Control getInitialFocusControl() {
        return delegate.getInitialFocusControl();
    }

    @Override
    protected void doInitializeFields() {
        delegate.doInitializeFields(getInputFilterString());
    }

    @Override
    protected void resetFields() {
        delegate.resetFields();
    }

    @Override
    protected boolean areFieldsComplete() {
        return delegate.areFieldsComplete();
    }

    @Override
    public String getFilterString() {
        return delegate.getFilterString();
    }

    @Override
    public SystemMessage verify() {
        if (!areFieldsComplete())
            return SystemMessageDialog.getExceptionMessage(Display.getCurrent().getActiveShell(), new Exception(
                Messages.Message_queue_and_library_must_be_specified));
        return null;
    }

}