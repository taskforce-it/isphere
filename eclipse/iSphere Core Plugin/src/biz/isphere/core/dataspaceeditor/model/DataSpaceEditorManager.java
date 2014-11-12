/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.model;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.DE;
import biz.isphere.core.dataspaceeditor.gui.designer.ControlPayload;
import biz.isphere.core.internal.Validator;

/**
 * This class manages the data model of the 'space object editors'. it is used
 * to create controls and maintain the model, such as adding or replacing
 * {@link AbstractDWidget}(s) to a {@link DEditor}.
 * <p>
 */
public final class DataSpaceEditorManager {

    public DataSpaceEditorManager() {
    }

    public static String getDataType(Class<? extends AbstractDWidget> widget) {
        String dataType;
        if (widget == DBoolean.class) {
            dataType = Messages.Data_type_Boolean;
        } else if (widget == DDecimal.class) {
            dataType = Messages.Data_type_Decimal;
        } else if (widget == DLongInteger.class) {
            dataType = Messages.Data_type_Integer_8_byte;
        } else if (widget == DInteger.class) {
            dataType = Messages.Data_type_Integer_4_byte;
        } else if (widget == DShortInteger.class) {
            dataType = Messages.Data_type_Integer_2_byte;
        } else if (widget == DTinyInteger.class) {
            dataType = Messages.Data_type_Integer_1_byte;
        } else if (widget == DText.class) {
            dataType = Messages.Data_type_Text;
        } else {
            throw new IllegalArgumentException("Illegal widget passed: " + widget.getClass().getName());
        }

        return dataType;
    }

    public Composite createDialogArea(Composite parent, int numColumns) {
        Composite dialogArea = new Composite(parent, SWT.NONE);
        GridLayout dialogEditorAreaLayout = new GridLayout(numColumns, false);
        dialogEditorAreaLayout.marginHeight = 15;
        dialogEditorAreaLayout.marginWidth = 15;
        dialogEditorAreaLayout.horizontalSpacing = 30;
        dialogEditorAreaLayout.verticalSpacing = 5;
        dialogArea.setLayout(dialogEditorAreaLayout);
        GridData dialogAreaLayoutData = new GridData();
        dialogAreaLayoutData.horizontalAlignment = SWT.FILL;
        dialogAreaLayoutData.verticalAlignment = SWT.FILL;
        dialogAreaLayoutData.grabExcessHorizontalSpace = true;
        dialogAreaLayoutData.grabExcessVerticalSpace = true;
        dialogArea.setLayoutData(dialogAreaLayoutData);
        return dialogArea;
    }

    public Control createReadOnlyWidgetControlAndAddToParent(Composite parent, AbstractDWidget widget) {
        Control control = createWidgetControlAndAddToParent(parent, widget);
        if (control instanceof Text) {
            ((Text)control).setEditable(false);
        } else {
            control.setEnabled(false);
        }
        return control;
    }

    public Control createWidgetControlAndAddToParent(Composite parent, AbstractDWidget widget) {
        Control control = null;
        if (widget instanceof DBoolean) {
            control = createBooleanWidget(parent, widget);
        } else if (widget instanceof DDecimal) {
            control = createDecimalWidget(parent, widget);
        } else if (widget instanceof AbstractDInteger) {
            control = createIntegerWidget(parent, widget);
        } else if (widget instanceof DText) {
            control = createTextWidget(parent, widget);
        }
        if (control != null) {
            setPayload(control, widget);
            createControlInfo(control, widget);
        }

        return control;
    }

    private void createControlInfo(Control control, AbstractDWidget widget) {

        String dataType = getDataType(widget.getClass());

        String tooltip = Messages.bind(Messages.Tooltip_0_data_at_offset_1_length_2,
            new Object[] { dataType, widget.getOffset(), widget.getLength() });

        control.setToolTipText(tooltip);
    }

