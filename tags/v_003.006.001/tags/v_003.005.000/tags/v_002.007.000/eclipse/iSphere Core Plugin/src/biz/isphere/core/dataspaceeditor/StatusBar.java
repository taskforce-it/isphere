/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.base.internal.StringHelper;

public class StatusBar {

    private Label positionLabel;
    private Label infoLabel;
    private Label messageLabel;

    public StatusBar(Composite aComposite) {
        createStatusBar(aComposite);
    }

    public void setPosition(int aColumn) {
        positionLabel.setText(String.valueOf(aColumn));
    }

    public void setPosition(int aRow, int aColumn) {
        positionLabel.setText(aRow + " : " + aColumn); //$NON-NLS-1$
    }

    public void setInfo(String anInfo) {
        infoLabel.setText(" " + anInfo); //$NON-NLS-1$
    }

    public void setMessage(String aMessage) {
        if (StringHelper.isNullOrEmpty(aMessage)) {
            messageLabel.setText(""); //$NON-NLS-1$
            return;
        }
        messageLabel.setText(" " + aMessage); //$NON-NLS-1$
    }

    private void createStatusBar(Composite aParent) {

        Composite statusBar = new Composite(aParent, SWT.NONE);
        GridLayout statusBarLayout = new GridLayout(3, false);
        statusBarLayout.verticalSpacing = 1;
        statusBarLayout.horizontalSpacing = 1;
        statusBarLayout.marginWidth = 0;
        statusBarLayout.marginHeight = 0;
        statusBar.setLayout(statusBarLayout);

        GridData statusBarGridData = new GridData();
        statusBarGridData.horizontalAlignment = GridData.FILL;
        statusBarGridData.grabExcessHorizontalSpace = true;
        statusBarGridData.verticalAlignment = GridData.CENTER;
        statusBarGridData.horizontalSpan = 2;
        statusBar.setLayoutData(statusBarGridData);

        positionLabel = createStatusBarLabel(statusBar, 50);
        positionLabel.setAlignment(SWT.CENTER);
        positionLabel.setText(""); //$NON-NLS-1$

        infoLabel = createStatusBarLabel(statusBar, 100);
        infoLabel.setText(""); //$NON-NLS-1$

        messageLabel = createStatusBarLabel(statusBar, -1);
        messageLabel.setText(""); //$NON-NLS-1$

    }

    private Label createStatusBarLabel(Composite aStatusBar, int aWidthHint) {
        Label label = new Label(aStatusBar, SWT.BORDER);
        GridData labelGridData = new GridData();
        labelGridData.verticalAlignment = GridData.CENTER;
        if (aWidthHint == -1) {
            labelGridData.grabExcessHorizontalSpace = true;
            labelGridData.horizontalAlignment = GridData.FILL;
        } else {
            labelGridData.widthHint = aWidthHint;
            labelGridData.horizontalAlignment = GridData.BEGINNING;
        }
        label.setLayoutData(labelGridData);
        return label;
    }

}
