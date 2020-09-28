/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.swt.widgets.AutoScrollbarsListener;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.base.swt.widgets.SelectAllFocusListener;
import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.swt.widgets.connectioncombo.ConnectionCombo;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;
import biz.isphere.core.swt.widgets.extension.point.IDirectoryDialog;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;
import biz.isphere.core.swt.widgets.hexeditor.HexText;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.core.swt.widgets.stringlisteditor.StringListEditor;

/**
 * Factory for creating SWT widgets.
 * 
 * @author Thomas Raddatz
 */
public final class WidgetFactory {

    private static final int NAME_FIELD_WIDTH_HINT = 90;

    /**
     * The instance of this Singleton class.
     */
    private static WidgetFactory instance;

    private WidgetFactoryContributionsHandler widgetFactoryContributionsHandler;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private WidgetFactory() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static WidgetFactory getInstance() {
        if (instance == null) {
            instance = new WidgetFactory();
        }
        return instance;
    }

    /**
     * Constructs a new instance of this class given its parent and a style
     * value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class SWT
     * which is applicable to instances of this class, or must be built by
     * bitwise OR'ing together (that is, using the int "|" operator) two or more
     * of those SWT style constants. The class description lists the style
     * constants that are applicable to the class. Style bits are also inherited
     * from superclasses.
     * 
     * @param parent - a shell which will be the parent of the new instance
     * @param style - the style of dialog to construct, such as {@link SWT#SAVE}
     *        or {@link SWT#OPEN}
     */
    public static IFileDialog getFileDialog(Shell parent, int style) {
        return WidgetFactory.getInstance().produceFileDialog(parent, style);
    }

    /**
     * Constructs a new instance of this class given its parent and a style
     * value describing its behavior and appearance.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     * @return the directory dialog
     * @see org.eclipse.swt.widgets.DirectoryDialog
     */
    public static IDirectoryDialog getDirectoryDialog(Shell parent) {
        return WidgetFactory.getInstance().produceDirectoryDialog(parent, SWT.APPLICATION_MODAL);
    }

    /**
     * Produces a color selector control.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return color selector
     */
    public static ColorSelector createColorSelector(Composite parent) {
        return WidgetFactory.getInstance().produceColorSelector(parent);
    }

    /**
     * Produces a separator.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return separator
     */
    public static Label createSeparator(Composite parent) {
        return createSeparator(parent, 1);
    }

