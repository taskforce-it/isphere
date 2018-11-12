/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.IOException;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;

public class SignOn {

    private Text textHost;
    private Text textUser;
    private Text textPassword;
    private StatusLineManager statusLineManager;
    private AS400 as400;

    public SignOn() {
        as400 = null;
    }

    public void createContents(Composite parent, String aHostName) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        final Composite compositeGeneral = new Composite(container, SWT.NONE);
        compositeGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayoutCompositeGeneral = new GridLayout();
        gridLayoutCompositeGeneral.numColumns = 2;
        compositeGeneral.setLayout(gridLayoutCompositeGeneral);

        final Label labelHost = new Label(compositeGeneral, SWT.NONE);
        labelHost.setText(Messages.Host_colon);

        textHost = WidgetFactory.createText(compositeGeneral);
        textHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textHost.setText(aHostName);

        final Label labelUser = new Label(compositeGeneral, SWT.NONE);
        labelUser.setText(Messages.User_colon);

        textUser = WidgetFactory.createText(compositeGeneral);
        textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textUser.setText("");

        final Label labelPassword = new Label(compositeGeneral, SWT.NONE);
        labelPassword.setText(Messages.Password_colon);

        textPassword = WidgetFactory.createPassword(compositeGeneral);
        textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPassword.setText("");

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
        statusLine.setLayoutData(gridDataStatusLine);

        if (StringHelper.isNullOrEmpty(textHost.getText())) {
            textHost.setFocus();
        } else if (StringHelper.isNullOrEmpty(textUser.getText())) {
            textUser.setFocus();
        } else if (StringHelper.isNullOrEmpty(textPassword.getText())) {
            textPassword.setFocus();
        }

    }

    protected void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
        } else {
            statusLineManager.setErrorMessage(null, null);
        }
    }

    public boolean processButtonPressed() {

        textHost.getText().trim();
        textUser.getText().trim();
        textPassword.getText().trim();

        if (textHost.getText().equals("")) {
            setErrorMessage(Messages.Enter_a_host);
            textHost.setFocus();
            return false;
        }

        if (textUser.getText().equals("")) {
            setErrorMessage(Messages.Enter_a_user);
            textUser.setFocus();
            return false;
        }

        if (textPassword.getText().equals("")) {
            setErrorMessage(Messages.Enter_a_password);
            textPassword.setFocus();
            return false;
        }

        as400 = new AS400(textHost.getText(), textUser.getText(), textPassword.getText());
        try {
            as400.validateSignon();
        } catch (AS400SecurityException e) {
            setErrorMessage(e.getMessage());
            textHost.setFocus();
            return false;
        } catch (IOException e) {
            setErrorMessage(e.getMessage());
            textHost.setFocus();
            return false;
        }
        return true;
    }

    public AS400 getAS400() {
        return as400;
    }

}
