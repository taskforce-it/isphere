/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import java.math.BigDecimal;
import java.util.EventListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.swt.widgets.ButtonLockedListener;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.dataspaceeditordesigner.gui.designer.ControlPayload;
import biz.isphere.core.dataspaceeditordesigner.listener.AdapterButtonModifiedListener;
import biz.isphere.core.dataspaceeditordesigner.listener.AdapterTextModifiedListener;
import biz.isphere.core.dataspaceeditordesigner.listener.IWidgetModifyListener;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;

/**
 * This class manages the data model of the 'space object editors'. it is used
 * to create controls and maintain the model, such as adding or replacing
 * {@link AbstractDWidget}(s) to a {@link DEditor}.
 * <p>
 */
public final class DataSpaceEditorManager {

    public static final String GENERATED = "*GENERATED";

    private static final String DATA_KEY_DIALOG_AREA = "DATA_KEY_DIALOG_AREA";

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
        } else if (widget == DComment.class) {
            dataType = Messages.Data_type_Comment;
        } else {
            throw new IllegalArgumentException("Illegal widget passed: " + widget.getClass().getName());
        }

        return dataType;
    }

    public Composite createDialogArea(Composite parent, DEditor dEditor, int columnsPerEditorColumn) {

        int numColumns;
        boolean makeColumnsEqual;
        if (dEditor == null) {
            numColumns = columnsPerEditorColumn;
            makeColumnsEqual = false;
        } else {
            numColumns = dEditor.getColumns() * columnsPerEditorColumn;
            makeColumnsEqual = dEditor.isColumnsEqualWidth();
        }

        Composite dialogArea = new Composite(parent, SWT.NONE);
        GridLayout dialogEditorAreaLayout = new GridLayout(numColumns, makeColumnsEqual);
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

        dialogArea.setData(DATA_KEY_DIALOG_AREA, DATA_KEY_DIALOG_AREA);
        
        return dialogArea;
    }

    public boolean isDialogArea(Composite composite) {
        
        if (composite.getData(DATA_KEY_DIALOG_AREA) != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public Control createReadOnlyWidgetControlAndAddToParent(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        Control control = createWidgetControlAndAddToParent(parent, columnsPerEditorColumn, widget);
        ControlPayload payload = getPayloadFromControl(control);
        EventListener listener = null;

        if (control instanceof Text) {
            ((Text)control).setEditable(false);
        } else if (control instanceof Button) {
            Button button = (Button)control;
            button.setEnabled(true);
            // button.setGrayed(true); available with 3.4 :-(
            listener = new ButtonLockedListener();
            button.addSelectionListener((ButtonLockedListener)listener);
        } else {
            control.setEnabled(false);
        }

        payload.setLocked(true, listener);

        return control;
    }

    public Control createWidgetControlAndAddToParent(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        Control control = null;
        if (widget instanceof DBoolean) {
            control = createBooleanWidget(parent, columnsPerEditorColumn, widget);
        } else if (widget instanceof DDecimal) {
            control = createDecimalWidget(parent, columnsPerEditorColumn, widget);
        } else if (widget instanceof AbstractDInteger) {
            control = createIntegerWidget(parent, columnsPerEditorColumn, widget);
        } else if (widget instanceof DText) {
            control = createTextWidget(parent, columnsPerEditorColumn, widget);
        } else if (widget instanceof DComment) {
            control = createCommentWidget(parent, columnsPerEditorColumn, widget);
        }

        if (control != null) {
            setPayload(control, widget);
            createControlInfo(control, widget);
        }

        return control;
    }

    private void createControlInfo(Control control, AbstractDWidget widget) {

        String dataType = getDataType(widget.getClass());

        String tooltip;
        if (widget instanceof DComment) {
            tooltip = Messages.Data_type_Comment;
        } else {
            tooltip = Messages.bind(Messages.Tooltip_0_data_at_offset_1_length_2, new Object[] { dataType, widget.getOffset(), widget.getLength() });
        }

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

    public void renameEditor(DEditor dEditor, String name) {
        dEditor.setName(name);
    }

    public void changeEditorDescription(DEditor dEditor, String description) {
        dEditor.setDescription(description);
    }

    public void changeEditorColumns(DEditor dEditor, int columns) {
        int b4Columns = dEditor.getColumns();
        dEditor.setColumns(columns);
        AbstractDWidget[] widgets = dEditor.getWidgets();
        for (AbstractDWidget widget : widgets) {
            float div = (float)widget.getHorizontalSpan() * columns / b4Columns;
            widget.setHorizontalSpan((int)div);
        }
    }

    public void resolveObjectReferences(DEditor[] dEditors) {
        for (DEditor dEditor : dEditors) {
            DReferencedObject[] referencedObjects = dEditor.getReferencedObjects();
            for (DReferencedObject referencedObject : referencedObjects) {
                referencedObject.setParent(dEditor);
            }

            AbstractDWidget[] dWidgets = dEditor.getWidgets();
            for (AbstractDWidget dWidget : dWidgets) {
                dWidget.setParent(dEditor);
            }
        }
    }

    public void addWidgetToEditor(DEditor dEditor, AbstractDWidget widget) {
        dEditor.addWidget(widget);
    }

    public void changeWidget(AbstractDWidget widget, DTemplateWidget changes) {

        widget.setLabel(changes.getLabel());
        widget.setOffset(changes.getOffset());
        widget.setLength(changes.getLength());
        widget.setHorizontalSpan(changes.getHorizontalSpan());
    }

    public void addControlModifyListener(Control control, IWidgetModifyListener modifyListener) {
        if (control instanceof Text) {
            Text text = (Text)control;
            text.addModifyListener(new AdapterTextModifiedListener(modifyListener));
        } else if (control instanceof Button) {
            Button button = (Button)control;
            button.addSelectionListener(new AdapterButtonModifiedListener(modifyListener));
        }
    }

    public void removeWidgetFromEditor(DEditor dEditor, AbstractDWidget widget) {
        dEditor.removeWidget(widget);
    }

    public void moveUpWidget(AbstractDWidget widget) {
        moveUpWidget(widget, 1);
    }

    private void moveUpWidget(AbstractDWidget widget, int positions) {
        widget.getParent().moveUpWidget(widget, positions);
    }

    public void moveDownWidget(AbstractDWidget widget) {
        moveDownWidget(widget, 1);
    }

    private void moveDownWidget(AbstractDWidget widget, int positions) {
        widget.getParent().moveDownWidget(widget, positions);
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

        Class<? extends AbstractDWidget> widgetClass = template.getWidgetClass();
        String label = template.getLabel();
        int offset = template.getOffset();

        AbstractDWidget dWidget = null;

        // Widget constructor: label
        if (DComment.class.equals(widgetClass)) {
            dWidget = new DComment(template.getLabel());
        } else if (DBoolean.class.equals(widgetClass)) {
            // Widget constructor: label, offset
            dWidget = new DBoolean(template.getLabel(), template.getOffset());
        } else if (DLongInteger.class.equals(widgetClass)) {
            dWidget = new DLongInteger(template.getLabel(), template.getOffset());
        } else if (DInteger.class.equals(widgetClass)) {
            dWidget = new DInteger(template.getLabel(), template.getOffset());
        } else if (DShortInteger.class.equals(widgetClass)) {
            dWidget = new DShortInteger(template.getLabel(), template.getOffset());
        } else if (DTinyInteger.class.equals(widgetClass)) {
            dWidget = new DTinyInteger(template.getLabel(), template.getOffset());
        } else {
            // Widget constructor: label, offset, length
            int length = template.getLength();
            if (DText.class.equals(widgetClass)) {
                dWidget = new DText(label, offset, length);
            } else {
                // Widget constructor: label, offset, length, fraction
                int fraction = template.getFraction();
                if (DDecimal.class.equals(widgetClass)) {
                    dWidget = new DDecimal(label, offset, length, fraction);
                }
            }
        }

        if (dWidget != null) {
            // Optional parameters
            dWidget.setHorizontalSpan(template.getHorizontalSpan());

            return dWidget;
        }

        throw new IllegalArgumentException("Illegal widget class: " + template.getClass()); //$NON-NLS-1$
    }

    public DReferencedObject createReferencedObjectFromTemplate(DTemplateReferencedObject template) {
        DReferencedObject referencedObject = new DReferencedObject(template.getName(), template.getLibrary(), template.getType());
        return referencedObject;
    }

    public ControlPayload getPayloadFromControl(Control control) {
        return (ControlPayload)control.getData(DE.KEY_DATA_SPACE_PAYLOAD);
    }

    public DEditor generateEditor(AbstractWrappedDataSpace dataSpace) {

        if (AbstractWrappedDataSpace.CHARACTER.equals(dataSpace.getDataType())) {
            return generateCharacterEditor(dataSpace.getLength());
        } else if (AbstractWrappedDataSpace.DECIMAL.equals(dataSpace.getDataType())) {
            return generateDecimalEditor(dataSpace.getLength(), dataSpace.getDecimalPositions());
        } else if (AbstractWrappedDataSpace.LOGICAL.equals(dataSpace.getDataType())) {
            return generateLogicalEditor();
        } else {
            throw new IllegalArgumentException("Unknown data area type: " + dataSpace.getDataType()); //$NON-NLS-1$
        }
    }

    private DEditor generateCharacterEditor(int length) {
        DTemplateEditor tEditor = new DTemplateEditor(GENERATED, 1);
        DEditor dEditor = createDialogFromTemplate(tEditor);

        DTemplateWidget tWidget = new DTemplateWidget(DText.class, Messages.DftText_Character_value_colon, 0, length);
        AbstractDWidget dWidget = createWidgetFromTemplate(tWidget);
        addWidgetToEditor(dEditor, dWidget);

        return dEditor;
    }

    private DEditor generateDecimalEditor(int digits, int decimalPositions) {
        DTemplateEditor tEditor = new DTemplateEditor(GENERATED, 1);
        DEditor dEditor = createDialogFromTemplate(tEditor);

        DTemplateWidget tWidget = new DTemplateWidget(DDecimal.class, Messages.DftText_Decimal_value_colon, 0, digits, decimalPositions);
        AbstractDWidget dWidget = createWidgetFromTemplate(tWidget);
        addWidgetToEditor(dEditor, dWidget);

        return dEditor;
    }

    private DEditor generateLogicalEditor() {
        DTemplateEditor tEditor = new DTemplateEditor(GENERATED, 1);
        DEditor dEditor = createDialogFromTemplate(tEditor);

        DTemplateWidget tWidget = new DTemplateWidget(DBoolean.class, Messages.DftText_Logical_value_colon, 0);
        AbstractDWidget dWidget = createWidgetFromTemplate(tWidget);
        addWidgetToEditor(dEditor, dWidget);

        return dEditor;
    }

    public static boolean hasOffset(Class<? extends AbstractDWidget> widgetClass) {
        if (!DComment.class.equals(widgetClass)) {
            return true;
        }
        return false;
    }

    public static boolean hasLength(Class<? extends AbstractDWidget> widgetClass) {
        if (DText.class.equals(widgetClass) || DDecimal.class.equals(widgetClass)) {
            return true;
        }
        return false;
    }

    public static boolean hasFraction(Class<? extends AbstractDWidget> widgetClass) {
        if (DDecimal.class.equals(widgetClass)) {
            return true;
        }
        return false;
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
        Validator validator = Validator.getDecimalInstance(dDecimal.getLength(), dDecimal.getFraction());

        return validator;
    }

    private void setPayload(Control control, AbstractDWidget widget) {
        control.setData(DE.KEY_DATA_SPACE_PAYLOAD, new ControlPayload(widget, control));
    }

    public AbstractDWidget getWidgetFromControl(Control control) {
        ControlPayload payload = getPayloadFromControl(control);
        AbstractDWidget widget = payload.getWidget();
        return widget;
    }

    public String getControlValue(Control control) {
        ControlPayload payload = getPayloadFromControl(control);
        if (payload == null) {
            return null;
        }

        if (control instanceof Button) {
            if (((Button)control).getSelection()) {
                return DE.BOOLEAN_TRUE_1;
            } else {
                return DE.BOOLEAN_FALSE_0;
            }
        } else if (control instanceof Text) {
            return ((Text)control).getText();
        }

        throw new IllegalArgumentException("Unknown SWT control: " + control); //$NON-NLS-1$
    }

    public boolean isManagedControl(Control control) {
        if (getPayloadFromControl(control) != null) {
            return true;
        }
        return false;
    }

    public boolean hasInvalidDataWarning(Control control) {
        if (getPayloadFromControl(control) != null) {
            return getPayloadFromControl(control).hasInvalidDataWarning();
        }
        return false;
    }

    public boolean hasInvalidDataError(Control control) {
        if (getPayloadFromControl(control) != null) {
            return getPayloadFromControl(control).hasInvalidDataError();
        }
        return false;
    }

    public void setControlValue(Control control, DDataSpaceValue value) {
        if (!isManagedControl(control)) {
            return;
        }

        ControlPayload payload = getPayloadFromControl(control);
        AbstractDWidget widget = payload.getWidget();

        try {

            if (widget instanceof DText) {
                String textValue = value.getString(widget.getOffset(), widget.getLength());
                ((Text)control).setText(StringHelper.trimR(textValue));
                if (textValue.length() != widget.getLength()) {
                    payload.setInvalidDataWarning(true);
                }
            } else if (widget instanceof DBoolean) {
                Boolean boolValue = value.getBoolean(widget.getOffset(), widget.getLength());
                Button button = (Button)control;
                button.setSelection(boolValue);
                if (isLocked(button) && payload.getLockedListener() != null) {
                    ButtonLockedListener listener = (ButtonLockedListener)payload.getLockedListener();
                    listener.setLockedValue(boolValue);
                }
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

        } catch (Throwable e) {
            payload.setInvalidDataError(true);
        }
    }

    public Throwable updateDataSpaceFromControl(Control control, DDataSpaceValue value) {
        if (!isManagedControl(control)) {
            return null;
        }

        ControlPayload payload = getPayloadFromControl(control);
        AbstractDWidget widget = payload.getWidget();

        try {

            if (widget instanceof DText) {
                String text = ((Text)control).getText();
                value.setString(text, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DBoolean) {
                boolean boolValue = ((Button)control).getSelection();
                value.setBoolean(boolValue, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DTinyInteger) {
                Byte tinyValue = Byte.parseByte(((Text)control).getText());
                value.setTiny(tinyValue, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DShortInteger) {
                Short shortValue = Short.valueOf(((Text)control).getText());
                value.setShort(shortValue, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DInteger) {
                Integer intValue = Integer.valueOf(((Text)control).getText());
                value.setInteger(intValue, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DLongInteger) {
                Long longValue = Long.valueOf(((Text)control).getText());
                value.setLongInteger(longValue, widget.getOffset(), widget.getLength());
            } else if (widget instanceof DDecimal) {
                BigDecimal decimalValue = new BigDecimal(((Text)control).getText());
                DDecimal decimalWidget = (DDecimal)widget;
                value.setDecimal(decimalValue, decimalWidget.getOffset(), decimalWidget.getLength(), decimalWidget.getFraction());
            }

        } catch (Throwable e) {
            return e;
        }

        return null;
    }

    private boolean isLocked(Control control) {
        ControlPayload payload = getPayloadFromControl(control);
        return payload.isLocked();
    }

    private Button createBooleanWidget(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        createLabel(parent, widget);

        Button checkBox = WidgetFactory.createCheckbox(parent);
        GridData layoutData = getDataWidgetLayoutData(columnsPerEditorColumn, widget);
        checkBox.setLayoutData(layoutData);
        return checkBox;
    }

    private Text createDecimalWidget(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = WidgetFactory.createDecimalText(parent);
        GridData layoutData = getDataWidgetLayoutData(columnsPerEditorColumn, widget);
        text.setLayoutData(layoutData);
        return text;
    }

    private Text createIntegerWidget(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = WidgetFactory.createIntegerText(parent);
        GridData layoutData = getDataWidgetLayoutData(columnsPerEditorColumn, widget);
        text.setLayoutData(layoutData);
        return text;
    }

    private Text createTextWidget(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        createLabel(parent, widget);

        Text text = WidgetFactory.createText(parent);
        text.setTextLimit(widget.getLength());
        GridData layoutData = getDataWidgetLayoutData(columnsPerEditorColumn, widget);
        text.setLayoutData(layoutData);

        return text;
    }

    protected GridData getDataWidgetLayoutData(int columnsPerEditorColumn, AbstractDWidget widget) {
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalSpan = columnsPerEditorColumn * (widget.getHorizontalSpan() - 1) + 1;
        return layoutData;
    }

    private Label createCommentWidget(Composite parent, int columnsPerEditorColumn, AbstractDWidget widget) {

        int style;
        int heightHint;
        if (DComment.SEPARATOR.equals(widget.getLabel())) {
            style = SWT.SEPARATOR | SWT.HORIZONTAL;
            heightHint = 10;
        } else {
            style = SWT.NONE;
            heightHint = -1;
        }

        Label label = new Label(parent, style);
        if (!DComment.NONE.equals(widget.getLabel())) {
            label.setText(widget.getLabel());
        }
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalSpan = columnsPerEditorColumn * (widget.getHorizontalSpan());
        layoutData.heightHint = heightHint;
        label.setLayoutData(layoutData);

        return label;
    }

    private void createLabel(Composite parent, AbstractDWidget widget) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(widget.getLabel());
    }

}
