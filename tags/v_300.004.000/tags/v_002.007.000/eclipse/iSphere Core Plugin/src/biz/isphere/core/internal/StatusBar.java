/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.StringHelper;

public class StatusBar {

    private CLabel positionLabel;
    private CLabel infoLabel;
    private CLabel messageLabel;
    private Composite statusBarArea;

    public StatusBar(Composite aComposite) {
        createStatusBar(aComposite, true);
    }

    public StatusBar(Composite aComposite, boolean addDefaultItems) {
        createStatusBar(aComposite, addDefaultItems);
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

    private void createStatusBar(Composite aParent, boolean addDefaultItems) {

        statusBarArea = new Composite(aParent, SWT.NONE);
        GridLayout statusBarLayout = new GridLayout(0, false);
        statusBarLayout.verticalSpacing = 1;
        statusBarLayout.horizontalSpacing = 1;
        statusBarLayout.marginWidth = 0;
        statusBarLayout.marginHeight = 0;
        statusBarArea.setLayout(statusBarLayout);

        GridData statusBarGridData = new GridData();
        statusBarGridData.horizontalAlignment = GridData.FILL;
        statusBarGridData.grabExcessHorizontalSpace = true;
        statusBarGridData.verticalAlignment = GridData.CENTER;
        statusBarGridData.horizontalSpan = 2;
        statusBarArea.setLayoutData(statusBarGridData);

        if (!addDefaultItems) {
            return;
        }

        positionLabel = createStatusBarLabel(statusBarArea, "", 50, SWT.CENTER);

        infoLabel = createStatusBarLabel(statusBarArea, "", 100);

        messageLabel = createStatusBarLabel(statusBarArea, "", -1);

    }

    public CLabel createStatusBarLabel(String text) {
        return createStatusBarLabel(statusBarArea, text, -1, SWT.LEFT);
    }

    public CLabel createStatusBarLabel(String text, int aWidthHint) {
        return createStatusBarLabel(statusBarArea, text, aWidthHint, SWT.LEFT);
    }

    public CLabel createStatusBarLabel(String text, int aWidthHint, int alignment) {
        return createStatusBarLabel(statusBarArea, text, aWidthHint, alignment);
    }

    public CLabel createStatusBarImage(Image image) {
        return createStatusBarImage(statusBarArea, image);
    }

    private CLabel createStatusBarLabel(Composite aStatusBar, String text, int aWidthHint) {
        return createStatusBarLabel(aStatusBar, text, aWidthHint, SWT.LEFT);
    }

    private CLabel createStatusBarLabel(Composite aStatusBar, String text, int aWidthHint, int alignment) {

        CLabel label = new CLabel(aStatusBar, SWT.BORDER);
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
        label.setAlignment(alignment);
        label.setText(text);
//        label.setLeftMargin(5);

        GridLayout layout = (GridLayout)statusBarArea.getLayout();
        layout.numColumns++;

        return label;
    }

    private CLabel createStatusBarImage(Composite aStatusBar, Image image) {

        CLabel label = new CLabel(aStatusBar, SWT.BORDER);
        GridData labelGridData = new GridData();
        labelGridData.verticalAlignment = GridData.CENTER;

        label.setLayoutData(labelGridData);
        label.setImage(image);
//        label.setLeftMargin(3);
//        label.setRightMargin(3);

        GridLayout layout = (GridLayout)statusBarArea.getLayout();
        layout.numColumns++;

        return label;
    }

}
