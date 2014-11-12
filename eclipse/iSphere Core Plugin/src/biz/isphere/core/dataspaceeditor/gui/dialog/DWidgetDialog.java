/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditor.model.DDecimal;
import biz.isphere.core.dataspaceeditor.model.DTemplateWidget;
import biz.isphere.core.dataspaceeditor.model.DText;
import biz.isphere.core.dataspaceeditor.model.DataSpaceEditorManager;

public class DWidgetDialog extends XDialog {

    private Class<AbstractDWidget> widgetClass;
    private DTemplateWidget widgetTemplate;

    private Text textLabel;
    private Text textOffset;
    private Text textLength;
    private Text textFraction;

    public DWidgetDialog(Shell parentShell, Class<AbstractDWidget> widgetClass) {
        super(parentShell);
        this.widgetClass = widgetClass;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        String field = DataSpaceEditorManager.getDataType(widgetClass);
        newShell.setText(Messages.bind(Messages.New_0_Field, field));
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        GridData mainAreaLayoutData = new GridData();
        mainAreaLayoutData.horizontalAlignment = SWT.FILL;
        mainAreaLayoutData.grabExcessHorizontalSpace = true;
        mainArea.setLayoutData(mainAreaLayoutData);

        // Label
        Label labelLabel = new Label(mainArea, SWT.NONE);
        labelLabel.setText(Messages.Label_colon);

        textLabel = new Text(mainArea, SWT.BORDER);
        GridData textLabelLayoutData = new GridData();
        textLabelLayoutData.widthHint = 150;
        textLabelLayoutData.horizontalAlignment = SWT.FILL;
        textLabelLayoutData.grabExcessHorizontalSpace = true;
        textLabel.setLayoutData(textLabelLayoutData);

        // Offset
        Label labelOffset = new Label(mainArea, SWT.NONE);
        labelOffset.setText(Messages.Offset_colon);

        textOffset = new Text(mainArea, SWT.BORDER);
        textOffset.addVerifyListener(new NumericOnlyVerifyListener());

        // Length
        if (DText.class.equals(widgetClass) || DDecimal.class.equals(widgetClass)) {
            Label labelLength = new Label(mainArea, SWT.NONE);
            labelLength.setText(Messages.Length_colon);

            textLength = new Text(mainArea, SWT.BORDER);
            textLength.addVerifyListener(new NumericOnlyVerifyListener());

            // Fraction
            if (DDecimal.class.equals(widgetClass)) {
                Label labelFraction = new Label(mainArea, SWT.NONE);
                labelFraction.setText(Messages.Decimal_positions_colon);

                textFraction = new Text(mainArea, SWT.BORDER);
                textFraction.addVerifyListener(new NumericOnlyVerifyListener());
            }
        }

        return dialogArea;
    }

    public DTemplateWidget getWidget() {
        return widgetTemplate;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void okPressed() {
        String label = textLabel.getText();
        int offset = getIntValue(textOffset);
        int length = getIntValue(textLength);
        int fraction = getIntValue(textFraction);
        widgetTemplate = new DTemplateWidget(widgetClass, label, offset, length, fraction);
        super.okPressed();
    }

    private int getIntValue(Text text) {
        if (text == null) {
            return -1;
        }
        return IntHelper.tryParseInt(text.getText());
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
        // Point point = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        return new Point(250, 150);
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
