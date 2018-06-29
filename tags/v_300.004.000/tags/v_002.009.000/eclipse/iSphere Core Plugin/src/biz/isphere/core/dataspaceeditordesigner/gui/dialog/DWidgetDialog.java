/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DComment;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;

public class DWidgetDialog extends AbstractDialog {

    private DEditor dEditor;
    private Class<? extends AbstractDWidget> widgetClass;
    private DTemplateWidget widgetTemplate;
    private AbstractDWidget widget;

    private Control textLabel;
    private Text textOffset;
    private Text textLength;
    private Text textFraction;
    private Combo comboHorizontalSpan;

    public DWidgetDialog(Shell parentShell, DEditor dEditor, AbstractDWidget widget) {
        super(parentShell);
        this.dEditor = dEditor;
        this.widgetClass = widget.getClass();
        this.widget = widget;
    }

    public DWidgetDialog(Shell parentShell, DEditor dEditor, Class<? extends AbstractDWidget> widgetClass) {
        super(parentShell);
        this.dEditor = dEditor;
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
        if (DComment.class.equals(widgetClass)) {
            Combo combo = createComboField(parent, Messages.Label_colon, false);
            combo.setItems(new String[] { DComment.SEPARATOR, DComment.NONE });
            combo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateLabel();
                }
            });
            textLabel = combo;
        } else {
            Text text = createTextField(parent, Messages.Label_colon);
            text.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateLabel();
                }
            });
            textLabel = text;
        }

        // Offset
        if (DataSpaceEditorManager.hasOffset(widgetClass)) {
            textOffset = createIntegerField(parent, Messages.Offset_colon);
            textOffset.setText("0"); //$NON-NLS-1$
            textOffset.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateOffset();
                }
            });
        }

        // Length
        if (DataSpaceEditorManager.hasLength(widgetClass)) {
            textLength = createIntegerField(parent, Messages.Length_colon);
            textLength.setText("0"); //$NON-NLS-1$
            textLength.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateLength();
                }
            });
        }

        // Fraction
        if (DataSpaceEditorManager.hasFraction(widgetClass)) {
            textFraction = createIntegerField(parent, Messages.Decimal_positions_colon);
            textFraction.setText("0"); //$NON-NLS-1$
            textFraction.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    validateFraction();
                }
            });
        }

        comboHorizontalSpan = createComboField(parent, Messages.Horizontal_span_colon, true);
        comboHorizontalSpan.setItems(getHorizontalSpanValues());
        comboHorizontalSpan.select(0);
        comboHorizontalSpan.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateHorizontalSpan();
            }
        });

    }

    public DTemplateWidget getWidgetTemplate() {
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

        if (StringHelper.isNullOrEmpty(getLabelText())) {
            setErrorMessage(textLabel, Messages.Label_is_missing_Please_specify_a_label);
            return false;
        }

        clearErrorMessage(textLabel);
        return true;
    }

    private boolean validateOffset() {
        if (!DataSpaceEditorManager.hasOffset(widgetClass)) {
            return true;
        }

        if (StringHelper.isNullOrEmpty(textOffset.getText())) {
            setErrorMessage(textOffset, Messages.Offset_is_missing_Please_specify_an_offset);
            return false;
        }

        if (getIntValue(textOffset) < 0) {
            setErrorMessage(textOffset, Messages.Invalid_offset_Offset_must_be_greater_or_equal_zero);
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
            setErrorMessage(textLength, Messages.Invalid_length_Length_must_be_greater_or_equal_1);
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

    private boolean validateHorizontalSpan() {

        if (StringHelper.isNullOrEmpty(comboHorizontalSpan.getText())) {
            setErrorMessage(comboHorizontalSpan, Messages.Horizontal_span_is_missing_Please_specify_a_span);
            return false;
        }

        if (getIntValue(comboHorizontalSpan) < 0 || getIntValue(comboHorizontalSpan) > dEditor.getColumns()) {
            setErrorMessage(comboHorizontalSpan,
                Messages.bind(Messages.Invalid_horizontal_span_Span_must_be_between_A_and_B, new Object[] { 1, dEditor.getColumns() }));
            return false;
        }

        clearErrorMessage(comboHorizontalSpan);
        return true;
    }

    @Override
    protected void performOKPressed() {

        String label = getLabelText();
        int offset = getIntValue(textOffset);
        int length = getIntValue(textLength);
        int fraction = getIntValue(textFraction);
        int horizontalSpan = getIntValue(comboHorizontalSpan);

        widgetTemplate = new DTemplateWidget(widgetClass, label, offset, length, fraction, horizontalSpan);
    }

    private int getIntValue(Text text) {
        if (text == null) {
            return -1;
        }
        return IntHelper.tryParseInt(text.getText());
    }

    private int getIntValue(Combo combo) {
        if (combo == null) {
            return -1;
        }
        return IntHelper.tryParseInt(combo.getText());
    }

    protected void setInitialValues() {

        if (widget == null) {
            return;
        }

        setLabelText(widget.getLabel());

        if (DataSpaceEditorManager.hasOffset(widgetClass)) {
            textOffset.setText(new Integer(widget.getOffset()).toString());
        }

        if (DataSpaceEditorManager.hasLength(widgetClass)) {
            textLength.setText(new Integer(widget.getLength()).toString());
        }

        comboHorizontalSpan.setText(new Integer(widget.getHorizontalSpan()).toString());
    }

    public void setLabelText(String text) {
        if (textLabel instanceof Combo) {
            ((Combo)textLabel).setText(text);
        } else {
            ((Text)textLabel).setText(text);
        }
    }

    public String getLabelText() {
        if (textLabel instanceof Combo) {
            return ((Combo)textLabel).getText();
        } else {
            return ((Text)textLabel).getText();
        }
    }

    private String[] getHorizontalSpanValues() {
        List<String> values = new ArrayList<String>();

        for (int i = 1; i <= dEditor.getColumns(); i++) {
            values.add(new Integer(i).toString());
        }

        return values.toArray(new String[values.size()]);
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        // Point point = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        return new Point(295, 205);
    }
}
