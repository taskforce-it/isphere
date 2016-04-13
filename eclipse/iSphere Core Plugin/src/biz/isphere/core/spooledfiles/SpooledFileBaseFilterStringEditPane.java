/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SpooledFileBaseFilterStringEditPane {

    private Text userText;
    private Text outqText;
    private Text outqLibText;
    private Text userDataText;
    private Text formTypeText;
    private Text nameText;

    public void createContents(Composite composite_prompts, ModifyListener keyListener, String inputFilterString) {

        Label userLabel = new Label(composite_prompts, SWT.NONE);
        userLabel.setText(Messages.User + ":");
        userText = WidgetFactory.createUpperCaseText(composite_prompts);
        GridData gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        userText.setLayoutData(gd);
        userText.setTextLimit(10);

        Label outqLabel = new Label(composite_prompts, SWT.NONE);
        outqLabel.setText(Messages.Output_queue + ":");
        outqText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        outqText.setLayoutData(gd);
        outqText.setTextLimit(10);

        Label outqLibLabel = new Label(composite_prompts, SWT.NONE);
        outqLibLabel.setText(Messages.___Library + ":");
        outqLibText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        outqLibText.setLayoutData(gd);
        outqLibText.setTextLimit(10);

        Label nameLabel = new Label(composite_prompts, SWT.NONE);
        nameLabel.setText(Messages.Spooled_file_name + ":");
        nameText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 1;
        nameText.setLayoutData(gd);
        nameText.setTextLimit(10);
        Label nameGenericLabel = new Label(composite_prompts, SWT.NONE);
        nameGenericLabel.setText("*GENERIC*");

        Label dtaLabel = new Label(composite_prompts, SWT.NONE);
        dtaLabel.setText(Messages.User_data + ":");
        userDataText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        userDataText.setLayoutData(gd);
        userDataText.setTextLimit(10);

        Label typeLabel = new Label(composite_prompts, SWT.NONE);
        typeLabel.setText(Messages.Form_type + ":");
        formTypeText = WidgetFactory.createUpperCaseText(composite_prompts);
        gd = new GridData();
        gd.widthHint = 75;
        gd.horizontalSpan = 2;
        formTypeText.setLayoutData(gd);
        formTypeText.setTextLimit(10);

        resetFields();
        doInitializeFields(inputFilterString);

        userText.addModifyListener(keyListener);
        outqText.addModifyListener(keyListener);
        outqLibText.addModifyListener(keyListener);
        userDataText.addModifyListener(keyListener);
        formTypeText.addModifyListener(keyListener);
        nameText.addModifyListener(keyListener);

    }

    public Control getInitialFocusControl() {
        return userText;
    }

    public void doInitializeFields(String inputFilterString) {
        if (inputFilterString != null) {
            SpooledFileFilter filter = new SpooledFileFilter(inputFilterString);

            if (filter.getUser() != null) {
                userText.setText(filter.getUser());
            } else {
                userText.setText("*");
            }

            if (filter.getOutputQueue() != null) {
                outqText.setText(filter.getOutputQueue());
            } else {
                outqText.setText("*");
            }

            if (filter.getOutputQueueLibrary() != null) {
                outqLibText.setText(filter.getOutputQueueLibrary());
            } else {
                outqLibText.setText("*");
            }

            if (filter.getUserData() != null) {
                userDataText.setText(filter.getUserData());
            } else {
                userDataText.setText("*");
            }

            if (filter.getFormType() != null) {
                formTypeText.setText(filter.getFormType());
            } else {
                formTypeText.setText("*");
            }

            if (filter.getName() != null) {
                nameText.setText(filter.getName());
            } else {
                nameText.setText("*");
            }
        }
    }

    public void resetFields() {
        userText.setText("*");
        outqText.setText("*");
        outqLibText.setText("*");
        userDataText.setText("*");
        formTypeText.setText("*");
        nameText.setText("*");
    }

    public boolean areFieldsComplete() {
        return true;
    }

    public String getFilterString() {

        SpooledFileFilter filter = new SpooledFileFilter();

        if (isValidFilterValue(userText.getText())) {
            filter.setUser(userText.getText().toUpperCase());
        }

        if (isValidFilterValue(outqText.getText())) {
            filter.setOutputQueue(outqText.getText().toUpperCase());
        }

        if (isValidFilterValue(outqLibText.getText())) {
            filter.setOutputQueueLibrary(outqLibText.getText().toUpperCase());
        }

        if (isValidFilterValue(userDataText.getText())) {
            filter.setUserData(userDataText.getText().toUpperCase());
        }

        if (isValidFilterValue(formTypeText.getText())) {
            filter.setFormType(formTypeText.getText().toUpperCase());
        }

        if (isValidFilterValue(nameText.getText())) {
            filter.setName(nameText.getText().toUpperCase());
        }

        return filter.getFilterString();
    }

    private boolean isValidFilterValue(String text) {
        if ((text != null) && (text.length() > 0) && (!text.equals("*"))) {
            return true;
        }
        return false;
    }
}