    public void addReferencedObject(DEditor dEditor, DReferencedObject referencedObject) {
        dEditor.addReferencedByObject(referencedObject);
    }

    public DEditor detachFromParent(DReferencedObject referencedObject) {
        DEditor dEditor = referencedObject.getParent();
        dEditor.removeReferencedByObject(referencedObject);
        return dEditor;
    }

    public void resolveObjectReferences(DEditor[] dEditors) {
        for (DEditor dEditor : dEditors) {
            DReferencedObject[] referencedObjects = dEditor.getReferencedObjects();
            for (DReferencedObject referencedObject : referencedObjects) {
                referencedObject.setParent(dEditor);
            }
        }
    }

    public void addWidgetToEditor(DEditor dEditor, AbstractDWidget widget) {
        dEditor.addWidget(widget);
    }

    public void removeWidgetFromEditor(DEditor dEditor, AbstractDWidget widget) {
        dEditor.removeWidget(widget);
    }

    public boolean validate(Control[] controls) {
        for (Control control : controls) {
            if (getPayloadFromControl(control) != null) {
                AbstractDWidget widget = getWidgetFromControl(control);

                if (widget instanceof DBoolean) {
                    return validateBoolean(getButtonControl(control));
                } else if (widget instanceof DDecimal) {
                    return validateDecimal(getTextControl(control));
                } else if (widget instanceof DLongInteger) {
                    return validateLongInteger(getTextControl(control));
                } else if (widget instanceof DInteger) {
                    return validateInteger(getTextControl(control));
                } else if (widget instanceof DShortInteger) {
                    return validateShort(getTextControl(control));
                } else if (widget instanceof DTinyInteger) {
                    return validateTiny(getTextControl(control));
                } else if (widget instanceof DText) {
                    return validateText(getTextControl(control));
                }
            }
        }
        return true;
    }

    public DEditor createDialogFromTemplate(DTemplateEditor template) {
        DEditor dDialog = new DEditor(template.getName(), template.getDescription(), template.getColumns());
        return dDialog;
    }

    public AbstractDWidget createWidgetFromTemplate(DTemplateWidget template) {

        Class<AbstractDWidget> widgetClass = template.getWidgetClass();
        String label = template.getLabel();
        int offset = template.getOffset();

        // Widget constructor: label, offset
        if (DBoolean.class.equals(widgetClass)) {
            return new DBoolean(template.getLabel(), template.getOffset());
        } else if (DLongInteger.class.equals(widgetClass)) {
            return new DLongInteger(template.getLabel(), template.getOffset());
        } else if (DInteger.class.equals(widgetClass)) {
            return new DInteger(template.getLabel(), template.getOffset());
        } else if (DShortInteger.class.equals(widgetClass)) {
            return new DShortInteger(template.getLabel(), template.getOffset());
        } else if (DTinyInteger.class.equals(widgetClass)) {
            return new DTinyInteger(template.getLabel(), template.getOffset());
        } else {
            // Widget constructor: label, offset, length
            int length = template.getLength();
            if (DText.class.equals(widgetClass)) {
                return new DText(label, offset, length);
            } else {
                // Widget constructor: label, offset, length, fraction
                int fraction = template.getFraction();
                if (DDecimal.class.equals(widgetClass)) {
                    return new DDecimal(label, offset, length, fraction);
                }
            }
        }
        throw new IllegalArgumentException("Illegal widget class: " + template.getClass());
    }

    public DReferencedObject createReferencedObjectFromTemplate(DTemplateReferencedObject template) {
        DReferencedObject referencedObject = new DReferencedObject(template.getName(), template.getLibrary(), template.getType());
        return referencedObject;
    }

    public ControlPayload getPayloadFromControl(Control control) {
        return (ControlPayload)control.getData(DE.KEY_PAYLOAD);
    }

    private boolean validateBoolean(Button button) {
        return true;
    }

