/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.spooledfiles.SpooledFileBaseFilterStringEditPane;

import com.ibm.etools.systems.core.messages.SystemMessage;
import com.ibm.etools.systems.core.ui.SystemWidgetHelpers;
import com.ibm.etools.systems.filters.ui.SystemFilterStringEditPane;

public class SpooledFileFilterStringEditPane extends SystemFilterStringEditPane {

    private SpooledFileBaseFilterStringEditPane base = new SpooledFileBaseFilterStringEditPane();

    public SpooledFileFilterStringEditPane(Shell shell) {
        super(shell);
    }

    @Override
    public Control createContents(Composite parent) {

        int nbrColumns = 3;
        Composite composite_prompts = SystemWidgetHelpers.createComposite(parent, nbrColumns);
        ((GridLayout)composite_prompts.getLayout()).marginWidth = 0;

        ModifyListener keyListener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateStringInput();
            }
        };

        base.createContents(composite_prompts, keyListener, inputFilterString);

        return composite_prompts;

    }

    @Override
    public Control getInitialFocusControl() {
        return base.getInitialFocusControl();
    }

    @Override
    protected void doInitializeFields() {
        base.doInitializeFields(inputFilterString);
    }

    @Override
    protected void resetFields() {
        base.resetFields();
    }

    @Override
    protected boolean areFieldsComplete() {
        return base.areFieldsComplete();
    }

    @Override
    public String getFilterString() {
        return base.getFilterString();
    }

    @Override
    public SystemMessage verify() {
        return null;
    }

    @Override
    protected SystemMessage validateStringInput() {

        SystemMessage systemMessage = super.validateStringInput();
        if (systemMessage != null) {
            return systemMessage;
        }

        String errorText = base.validateInput();
        if (errorText == null) {
            return null;
        }

        try {
            systemMessage = new SystemMessage("", "", "", SystemMessage.ERROR, errorText, "");
            fireChangeEvent(systemMessage);
        } catch (Throwable e) {
            // e.printStackTrace();
        }

        return systemMessage;
    }

}