    /**
     * Produces a separator, spanning multiple columns.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param span - number of columns to span
     * @return separator
     */
    public static Label createSeparator(Composite parent, int span) {
        return WidgetFactory.getInstance().produceSeparator(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    }

    /**
     * Produces a line filler.
     * 
     * @param parent - composite control which will be the parent of the new
     *        instance (cannot be null)
     */
    public static Control createLineFiller(Composite parent) {
        return WidgetFactory.getInstance().produceLineFiller(parent, SWT.DEFAULT);
    }

    /**
     * Produces a 'name' text field. The field is upper-case only and limited to
     * 10 characters.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return text field
     */
    public static Text createNameText(Composite parent) {

        return createNameText(parent, true);
    }

    /**
     * Produces a 'name' text field. The field is upper-case only and limited to
     * 10 characters.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param widthHint - set default text width
     * @return text field
     */
    public static Text createNameText(Composite parent, boolean widthHint) {

        Text text = createUpperCaseText(parent);
        text.setTextLimit(10);

        if (widthHint) {
            GridData gd = new GridData();
            gd.widthHint = NAME_FIELD_WIDTH_HINT;
            text.setLayoutData(gd);
        }

        return text;
    }

    /**
     * Produces a single line text field with a border.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return text field
     */
    public static Text createText(Composite parent) {
        return WidgetFactory.getInstance().produceText(parent, SWT.NONE, true);
    }

    /**
     * Produces a read-only single line text field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only text field
     */
    public static Text createReadOnlyText(Composite parent) {

        Text text = WidgetFactory.getInstance().produceText(parent, SWT.NONE, false);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a single line text field with a border. Input is upper-case
     * only.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return text field
     */
    public static Text createUpperCaseText(Composite parent) {

        Text text = WidgetFactory.getInstance().produceText(parent, SWT.NONE, true);
        text.addVerifyListener(new UpperCaseOnlyVerifier());

        return text;
    }

    /**
     * Produces an upper-case, read-only single line text field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only text field
     */
    public static Text createUpperCaseReadOnlyText(Composite parent) {

        Text text = WidgetFactory.getInstance().produceText(parent, SWT.NONE, false);
        text.addVerifyListener(new UpperCaseOnlyVerifier());
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a password field with a border.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return password field
     */
    public static Text createPassword(Composite parent) {
        return WidgetFactory.getInstance().produceText(parent, SWT.PASSWORD, true);
    }

    /**
     * Produces a label with selectable text.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return label with selectable text
     */
    public static Text createSelectableLabel(Composite parent) {

        Text text = new Text(parent, SWT.NONE);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a multi-line line label with selectable text. If the text does
     * not fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return multi-line label with selectable text
     */
    public static Text createSelectableMultilineLabel(Composite parent) {

        Text text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);

        Listener scrollBarListener = new AutoScrollbarsListener();
        text.addListener(SWT.Resize, scrollBarListener);
        text.addListener(SWT.Modify, scrollBarListener);
        text.setEditable(false);

        // if (autoSelect) {
        // text.addFocusListener(new SelectAllFocusListener());
        // }

        return text;
    }

    /**
     * Produces a multi-line line text field with a border. If the text does not
     * fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return multi-line text field
     */
    public static Text createMultilineText(Composite parent) {
        return WidgetFactory.getInstance().produceMultilineText(parent, SWT.NONE, false);
    }

    /**
     * Produces a multi-line line text field with a border. If the text does not
     * fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param wordWrap - <code>true</code>, to enable word wrap
     * @param autoSelect - <code>true</code>, to select the field content when
     *        entering the field wit the cursor
     * @return multi-line text field
     */
    public static Text createMultilineText(Composite parent, boolean wordWrap, boolean autoSelect) {
        int style;
        if (wordWrap) {
            style = SWT.WRAP;
        } else {
            style = SWT.H_SCROLL;
        }
        return WidgetFactory.getInstance().produceMultilineText(parent, style, autoSelect);
    }

    /**
     * Produces a read-only multiline line text field. If the text does not fit
     * into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only multi-line text field
     */
    public static Text createReadOnlyMultilineText(Composite parent) {

        Text text = WidgetFactory.createMultilineText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a read-only multiline line text field. If the text does not fit
     * into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param wordWrap - <code>true</code>, to enable word wrap
     * @param autoSelect - <code>true</code>, to select the field content when
     *        entering the field wit the cursor
     * @return read-only multi-line text field
     */
    public static Text createReadOnlyMultilineText(Composite parent, boolean wordWrap, boolean autoSelect) {

        Text text = WidgetFactory.createMultilineText(parent, wordWrap, autoSelect);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces an integer text field with a border. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return integer text field
     */
    public static Text createIntegerText(Composite parent) {
        return createIntegerText(parent, false);
    }

    /**
     * Produces an integer text field with a border. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param hasSign - specifies whether to enable the signs '+' and '-'.
     * @return integer text field
     */
    public static Text createIntegerText(Composite parent, boolean hasSign) {

        Text text = WidgetFactory.createText(parent);
        text.addVerifyListener(new NumericOnlyVerifyListener(false, hasSign));

        return text;
    }

    /**
     * Produces a read-only integer text field. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only integer text field
     */
    public static Text createReadOnlyIntegerText(Composite parent) {

        Text text = WidgetFactory.createIntegerText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a decimal text field with a border. Only the character 0-9 and
     * comma are allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return decimal text field
     */
    public static Text createDecimalText(Composite parent) {
        return createDecimalText(parent, false);
    }

    /**
     * Produces a decimal text field with a border. Only the character 0-9 and
     * comma are allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param hasSign - specifies whether to enable the signs '+' and '-'.
     * @return decimal text field
     */
    public static Text createDecimalText(Composite parent, boolean hasSign) {

        Text text = WidgetFactory.createText(parent);
        text.addVerifyListener(new NumericOnlyVerifyListener(true, hasSign));

        return text;
    }

    /**
     * Produces a read-only decimal text field with a border. Only the character
     * 0-9 and comma are allowed to be entered.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param border - specifies whether or not to add a border
     * @return read-only decimal text field
     */
    public static Text createReadOnlyDecimalText(Composite parent, boolean border) {

        Text text = WidgetFactory.createDecimalText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return combo field
     */
    public static Combo createCombo(Composite parent) {
        return WidgetFactory.getInstance().produceComboField(parent, SWT.NONE);
    }

    /**
     * Produces a history combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only history combo field
     */
    public static HistoryCombo createNameHistoryCombo(Composite parent) {

        HistoryCombo combo = WidgetFactory.getInstance().produceHistoryComboField(parent, SWT.NONE);
        combo.addVerifyListener(new UpperCaseOnlyVerifier());
        combo.setTextLimit(10);

        return combo;
    }

    /**
     * Produces a combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return combo field
     */
    public static Combo createDecimalCombo(Composite parent) {

        Combo combo = WidgetFactory.getInstance().produceComboField(parent, SWT.NONE);
        combo.addVerifyListener(new NumericOnlyVerifyListener(true, false));

        return combo;
    }

    /**
     * Produces an upper-case combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return combo field
     */
    public static Combo createUpperCaseCombo(Composite parent) {
        Combo combo = WidgetFactory.getInstance().produceComboField(parent, SWT.NONE);
        combo.addVerifyListener(new UpperCaseOnlyVerifier());
        return combo;
    }

    /**
     * Produces a read-only combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only combo field
     */
    public static Combo createReadOnlyCombo(Composite parent) {
        return WidgetFactory.getInstance().produceComboField(parent, SWT.READ_ONLY);
    }

    /**
     * Produces a read-only combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param style - the style of dialog to construct
     * @return read-only combo field
     */
    public static Combo createReadOnlyCombo(Composite parent, int style) {
        return WidgetFactory.getInstance().produceComboField(parent, SWT.READ_ONLY | style);
    }

    /**
     * Produces a read-only history combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only history combo field
     */
    public static HistoryCombo createReadOnlyHistoryCombo(Composite parent) {
        return WidgetFactory.getInstance().produceHistoryComboField(parent, SWT.READ_ONLY);
    }

    /**
     * Produces a read-only connection combo field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only connection combo field
     */
    public static ConnectionCombo createConnectionCombo(Composite parent, int style) {
        return WidgetFactory.getInstance().produceConnectionComboField(parent, style);
    }

    /**
     * Produces a spinner field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return spinner field
     */
    public static Spinner createSpinner(Composite parent) {
        return WidgetFactory.getInstance().produceSpinnerField(parent, SWT.BORDER);
    }

    /**
     * Produces a read-only spinner field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only spinner field
     */
    public static Spinner createReadOnlySpinner(Composite parent) {
        return WidgetFactory.getInstance().produceSpinnerField(parent, SWT.BORDER | SWT.READ_ONLY);
    }

    /**
     * Produces a checkbox field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return checkbox field
     */
    public static Button createCheckbox(Composite parent) {
        return WidgetFactory.getInstance().produceCheckboxField(parent, null);
    }

    /**
     * Produces a checkbox field with a label.
     * 
     * @param parent - a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param label - label of the checkbox
     * @return checkbox field
     */
    public static Button createCheckbox(Composite parent, String label) {
        return WidgetFactory.getInstance().produceCheckboxField(parent, label);
    }

    /**
     * Produces a read-only checkbox field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only checkbox field
     */
    public static Button createReadOnlyCheckbox(Composite parent) {

        Button checkBox = WidgetFactory.createCheckbox(parent);
        checkBox.setEnabled(false);

        return checkBox;
    }

    /**
     * Produces a push button.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return push button
     */
    public static Button createPushButton(Composite parent) {
        return WidgetFactory.getInstance().producePushButton(parent);
    }

    /**
     * Produces a push button with a label.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param string - button label
     * @return push button
     */
    public static Button createPushButton(Composite parent, String label) {
        Button button = WidgetFactory.getInstance().producePushButton(parent);
        button.setText(label);
        return button;
    }

    /**
     * Produces a read-only push button field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only push button field
     */
    public static Button createReadOnlyPushButton(Composite parent) {

        Button pushButton = WidgetFactory.createPushButton(parent);
        pushButton.setEnabled(false);

        return pushButton;
    }

    /**
     * Produces a toggle button.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return toggle button
     */
    public static Button createToggleButton(Composite parent) {
        return WidgetFactory.getInstance().produceToggleButton(parent);
    }

    /**
     * Produces a toggle button with a label.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param style - the style of control to construct, such as
     *        {@link SWT#FLAT}
     * @return toggle button
     */
    public static Button createToggleButton(Composite parent, int style) {
        Button button = WidgetFactory.getInstance().produceToggleButton(parent);
        return button;
    }

    /**
     * Produces a toggle button with a label.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param string - button label
     * @return toggle button
     */
    public static Button createToggleButton(Composite parent, String label) {
        Button button = WidgetFactory.getInstance().produceToggleButton(parent);
        button.setText(label);
        return button;
    }

    /**
     * Produces a toggle button with a label.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param string - button label
     * @param style - the style of control to construct, such as
     *        {@link SWT#FLAT}
     * @return toggle button
     */
    public static Button createToggleButton(Composite parent, String label, int style) {
        Button button = WidgetFactory.getInstance().produceToggleButton(parent);
        button.setText(label);
        return button;
    }

    /**
     * Produces a radio button field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return radio button field
     */
    public static Button createRadioButton(Composite parent) {
        return WidgetFactory.getInstance().produceRadioButton(parent, null);
    }

    /**
     * Produces a radio button field.
     * 
     * @param parent - a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param label - label of the radio button
     * @return radio button field
     */
    public static Button createRadioButton(Composite parent, String label) {
        return WidgetFactory.getInstance().produceRadioButton(parent, label);
    }

    /**
     * Produces a read-only radio button field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return read-only radio button field
     */
    public static Button createReadOnlyRadioButton(Composite parent) {

        Button radioButton = WidgetFactory.createRadioButton(parent);
        radioButton.setEnabled(false);

        return radioButton;
    }

    /**
     * Produces a hex editor composed of two synchronized displays: an
     * hexadecimal and a basic ASCII char display.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return hex editor
     */
    public static HexText createHexText(Composite parent) {

        HexText hexText = new HexText(parent, SWT.NONE);

        return hexText;
    }

    /**
     * Produces a text field with a border and content assistance. If the text
     * does not fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return text field with content assistance
     */
    public static ContentAssistText createContentAssistText(Composite parent) {
        return WidgetFactory.getInstance().produceContentAssistText(parent, SWT.BORDER, false);
    }

    /**
     * Produce a date selector field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return DateTime field configured as a date selector
     */
    public static IDateEdit createDateEdit(Composite parent) {
        return WidgetFactory.getInstance().produceDateEdit(parent);
    }

    /**
     * Produce a time selector field.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return DateTime field configured as a time selector
     */
    public static ITimeEdit createTimeEdit(Composite parent) {
        return WidgetFactory.getInstance().produceTimeEdit(parent);
    }

    /**
     * Produces a StringListEditor.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @return editor for editing a list of strings
     */
    public static StringListEditor createStringListEditor(Composite parent) {
        return createStringListEditor(parent, SWT.NONE);
    }

    /**
     * Produces a StringListEditor.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param style the style of control to construct
     * @return editor for editing a list of strings
     */
    public static StringListEditor createStringListEditor(Composite parent, int style) {
        return WidgetFactory.getInstance().produceStringListEditor(parent, style);
    }

    /**
     * Produces a SqlEditor.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param historyKey the history key used for storing the history values
     * @param dialogSettingsManager the dialog settings manager used for storing
     *        history values
     * @return editor for editing SQL where clauses
     */
    public static SqlEditor createSqlEditor(Composite parent, String historyKey, DialogSettingsManager dialogSettingsManager) {
        return createSqlEditor(parent, historyKey, dialogSettingsManager, SWT.NONE);
    }

    /**
     * Produces a SqlEditor.
     * 
     * @param parent a composite control which will be the parent of the new
     *        instance (cannot be null)
     * @param historyKey the history key used for storing the history values
     * @param dialogSettingsManager the dialog settings manager used for storing
     *        history values
     * @param style the style of control to construct
     * @return editor for editing SQL where clauses
     */
    public static SqlEditor createSqlEditor(Composite parent, String historyKey, DialogSettingsManager dialogSettingsManager, int style) {
        return WidgetFactory.getInstance().produceSqlEditor(parent, historyKey, dialogSettingsManager, style);
    }

    /*
     * Private worker procedures, doing the actual work.
     */

    private IFileDialog produceFileDialog(Shell parent, int style) {

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(parent, style);

        return dialog;
    }

    private IDirectoryDialog produceDirectoryDialog(Shell parent, int style) {

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IDirectoryDialog dialog = factory.getDirectoryDialog(parent, style);

        return dialog;
    }

    private ColorSelector produceColorSelector(Composite parent) {
        return new ColorSelector(parent);
    }

    private Text produceText(Composite parent, int style, boolean autoSelect) {

        Text text = new Text(parent, style | SWT.BORDER);
        if (autoSelect) {
            text.addFocusListener(new SelectAllFocusListener());
        }

        return text;
    }

    private Text produceMultilineText(Composite parent, int style, boolean autoSelect) {

        Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | style);

        Listener scrollBarListener = new AutoScrollbarsListener();
        text.addListener(SWT.Resize, scrollBarListener);
        text.addListener(SWT.Modify, scrollBarListener);

        if (autoSelect) {
            text.addFocusListener(new SelectAllFocusListener());
        }

        return text;
    }

    private ContentAssistText produceContentAssistText(Composite parent, int style, boolean autoSelect) {

        ContentAssistText text = new ContentAssistText(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP | style);

        if (autoSelect) {
            text.addFocusListener(new SelectAllFocusListener());
        }

        return text;
    }

    private Combo produceComboField(Composite parent, int style) {

        Combo combo = new Combo(parent, style | SWT.DROP_DOWN);

        return combo;
    }

    private HistoryCombo produceHistoryComboField(Composite parent, int style) {

        HistoryCombo combo = new HistoryCombo(parent, style | SWT.DROP_DOWN);

        return combo;
    }

    private ConnectionCombo produceConnectionComboField(Composite parent, int style) {

        ConnectionCombo combo = new ConnectionCombo(parent, style);

        return combo;
    }

    private Spinner produceSpinnerField(Composite parent, int style) {

        Spinner spinner = new Spinner(parent, style);

        return spinner;
    }

    private Button produceCheckboxField(Composite parent, String label) {

        Button checkBox = new Button(parent, SWT.CHECK);

        if (label != null) {
            checkBox.setText(label);
        }

        return checkBox;
    }

    private Button producePushButton(Composite parent) {

        Button pushButton = new Button(parent, SWT.PUSH);

        return pushButton;
    }

    private Button produceToggleButton(Composite parent) {

        Button pushButton = new Button(parent, SWT.TOGGLE);

        return pushButton;
    }

    private Button produceRadioButton(Composite parent, String label) {

        Button radioButton = new Button(parent, SWT.RADIO);

        if (label != null) {
            radioButton.setText(label);
        }

        return radioButton;
    }

    private IDateEdit produceDateEdit(Composite parent) {

        IDateEdit dateEdit = getWidgetFactoryContributionsHandler().getDateEdit(parent, SWT.BORDER);

        return dateEdit;
    }

    private ITimeEdit produceTimeEdit(Composite parent) {

        ITimeEdit timeEdit = getWidgetFactoryContributionsHandler().getTimeEdit(parent, SWT.BORDER);

        return timeEdit;
    }

    private Label produceSeparator(Composite parent, int span) {

        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = span;
        separator.setLayoutData(gridData);

        return separator;
    }

    private StringListEditor produceStringListEditor(Composite parent, int style) {
        return new StringListEditor(parent, style);
    }

    private SqlEditor produceSqlEditor(Composite parent, String historyKey, DialogSettingsManager dialogSettingsManager, int style) {
        return new SqlEditor(parent, historyKey, dialogSettingsManager, style);
    }

    private Control produceLineFiller(Composite parent, int height) {

        Label filler = new Label(parent, SWT.NONE);
        filler.setSize(height, 1);

        Layout layout = parent.getLayout();
        if (layout instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout)layout;
            GridData gd = new GridData();
            gd.horizontalSpan = gridLayout.numColumns;
            if (height != SWT.DEFAULT) {
                gd.heightHint = height;
            }
            filler.setLayoutData(gd);
        }

        return filler;
    }

    private WidgetFactoryContributionsHandler getWidgetFactoryContributionsHandler() {
        if (widgetFactoryContributionsHandler == null) {
            widgetFactoryContributionsHandler = new WidgetFactoryContributionsHandler();
        }
        return widgetFactoryContributionsHandler;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }
}