    private boolean validateDecimal(Text text) {
        Validator validator = getDecimalValidator(text);
        return validator.validate(text.getText());
    }

    private boolean validateLongInteger(Text text) {
        try {
            Long.parseLong(text.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateInteger(Text text) {
        try {
            Integer.parseInt(text.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateShort(Text text) {
        try {
            Short.parseShort(text.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateTiny(Text text) {
        try {
            Byte.parseByte(text.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateText(Text text) {
        return true;
    }

    private Button getButtonControl(Control control) {
        return ((Button)control);
    }

    private Text getTextControl(Control control) {
        return ((Text)control);
    }

    private Validator getDecimalValidator(Control control) {
        AbstractDWidget widget = getWidgetFromControl(control);
        if (!(widget instanceof DDecimal)) {
            return null;
        }

        DDecimal dDecimal = (DDecimal)widget;
        Validator validator = Validator.getDecInstance();
        validator.setLength(dDecimal.getLength());
        validator.setPrecision(dDecimal.getFraction());

        return validator;
    }

    private void setPayload(Control control, AbstractDWidget widget) {
        control.setData(DE.KEY_PAYLOAD, new ControlPayload(widget, control));
    }

    public AbstractDWidget getWidgetFromControl(Control control) {
        ControlPayload payload = getPayloadFromControl(control);
        AbstractDWidget widget = payload.getWidget();
        return widget;
    }

    public void setControlValue(Control control, DDataSpaceValue value) {
        ControlPayload payload = getPayloadFromControl(control);
        if (payload == null) {
            return;
        }

        AbstractDWidget widget = payload.getWidget();

        if (widget instanceof DText) {
            String textValue = value.getString(widget.getOffset(), widget.getLength());
            ((Text)control).setText(textValue);
        } else if (widget instanceof DBoolean) {
            Boolean boolValue = value.getBoolean(widget.getOffset(), widget.getLength());
            ((Button)control).setSelection(boolValue);
        } else if (widget instanceof DTinyInteger) {
            Byte tinyValue = value.getTiny(widget.getOffset(), widget.getLength());
            ((Text)control).setText(tinyValue.toString());
        } else if (widget instanceof DShortInteger) {
            Short shortValue = value.getShort(widget.getOffset(), widget.getLength());
            ((Text)control).setText(shortValue.toString());
        } else if (widget instanceof DInteger) {
            Integer intValue = value.getInteger(widget.getOffset(), widget.getLength());
            ((Text)control).setText(intValue.toString());
        } else if (widget instanceof DLongInteger) {
            Long longValue = value.getLongInteger(widget.getOffset(), widget.getLength());
            ((Text)control).setText(longValue.toString());
        } else if (widget instanceof DDecimal) {
            BigDecimal decimalValue = value.getDecimal(widget.getOffset(), widget.getLength(), ((DDecimal)widget).getFraction());
            ((Text)control).setText(decimalValue.toString());
        }
    }

    private Button createBooleanWidget(Composite parent, AbstractDWidget widget) {

        createLabel(parent, widget);

        Button checkBox = new Button(parent, SWT.CHECK);
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        checkBox.setLayoutData(layoutData);
        return checkBox;
    }

    private Text createDecimalWidget(Composite parent, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = new Text(parent, SWT.BORDER);
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        text.setLayoutData(layoutData);
        text.addVerifyListener(new NumericOnlyVerifyListener(true));
        return text;
    }

    private Text createIntegerWidget(Composite parent, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = new Text(parent, SWT.BORDER);
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        text.setLayoutData(layoutData);
        text.addVerifyListener(new NumericOnlyVerifyListener());
        return text;
    }

    private Text createTextWidget(Composite parent, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = new Text(parent, SWT.BORDER);
        text.setTextLimit(widget.getLength());
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        text.setLayoutData(layoutData);

        return text;
    }


    private void createLabel(Composite parent, AbstractDWidget widget) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(widget.getLabel());
    }

}
