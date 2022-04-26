/*******************************************************************************
 * javahexeditor, a java hex editor
 * Copyright (C) 2006-2015 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
 * The official javahexeditor site is sourceforge.net/projects/javahexeditor
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.internal.FontHelper;
import biz.isphere.joblogexplorer.Messages;

/**
 * Status line component of the editor. Displays the current position, the
 * insert/overwrite status and the value at the cursor position.
 */
public final class StatusLine {

    public static final String STATUS_LINE_ID = "biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditorActionBarContributor.StatusLine"; //$NON-NLS-1$

    private CLabel labelInfoMessage;
    private CLabel labelNumberOfMessages;

    private StatusLineData data;

    public StatusLine() {

        setData(null);
    }
    
    public void fill(Composite parent) {

        labelNumberOfMessages = addLabel(parent, 200, SWT.DEFAULT);
        addSeparator(parent);
        labelInfoMessage = addLabel(parent, 200, SWT.DEFAULT);

        updateControls();
    }

    public void setData(StatusLineData data) {
        if (data == null) {
            data = new StatusLineData();
        }
        this.data = data;
        updateControls();
    }
    
    private void addSeparator(Composite parent) {

        Label separator = new Label(parent, SWT.SEPARATOR);
        StatusLineLayoutData gridData = new StatusLineLayoutData();
        separator.setLayoutData(gridData);
    }

    private CLabel addLabel(Composite parent, int numChars, int widthHint) {

        CLabel label = new CLabel(parent, SWT.SHADOW_NONE | SWT.LEFT);
        StatusLineLayoutData gridData1 = new StatusLineLayoutData();
        if (widthHint > 0) {
            gridData1.widthHint = widthHint;
        } else {
            gridData1.widthHint = (FontHelper.getFontCharWidth(label) * numChars) + 6;
        }
        label.setLayoutData(gridData1);

        return label;
    }

    private void updateControls() {

        if (data.getNumberOfMessagesSelected() > 0) {
            String message = Messages.bind(Messages.Number_of_messages_B_slash_A, data.getNumberOfMessages(), data.getNumberOfMessagesSelected());
            updateLabel(labelNumberOfMessages, message);
        } else {
            String message = Messages.bind(Messages.Number_of_messages_A, data.getNumberOfMessages());
            updateLabel(labelNumberOfMessages, message);
        }

        updateLabel(labelInfoMessage, data.getMessage());
    }

    private void updateLabel(CLabel label, String value) {

        if (!isOKForUpdate(label)) {
            return;
        }

        if (value != null) {
            label.setText(value);
            label.setToolTipText(value);
        } else {
            label.setText(""); //$NON-NLS-1$
            label.setToolTipText(""); //$NON-NLS-1$
        }
        label.setVisible(true);
    }

    private boolean isOKForUpdate(Control control) {
        return control != null && !control.isDisposed();
    }

    private int getWidthHint(Control control) {
        StatusLineLayoutData layoutData = (StatusLineLayoutData)control.getLayoutData();
        return layoutData.widthHint;
    }
}
