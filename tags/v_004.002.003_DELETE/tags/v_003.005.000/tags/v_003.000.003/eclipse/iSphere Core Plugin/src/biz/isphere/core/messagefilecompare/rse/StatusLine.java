/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.FontHelper;

/**
 * Status line component of the editor. Displays the current position, the
 * insert/overwrite status and the value at the cursor position.
 */
public final class StatusLine {

    public static final String STATUS_LINE_ID = "biz.isphere.core.messagefilecompare.rse.AbstractMessageFileCompareEditorActionBarContributor.StatusLine";

    private CLabel messageLabel;
    private CLabel numItemsLabel;

    private int messageWidthHint = -1;
    private int numItemsWidthHint = -1;

    boolean showMessage = true;
    boolean showNumItems = true;

    private int numItems;
    private String message = "";

    public void fill(Composite parent) {

        if (showNumItems) {
            addSeparator(parent);
            numItemsLabel = addLabel(parent, 5, numItemsWidthHint);
            numItemsWidthHint = getWidthHint(numItemsLabel);
            numItemsLabel.setAlignment(SWT.CENTER);
            numItemsLabel.setText("55");
            addImageLabel(parent, ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_FILTERED_ITEMS).createImage());
        }

        if (showMessage) {
            addSeparator(parent);
            messageLabel = addLabel(parent, 250, messageWidthHint);
            messageWidthHint = getWidthHint(messageLabel);
            messageLabel.setText("foo");
        }

        updateControls();
    }

    public void setShowNumItems(boolean show) {
        showNumItems = show;
    }

    public void setShowMessage(boolean show) {
        showMessage = show;
    }

    public void setMessage(String message) {
        this.message = message;
        updateControls();
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
        updateControls();
    }

    private void addSeparator(Composite parent) {

        Label separator = new Label(parent, SWT.SEPARATOR);
        StatusLineLayoutData layoutData = new StatusLineLayoutData();
        separator.setLayoutData(layoutData);
    }

    private CLabel addLabel(Composite parent, int numChars, int widthHint) {

        CLabel label = new CLabel(parent, SWT.SHADOW_NONE);
        StatusLineLayoutData layoutData = new StatusLineLayoutData();
        if (widthHint > 0) {
            layoutData.widthHint = widthHint;
        } else {
            layoutData.widthHint = (FontHelper.getFontCharWidth(label) * numChars) + 6;
        }
        label.setLayoutData(layoutData);

        return label;
    }

    private CLabel addImageLabel(Composite aStatusBar, Image image) {

        CLabel label = new CLabel(aStatusBar, SWT.SHADOW_NONE);
        label.setImage(image);

        return label;
    }

    private void updateControls() {

        if (isOKForUpdate(messageLabel)) {
            if (message != null) {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            } else {
                messageLabel.setText(""); //$NON-NLS-1$
                messageLabel.setToolTipText(""); //$NON-NLS-1$
            }
            messageLabel.setVisible(showMessage);
        }

        if (isOKForUpdate(numItemsLabel)) {
            numItemsLabel.setText(formatDecimal(numItems));
            numItemsLabel.setVisible(showNumItems);
        }
    }

    private boolean isOKForUpdate(Control control) {
        return control != null && !control.isDisposed();
    }

    private String formatDecimal(int value) {
        return getDecimalValue(value);
    }

    private String getDecimalValue(int value) {
        return Integer.toString(value);
    }

    private int getWidthHint(Control control) {
        StatusLineLayoutData layoutData = (StatusLineLayoutData)control.getLayoutData();
        return layoutData.widthHint;
    }
}
