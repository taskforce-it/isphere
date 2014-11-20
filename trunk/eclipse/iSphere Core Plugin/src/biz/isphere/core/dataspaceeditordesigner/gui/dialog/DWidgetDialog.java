/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;

public class DWidgetDialog extends AbstractDialog {

    private Class<? extends AbstractDWidget> widgetClass;
    private DTemplateWidget widgetTemplate;
    private AbstractDWidget widget;

    private Text textLabel;
    private Text textOffset;
    private Text textLength;
    private Text textFraction;

    public DWidgetDialog(Shell parentShell, AbstractDWidget widget) {
        super(parentShell);
        this.widgetClass = widget.getClass();
        this.widget = widget;
    }

    public DWidgetDialog(Shell parentShell, Class<? extends AbstractDWidget> widgetClass) {
        super(parentShell);
        this.widgetClass = widgetClass;
        this.widget = null;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        String field = DataSpaceEditorManager.getDataType(widgetClass);
        if (widget == null) {
            newShell.setText(Messages.bind(Messages.New_0_Field, field));
        } else {
            newShell.setText(Messages.bind(Messages.Change_0_Field, field));
        }
    }

    @Override
    protected void createContent(Composite parent) {

        // Label
        textLabel = createTextField(parent, Messages.Label_colon);
        textLabel.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateLabel();
            }
        });

        // Offset
        textOffset = createNumericField(parent, Messages.Offset_colon);
        textOffset.setText("0"); //$NON-NLS-1$
        textOffset.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateOffset();
            }
        });

        // Length
        if (DataSpaceEditorManager.hasLength(widgetClass)) {
            textLength = createNumericField(parent, Messages.Length_colon);
            textLength.setText("0"); //$NON-NLS-1$
            textLength.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateLength();
                }
            });
        }

        // Fraction
        if (DataSpaceEditorManager.hasFraction(widgetClass)) {
            textFraction = createNumericField(parent, Messages.Decimal_positions_colon);
            textFraction.setText("0"); //$NON-NLS-1$
            textFraction.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateFraction();
                }
            });
        }
    }

    public DTemplateWidget getWidget() {
        return widgetTemplate;
    }

    @Override
    protected boolean validate() {

        // Label
        if (!validateLabel()) {
            return false;
        }

        // Offset
        if (!validateOffset()) {
            return false;
        }

        // Offset
        if (!validateLength()) {
            return false;
        }

        // Offset
        if (!validateFraction()) {
            return false;
        }

        return true;
    }

    private boolean validateLabel() {

        if (StringHelper.isNullOrEmpty(textLabel.getText())) {
            setErrorMessage(textLabel, "Label is missing. Please specify a label.");
            return false;
        }

        clearErrorMessage(textLabel);
        return true;
    }

    private boolean validateOffset() {

        if (StringHelper.isNullOrEmpty(textOffset.getText())) {
            setErrorMessage(textOffset, "Offset is missing. Please specify an offset.");
            return false;
        }

        if (getIntValue(textOffset) < 0) {
            setErrorMessage(textOffset, "Invalid offset. Offset must be greater or equal zero.");
            return false;
        }

        clearErrorMessage(textOffset);
        return true;
    }

    private boolean validateLength() {
        if (!DataSpaceEditorManager.hasLength(widgetClass)) {
            return true;
        }

        if (getIntValue(textLength) <= 0) {
            setErrorMessage(textLength, "Invalid Length. Length must be greater or equal 1.");
            return false;
        }

        clearErrorMessage(textLength);
        return true;
    }

    private boolean validateFraction() {
        if (!DataSpaceEditorManager.hasFraction(widgetClass)) {
            return true;
        }

        clearErrorMessage(textFraction);
        return true;
    }

    @Override
    protected void performOKPressed() {

        String label = textLabel.getText();
        int offset = getIntValue(textOffset);
        int length = getIntValue(textLength);
        int fraction = getIntValue(textFraction);

        widgetTemplate = new DTemplateWidget(widgetClass, label, offset, length, fraction);
    }

    private int getIntValue(Text text) {
        if (text == null) {
            return -1;
        }
        return IntHelper.tryParseInt(text.getText());
    }

    protected void setInitialValues() {

        if (widget == null) {
            return;
        }

        textLabel.setText(widget.getLabel());
        textOffset.setText(new Integer(widget.getOffset()).toString());
        textLength.setText(new Integer(widget.getLength()).toString());
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        // Point point = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        return new Point(280, 180);
    }
}
